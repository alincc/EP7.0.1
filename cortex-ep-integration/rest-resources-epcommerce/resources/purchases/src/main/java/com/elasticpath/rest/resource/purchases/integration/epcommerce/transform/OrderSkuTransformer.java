/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.integration.epcommerce.transform;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItemTaxSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemConfigurationEntity;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartItemModifiersRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transforms between a {@link OrderSku} and {@link PurchaseLineItemEntity}, and vice versa.
 */
@Singleton
@Named("orderSkuTransformer")
public class OrderSkuTransformer extends AbstractDomainTransformer<OrderSku, PurchaseLineItemEntity> {

	private final MoneyTransformer moneyTransformer;
	private final ProductSkuRepository productSkuRepository;
	private final PricingSnapshotRepository pricingSnapshotRepository;
	private final CartItemModifiersRepository cartItemModifiersRepository;

	/**
	 * Default constructor.
	 *  @param moneyTransformer     the money transformer
	 * @param productSkuRepository product sku repository
	 * @param pricingSnapshotRepository the pricing snapshot repository
	 * @param cartItemModifiersRepository the cart item modifiers repository
	 */
	@Inject
	public OrderSkuTransformer(
			@Named("moneyTransformer")
			final MoneyTransformer moneyTransformer,
			@Named("productSkuRepository")
			final ProductSkuRepository productSkuRepository,
			@Named("pricingSnapshotRepository")
			final PricingSnapshotRepository pricingSnapshotRepository,
			@Named("cartItemModifiersRepository")
			final CartItemModifiersRepository cartItemModifiersRepository) {

		this.moneyTransformer = moneyTransformer;
		this.productSkuRepository = productSkuRepository;
		this.pricingSnapshotRepository = pricingSnapshotRepository;
		this.cartItemModifiersRepository = cartItemModifiersRepository;
	}

	@Override
	public OrderSku transformToDomain(final PurchaseLineItemEntity purchaseLineItemEntity, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public PurchaseLineItemEntity transformToEntity(final OrderSku orderSku, final Locale locale) {
		PurchaseLineItemEntity.Builder lineItemDto = PurchaseLineItemEntity.builder()
				.withName(orderSku.getDisplayName());

		lineItemDto.withConfiguration(createConfiguration(orderSku));

		if (isBundleComponent(orderSku)) {
			int parentQuantity = orderSku.getParent().getQuantity();
			lineItemDto.withQuantity(orderSku.getQuantity() / parentQuantity);
		} else {
			lineItemDto.withQuantity(orderSku.getQuantity())
					.withLineExtensionAmount(createAmount(orderSku, locale))
					.withLineExtensionTax(createTax(orderSku, locale))
					.withLineExtensionTotal(createTotal(orderSku, locale));
		}

		return lineItemDto.build();
	}

	private PurchaseLineItemConfigurationEntity createConfiguration(final OrderSku orderSku) {
		PurchaseLineItemConfigurationEntity.Builder builder = PurchaseLineItemConfigurationEntity.builder();
		Map<String, String> fields = orderSku.getFields();
		if (fields != null) {
			List<CartItemModifierField> cartItemModifierFields  = retrieveCartItemModifierFields(orderSku.getSkuGuid());
			cartItemModifierFields.forEach(field -> builder.addingProperty(field.getCode(), fields.get(field.getCode())));
		}
		return builder.build();
	}

	private boolean isBundleComponent(final OrderSku orderSku) {
		return orderSku.getParent() != null;
	}

	private Collection<CostEntity> createAmount(final OrderSku orderSku, final Locale locale) {
		final ShoppingItemPricingSnapshot pricingSnapshot = Assign.ifSuccessful(pricingSnapshotRepository.getPricingSnapshotForOrderSku(orderSku));
		Money amount = pricingSnapshot.getPriceCalc().withCartDiscounts().getMoney();

		CostEntity costEntity = moneyTransformer.transformToEntity(amount, locale);
		return Collections.singleton(costEntity);
	}

	private Collection<CostEntity> createTax(final OrderSku orderSku, final Locale locale) {
		Money tax = getTax(orderSku, orderSku.getCurrency());

		CostEntity costEntity = moneyTransformer.transformToEntity(tax, locale);
		return Collections.singleton(costEntity);
	}

	private Collection<CostEntity> createTotal(final OrderSku orderSku, final Locale locale) {
		final ShoppingItemPricingSnapshot pricingSnapshot = Assign.ifSuccessful(pricingSnapshotRepository.getPricingSnapshotForOrderSku(orderSku));
		Money subTotal = pricingSnapshot.getPriceCalc().withCartDiscounts().getMoney();
		Money total;

		//We only ship to one address, so we will not have a case where one shipment is tax inclusive and another is exclusive.
		//TODO: CE should be making the correct calculations for inclusive tax so that we don't have to.
		if (findShipment(orderSku).isInclusiveTax()) {
			total = subTotal;
		} else {
			Money tax = getTax(orderSku, orderSku.getCurrency());
			total = subTotal.add(tax);
		}

		CostEntity costEntity = moneyTransformer.transformToEntity(total, locale);
		return Collections.singleton(costEntity);
	}

	private OrderShipment findShipment(final OrderSku orderSku) {
		OrderShipment shipment = orderSku.getShipment();
		if (shipment == null) {
			for (ShoppingItem shoppingItem : orderSku.getChildren()) {
				OrderSku childSku = (OrderSku) shoppingItem;
				shipment = findShipment(childSku);
				if (shipment != null) {
					break;
				}
			}
		}
		return shipment;
	}

	/**
	 * Recursively Gets the tax for the shopping item. <br>
	 * Note: bundles currently do not have tax, and must be calculated by summing up the tax of their constituents.
	 *
	 * @param item         the shopping item.
	 * @param rootCurrency the currency of the root item.
	 * @return the calculated tax.
	 */
	Money getTax(final OrderSku item, final Currency rootCurrency) {
		final ShoppingItemTaxSnapshot taxSnapshot = Assign.ifSuccessful(pricingSnapshotRepository.getTaxSnapshotForOrderSku(item));

		BigDecimal taxAmount = taxSnapshot.getTaxAmount();

		Money tax = Money.valueOf(taxAmount, rootCurrency);

		final Boolean isProductBundle = Assign.ifSuccessful(productSkuRepository.isProductBundle(item.getSkuGuid()));
		if (isProductBundle) {
			final List<OrderSku> bundleItems = toOrderSkus(item.getChildren());

			for (final OrderSku bundleItem : bundleItems) {
				tax = tax.add(getTax(bundleItem, rootCurrency));
			}
		}
		return tax;
	}

	/**
	 * Gets the list of cart item modifier fields for the given skuGuid.
	 * @param skuGuid the sku guid
	 * @return the list of fields, can be empty
	 */
	private List<CartItemModifierField> retrieveCartItemModifierFields(final String skuGuid) {
		ExecutionResult<ProductSku> productSkuExecutionResult = productSkuRepository.getProductSkuWithAttributesByGuid(skuGuid);

		if (!productSkuExecutionResult.isSuccessful()) {
			return Collections.emptyList();
		}
		ProductSku productSku = productSkuExecutionResult.getData();

		return cartItemModifiersRepository.findCartItemModifiersByProduct(productSku.getProduct());
}
	/**
	 * Converts ShoppingItem instances to OrderSku instances, by casting.
	 *
	 * @param shoppingItems the shopping items
	 * @return a collection of order skus
	 */
	private List<OrderSku> toOrderSkus(final List<ShoppingItem> shoppingItems) {
		return Lists.transform(shoppingItems, new Function<ShoppingItem, OrderSku>() {
			@Override
			public OrderSku apply(final ShoppingItem input) {
				return ((OrderSku) input);
			}
		});
	}

}
