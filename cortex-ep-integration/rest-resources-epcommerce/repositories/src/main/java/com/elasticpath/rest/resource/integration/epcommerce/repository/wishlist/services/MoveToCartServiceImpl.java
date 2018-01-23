/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Single;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.carts.LineItemConfigurationEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.carts.LineItemsIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.ItemValidationService;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.MoveToCartService;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistRepository;

/**
 * Move to cart service.
 */
@Component
public class MoveToCartServiceImpl implements MoveToCartService {

	private WishlistRepository wishlistRepository;
	private ShoppingCartRepository shoppingCartRepository;
	private ItemValidationService itemValidationService;
	private ReactiveAdapter reactiveAdapter;
	private static final Logger LOG = LoggerFactory.getLogger(MoveToCartServiceImpl.class);


	@Override
	public Single<LineItemIdentifier> move(final WishlistLineItemIdentifier wishlistLineItemIdentifier, final LineItemEntity lineItemEntity) {
		WishlistIdentifier wishlistIdentifier = wishlistLineItemIdentifier.getWishlistLineItems().getWishlist();
		LOG.trace("Moving item {} from wishlist {} to cart", lineItemEntity, wishlistIdentifier);

		return this.<LineItemIdentifier>validateQuantity(lineItemEntity)
				.switchIfEmpty(itemValidationService.isItemPurchasable(wishlistLineItemIdentifier)
						.flatMap(this::getResourceOperationFailure))
				.switchIfEmpty(moveItemToCart(wishlistLineItemIdentifier, lineItemEntity, wishlistIdentifier).toObservable())
				.toSingle();
	}

	/**
	 * Validate quantity.
	 * 
	 * @param lineItemEntity line item entity
	 * @param <T> the type parameter
	 * @return the structured error message, if any.
	 */
	protected <T> Observable<T> validateQuantity(final LineItemEntity lineItemEntity) {
		Integer quantity = lineItemEntity.getQuantity();
		if (quantity == null) {
			return this.<T>getInvalidQuantityErrorMessage().toObservable();
		} else if (quantity <= 0) {
			return this.<T>getInvalidQuantityErrorMessage(quantity).toObservable();
		}
		return Observable.empty();
	}

	/**
	 * Get structured error message.
	 * 
	 * @param <T> the type parameter
	 * @return the message
	 */
	protected <T> Single<T> getInvalidQuantityErrorMessage() {
		List<Message> structuredErrors = new ArrayList<>();
		Map<String, String> data = ImmutableMap.of("field-name", "quantity");
		String message = "'quantity' value must be an integer.";
		structuredErrors.add(Message.builder()
				.withId("field.invalid.integer.format")
				.withDebugMessage(message)
				.withData(data)
				.build());
		return Single.error(ResourceOperationFailure.badRequestBody(message, structuredErrors));
	}

	/**
	 * Get structured error message.
	 * 
	 * @param quantity the quantity
	 * @param <T> the type parameter
	 * @return the message
	 */
	protected <T> Single<T> getInvalidQuantityErrorMessage(final Integer quantity) {
		List<Message> structuredErrors = new ArrayList<>();
		Map<String, String> data = ImmutableMap.of("field-name", "quantity",
				"min-value", "1",
				"invalid-value", String.valueOf(quantity));
		String message = "'quantity' value '" + quantity + "' must be greater than or equal to '1'.";
		structuredErrors.add(Message.builder()
				.withId("field.invalid.minimum.value")
				.withDebugMessage(message)
				.withData(data)
				.build());
		return Single.error(ResourceOperationFailure.badRequestBody(message, structuredErrors));
	}

	/**
	 * Get the resource operation failure.
	 * 
	 * @param message the message
	 * @param <T> the type parameter
	 * @return the resource operation failure
	 */
	protected <T> Observable<T> getResourceOperationFailure(final Message message) {
		List<Message> structuredErrors = new ArrayList<>();
		structuredErrors.add(message);
		return Observable.error(ResourceOperationFailure.stateFailure("Item is not purchasable", structuredErrors));
	}

	/**
	 * Move item to cart.
	 * 
	 * @param wishlistLineItemIdentifier wishlist line item identifier
	 * @param lineItemEntity line item entity
	 * @param wishlistIdentifier wishlist identifier
	 * @return the new line item identifier.
	 */
	protected Single<LineItemIdentifier> moveItemToCart(final WishlistLineItemIdentifier wishlistLineItemIdentifier, final LineItemEntity
			lineItemEntity, final WishlistIdentifier wishlistIdentifier) {
		String lineItemGuid = wishlistLineItemIdentifier.getLineItemId().getValue().toString();
		String wishlistId = wishlistIdentifier.getWishlistId().getValue().toString();

		return wishlistRepository.getWishlist(wishlistId)
				.flatMap(wishList -> wishlistRepository.getProductSku(wishList, lineItemGuid)
						.map(ProductSku::getSkuCode)
						.flatMap(skuCode -> getDefaultShoppingCart()
								.flatMap(cart -> moveItemToCart(cart, lineItemGuid, skuCode, lineItemEntity)
										.flatMap(updatedCart -> buildLineItemIdentifier(wishList, skuCode, updatedCart)))));
	}

	/**
	 * Get the default shopping cart.
	 * 
	 * @return the cart
	 */
	protected Single<ShoppingCart> getDefaultShoppingCart() {
		return reactiveAdapter.fromRepositoryAsSingle(shoppingCartRepository::getDefaultShoppingCart);
	}

	/**
	 * Move item to cart.
	 * 
	 * @param shoppingCart shopping cart
	 * @param lineItemGuid line item guid
	 * @param skuCode sku code
	 * @param lineItemEntity line item entity
	 * @return the updated cart
	 */
	protected Single<ShoppingCart> moveItemToCart(final ShoppingCart shoppingCart, final String lineItemGuid, final String skuCode,
												final LineItemEntity lineItemEntity) {
		return reactiveAdapter.fromRepositoryAsSingle(() -> shoppingCartRepository.moveItemToCart(shoppingCart, lineItemGuid,
				skuCode, lineItemEntity.getQuantity(), getConfigurableFields(lineItemEntity)));
	}

	/**
	 * Get configurable fields for a line item.
	 * 
	 * @param lineItemEntity line item entity
	 * @return configurable fields
	 */
	protected Map<String, String> getConfigurableFields(final LineItemEntity lineItemEntity) {
		return Optional.ofNullable(lineItemEntity.getConfiguration())
				.map(LineItemConfigurationEntity::getDynamicProperties)
				.orElse(Collections.emptyMap());
	}

	/**
	 * Build line item identifier.
	 * 
	 * @param wishList the wishlist
	 * @param skuCode the sku code
	 * @param cart the cart
	 * @return the line item identifier
	 */
	protected Single<LineItemIdentifier> buildLineItemIdentifier(final WishList wishList, final String skuCode, final ShoppingCart cart) {
		ShoppingItem addedItem = cart.getCartItem(skuCode);
		return Single.just(LineItemIdentifier.builder()
				.withLineItemId(StringIdentifier.of(addedItem.getGuid()))
				.withLineItems(LineItemsIdentifier.builder()
						.withCart(CartIdentifier.builder()
								.withCartId(StringIdentifier.of(cart.getGuid()))
								.withScope(StringIdentifier.of(wishList.getStoreCode()))
								.build())
						.build())
				.build());
	}

	@Reference
	public void setWishlistRepository(final WishlistRepository wishlistRepository) {
		this.wishlistRepository = wishlistRepository;
	}

	@Reference
	public void setShoppingCartRepository(final ShoppingCartRepository shoppingCartRepository) {
		this.shoppingCartRepository = shoppingCartRepository;
	}

	@Reference
	public void setItemValidationService(final ItemValidationService itemValidationService) {
		this.itemValidationService = itemValidationService;
	}

	@Reference
	public void setReactiveAdapter(final ReactiveAdapter reactiveAdapter) {
		this.reactiveAdapter = reactiveAdapter;
	}
}
