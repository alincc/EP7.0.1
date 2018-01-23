/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.price.PriceRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.ItemValidationService;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistRepository;

/**
 * Item validation service.
 */
@Component
public class ItemValidationServiceImpl implements ItemValidationService {

	private WishlistRepository wishlistRepository;
	private ItemRepository itemRepository;
	private PriceRepository priceRepository;
	private StoreProductRepository storeProductRepository;
	private ReactiveAdapter reactiveAdapter;

	@Override
	public Observable<Message> isItemPurchasable(final WishlistLineItemIdentifier wishlistLineItemIdentifier) {
		WishlistIdentifier wishlistIdentifier = wishlistLineItemIdentifier.getWishlistLineItems().getWishlist();
		String lineItemGuid = wishlistLineItemIdentifier.getLineItemId().getValue().toString();
		String wishlistId = wishlistIdentifier.getWishlistId().getValue().toString();
		
		return wishlistRepository.getWishlist(wishlistId)
				.flatMapObservable(toMessages(lineItemGuid));
	}

	/**
	 * Get structured advise messages, if any.
	 * @param lineItemGuid the line item guid
	 * @return the function
	 */
	protected Func1<WishList, Observable<Message>> toMessages(final String lineItemGuid) {
		return wishList -> wishlistRepository.getProductSku(wishList, lineItemGuid)
				.flatMapObservable(productSku -> getStoreProduct(wishList.getStoreCode(), productSku.getProduct())
						.flatMap(storeProduct -> isPurchasable(productSku, wishList.getStoreCode(), storeProduct))
						.flatMapObservable(isPurchasable -> getMessages(productSku, isPurchasable)));
	}

	/**
	 * Checks if the item is purchasable.
	 * 
	 * @param productSku the product sku
	 * @param storeCode store code
	 * @param storeProduct store product
	 * @return purchasable status
	 */
	protected Single<Boolean> isPurchasable(final ProductSku productSku, final String storeCode, final StoreProduct storeProduct) {
		boolean isPurchasable = storeProduct.isSkuPurchasable(productSku.getSkuCode()) && !storeProduct.isNotSoldSeparately();
		if (isPurchasable) {
			return reactiveAdapter.fromRepositoryAsSingle(() -> itemRepository.getItemIdForSku(productSku))
					.flatMap(itemId -> reactiveAdapter.fromRepositoryAsSingle(() -> priceRepository.priceExists(storeCode, itemId)));
		}
		return Single.just(false);
	}

	/**
	 * Construct the structured message.
	 * 
	 * @param productSku the product sku
	 * @param isPurchasable the purchasable status
	 * @return structured message
	 */
	protected Observable<Message> getMessages(final ProductSku productSku, final Boolean isPurchasable) {
		List<Message> structuredMessages = new ArrayList<>();
		if (!isPurchasable) {
			final Map<String, String> data = ImmutableMap.of("item-code", productSku.getSkuCode());
			structuredMessages.add(Message.builder()
					.withId("cart.item.not.purchasable")
					.withDebugMessage("Item '" + productSku.getSkuCode()
							+ "' is not purchasable and cannot be added to your cart.")
					.withData(data)
					.build());
		}
		return Observable.from(structuredMessages);
	}

	/**
	 * Get the store product.
	 * 
	 * @param storeCode store code
	 * @param product product
	 * @return store product
	 */
	protected Single<StoreProduct> getStoreProduct(final String storeCode, final Product product) {
		return reactiveAdapter.fromRepositoryAsSingle(() -> storeProductRepository
				.findDisplayableStoreProductWithAttributesByProductGuid(storeCode, product.getGuid()));
	}

	@Reference
	public void setWishlistRepository(final WishlistRepository wishlistRepository) {
		this.wishlistRepository = wishlistRepository;
	}

	@Reference
	public void setItemRepository(final ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	@Reference
	public void setPriceRepository(final PriceRepository priceRepository) {
		this.priceRepository = priceRepository;
	}

	@Reference
	public void setStoreProductRepository(final StoreProductRepository storeProductRepository) {
		this.storeProductRepository = storeProductRepository;
	}

	@Reference
	public void setReactiveAdapter(final ReactiveAdapter reactiveAdapter) {
		this.reactiveAdapter = reactiveAdapter;
	}
}
