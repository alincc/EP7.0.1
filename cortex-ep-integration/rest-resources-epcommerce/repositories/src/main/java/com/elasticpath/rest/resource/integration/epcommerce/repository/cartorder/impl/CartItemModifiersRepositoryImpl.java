/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;


import org.apache.commons.lang3.StringUtils;

import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.cartmodifier.CartItemModifierFieldOption;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartItemModifiersRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.service.cartitemmodifier.CartItemModifierService;

/**
 * Implementation of {@link CartItemModifiersRepository}.
 */
@Singleton
@Named("cartItemModifiersRepository")
public class CartItemModifiersRepositoryImpl implements CartItemModifiersRepository {

	private static final String CARTITEMMODIFIERGROUP_NOT_FOUND_MESSAGE = "CartItemModifierGroup not found";

	private static final String CARTITEMMODIFIERFIELD_NOT_FOUND_MESSAGE = "CartItemModifierField not found";

	private static final String CARTITEMMODIFIERFIELDOPTION_NOT_FOUND_MESSAGE = "CartItemModifierFieldOption not found";

	private final CartItemModifierService cartItemModifierService;
	private final ShoppingCartRepository shoppingCartRepository;
	private final ProductSkuRepository productSkuRepository;
	private final OrderRepository orderRepository;

	/**
	 * Constructor of service contexts repository.
	 *
	 * @param cartItemModifierService a {@link CartItemModifierService}.
	 * @param shoppingCartRepository  a {@link ShoppingCartRepository}.
	 * @param productSkuRepository    a {@link ProductSkuRepository}.
	 * @param orderRepository         a {@link OrderRepository}.
	 */
	@Inject
	public CartItemModifiersRepositoryImpl(
			@Named("cartItemModifierService")
			final CartItemModifierService cartItemModifierService,
			@Named("shoppingCartRepository")
			final ShoppingCartRepository shoppingCartRepository,
			@Named("productSkuRepository")
			final ProductSkuRepository productSkuRepository,
			@Named("orderRepository")
			final OrderRepository orderRepository) {

		this.cartItemModifierService = cartItemModifierService;
		this.shoppingCartRepository = shoppingCartRepository;
		this.productSkuRepository = productSkuRepository;
		this.orderRepository = orderRepository;
	}

	@Override
	public ExecutionResult<CartItemModifierGroup> findCartItemModifierGroupByCode(final String cartItemModifierGroupCode) {
		checkNotNull(cartItemModifierGroupCode);

		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				CartItemModifierGroup cartItemModifierGroup = Assign.ifNotNull(
					cartItemModifierService.findCartItemModifierGroupByCode(cartItemModifierGroupCode),
					OnFailure.returnNotFound(CARTITEMMODIFIERGROUP_NOT_FOUND_MESSAGE));
				return ExecutionResultFactory.createReadOK(cartItemModifierGroup);
			}
		}.execute();
	}

	@Override
	public ExecutionResult<CartItemModifierField> findCartItemModifierFieldBy(final String cartItemModifierFieldCode,
			final String cartItemModifierGroupCode) {
		checkNotNull(cartItemModifierGroupCode);
		checkNotNull(cartItemModifierFieldCode);

		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				CartItemModifierField cartItemModifierField = Assign.ifNotNull(
					getCartItemModifierField(cartItemModifierFieldCode, cartItemModifierGroupCode),
					OnFailure.returnNotFound(CARTITEMMODIFIERFIELD_NOT_FOUND_MESSAGE));
				return ExecutionResultFactory.createReadOK(cartItemModifierField);
			}
		}.execute();
	}

	@Override
	public ExecutionResult<CartItemModifierFieldOption> findCartItemModifierFieldOptionBy(final String cartItemModifierOptionValue,
			final String cartItemModifierFieldCode, final String cartItemModifierGroupCode) {
		checkNotNull(cartItemModifierGroupCode);
		checkNotNull(cartItemModifierFieldCode);
		checkNotNull(cartItemModifierOptionValue);

		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				CartItemModifierField cartItemModifierField = Assign.ifNotNull(
					getCartItemModifierField(cartItemModifierFieldCode, cartItemModifierGroupCode),
					OnFailure.returnNotFound(CARTITEMMODIFIERFIELDOPTION_NOT_FOUND_MESSAGE));
				CartItemModifierFieldOption cartItemModifierFieldOption = Assign.ifNotNull(
					getCartItemModifierFieldOption(cartItemModifierOptionValue, cartItemModifierField),
					OnFailure.returnNotFound(CARTITEMMODIFIERFIELDOPTION_NOT_FOUND_MESSAGE));
				return ExecutionResultFactory.createReadOK(cartItemModifierFieldOption);
			}
		}.execute();
	}

	@Override
	public ExecutionResult<Map<CartItemModifierField, String>> findCartItemModifierValues(final String shoppingCartGuid,
			final String shoppingItemGuid) {

		return new ExecutionResultChain() {
			@Override
			protected ExecutionResult<?> build() {
				ShoppingItem shoppingItem = getShoppingItem(shoppingCartGuid, shoppingItemGuid);
				Set<CartItemModifierGroup> cartItemModifierGroups = getCartItemModifierGroups(shoppingItem.getSkuGuid());
				Set<CartItemModifierField> allCartItemModifierFields = getCartItemModifierFields(cartItemModifierGroups);
				return getModifierFieldValues(shoppingItem.getFields(), allCartItemModifierFields);
			}
		}.execute();
	}

	@Override
	public ExecutionResult<Map<CartItemModifierField, String>> findPurchaseItemModifierValues(final String storeCode, final String purchaseGuid,
			final String purchaseLineItemGuid) {

		return new ExecutionResultChain() {
			@Override
			protected ExecutionResult<?> build() {
				Order order = Assign.ifSuccessful(orderRepository.findByGuid(storeCode, purchaseGuid));
				OrderSku orderSku = order.getOrderSkuByGuid(purchaseLineItemGuid);
				Map<String, String> itemData = orderSku.getFields();

				Set<CartItemModifierGroup> cartItemModifierGroups = getCartItemModifierGroups(orderSku.getSkuGuid());
				Set<CartItemModifierField> allCartItemModifierFields = getCartItemModifierFields(cartItemModifierGroups);
				return getModifierFieldValues(itemData, allCartItemModifierFields);
			}
		}.execute();
	}

	private ShoppingItem getShoppingItem(final String shoppingCartGuid, final String cartItemGuid) {
		ShoppingCart shoppingCart = Assign.ifSuccessful(shoppingCartRepository.getShoppingCart(shoppingCartGuid));

		return shoppingCart.getCartItemByGuid(cartItemGuid);
	}

	private Set<CartItemModifierField> getCartItemModifierFields(final Set<CartItemModifierGroup> cartItemModifierGroups) {
		return cartItemModifierGroups.stream().map(CartItemModifierGroup::getCartItemModifierFields)
			.flatMap(Set::stream)
			.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	private Set<CartItemModifierGroup> getCartItemModifierGroups(final String skuGuid) {
		ProductSku productSku = Assign.ifSuccessful(productSkuRepository.getProductSkuWithAttributesByGuid(skuGuid));
		Product product = productSku.getProduct();
		ProductType productType = product.getProductType();
		return productType.getCartItemModifierGroups();
	}

	private ExecutionResult<Map<CartItemModifierField, String>> getModifierFieldValues(final Map<String, String> itemData,
			final Set<CartItemModifierField> allCartItemModifierFields) {
		Map<CartItemModifierField, String> matchingModifierFieldData = new LinkedHashMap<>();
		for (CartItemModifierField modField : allCartItemModifierFields) {
			String key = modField.getCode();
			if (itemData.containsKey(key)) {
				matchingModifierFieldData.put(modField, itemData.get(key));
			} else {
				matchingModifierFieldData.put(modField, StringUtils.EMPTY);
			}
		}
		return ExecutionResultFactory.createReadOK(matchingModifierFieldData);
	}

	private CartItemModifierField getCartItemModifierField(final String cartItemModifierFieldCode, final String cartItemModifierGroupCode) {
		CartItemModifierField cartItemModifierField = null;

		CartItemModifierGroup cartItemModifierGroup = cartItemModifierService.findCartItemModifierGroupByCode(cartItemModifierGroupCode);
		for (CartItemModifierField field : cartItemModifierGroup.getCartItemModifierFields()) {
			if (cartItemModifierField == null && StringUtils.equals(cartItemModifierFieldCode, field.getCode())) {
				cartItemModifierField = field;
			}
		}
		return cartItemModifierField;
	}

	private CartItemModifierFieldOption getCartItemModifierFieldOption(final String cartItemModifierOptionValue,
			final CartItemModifierField cartItemModifierField) {
		CartItemModifierFieldOption cartItemModifierFieldOption = null;

		for (CartItemModifierFieldOption option : cartItemModifierField.getCartItemModifierFieldOptions()) {
			if (cartItemModifierFieldOption == null && StringUtils.equals(cartItemModifierOptionValue, option.getValue())) {
				cartItemModifierFieldOption = option;
			}
		}
		return cartItemModifierFieldOption;
	}

	@Override
	public List<CartItemModifierField> findCartItemModifiersByProduct(final Product product) {

		return cartItemModifierService.findCartItemModifierFieldsByProductType(product.getProductType());

	}

	@Override
	public List<String> findMissingRequiredFieldCodesByShoppingItem(final ShoppingItem shoppingItem) {

		// find fields by given sku guid.
		ProductSku productSku = Assign.ifSuccessful(productSkuRepository.getProductSkuWithAttributesByGuid(shoppingItem.getSkuGuid()));
		List<CartItemModifierField> fields = findCartItemModifiersByProduct(productSku.getProduct());

		// find and return the missed code.
		return fields.stream()
			.filter(CartItemModifierField::isRequired)
			.map(CartItemModifierField::getCode)
			.filter(code -> StringUtils.isBlank(shoppingItem.getFields().get(code)))
			.collect(Collectors.toList());

	}

}
