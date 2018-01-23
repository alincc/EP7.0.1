/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;
import rx.Single;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.price.PriceRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistRepository;

/**
 * The test of {@link ItemValidationServiceImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemValidationServiceImplTest {

	private static final String ENCODED_ITEM_ID = "encodedItemId";
	private static final String LINE_ITEM_ID = "lineItemId";
	private static final String SKU_CODE = "skuCode";

	@Mock
	private ItemRepository itemRepository;

	@Mock
	private WishlistRepository wishlistRepository;

	@Mock
	private PriceRepository priceRepository;

	@Mock
	private StoreProductRepository storeProductRepository;

	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapterImpl;

	@InjectMocks
	private ItemValidationServiceImpl itemValidationService;

	@Before
	public void initialize() {
		itemValidationService.setReactiveAdapter(reactiveAdapterImpl);
	}


	/**
	 * Test item is purchasable.
	 */
	@Test
	public void shouldBeTrueWhenItemIsPurchasable() {
		WishlistLineItemIdentifier wishlistLineItemIdentifier = prepareMocksForPurchasableItems(true, false, true);

		Observable<Message> messages = itemValidationService.isItemPurchasable(wishlistLineItemIdentifier);
		Boolean isPurchasable = messages.isEmpty().toBlocking().single();

		assertThat(isPurchasable)
				.as("Item should be purchasable")
				.isTrue();
		verify(storeProductRepository).findDisplayableStoreProductWithAttributesByProductGuid(any(), any());
		verify(priceRepository).priceExists(any(), any());
	}

	/**
	 * Test item is not purchasable.
	 */
	@Test
	public void shouldBeFalseWhenItemIsNotPurchasable() {
		WishlistLineItemIdentifier wishlistLineItemIdentifier = prepareMocksForPurchasableItems(false, false, true);

		Observable<Message> messages = itemValidationService.isItemPurchasable(wishlistLineItemIdentifier);
		Boolean isPurchasable = messages.isEmpty().toBlocking().single();

		assertThat(isPurchasable)
				.as("Item should not be purchasable")
				.isFalse();
		verify(storeProductRepository).findDisplayableStoreProductWithAttributesByProductGuid(any(), any());
	}

	/**
	 * Test item is not purchasable because it is not sold separately.
	 */
	@Test
	public void shouldBeFalseWhenItemIsNotSoldSeparately() {
		WishlistLineItemIdentifier wishlistLineItemIdentifier = prepareMocksForPurchasableItems(true, true, true);

		Observable<Message> messages = itemValidationService.isItemPurchasable(wishlistLineItemIdentifier);
		Boolean isPurchasable = messages.isEmpty().toBlocking().single();

		assertThat(isPurchasable)
				.as("Item should not be purchasable")
				.isFalse();
		verify(storeProductRepository).findDisplayableStoreProductWithAttributesByProductGuid(any(), any());
	}

	/**
	 * Test item is not purchasable because the price does not exist.
	 */
	@Test
	public void shouldBeFalseWhenPriceDoesNotExist() {
		WishlistLineItemIdentifier wishlistLineItemIdentifier = prepareMocksForPurchasableItems(true, false, false);

		Observable<Message> messages = itemValidationService.isItemPurchasable(wishlistLineItemIdentifier);
		Boolean isPurchasable = messages.isEmpty().toBlocking().single();

		assertThat(isPurchasable)
				.as("Item should not be purchasable")
				.isFalse();
		verify(storeProductRepository).findDisplayableStoreProductWithAttributesByProductGuid(any(), any());
		verify(priceRepository).priceExists(any(), any());
	}

	private WishlistLineItemIdentifier prepareMocksForPurchasableItems(final boolean isProductSkuPurchasable,
																	   final boolean isProductSoldSeparately, final boolean doesPriceExist) {

		WishlistLineItemIdentifier mockWishlistLineItemIdentifier = mock(WishlistLineItemIdentifier.class, Answers.RETURNS_DEEP_STUBS.get());
		WishlistIdentifier mockWishlistIdentifier = mock(WishlistIdentifier.class, Answers.RETURNS_DEEP_STUBS.get());
		WishList mockWishList = mock(WishList.class, Answers.RETURNS_DEEP_STUBS.get());

		ProductSku mockProductSku = mock(ProductSku.class, Answers.RETURNS_DEEP_STUBS.get());
		Product mockProduct = mock(Product.class, Answers.RETURNS_DEEP_STUBS.get());
		StoreProduct mockStoreProduct = mock(StoreProduct.class, Answers.RETURNS_DEEP_STUBS.get());

		when(mockWishlistLineItemIdentifier.getWishlistLineItems().getWishlist()).thenReturn(mockWishlistIdentifier);
		when(mockWishlistLineItemIdentifier.getLineItemId().getValue().toString()).thenReturn(LINE_ITEM_ID);
		when(wishlistRepository.getWishlist(any())).thenReturn(Single.just(mockWishList));
		when(wishlistRepository.getProductSku(mockWishList, LINE_ITEM_ID)).thenReturn(Single.just(mockProductSku));
		String storeCode = "storeCode";
		when(mockWishList.getStoreCode()).thenReturn(storeCode);
		when(itemRepository.getItemIdForSku(mockProductSku)).thenReturn(ExecutionResultFactory.createReadOK(ENCODED_ITEM_ID));
		when(mockProductSku.getProduct()).thenReturn(mockProduct);
		when(storeProductRepository.
				findDisplayableStoreProductWithAttributesByProductGuid(storeCode, mockProduct.getGuid()))
				.thenReturn(ExecutionResultFactory.createReadOK(mockStoreProduct));

		when(mockProductSku.getSkuCode()).thenReturn(SKU_CODE);

		when(mockStoreProduct.isSkuPurchasable(SKU_CODE)).thenReturn(isProductSkuPurchasable);
		when(mockStoreProduct.isNotSoldSeparately()).thenReturn(isProductSoldSeparately);
		when(priceRepository.priceExists(storeCode, ENCODED_ITEM_ID)).thenReturn(ExecutionResultFactory.createReadOK(doesPriceExist));

		return mockWishlistLineItemIdentifier;
	}

}