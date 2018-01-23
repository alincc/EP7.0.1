/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.sellingchannel.director.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.CartItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.money.Money;
import com.elasticpath.sellingchannel.director.ShoppingItemAssembler;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.misc.TimeService;

/**
 * Tests the {@code CartDirectorImpl} in isolation.
 */
@SuppressWarnings({ "serial" })
@RunWith(MockitoJUnitRunner.class)
public class CartDirectorImplTest {

	private static final String SHOPPING_ITEM_GUID = "shoppingItemGuid";
	private static final String SKU_CODE = "skuCode";
	private static final String SKU_CODE_2 = "testSkuCode2";
	private static final String SKU_123 = "123";
	private static final String SKU_A = "skuA";
	private static final String SKU_GUID = "testSkuGuid";
	private static final String SKU_GUID_2 = "testSkuGuid2";
	private static final long CART_ITEM_UID = 1;
	private static final Currency CURRENCY_CAD = Currency.getInstance("CAD");
	private static final int SHOPPING_ITEM_ORDERING = 5;
	private static final int QUANTITY = 3;

	@Mock private ProductSkuLookup productSkuLookup;
	@Mock private ShoppingItemAssembler shoppingItemAssembler;

	@Spy private CartDirectorImpl cartDirector;

	@Mock private ProductSku productSku;
	@Mock private ProductSku productSku2;
	@Mock private ShoppingCart shoppingCart;
	@Mock private CartItem shoppingItemAlreadyInCart;
	@Mock private ShoppingItem addedShoppingItem;
	@Mock private PriceLookupFacade priceLookupFacade;

	private final List<ShoppingItem> itemsInCart = new ArrayList<>();
	private Integer changeQuantityArgument;

	@Before
	public void setUp() {
		cartDirector.setProductSkuLookup(productSkuLookup);
		cartDirector.setShoppingItemAssembler(shoppingItemAssembler);
		cartDirector.setPriceLookupFacade(priceLookupFacade);

		// Configure class under test to ignore irrelevant code branches
		doNothing().when(cartDirector).priceShoppingItemWithAdjustments(any(ShoppingCart.class), any(ShoppingItem.class));
		doReturn(true).when(cartDirector).isProductPurchasableInStore(any(Store.class), any(Product.class), anyBoolean(), anyString());

		doAnswer((Answer<ShoppingItem>) invocationOnMock -> {
			changeQuantityArgument = (Integer) invocationOnMock.getArguments()[1];
			return (ShoppingItem) invocationOnMock.getArguments()[0];
		}).when(cartDirector).changeQuantityForCartItem(any(ShoppingItem.class), anyInt(), any(ShoppingCart.class));

		// Dependency and parameter mocking
		itemsInCart.add(shoppingItemAlreadyInCart);
		given(shoppingCart.getCartItems()).willReturn(itemsInCart);
		given(shoppingCart.getCartItems(SKU_CODE)).willReturn(itemsInCart);
		given(shoppingCart.getCartItemsBySkuGuid(SKU_GUID)).willReturn(itemsInCart);

		given(shoppingItemAlreadyInCart.getQuantity()).willReturn(1);
		given(addedShoppingItem.getQuantity()).willReturn(1);

		given(shoppingItemAlreadyInCart.getSkuGuid()).willReturn(SKU_GUID);
		given(addedShoppingItem.getSkuGuid()).willReturn(SKU_GUID);

		given(productSkuLookup.findByGuid(SKU_GUID)).willReturn(productSku);
		given(productSku.getSkuCode()).willReturn(SKU_CODE);

		given(shoppingItemAlreadyInCart.getDependentItems()).willReturn(Collections.emptyList());
		given(addedShoppingItem.hasPrice()).willReturn(true);

		given(shoppingCart.addShoppingCartItem(any(ShoppingItem.class))).willAnswer(new ReturnsArgumentAt(0));
	}
	/**
	 * Tests that the adding an item to a cart which already has an item with that sku and the product is not configurable
	 * will add to the quantity.
	 */
	@Test
	public void quantityUpdatedWhenSkuAlreadyInCartAndNotConfigurable() {
		given(addedShoppingItem.isSameMultiSkuItem(productSkuLookup, shoppingItemAlreadyInCart)).willReturn(true);
		doNothing().when(cartDirector).priceShoppingItem(any(ShoppingItem.class), any(Store.class), any(Shopper.class));

		cartDirector.addToCart(addedShoppingItem, shoppingCart, null);
		verify(cartDirector).changeQuantityForCartItem(shoppingItemAlreadyInCart, 2, shoppingCart);

	}

	/**
	 * Tests that the adding an item to a cart which already has an item with that sku and the product *is* configurable
	 * will add the new cart item.
	 */
	@Test
	public void testAddToCartSkuAlreadyInCartConfigurable() {
		doNothing().when(cartDirector).priceShoppingItem(any(ShoppingItem.class), any(Store.class), any(Shopper.class));

		final ProductSku productSku = createProductSku(SKU_123);

		when(addedShoppingItem.getSkuGuid()).thenReturn(productSku.getGuid());
		when(shoppingCart.getCartItem(SKU_123)).thenReturn(shoppingItemAlreadyInCart);
		when(addedShoppingItem.getQuantity()).thenReturn(1);
		when(addedShoppingItem.hasPrice()).thenReturn(true);

		cartDirector.addToCart(addedShoppingItem, shoppingCart, null);
		verify(shoppingCart).addShoppingCartItem(addedShoppingItem);

	}

	/**
	 * Tests that updateCartItem() finds the existing cart item and sets sku, price and quantity. 
	 */
	@Test
	public void testUpdateCartItem() {
		final long itemUid = 5;
		final ShoppingItemDto dto = new ShoppingItemDto(SKU_A, 1);
		final CartItem updatedShoppingItem = mock(CartItem.class, "updated");

		when(shoppingItemAlreadyInCart.getGuid()).thenReturn(SHOPPING_ITEM_GUID);
		when(shoppingItemAssembler.createShoppingItem(dto)).thenReturn(updatedShoppingItem);

		doReturn(updatedShoppingItem).when(cartDirector).addToCart(eq(updatedShoppingItem), eq(shoppingCart), any(ShoppingItem.class));
		doReturn(true).when(cartDirector).isProductPurchasableInStore(any(Store.class), any(Product.class), eq(false), eq(SKU_CODE));
		doNothing().when(cartDirector).priceShoppingItem(eq(addedShoppingItem), any(Store.class), any(Shopper.class));
		doReturn(shoppingItemAlreadyInCart).when(cartDirector).getCartItem(shoppingCart, itemUid);

		assertSame(updatedShoppingItem, cartDirector.updateCartItem(shoppingCart, itemUid, dto));

		verify(shoppingCart).removeCartItem(itemUid);
		verify(shoppingItemAssembler).createShoppingItem(dto);
		verify(updatedShoppingItem).setGuid(SHOPPING_ITEM_GUID);
	}

	/**
	 * Test that the pricing functor does its job.
	 */
	@Test
	public void testPriceShoppingItemsWithTraverser() {
		final Price unitPrice = new PriceImpl();
		unitPrice.setListPrice(Money.valueOf(BigDecimal.ONE, CURRENCY_CAD));
		final PriceLookupFacade priceLookupFacade = mock(PriceLookupFacade.class);

		when(addedShoppingItem.getQuantity()).thenReturn(QUANTITY);
		when(priceLookupFacade.getShoppingItemPrice(addedShoppingItem, null, null)).thenReturn(unitPrice);

		cartDirector.setPriceLookupFacade(priceLookupFacade);
		cartDirector.setProductSkuLookup(productSkuLookup);

		cartDirector.priceShoppingItem(addedShoppingItem, null, null);
		verify(addedShoppingItem).setPrice(QUANTITY, unitPrice);
	}

	/**
	 * Test that the pricing functor does its job and prices child items.
	 */
	@Test
	public void testPriceChildShoppingItems() {
		final ShoppingItemImpl shoppingItemChild = new ShoppingItemImpl();
		final ShoppingItemImpl shoppingItemParent = new ShoppingItemImpl() {
			@Override
			public boolean isBundle(final ProductSkuLookup productSkuLookup) {
				return true;
			}
		};
		shoppingItemParent.addChild(shoppingItemChild);

		final ProductSkuImpl productSku = new ProductSkuImpl();
		productSku.setGuid(new RandomGuidImpl().toString());
		shoppingItemChild.setSkuGuid(productSku.getGuid());
		shoppingItemChild.setQuantity(QUANTITY);
		final ProductSkuImpl parentSku = new ProductSkuImpl();
		parentSku.setGuid(new RandomGuidImpl().toString());
		shoppingItemParent.setSkuGuid(parentSku.getGuid());
		shoppingItemParent.setQuantity(QUANTITY);

		final Price unitPrice = mock(Price.class);
		final Money twoBucks = Money.valueOf("2", CURRENCY_CAD);

		when(productSkuLookup.findByGuid(parentSku.getGuid())).thenReturn(parentSku);
		when(productSkuLookup.findByGuid(productSku.getGuid())).thenReturn(productSku);
		when(priceLookupFacade.getShoppingItemPrice(shoppingItemParent, null, null)).thenReturn(unitPrice);
		when(unitPrice.getCurrency()).thenReturn(CURRENCY_CAD);
		when(unitPrice.getListPrice(QUANTITY)).thenReturn(twoBucks);
		when(unitPrice.getSalePrice(QUANTITY)).thenReturn(twoBucks);
		when(unitPrice.getComputedPrice(QUANTITY)).thenReturn(twoBucks);

		cartDirector.priceShoppingItem(shoppingItemParent, null, null);
		assertEquals(shoppingItemChild.getPrice(), unitPrice);
		assertEquals(shoppingItemParent.getPrice(), unitPrice);
	}

	/**
	 * Test that the pricing functor does its job and prices child items, and director throws Exception if a null is found.
	 */
	@Test
	public void testPriceChildShoppingItemChecksNullPrice() {
		final ShoppingItemImpl shoppingItemChild = new ShoppingItemImpl();
		final ShoppingItemImpl shoppingItemParent = new ShoppingItemImpl() {
			@Override
			public boolean isBundle(final ProductSkuLookup productSkuLookup) {
				return true;
			}
		};
		shoppingItemParent.addChild(shoppingItemChild);

		final ProductSkuImpl productSku = new ProductSkuImpl();
		productSku.setGuid(new RandomGuidImpl().toString());
		shoppingItemChild.setSkuGuid(productSku.getGuid());
		shoppingItemChild.setQuantity(QUANTITY);
		final ProductSkuImpl parentSku = new ProductSkuImpl();
		parentSku.setGuid(new RandomGuidImpl().toString());
		shoppingItemParent.setSkuGuid(parentSku.getGuid());
		shoppingItemParent.setQuantity(QUANTITY);

		final Price unitPrice = mock(Price.class);
		final Currency currency = CURRENCY_CAD;
		final Money twoBucks = Money.valueOf("2", currency);

		when(productSkuLookup.findByGuid(parentSku.getGuid())).thenReturn(parentSku);
		when(productSkuLookup.findByGuid(productSku.getGuid())).thenReturn(productSku);
		when(priceLookupFacade.getShoppingItemPrice(shoppingItemParent, null, null)).thenReturn(unitPrice);
		given(unitPrice.getCurrency()).willReturn(CURRENCY_CAD);
		given(unitPrice.getSalePrice(QUANTITY)).willReturn(twoBucks);
		given(unitPrice.getComputedPrice(QUANTITY)).willReturn(twoBucks);

		cartDirector.priceShoppingItem(shoppingItemParent, null, null);
	}


	/**
	 * Test if a sku is allowed to add to cart.
	 */
	@Test
	public void testIsSkuAllowedAddToCart() {
		final ProductSku sku = mock(ProductSku.class);
		final Product product = mock(Product.class);
		final Date currentTime = new Date();
		final Store store = mock(Store.class);
		final Catalog catalog = mock(Catalog.class);

		final TimeService timeService = mock(TimeService.class);
		cartDirector.setTimeService(timeService);

		when(productSkuLookup.findBySkuCode(SKU_CODE)).thenReturn(sku);
		when(sku.getProduct()).thenReturn(null);

		assertFalse("should return false if product cannot be found", cartDirector.isSkuAllowedAddToCart(SKU_CODE, shoppingCart));

		when(productSkuLookup.findBySkuCode(SKU_CODE)).thenReturn(sku);
		when(sku.getProduct()).thenReturn(product);
		when(product.isHidden()).thenReturn(true);

		assertFalse("should return false if product is hidden", cartDirector.isSkuAllowedAddToCart(SKU_CODE, shoppingCart));

		when(productSkuLookup.findBySkuCode(SKU_CODE)).thenReturn(sku);
		when(sku.getProduct()).thenReturn(product);
		when(product.isHidden()).thenReturn(false);
		when(timeService.getCurrentTime()).thenReturn(currentTime);
		when(product.isWithinDateRange(currentTime)).thenReturn(false);

		assertFalse("should return false if product is not in date range", cartDirector.isSkuAllowedAddToCart(SKU_CODE, shoppingCart));

		when(productSkuLookup.findBySkuCode(SKU_CODE)).thenReturn(sku);
		when(sku.getProduct()).thenReturn(product);
		when(product.isHidden()).thenReturn(false);
		when(timeService.getCurrentTime()).thenReturn(currentTime);
		when(product.isWithinDateRange(currentTime)).thenReturn(true);
		when(shoppingCart.getStore()).thenReturn(store);
		when(store.getCatalog()).thenReturn(catalog);
		when(product.isInCatalog(catalog)).thenReturn(false);

		assertFalse("should return false if the product catalog is not the store catalog",
			cartDirector.isSkuAllowedAddToCart(SKU_CODE, shoppingCart));

		when(productSkuLookup.findBySkuCode(SKU_CODE)).thenReturn(sku);
		when(sku.getProduct()).thenReturn(product);
		when(product.isHidden()).thenReturn(false);
		when(timeService.getCurrentTime()).thenReturn(currentTime);
		when(product.isWithinDateRange(currentTime)).thenReturn(true);
		when(shoppingCart.getStore()).thenReturn(store);
		when(store.getCatalog()).thenReturn(catalog);
		when(product.isInCatalog(catalog)).thenReturn(true);
		when(sku.isWithinDateRange(currentTime)).thenReturn(false);

		assertFalse("should return false if the sku is not in date range", cartDirector.isSkuAllowedAddToCart(SKU_CODE, shoppingCart));

		when(productSkuLookup.findBySkuCode(SKU_CODE)).thenReturn(sku);
		when(sku.getProduct()).thenReturn(product);
		when(product.isHidden()).thenReturn(false);
		when(timeService.getCurrentTime()).thenReturn(currentTime);
		when(product.isWithinDateRange(currentTime)).thenReturn(true);
		when(shoppingCart.getStore()).thenReturn(store);
		when(store.getCatalog()).thenReturn(catalog);
		when(product.isInCatalog(catalog)).thenReturn(true);
		when(sku.isWithinDateRange(currentTime)).thenReturn(true);

		assertTrue("should be true", cartDirector.isSkuAllowedAddToCart(SKU_CODE, shoppingCart));
	}

	/**
	 * Tests {@link CartDirectorImpl#retainShoppingItemIdentity(ShoppingItem, ShoppingItem)} for a non-configurable shopping item.
	 */
	@Test
	public void testRetainShoppingItemIdentity() {
		final ShoppingItem existingItem = mock(ShoppingItem.class, "existing");
		final ShoppingItem newItem = mock(ShoppingItem.class, "new");

		when(existingItem.getGuid()).thenReturn(SHOPPING_ITEM_GUID);

		cartDirector.retainShoppingItemIdentity(existingItem, newItem);

		verify(newItem).setGuid(SHOPPING_ITEM_GUID);
	}

	/**
	 * Tests that if a shopping cart contains a non-purchasable cart item, it gets removed from the cart.
	 */
	@Test
	public void testRemoveShoppingItemThatIsNotPurchasable() {
		final String skuCode = "code";
		final ShoppingItem item = new ShoppingItemImpl();
		final ProductSku sku = createProductSku(skuCode);
		item.setSkuGuid(sku.getGuid());
		final List<ShoppingItem> cartItems = new ArrayList<ShoppingItem>() { {
			add(item);
		} };

		ShoppingCart cart = new ShoppingCartImpl() {
			@Override
			public List<ShoppingItem> getCartItems() {
				return cartItems;
			}
			@Override
			public Store getStore() {
				return null;
			}
		};

		doReturn(false).when(cartDirector).isProductPurchasableInStore(any(Store.class), any(Product.class), eq(false), eq(skuCode));
		doNothing().when(cartDirector).refreshShoppingItems(cartItems, shoppingCart);

		cartDirector.removeAnyNonPurchasableItems(cart.getCartItems(), cart);
		assertTrue("Shopping cart items should be empty after the non purchasable item has been removed", cart.getCartItems().isEmpty());
		assertEquals("Shopping cart should contain a sku code for the removed shopping item",
				skuCode, cart.getNotPurchasableCartItemSkus().iterator().next());
	}

	/**
	 * Tests that if a shopping cart contains an item with no price tiers associated.
	 */
	@Test
	public void testRemoveShoppingItemThatHasNoPriceTiersAssociated() {
		String skuCode = "code";
		final ShoppingItem item = new ShoppingItemImpl() {
			@Override
			public Price getPrice() {
				return new PriceImpl();
			}
		};
		final ProductSku sku = createProductSku(skuCode);
		item.setSkuGuid(sku.getGuid());

		final List<ShoppingItem> cartItems = new ArrayList<>(Arrays.asList(item));

		ShoppingCart cart = new ShoppingCartImpl() {
			@Override
			public List<ShoppingItem> getCartItems() {
				return cartItems;
			}
			@Override
			public Store getStore() {
				return null;
			}
		};

		doReturn(true).when(cartDirector).isProductPurchasableInStore(any(Store.class), any(Product.class), eq(false), eq(SKU_CODE));
		doNothing().when(cartDirector).refreshShoppingItems(cartItems, shoppingCart);

		cartDirector.removeAnyNonPurchasableItems(cart.getCartItems(), cart);
		assertTrue("Shopping cart items should be empty after the item with no price tiers has been removed", cart.getCartItems().isEmpty());
		assertEquals("Shopping cart should contain a sku code for the removed shopping item",
				skuCode, cart.getNotPurchasableCartItemSkus().iterator().next());
	}

	/**
	 * Tests that if a shopping cart contains an item with no price tiers associated it doesn't get removed.
	 */
	@Test
	public void testDoNotRemoveShoppingItemThatHasPriceAssociatedAndIsPurchasable() {
		String skuCode = "code";
		final ShoppingItem item = new ShoppingItemImpl();
		final Price price = new PriceImpl();
		price.setListPrice(Money.valueOf(BigDecimal.ONE, CURRENCY_CAD));
		item.setPrice(1, price);

		final ProductSku sku = createProductSku(skuCode);
		item.setSkuGuid(sku.getGuid());

		final List<ShoppingItem> cartItems = Arrays.asList(item);

		ShoppingCart cart = new ShoppingCartImpl() {
			@Override
			public List<ShoppingItem> getCartItems() {
				return cartItems;
			}
			@Override
			public Store getStore() {
				return null;
			}
		};

		doReturn(true).when(cartDirector).isProductPurchasableInStore(any(Store.class), any(Product.class), eq(false), eq(SKU_CODE));
		doNothing().when(cartDirector).refreshShoppingItems(cartItems, shoppingCart);

		cartDirector.removeAnyNonPurchasableItems(cart.getCartItems(), cart);
		assertFalse("Shopping cart items should not be empty after the item with no price tiers has not been removed",
				cart.getCartItems().isEmpty());
		assertTrue("Shopping cart should not contain a sku code for the shopping item that has not been removes",
				cart.getNotPurchasableCartItemSkus().isEmpty());
	}

	@Test
	public void verifyClearItemsClearsItems() throws Exception {
		cartDirector.clearItems(shoppingCart);
		verify(shoppingCart).clearItems();
	}

	@Test
	public void testAddToCartWithDifferentCartItemDataAddsLineItem() {
		givenItemHasFieldWithValue(addedShoppingItem, "Key1", "Key1MatchingValue");
		givenItemHasFieldWithValue(shoppingItemAlreadyInCart, "Key1", "Key1MatchingValue");
		givenItemHasFieldWithValue(addedShoppingItem, "Key2", "Key2MismatchedValue1");
		givenItemHasFieldWithValue(shoppingItemAlreadyInCart, "Key2", "Key2MismatchedValue2");

		cartDirector.addToCart(addedShoppingItem, shoppingCart, null);

		verify(shoppingCart).addShoppingCartItem(addedShoppingItem);
		verifyQuantityNotChanged();
	}

	@Test
	public void testAddToCartWithSameCartItemDataIncreasesQuantity() {
		givenItemHasFieldWithValue(addedShoppingItem, "Key3", "Key3MatchingValue");
		givenItemHasFieldWithValue(shoppingItemAlreadyInCart, "Key3", "Key3MatchingValue");
		givenItemHasFieldWithValue(addedShoppingItem, "Key4", "Key4MatchingValue");
		givenItemHasFieldWithValue(shoppingItemAlreadyInCart, "Key4", "Key4MatchingValue");

		given(addedShoppingItem.isSameMultiSkuItem(productSkuLookup, shoppingItemAlreadyInCart)).willReturn(true);

		cartDirector.addToCart(addedShoppingItem, shoppingCart, null);

		verify(shoppingCart, never()).addShoppingCartItem(addedShoppingItem);
		verifyQuantityChanged(2);
	}

	@Test
	public void testAddToCartWithNoCartItemDataIncreasesQuantity() {
		given(addedShoppingItem.isSameMultiSkuItem(productSkuLookup, shoppingItemAlreadyInCart)).willReturn(true);

		cartDirector.addToCart(addedShoppingItem, shoppingCart, null);

		verify(shoppingCart, never()).addShoppingCartItem(addedShoppingItem);
		verifyQuantityChanged(2);
	}

	@Test
	public void testAddToCartWithDifferentSkuCodeAndSameCartItemDataAddsLineItem() {
		givenItemHasFieldWithValue(addedShoppingItem, "Key5", "Key5MatchingValue");
		givenItemHasFieldWithValue(shoppingItemAlreadyInCart, "Key5", "Key5MatchingValue");
		givenItemHasFieldWithValue(addedShoppingItem, "Key6", "Key6MatchingValue");
		givenItemHasFieldWithValue(shoppingItemAlreadyInCart, "Key6", "Key6MatchingValue");
		given(addedShoppingItem.getSkuGuid()).willReturn(SKU_GUID_2);
		given(productSkuLookup.findByGuid(SKU_GUID_2)).willReturn(productSku2);
		given(productSku.getSkuCode()).willReturn(SKU_CODE_2);

		cartDirector.addToCart(addedShoppingItem, shoppingCart, null);

		verify(shoppingCart).addShoppingCartItem(addedShoppingItem);
		verifyQuantityNotChanged();
	}

	@Test
	public void testUpdateCartItemRetainsCartItemData() {
		ShoppingItemDto shoppingItemDto = new ShoppingItemDto(SKU_CODE, 1);
		ShoppingItem newShoppingItem = mock(ShoppingItem.class);

		given(newShoppingItem.getSkuGuid()).willReturn(SKU_GUID);
		given(shoppingItemAssembler.createShoppingItem(shoppingItemDto)).willReturn(newShoppingItem);
		given(shoppingItemAlreadyInCart.getUidPk()).willReturn(CART_ITEM_UID);

		cartDirector.updateCartItem(shoppingCart, CART_ITEM_UID, shoppingItemDto);

		assertEquals("Fields should have been copied over.", shoppingItemAlreadyInCart.getFields(), shoppingItemDto.getItemFields());
	}

	@Test
	public void testUpdateCartItemRetainsGuidAndOrdering() {
		ShoppingItemDto shoppingItemDto = new ShoppingItemDto(SKU_CODE, 1);
		givenItemHasFieldWithValue(addedShoppingItem, "newKey", "newValue");

		given(addedShoppingItem.getSkuGuid()).willReturn(SKU_GUID);
		given(shoppingItemAssembler.createShoppingItem(shoppingItemDto)).willReturn(addedShoppingItem);
		given(shoppingItemAlreadyInCart.getUidPk()).willReturn(CART_ITEM_UID);

		String shoppingItemGuid = "testShoppingItemGuid";
		given(shoppingItemAlreadyInCart.getGuid()).willReturn(shoppingItemGuid);
		given(shoppingItemAlreadyInCart.getOrdering()).willReturn(SHOPPING_ITEM_ORDERING);

		cartDirector.updateCartItem(shoppingCart, CART_ITEM_UID, shoppingItemDto);

		verify(addedShoppingItem).setGuid(shoppingItemGuid);
		verify(addedShoppingItem).setOrdering(SHOPPING_ITEM_ORDERING);
	}

	private void givenItemHasFieldWithValue(final ShoppingItem mockShoppingItem, final String key, final String value) {
		Map<String, String> fieldsMap = mockShoppingItem.getFields();

		if (fieldsMap == null || fieldsMap.isEmpty()) {
			fieldsMap = new HashMap<>();
			given(mockShoppingItem.getFields()).willReturn(fieldsMap);
		}

		fieldsMap.put(key, value);

		given(mockShoppingItem.getFieldValue(key)).willReturn(value);
	}

	private void verifyQuantityChanged(final Integer targetQuantity) {
		assertEquals("Quantity should have been changed to " + targetQuantity + ".", targetQuantity, changeQuantityArgument);
	}

	private void verifyQuantityNotChanged() {
		assertNull("Cart item quantity change should not have been called.", changeQuantityArgument);
	}


	private ProductSku createProductSku(final String skuCode) {
		final Product product = new ProductImpl();
		product.initialize();

		return createProductSku(skuCode, product);
	}

	private ProductSku createProductSku(final String skuCode, final Product product) {
		final ProductSku sku = new ProductSkuImpl();
		sku.initialize();
		sku.setSkuCode(skuCode);
		sku.setProduct(product);

		given(productSkuLookup.findByGuid(sku.getGuid())).willReturn(sku);

		return sku;
	}
}
