/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.services;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Single;
import rx.functions.Func1;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.MoveToWishlistService;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistRepository;

/**
 * Move to wishlist service.
 */
@Component
public class MoveToWishlistServiceImpl implements MoveToWishlistService {

	private ShoppingCartRepository shoppingCartRepository;
	private WishlistRepository wishlistRepository;
	private ReactiveAdapter reactiveAdapter;

	private static final Logger LOG = LoggerFactory.getLogger(MoveToWishlistServiceImpl.class);

	@Override
	public Single<WishlistLineItemIdentifier> move(final LineItemIdentifier lineItemIdentifier) {

		String cartLineItemId = lineItemIdentifier.getLineItemId().getValue().toString();

		CartIdentifier cartIdentifier = lineItemIdentifier.getLineItems().getCart();
		String cartId = cartIdentifier.getCartId().getValue().toString();
		String scope = cartIdentifier.getScope().getValue().toString();

		LOG.trace("Moving line id {} from cart {} to wishlist", cartLineItemId, cartId);

		return wishlistRepository.getDefaultWishlistId(scope)
				.flatMap(toWoshlistLineItemIdentifier(cartLineItemId, scope));
	}

	/**
	 * Move item to wishlist.
	 * 
	 * @param cartLineItemId line item id
	 * @param scope scope
	 * @return the function
	 */
	protected Func1<String, Single<WishlistLineItemIdentifier>> toWoshlistLineItemIdentifier(final String cartLineItemId, final String scope) {
		return wishlistId -> getDefaultShoppingCart()
				.flatMap(cart -> moveToWishlist(cartLineItemId, cart))
				.map(shoppingItem -> getWishlistLineItemIdentifier(scope, wishlistId, shoppingItem));
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
	 * Move item to wishlist.
	 * 
	 * @param cartLineItemId line item id
	 * @param cart cart
	 * @return the shopping item on wishlist
	 */
	protected Single<ShoppingItem> moveToWishlist(final String cartLineItemId, final ShoppingCart cart) {
		return reactiveAdapter.fromRepositoryAsSingle(() -> shoppingCartRepository.moveItemToWishlist(cart, cartLineItemId));
	}

	/**
	 * Get the wishlist line item identifier.
	 * 
	 * @param scope scope
	 * @param wishlistId wishlistId
	 * @param shoppingItem shopping item
	 * @return wishlist line item identifier
	 */
	protected WishlistLineItemIdentifier getWishlistLineItemIdentifier(final String scope, final String wishlistId, final ShoppingItem shoppingItem) {
		return wishlistRepository.getWishlistLineItemIdentifier(scope, wishlistId, shoppingItem.getGuid());
	}

	@Reference
	public void setShoppingCartRepository(final ShoppingCartRepository shoppingCartRepository) {
		this.shoppingCartRepository = shoppingCartRepository;
	}

	@Reference
	public void setWishlistRepository(final WishlistRepository wishlistRepository) {
		this.wishlistRepository = wishlistRepository;
	}

	@Reference
	public void setReactiveAdapter(final ReactiveAdapter reactiveAdapter) {
		this.reactiveAdapter = reactiveAdapter;
	}
}
