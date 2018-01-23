/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.exception.InvalidBundleTreeStructureException;
import com.elasticpath.domain.catalog.Availability;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.LineItemConfigurationEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.domain.wrapper.LineItem;
import com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.transform.LineItemTransformer;
import com.elasticpath.rest.resource.carts.lineitems.integration.LineItemLookupStrategy;
import com.elasticpath.rest.resource.carts.lineitems.integration.LineItemWriterStrategy;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.sellingchannel.ProductNotPurchasableException;
import com.elasticpath.sellingchannel.ProductUnavailableException;

/**
 * EP Commerce implementation of the cart lineitem writer strategy.
 */
@Singleton
@Named("lineItemWriterStrategy")
public class LineItemWriterStrategyImpl implements LineItemWriterStrategy {

	private static final String LINEITEM_WAS_NOT_FOUND = "No line item was found with GUID = %s.";
	private static final String INVALID_BUNDLE_CONFIGURATION = "Dynamic Bundle not supported";
	private static final String SKU_WAS_NOT_FOUND = "Could not find item for item configuration ID.";
	private static final String CANNOT_ADD_TO_CART = "Item cannot be added to cart.";

	private final ShoppingCartRepository shoppingCartRepository;
	private final ItemRepository itemRepository;
	private final StoreProductRepository storeProductRepository;
	private final LineItemTransformer lineItemTransformer;
	private final LineItemLookupStrategy lineItemLookupStrategy;
	private final ProductSkuRepository productSkuRepository;

	/**
	 * Constructor for LineItemWriterStrategyImpl.
	 *
	 * @param shoppingCartRepository shopping cart repo
	 * @param itemRepository         the item repo
	 * @param storeProductRepository the store product repo
	 * @param productSkuRepository   product sku repository
	 * @param lineItemTransformer    the line item transformer
	 * @param lineItemLookupStrategy the {@link LineItemLookupStrategy}
	 */
	@Inject
	LineItemWriterStrategyImpl(
			@Named("shoppingCartRepository")
			final ShoppingCartRepository shoppingCartRepository,
			@Named("itemRepository")
			final ItemRepository itemRepository,
			@Named("storeProductRepository")
			final StoreProductRepository storeProductRepository,
			@Named("productSkuRepository")
			final ProductSkuRepository productSkuRepository,
			@Named("lineItemTransformer")
			final LineItemTransformer lineItemTransformer,
			@Named("lineItemLookupStrategy")
			final LineItemLookupStrategy lineItemLookupStrategy) {

		this.shoppingCartRepository = shoppingCartRepository;
		this.itemRepository = itemRepository;
		this.storeProductRepository = storeProductRepository;
		this.lineItemTransformer = lineItemTransformer;
		this.lineItemLookupStrategy = lineItemLookupStrategy;
		this.productSkuRepository = productSkuRepository;
	}

	@Override
	public ExecutionResult<Void> deleteLineItemFromCart(final String scope, final LineItemEntity entity) {
		ShoppingCart cart = Assign.ifSuccessful(shoppingCartRepository.getShoppingCart(entity.getCartId()));
		String lineItemId = entity.getLineItemId();
		ShoppingItem lineItem = Assign.ifNotNull(cart.getShoppingItemByGuid(lineItemId),
				OnFailure.returnNotFound(LINEITEM_WAS_NOT_FOUND, lineItemId));
		// ensure successful saved cart
		Ensure.successful(shoppingCartRepository.removeItemFromCart(cart, lineItem.getUidPk()));
		return ExecutionResultFactory.<Void>createDeleteOK();

	}

	@Override
	public ExecutionResult<Void> deleteAllLineItemsFromCart(final String scope, final String cartGuid) {
		Ensure.successful(shoppingCartRepository.removeAllItemsFromCart(scope, cartGuid));
		return ExecutionResultFactory.createDeleteOK();
	}

	@Override
	public ExecutionResult<Void> updateLineItem(final String scope, final LineItemEntity updatedLineItemEntity) {
		ShoppingCart cart = Assign.ifSuccessful(getCart(updatedLineItemEntity));
		LineItemEntity lineItem = Assign.ifSuccessful(lineItemLookupStrategy.getLineItem(scope, cart.getGuid(),
				updatedLineItemEntity.getLineItemId()));
		String itemId = lineItem.getItemId();
		String storeCode = cart.getStore()
				.getCode();

		ProductSku productSku = Assign.ifSuccessful(itemRepository.getSkuForItemId(itemId));


		ensureItemIsAvailable(productSku, storeCode);
		ensureItemIsAvailableForPurchase(storeCode, itemId);

		if (updatedLineItemEntity.getQuantity() == 0) {
			return deleteLineItemFromCart(scope, updatedLineItemEntity);
		}

		Ensure.successful(updateItemInCart(updatedLineItemEntity, productSku.getSkuCode(), cart));

		return ExecutionResultFactory.createUpdateOK();
	}

	private ExecutionResult<ShoppingCart> getCart(final LineItemEntity entity) {
		ExecutionResult<ShoppingCart> cartResult;
		String cartId = entity.getCartId();
		if (cartId == null) {
			cartResult = shoppingCartRepository.getDefaultShoppingCart();
		} else {
			cartResult = shoppingCartRepository.getShoppingCart(cartId);
		}
		return cartResult;
	}

	@Override
	public ExecutionResult<LineItemEntity> addToCart(final String scope, final LineItemEntity entity) {
		ShoppingCart cart = Assign.ifSuccessful(shoppingCartRepository.getShoppingCart(entity.getCartId()));
		return addSkuToCart(cart, entity.getItemId(), entity.getQuantity(), Optional.ofNullable(entity.getConfiguration()));
	}

	private ExecutionResult<LineItemEntity> addSkuToCart(final ShoppingCart cart, final String itemId, final int quantity,
			 final Optional<LineItemConfigurationEntity> configuration) {

		ProductSku productSku = Assign.ifSuccessful(itemRepository.getSkuForItemId(itemId));

		String storeCode = cart.getStore().getCode();

		ensureItemIsAvailable(productSku, storeCode);
		ensureItemIsAvailableForPurchase(storeCode, itemId);

		ShoppingItem shoppingItem = Assign.ifSuccessful(createShoppingItem(cart, productSku.getSkuCode(), quantity, configuration));
		return convertShoppingItemToLineItemEntity(cart, shoppingItem, isExistingShoppingItem(shoppingItem, quantity));
	}

	private void ensureItemIsAvailable(final ProductSku productSku, final String storeCode) {
		Boolean productSkuAvailable = Assign.ifSuccessful(isSkuAvailableInStore(storeCode, productSku));
		Ensure.isTrue(productSkuAvailable, OnFailure.returnStateFailure("Item is not available"));
	}

	private void ensureItemIsAvailableForPurchase(final String storeCode, final String itemId) {
		Boolean isPurchasable = Assign.ifSuccessful(lineItemLookupStrategy.isItemPurchasable(storeCode, itemId));
		Ensure.isTrue(isPurchasable, OnFailure.returnForbidden("The item is not available for purchase"));
	}

	private ExecutionResult<Boolean> isSkuAvailableInStore(final String storeCode, final ProductSku sku) {
		StoreProduct storeProduct = Assign.ifSuccessful(storeProductRepository
						.findDisplayableStoreProductWithAttributesByProductGuid(storeCode, sku.getProduct().getGuid()));
		boolean skuAvailable = !Objects.equals(storeProduct.getSkuAvailability(sku.getSkuCode()), Availability.NOT_AVAILABLE);
		return ExecutionResultFactory.createReadOK(skuAvailable);
	}

	private ExecutionResult<LineItemEntity> convertShoppingItemToLineItemEntity(
			final ShoppingCart cart, final ShoppingItem shoppingItem, final boolean existing) {

		ProductSku productSku = Assign.ifSuccessful(productSkuRepository.getProductSkuWithAttributesByGuid(shoppingItem.getSkuGuid()));
		String itemId = Assign.ifSuccessful(itemRepository.getItemIdForSku(productSku));
		String cartGuid = cart.getGuid();
		LineItem lineItem = ResourceTypeFactory.createResourceEntity(LineItem.class)
				.setShoppingItem(shoppingItem)
				.setCartId(cartGuid)
				.setItemId(itemId);
		LineItemEntity lineItemEntity = lineItemTransformer.transformToEntity(lineItem);

		return ExecutionResultFactory.createCreateOKWithData(lineItemEntity, existing);
	}

	private ExecutionResult<ShoppingItem> createShoppingItem(final ShoppingCart cart, final String skuCode, final int quantity,
			 final Optional<LineItemConfigurationEntity> configuration) {
		// cannot be final
		ExecutionResult<ShoppingItem> result;

		try {
			final Map<String, String> fields = configuration.map(LineItemConfigurationEntity::getDynamicProperties).orElse(Collections.emptyMap());
			ExecutionResult<ShoppingCart> cartResult = shoppingCartRepository.addItemToCart(cart, skuCode, quantity, fields);
			Ensure.successful(cartResult);
			ShoppingCart updatedCart = Assign.ifNotNull(cartResult.getData(), OnFailure.returnStateFailure(CANNOT_ADD_TO_CART));
			ShoppingItem shoppingItem = Assign.ifNotNull(
				findShoppingItemBySkuCodeAndFields(updatedCart, skuCode, fields), OnFailure.returnStateFailure(CANNOT_ADD_TO_CART));

			result = ExecutionResultFactory.createReadOK(shoppingItem);
		} catch (ProductNotPurchasableException productNotPurchasableException) {
			result = ExecutionResultFactory.createStateFailure(productNotPurchasableException.getMessage());
		} catch (InvalidBundleTreeStructureException invalidBundleTreeStructureException) {
			result = ExecutionResultFactory.createNotFound(INVALID_BUNDLE_CONFIGURATION);
		} catch (ProductUnavailableException productUnavailableException) {
			result = ExecutionResultFactory.createNotFound(SKU_WAS_NOT_FOUND);
		} catch (EpSystemException epSystemException) {
			result = ExecutionResultFactory.createServerError(epSystemException.getMessage());
		}

		return result;
	}

	private boolean isExistingShoppingItem(final ShoppingItem shoppingItem, final int newQuantity) {
		return newQuantity != shoppingItem.getQuantity();
	}

	private ExecutionResult<Void> updateItemInCart(
			final LineItemEntity updatedLineItemEntity, final String skuCode, final ShoppingCart shoppingCart) {

		Map<String, String> fields = Optional.ofNullable(updatedLineItemEntity.getConfiguration())
				.map(LineItemConfigurationEntity::getDynamicProperties)
				.orElse(Collections.emptyMap());

		String lineItemId = updatedLineItemEntity.getLineItemId();
		ShoppingItem shoppingItem = shoppingCart.getCartItemByGuid(lineItemId);
		shoppingItem.mergeFieldValues(fields);

		Ensure.successful(shoppingCartRepository.updateCartItem(shoppingCart, shoppingItem.getUidPk(),
				skuCode, updatedLineItemEntity.getQuantity()));
		return ExecutionResultFactory.createUpdateOK();
	}

	/**
	 * Find out the shopping item exact match sku code and fields.
	 *
	 * @param shoppingCart the updated shopping cart.
	 * @param skuCode      the sku code.
	 * @param fields       the input fields.
	 * @return the exact one, or null if not found
	 */
	protected ShoppingItem findShoppingItemBySkuCodeAndFields(final ShoppingCart shoppingCart, final String skuCode,
			final Map<String, String> fields) {
		List<ShoppingItem> shoppingItems = shoppingCart.getCartItems(skuCode);
		final Optional<ShoppingItem> shoppingItemOptional = shoppingItems.stream().filter(item -> fields.equals(item.getFields())).findAny();

		return shoppingItemOptional.orElse(null);
	}
}
