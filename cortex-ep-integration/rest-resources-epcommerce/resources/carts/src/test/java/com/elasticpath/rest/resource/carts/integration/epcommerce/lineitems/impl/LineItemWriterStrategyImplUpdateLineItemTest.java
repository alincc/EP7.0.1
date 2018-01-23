/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.catalog.Availability;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.LineItemConfigurationEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.transform.LineItemTransformer;
import com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.transform.LineItemTransformerImpl;
import com.elasticpath.rest.resource.carts.lineitems.integration.LineItemLookupStrategy;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;

/**
 * Tests the {@link LineItemWriterStrategyImpl} updateLineItem.
 */
public class LineItemWriterStrategyImplUpdateLineItemTest {
	private static final String STORECODE = "STORECODE";
	private static final String ITEM_ID = "item id";
	private static final String LINE_ITEM_GUID = "item guid";
	private static final int QUANTITY = 2;
	private static final String CART_GUID = "cart guid";
	private static final String SKUCODE = "sku code";
	private static final String SKU_GUID = "sku guid";
	private static final String RECIPIENT_NAME_KEY = "recipientName";
	private static final String RECIPIENT_NAME_VALUE = "Luke Skywalker";
	private static final String SENDER_NAME_KEY = "senderName";
	private static final String SENDER_NAME_VALUE = "Darth Vader";
	private static final long SHOPPING_ITEM_UID = 4;

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final ShoppingCart cart = context.mock(ShoppingCart.class);
	private final StoreProduct mockStoreProduct = context.mock(StoreProduct.class);
	private final Product product = context.mock(Product.class);
	private final ProductSku mockSku = context.mock(ProductSku.class, "resultSku");
	private final ShoppingCartRepository mockShoppingCartRepository = context.mock(ShoppingCartRepository.class);
	private final ItemRepository mockItemRepository = context.mock(ItemRepository.class);
	private final ShoppingItem resultShoppingItem = context.mock(ShoppingItem.class, "result");
	private final StoreProductRepository mockStoreProductRepository = context.mock(StoreProductRepository.class);
	private final ShoppingItem mockExistingShoppingItem = context.mock(ShoppingItem.class, "existingItem");
	private final ProductSkuRepository mockProductSkuRepository = context.mock(ProductSkuRepository.class);
	private final LineItemTransformer lineItemTransformer = new LineItemTransformerImpl();
	private final LineItemLookupStrategy mockLineItemLookupStrategy = context.mock(LineItemLookupStrategy.class);

	private final LineItemWriterStrategyImpl strategy = new LineItemWriterStrategyImpl(
			mockShoppingCartRepository,
			mockItemRepository, mockStoreProductRepository,
			mockProductSkuRepository, lineItemTransformer, mockLineItemLookupStrategy);

	/**
	 * Tests Happy Path.
	 */
	@Test
	public void testUpdateLineItemNoConfiguration() {
		mockUpdateCartItem();
		mockShoppingCartExpectations(CART_GUID);
		mockItemRepositoryGetSkuForItemId(ExecutionResultFactory.createReadOK(mockSku));
		mockItemRepositoryGetItemIdForSku(ExecutionResultFactory.createReadOK(ITEM_ID));
		mockStoreProductRepositoryExpectations(Availability.AVAILABLE);
		mockResultItem();
		ensureLineItemLookupReturnsLineItem();
		createExpectationsForPurchasableItem();

		context.checking(new Expectations() {
			{
				allowing(mockExistingShoppingItem).mergeFieldValues(Collections.emptyMap());
			}
		});
		LineItemEntity entity = createLineItemEntity();

		assertNull(entity.getConfiguration());

		ExecutionResult<Void> executionResult = strategy.updateLineItem(STORECODE, entity);

		assertEquals(ResourceStatus.UPDATE_OK, executionResult.getResourceStatus());
	}

	@Test
	public void testUpdateLineItemWithConfiguration() {
		mockUpdateCartItem();
		mockShoppingCartExpectations(CART_GUID);
		mockItemRepositoryGetSkuForItemId(ExecutionResultFactory.createReadOK(mockSku));
		mockItemRepositoryGetItemIdForSku(ExecutionResultFactory.createReadOK(ITEM_ID));
		mockStoreProductRepositoryExpectations(Availability.AVAILABLE);
		mockResultItem();
		ensureLineItemLookupReturnsLineItem();
		createExpectationsForPurchasableItem();
		Map<String, String> fieldsMap = new HashMap<>();
		fieldsMap.put(RECIPIENT_NAME_KEY, RECIPIENT_NAME_VALUE);
		fieldsMap.put(SENDER_NAME_KEY, SENDER_NAME_VALUE);

		context.checking(new Expectations() {
			{
				oneOf(mockExistingShoppingItem).mergeFieldValues(fieldsMap);
			}
		});
		LineItemEntity entity = createLineItemEntityWithConfiguration();

		assertEquals(entity.getConfiguration().getDynamicProperties().size(), 2);

		ExecutionResult<Void> executionResult = strategy.updateLineItem(STORECODE, entity);

		assertEquals(ResourceStatus.UPDATE_OK, executionResult.getResourceStatus());
	}

	@Test
	public void ensureForbiddenReturnedWhenLineItemIsNotPurchasable() {
		mockItemRepositoryGetSkuForItemId(ExecutionResultFactory.createReadOK(mockSku));
		mockShoppingCartExpectations(CART_GUID);
		mockStoreProductRepositoryExpectations(Availability.AVAILABLE);
		ensureLineItemLookupReturnsLineItem();
		createExpectationsForNonPurchasableItem();

		LineItemEntity entity = createLineItemEntity();
		thrown.expect(containsResourceStatus(ResourceStatus.FORBIDDEN));

		strategy.updateLineItem(STORECODE, entity);
	}

	/**
	 * Tests updating line item when Cart ID is Null and gets Default Shopping Cart.
	 */
	@Test
	public void testUpdateLineItemWhenCartIdNull() {
		mockUpdateCartItem();
		mockShoppingCartExpectations(null);
		mockItemRepositoryGetSkuForItemId(ExecutionResultFactory.createReadOK(mockSku));
		mockItemRepositoryGetItemIdForSku(ExecutionResultFactory.createReadOK(ITEM_ID));
		mockStoreProductRepositoryExpectations(Availability.AVAILABLE);
		mockResultItem();
		ensureLineItemLookupReturnsLineItem();
		createExpectationsForPurchasableItem();
		
		context.checking(new Expectations() {
			{
				allowing(mockExistingShoppingItem).mergeFieldValues(Collections.emptyMap());
			}
		});
		LineItemEntity entity = LineItemEntity.builder().withItemId(ITEM_ID)
				.withLineItemId(LINE_ITEM_GUID)
				.withCartId(null)
				.withQuantity(QUANTITY)
				.build();

		ExecutionResult<Void> executionResult = strategy.updateLineItem(STORECODE, entity);

		assertEquals(ResourceStatus.UPDATE_OK, executionResult.getResourceStatus());
	}

	/**
	 * Tests updating line with unavailable item.
	 */
	@Test
	public void testUpdateLineItemWithUnavailableItem() {
		mockUpdateCartItem();
		mockShoppingCartExpectations(CART_GUID);
		mockItemRepositoryGetSkuForItemId(ExecutionResultFactory.createReadOK(mockSku));
		mockItemRepositoryGetItemIdForSku(ExecutionResultFactory.createReadOK(ITEM_ID));
		ensureLineItemLookupReturnsLineItem();
		mockResultItem();
		mockStoreProductRepositoryExpectations(Availability.NOT_AVAILABLE);

		LineItemEntity entity = createLineItemEntity();
		thrown.expect(containsResourceStatus(ResourceStatus.STATE_FAILURE));

		strategy.updateLineItem(STORECODE, entity);
	}

	/**
	 * Tests updating line item when Cart Not Found.
	 */
	@Test
	public void testUpdateLineItemWhenCartResultNotFound() {
		context.checking(new Expectations() {
			{
				allowing(mockShoppingCartRepository).getShoppingCart(CART_GUID);
				will(returnValue(ExecutionResultFactory.createNotFound("not found")));
			}
		});

		LineItemEntity entity = createLineItemEntity();
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.updateLineItem(STORECODE, entity);
	}

	/**
	 * Tests updating line item when Item Sku Not Found.
	 */
	@Test
	public void testUpdateLineItemWhenItemSkuCodeNotFound() {
		mockShoppingCartExpectations(CART_GUID);
		ensureLineItemLookupReturnsLineItem();

		context.checking(new Expectations() {
			{
				allowing(mockItemRepository).getSkuForItemId(ITEM_ID);
				will(returnValue(ExecutionResultFactory.createNotFound("not found")));
			}
		});
		LineItemEntity entity = createLineItemEntity();
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.updateLineItem(STORECODE, entity);
	}

	/**
	 * Tests Assemble Line Item Dto When Item Id Not Found.
	 */
	@Test
	public void testAssembleLineItemEntityWhenItemIdNotFound() {
		mockShoppingCartExpectations(CART_GUID);
		mockUpdateCartItem();
		mockResultItem();
		mockStoreProductRepositoryExpectations(Availability.AVAILABLE);
		mockItemRepositoryGetSkuForItemId(ExecutionResultFactory.createNotFound());
		ensureLineItemLookupReturnsLineItem();

		LineItemEntity entity = createLineItemEntity();
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.updateLineItem(STORECODE, entity);
	}

	/**
	 * Tests Update Line Item When Product Not Purchasable Exception Thrown.
	 */
	@Test
	public void testUpdateLineItemWhenProductNotPurchasableExceptionThrown() {
		mockShoppingCartExpectations(CART_GUID);
		mockItemRepositoryGetSkuForItemId(ExecutionResultFactory.createReadOK(mockSku));
		mockStoreProductRepositoryExpectations(Availability.AVAILABLE);
		ensureLineItemLookupReturnsLineItem();
		createExpectationsForPurchasableItem();

		context.checking(new Expectations() {
			{
				allowing(mockExistingShoppingItem).mergeFieldValues(Collections.emptyMap());
				allowing(mockShoppingCartRepository).updateCartItem(cart, SHOPPING_ITEM_UID, SKUCODE, QUANTITY);
				will(returnValue(ExecutionResultFactory.createStateFailureWithMessages(
						"errorMessage",
						emptyList()
				)));
			}
		});
		LineItemEntity entity = createLineItemEntity();
		thrown.expect(containsResourceStatus(ResourceStatus.STATE_FAILURE));

		strategy.updateLineItem(STORECODE, entity);
	}

	@Test
	public void ensureUpdateLineItemReturnsNotFoundWhenLineItemIsNotFound() {
		mockShoppingCartExpectations(CART_GUID);
		ensureLineItemLookUpReturnsNotfound();

		LineItemEntity entity = createLineItemEntity();
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.updateLineItem(STORECODE, entity);
	}

	private void mockResultItem() {
		context.checking(new Expectations() {
			{
				allowing(resultShoppingItem).getSkuGuid();
				will(returnValue(SKU_GUID));
				allowing(mockProductSkuRepository).getProductSkuWithAttributesByGuid(SKU_GUID);
				will(returnValue(ExecutionResultFactory.createReadOK(mockSku)));

				allowing(resultShoppingItem).getGuid();
				will(returnValue(LINE_ITEM_GUID));

				allowing(resultShoppingItem).getQuantity();
				will(returnValue(QUANTITY));

				allowing(mockSku).getSkuCode();
				will(returnValue(SKUCODE));

				allowing(mockSku).getProduct();
				will(returnValue(product));

				allowing(product).getGuid();
				will(returnValue("guid"));
			}
		});
	}

	private ShoppingCartRepository mockShoppingCartExpectations(final String cartGuid) {
		if (cartGuid == null) {
			context.checking(new Expectations() {
				{
					allowing(mockShoppingCartRepository).getDefaultShoppingCart();
					will(returnValue(ExecutionResultFactory.createReadOK(cart)));
				}
			});
		} else {
			context.checking(new Expectations() {
				{
					allowing(mockShoppingCartRepository).getShoppingCart(CART_GUID);
					will(returnValue(ExecutionResultFactory.createReadOK(cart)));
				}
			});
		}
		context.checking(new Expectations() {
			{
				allowing(cart).getGuid();
				will(returnValue(CART_GUID));

				Store store = context.mock(Store.class);
				allowing(cart).getStore();
				will(returnValue(store));
				allowing(store).getCode();
				will(returnValue(STORECODE));

				allowing(cart).getCartItemByGuid(LINE_ITEM_GUID);
				will(returnValue(mockExistingShoppingItem));

				allowing(mockExistingShoppingItem).getUidPk();
				will(returnValue(SHOPPING_ITEM_UID));

				allowing(cart).getShopper();
			}
		});
		return mockShoppingCartRepository;
	}

	private <T> void mockItemRepositoryGetSkuForItemId(final ExecutionResult<T> executionResult) {
		context.checking(new Expectations() {
			{
				allowing(mockItemRepository).getSkuForItemId(ITEM_ID);
				will(returnValue(executionResult));
			}
		});
	}

	private <T> void mockItemRepositoryGetItemIdForSku(final ExecutionResult<T> executionResult) {
		context.checking(new Expectations() {
			{
				allowing(mockShoppingCartRepository).updateCartItem(cart, SHOPPING_ITEM_UID, SKUCODE, QUANTITY);
				will(returnValue(executionResult));
			}
		});
	}

	private void mockUpdateCartItem() {
		context.checking(new Expectations() {
			{
				allowing(mockShoppingCartRepository).updateCartItem(cart, SHOPPING_ITEM_UID, SKUCODE, QUANTITY);
				will(returnValue(ExecutionResultFactory.createCreateOKWithData(
						new Pair<>(resultShoppingItem, cart), true)));
			}
		});
	}

	private LineItemEntity createLineItemEntity() {
		return LineItemEntity.builder().withItemId(ITEM_ID)
				.withLineItemId(LINE_ITEM_GUID)
				.withCartId(CART_GUID)
				.withQuantity(QUANTITY)
				.build();
	}

	private LineItemEntity createLineItemEntityWithConfiguration() {
		return LineItemEntity.builder().withItemId(ITEM_ID)
				.withLineItemId(LINE_ITEM_GUID)
				.withCartId(CART_GUID)
				.withQuantity(QUANTITY)
				.withConfiguration(createLineItemConfiguration())
				.build();
	}

	private LineItemConfigurationEntity createLineItemConfiguration() {
		return LineItemConfigurationEntity.builder()
				.addingProperty(RECIPIENT_NAME_KEY, RECIPIENT_NAME_VALUE)
				.addingProperty(SENDER_NAME_KEY, SENDER_NAME_VALUE)
				.build();
	}

	private void mockStoreProductRepositoryExpectations(final Availability availability) {
		context.checking(new Expectations() {
			{
				allowing(product).getGuid();
				will(returnValue("guid"));

				allowing(mockStoreProductRepository).findDisplayableStoreProductWithAttributesByProductGuid(STORECODE, "guid");
				will(returnValue(ExecutionResultFactory.createReadOK(mockStoreProduct)));

				allowing(mockStoreProduct).getSkuByCode(SKUCODE);
				will(returnValue(mockSku));

				allowing(mockSku).getProduct();
				will(returnValue(product));
				allowing(mockSku).getSkuCode();
				will(returnValue(SKUCODE));

				allowing(mockStoreProduct).getSkuAvailability(SKUCODE);
				will(returnValue(availability));
			}
		});
	}

	private void ensureLineItemLookupReturnsLineItem() {
		context.checking(new Expectations() {
			{
				allowing(mockLineItemLookupStrategy).getLineItem(STORECODE, CART_GUID, LINE_ITEM_GUID);
				LineItemEntity lineItem = LineItemEntity.builder()
						.withItemId(ITEM_ID)
						.build();
				will(returnValue(ExecutionResultFactory.createReadOK(lineItem)));
			}
		});
	}

	private void ensureLineItemLookUpReturnsNotfound() {
		context.checking(new Expectations() {
			{
				allowing(mockLineItemLookupStrategy).getLineItem(STORECODE, CART_GUID, LINE_ITEM_GUID);
				will(returnValue(ExecutionResultFactory.createNotFound()));
			}
		});
	}

	private void createExpectationsForPurchasableItem() {
		context.checking(new Expectations() {
			{
				allowing(mockLineItemLookupStrategy).isItemPurchasable(STORECODE, ITEM_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(true)));
			}
		});
	}

	private void createExpectationsForNonPurchasableItem() {
		context.checking(new Expectations() {
			{
				allowing(mockLineItemLookupStrategy).isItemPurchasable(STORECODE, ITEM_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(false)));
			}
		});
	}
}
