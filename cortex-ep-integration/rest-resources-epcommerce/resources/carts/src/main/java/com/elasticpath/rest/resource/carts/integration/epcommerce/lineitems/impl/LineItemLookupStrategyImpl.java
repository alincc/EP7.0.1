/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;


import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.LineItemConfigurationEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.domain.wrapper.LineItem;
import com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.transform.CartItemModifierFieldsTransformer;
import com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.transform.LineItemTransformer;
import com.elasticpath.rest.resource.carts.lineitems.integration.LineItemLookupStrategy;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartItemModifiersRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.price.PriceRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;

/**
 * EP Commerce implementation of the cart lineitem lookup strategy.
 */
@Singleton
@Named("lineItemLookupStrategy")
public class LineItemLookupStrategyImpl implements LineItemLookupStrategy {

	private final ItemRepository itemRepository;
	private final ShoppingCartRepository shoppingCartRepository;
	private final LineItemTransformer lineItemTransformer;
	private final ProductSkuRepository productSkuRepository;
	private final PriceRepository priceRepository;
	private final StoreProductRepository storeProductRepository;
	private final CartItemModifiersRepository cartItemModifiersRepository;
	private final CartItemModifierFieldsTransformer cartItemModifierFieldsTransformer;

	/**
	 * Instantiates a new line item lookup strategy.
	 *
	 * @param shoppingCartRepository the shopping cart repository
	 * @param itemRepository         the item repository
	 * @param lineItemTransformer    the line item transformer
	 * @param productSkuRepository   product sku repository
	 * @param priceRepository        the price repository
	 * @param storeProductRepository the store product repository
	 * @param cartItemModifiersRepository the cart item modifiers repository
	 * @param cartItemModifierFieldsTransformer the cart item modifiers fields transformer
	 */
	@Inject
	@SuppressWarnings("parameternumber")
	public LineItemLookupStrategyImpl(
		@Named("shoppingCartRepository")
			final ShoppingCartRepository shoppingCartRepository,
		@Named("itemRepository")
			final ItemRepository itemRepository,
		@Named("lineItemTransformer")
			final LineItemTransformer lineItemTransformer,
		@Named("productSkuRepository")
			final ProductSkuRepository productSkuRepository,
		@Named("priceRepository")
			final PriceRepository priceRepository,
		@Named("storeProductRepository")
			final StoreProductRepository storeProductRepository,
		@Named("cartItemModifiersRepository")
			final CartItemModifiersRepository cartItemModifiersRepository,
		@Named("cartItemModifierFieldsTransformer")
			final CartItemModifierFieldsTransformer cartItemModifierFieldsTransformer) {

		this.shoppingCartRepository = shoppingCartRepository;
		this.itemRepository = itemRepository;
		this.lineItemTransformer = lineItemTransformer;
		this.productSkuRepository = productSkuRepository;
		this.priceRepository = priceRepository;
		this.storeProductRepository = storeProductRepository;
		this.cartItemModifiersRepository = cartItemModifiersRepository;
		this.cartItemModifierFieldsTransformer = cartItemModifierFieldsTransformer;
	}

	@Override
	public ExecutionResult<LineItemEntity> getLineItem(final String scope, final String cartGuid, final String cartItemGuid) {

		ShoppingCart cart = Assign.ifSuccessful(getCart(cartGuid));
		ShoppingItem shoppingItem = Assign.ifNotNull(cart.getCartItemByGuid(cartItemGuid),
				OnFailure.returnNotFound("Cannot find line item."));
		ProductSku productSku = Assign.ifSuccessful(productSkuRepository.getProductSkuWithAttributesByGuid(shoppingItem.getSkuGuid()));
		String itemId =
				Assign.ifSuccessful(itemRepository.getItemIdForSku(productSku));
		String foundCartGuid = cart.getGuid();

		Map<CartItemModifierField, String> cartItemModifierValues = Assign.ifSuccessful(
			cartItemModifiersRepository.findCartItemModifierValues(cartGuid, cartItemGuid));

		LineItem lineItem = ResourceTypeFactory.createResourceEntity(LineItem.class)
				.setShoppingItem(shoppingItem)
				.setCartId(foundCartGuid)
				.setItemId(itemId)
				.setCartItemModifierValues(cartItemModifierValues);

		LineItemEntity lineItemEntity = lineItemTransformer.transformToEntity(lineItem);
		return ExecutionResultFactory.createReadOK(lineItemEntity);
	}

	@Override
	public ExecutionResult<Collection<String>> getLineItemIdsForCart(final String scope, final String cartGuid) {

		ShoppingCart cart = Assign.ifSuccessful(getCart(cartGuid));
		Collection<ShoppingItem> items = cart.getAllItems();
		Collection<String> lineItemGuids = new ArrayList<>(items.size());

		for (ShoppingItem item : items) {
			lineItemGuids.add(item.getGuid());
		}
		return ExecutionResultFactory.createReadOK(lineItemGuids);
	}

	@Override
	public ExecutionResult<Boolean> isItemPurchasable(final String storeCode, final String itemId) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				ProductSku sku = Assign.ifSuccessful(itemRepository.getSkuForItemId(itemId));
				Product product = sku.getProduct();
				StoreProduct storeProduct = Assign.ifSuccessful(storeProductRepository
								.findDisplayableStoreProductWithAttributesByProductGuid(storeCode, product.getGuid()));
				boolean isPurchasable = storeProduct.isSkuPurchasable(sku.getSkuCode())
						&& !storeProduct.isNotSoldSeparately();
				if (isPurchasable) {
					isPurchasable = Assign.ifSuccessful(priceRepository.priceExists(storeCode, itemId));
				}

				return ExecutionResultFactory.createReadOK(isPurchasable);
			}
		}.execute();
	}

	@Override
	public ExecutionResult<LineItemConfigurationEntity> getItemConfiguration(final String scope, final String itemId) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				ProductSku sku = Assign.ifSuccessful(itemRepository.getSkuForItemId(itemId));
				List<CartItemModifierField> fields = Assign.ifNotEmpty(cartItemModifiersRepository.findCartItemModifiersByProduct(sku.getProduct()),
					OnFailure.returnNotFound());

				return ExecutionResultFactory.createReadOK(cartItemModifierFieldsTransformer.transformToEntity(fields));
			}
		}.execute();
	}

	private ExecutionResult<ShoppingCart> getCart(final String cartGuid) {
		return shoppingCartRepository.getShoppingCart(cartGuid);
	}
}
