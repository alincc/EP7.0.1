/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.repositories;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import rx.Observable;
import rx.Single;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.items.ItemsIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistRepository;

/**
 * Item for WishlistLineItem repository.
 *
 * @param <I>  the identifier type
 * @param <LI> the linked identifier type
 */
@Component
public class ItemIdentifierRepositoryImpl<I extends WishlistLineItemIdentifier, LI extends ItemIdentifier>
		implements LinksRepository<WishlistLineItemIdentifier, ItemIdentifier> {

	private ItemRepository itemRepository;

	private WishlistRepository wishlistRepository;

	private ReactiveAdapter reactiveAdapter;

	@Override
	public Observable<ItemIdentifier> getElements(final WishlistLineItemIdentifier identifier) {

		WishlistIdentifier wishlistIdentifier = identifier.getWishlistLineItems().getWishlist();
		String wishlistId = wishlistIdentifier.getWishlistId().getValue().toString();
		String lineItemGuid = identifier.getLineItemId().getValue().toString();
		String scope = wishlistIdentifier.getWishlists().getScope().getValue().toString();

		return wishlistRepository.getProductSku(wishlistId, lineItemGuid)
				.flatMap(this::getItemIdFromSku)
				.flatMap(itemId -> buildItemIdentifier(scope, itemId))
				.toObservable();
	}

	/**
	 * Get item id.
	 *
	 * @param productSku the product sku
	 * @return the item id
	 */
	protected Single<IdentifierPart<Map<String, String>>> getItemIdFromSku(final ProductSku productSku) {
		return reactiveAdapter.fromServiceAsSingle(() -> itemRepository.getItemIdForProductSku(productSku));
	}

	/**
	 * Build the item identifier.
	 *
	 * @param scope scope
	 * @param itemId itemId
	 * @return item identifier
	 */
	protected Single<ItemIdentifier> buildItemIdentifier(final String scope, final IdentifierPart<Map<String, String>> itemId) {
		return Single.just(ItemIdentifier.builder()
				.withItems(ItemsIdentifier.builder().withScope(StringIdentifier.of(scope)).build())
				.withItemId(itemId)
				.build());
	}

	@Reference
	public void setItemRepository(final ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
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
