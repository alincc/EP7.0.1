/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.resource.purchases.integration.epcommerce.transform.OrderSkuTransformer;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Test for {@link PurchaseLineItemLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PurchaseLineItemLookupStrategyImplTest {

	private static final String PARENT_ORDER_SKU_GUID = "parent order Sku Guid";
	private static final String THE_RESULT_IS_SUCCESSFUL = "The result is successful";
	private static final String BUNDLE_CHILD_GUID = "BUNDLE_CHILD_GUID";
	private static final String ORDER_GUID = "1234";
	private static final String STORECODE = "storeCode";
	private static final String USERID = "userid";
	private static final String LINE_ITEM_GUID = "14";
	private static final String PARENT_LINEITEM_GUID = "parent line item guid";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ResourceOperationContext resourceOperationContext;
	@Mock
	private OrderRepository orderRepository;
	@Mock
	private OrderSkuTransformer orderSkuTransformer;
	@Mock
	private PurchaseLineItemEntity purchaseLineItemEntity;
	@Mock
	private ProductSkuRepository productSkuRepository;

	@InjectMocks
	private PurchaseLineItemLookupStrategyImpl purchaseLineItemLookupStrategy;

	/**
	 * Tests getting the line item.
	 */
	@Test
	public void testGettingLineItem() {
		OrderSku orderSku = createMockOrderSku(LINE_ITEM_GUID, false, null, Collections.<ShoppingItem>emptyList());
		Order order = createMockOrder(Collections.singleton(orderSku));

		shouldFindSubject();
		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(order));
		shouldTransformToEntity(orderSku, purchaseLineItemEntity);

		ExecutionResult<PurchaseLineItemEntity> lineItem = purchaseLineItemLookupStrategy.getLineItem(STORECODE, ORDER_GUID, LINE_ITEM_GUID, null);

		assertTrue(THE_RESULT_IS_SUCCESSFUL, lineItem.isSuccessful());
	}

	/**
	 * Tests getting the line item component.
	 */
	@Test
	public void testGettingLineItemComponent() {
		OrderSku parentOrderSku = createMockOrderSku(PARENT_LINEITEM_GUID, false, null, null);
		OrderSku orderSku = createMockOrderSku(LINE_ITEM_GUID, false, parentOrderSku, Collections.<ShoppingItem>emptyList());
		Order order = createMockOrder(Collections.singleton(orderSku));

		shouldFindSubject();
		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(order));
		shouldTransformToEntity(orderSku, purchaseLineItemEntity);

		ExecutionResult<PurchaseLineItemEntity> lineItem = purchaseLineItemLookupStrategy.getLineItem(STORECODE, ORDER_GUID,
				LINE_ITEM_GUID,
				PARENT_LINEITEM_GUID);

		assertTrue(THE_RESULT_IS_SUCCESSFUL, lineItem.isSuccessful());
	}

	/**
	 * Tests getting the line item component.
	 */
	@Test
	public void testGettingLineItemComponentWithWrongParent() {
		OrderSku parentOrderSku = createMockOrderSku(LINE_ITEM_GUID, false, null, Collections.<ShoppingItem>emptyList());
		OrderSku orderSku = createMockOrderSku(LINE_ITEM_GUID, false, parentOrderSku, Collections.<ShoppingItem>emptyList());
		Order order = createMockOrder(Collections.singleton(orderSku));

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(order));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseLineItemLookupStrategy.getLineItem(STORECODE, ORDER_GUID,
				LINE_ITEM_GUID,
				PARENT_LINEITEM_GUID);
	}

	/**
	 * Tests that a failure in retrieving the order will return an error result.
	 */
	@Test
	public void testOrderFailureReturnsError() {
		shouldFindByGuidWithResult(ExecutionResultFactory.<Order>createNotFound("not found"));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseLineItemLookupStrategy.getLineItem(STORECODE, ORDER_GUID, LINE_ITEM_GUID, null);
	}

	/**
	 * Tests that an error is returned if no line item found for order.
	 */
	@Test
	public void testNullLineItemReturnsError() {
		Order order = createMockOrder(Collections.emptyList());

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(order));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseLineItemLookupStrategy.getLineItem(STORECODE, ORDER_GUID, LINE_ITEM_GUID, null);
	}

	/**
	 * Tests that the purchaseLineItemLookupStrategy returns true for a bundle.
	 */
	@Test
	public void testIsLineItemBundleTrue() {
		OrderSku orderSku = createMockOrderSku(LINE_ITEM_GUID, true, null, Collections.<ShoppingItem>emptyList());
		Order order = createMockOrder(Collections.singleton(orderSku));

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(order));

		ExecutionResult<Boolean> lineItemResult = purchaseLineItemLookupStrategy.isLineItemBundle(STORECODE, ORDER_GUID, LINE_ITEM_GUID);

		assertTrue(THE_RESULT_IS_SUCCESSFUL, lineItemResult.isSuccessful());
		assertTrue("The result should be true as expected", lineItemResult.getData());
	}

	/**
	 * Tests Find Line Item IDs When Core Order Not Found.
	 */
	@Test
	public void testFindLineItemIdsWhenCoreOrderNotFound() {
		shouldFindByGuidWithResult(ExecutionResultFactory.<Order>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseLineItemLookupStrategy.findLineItemIds(STORECODE, ORDER_GUID);
	}

	/**
	 * Tests Find Line Item IDs When Core Order Successful.
	 */
	@Test
	public void testFindLineItemIdsWhenCoreOrderSuccessful() {
		ShoppingItem shoppingItem = createMockShoppingItem(LINE_ITEM_GUID);
		Order order = createMockOrder(Collections.singletonList(shoppingItem));

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(order));

		ExecutionResult<Collection<String>> lineItemResult = purchaseLineItemLookupStrategy.findLineItemIds(STORECODE, ORDER_GUID);

		assertTrue(THE_RESULT_IS_SUCCESSFUL, lineItemResult.isSuccessful());
	}

	/**
	 * Tests that the purchaseLineItemLookupStrategy returns false for a non-bundle.
	 */
	@Test
	public void testIsLineItemBundleFalse() {
		OrderSku orderSku = createMockOrderSku(LINE_ITEM_GUID, false, null, Collections.<ShoppingItem>emptyList());
		Order order = createMockOrder(Collections.singleton(orderSku));

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(order));

		ExecutionResult<Boolean> lineItemResult = purchaseLineItemLookupStrategy.isLineItemBundle(STORECODE, ORDER_GUID, LINE_ITEM_GUID);

		assertTrue(THE_RESULT_IS_SUCCESSFUL, lineItemResult.isSuccessful());
		assertFalse("The result should be false as expected", lineItemResult.getData());
	}

	/**
	 * Tests that the purchaseLineItemLookupStrategy returns a failure result if the order repository fails.
	 */
	@Test
	public void testIsLineItemBundleError() {
		shouldFindByGuidWithResult(ExecutionResultFactory.<Order>createNotFound("Order not found"));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseLineItemLookupStrategy.isLineItemBundle(STORECODE, ORDER_GUID, LINE_ITEM_GUID);
	}

	/**
	 * Test get component guids for line item.
	 */
	@Test
	public void testGetComponentIdsForLineItem() {
		ShoppingItem bundleChild = createMockShoppingItem(BUNDLE_CHILD_GUID);
		OrderSku orderSku = createMockOrderSku(LINE_ITEM_GUID, false, null, Arrays.asList(bundleChild));
		Order order = createMockOrder(Collections.singleton(orderSku));

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(order));

		ExecutionResult<Collection<String>> componentGuidsForLineItemResult =
				purchaseLineItemLookupStrategy.getComponentIdsForLineItemId(STORECODE, ORDER_GUID, LINE_ITEM_GUID);

		assertTrue(THE_RESULT_IS_SUCCESSFUL, componentGuidsForLineItemResult.isSuccessful());
		Collection<String> componentGuids = componentGuidsForLineItemResult.getData();
		assertTrue("The result is an collection of guid's as expected", CollectionUtil.isNotEmpty(componentGuids));
		assertEquals("The first item in the list equals bundle_child_guid", BUNDLE_CHILD_GUID, CollectionUtil.first(componentGuids));
	}

	/**
	 * Test get component guids for line item with children.
	 */
	@Test
	public void testGetComponentIdsForLineItemWithChildren() {
		String expectedGuids = "EXPECTED_GUIDS";
		OrderSku bundleChild = createMockOrderSku(expectedGuids,
				false,
				null,
				Collections.EMPTY_LIST);
		OrderSku childOrderSku = createMockOrderSku(BUNDLE_CHILD_GUID,
				false,
				null,
				Arrays.asList(bundleChild));
		OrderSku orderSku = createMockOrderSku(LINE_ITEM_GUID, true, null, Collections.singletonList(childOrderSku));
		Order order = createMockOrder(Collections.singleton(orderSku));

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(order));

		ExecutionResult<Collection<String>> componentGuidsForLineItemResult =
				purchaseLineItemLookupStrategy.getComponentIdsForLineItemId(STORECODE, ORDER_GUID, BUNDLE_CHILD_GUID);

		assertTrue(THE_RESULT_IS_SUCCESSFUL, componentGuidsForLineItemResult.isSuccessful());
		Collection<String> componentGuids = componentGuidsForLineItemResult.getData();
		assertTrue("Empty collection of component guids", CollectionUtil.isNotEmpty(componentGuids));
		assertEquals("The first item in the list doesn't equal bundle_child_guid", expectedGuids, CollectionUtil.first(componentGuids));
	}

	/**
	 * Test get component guids for line item when lookup into core fails.
	 */
	@Test
	public void testGetComponentIdsForLineItemWhenCoreFails() {
		shouldFindByGuidWithResult(ExecutionResultFactory.<Order>createNotFound("not found"));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseLineItemLookupStrategy.getComponentIdsForLineItemId(STORECODE, ORDER_GUID, LINE_ITEM_GUID);
	}

	/**
	 * Test get line item when isComponent is False.
	 */
	@Test
	public void testGetLineItemWhenIsComponentNotFound() {
		OrderSku orderSku = createMockOrderSku(LINE_ITEM_GUID, false, null, Collections.<ShoppingItem>emptyList());
		Order order = createMockOrder(Collections.singleton(orderSku));

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(order));
		shouldTransformToEntity(orderSku, purchaseLineItemEntity);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseLineItemLookupStrategy.getLineItem(STORECODE, ORDER_GUID,
				LINE_ITEM_GUID,
				PARENT_ORDER_SKU_GUID);
	}

	private void shouldTransformToEntity(final OrderSku orderSku, final PurchaseLineItemEntity purchaseLineItemEntity) {
		when(orderSkuTransformer.transformToEntity(orderSku, Locale.CANADA)).thenReturn(purchaseLineItemEntity);
	}

	private void shouldFindSubject() {
		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale(STORECODE, USERID, Locale.CANADA);
		when(resourceOperationContext.getSubject()).thenReturn(subject);
	}

	private void shouldFindByGuidWithResult(final ExecutionResult<Order> result) {
		when(orderRepository.findByGuid(STORECODE, ORDER_GUID)).thenReturn(result);
	}

	// TODO: suppression used to handle both OrderSkus and ShoppingItems
	@SuppressWarnings("unchecked")
	private OrderSku createMockOrderSku(final String lineItemGuid,
			final boolean isBundle,
			final OrderSku parent,
			final List bundleItems) {

		OrderSku orderSku = mock(OrderSku.class);
		when(orderSku.getGuid()).thenReturn(lineItemGuid);
		when(productSkuRepository.isProductBundle(orderSku.getSkuGuid())).thenReturn(ExecutionResultFactory.createReadOK(isBundle));
		when(orderSku.getParent()).thenReturn(parent);
		when(orderSku.getChildren()).thenReturn(bundleItems);
		return orderSku;
	}

	// TODO: suppression used to handle both OrderSkus and ShoppingItems
	@SuppressWarnings("unchecked")
	private Order createMockOrder(final Collection shoppingItems) {
		Order order = mock(Order.class);
		when(order.getRootShoppingItems()).thenReturn(shoppingItems);
		return order;
	}

	private ShoppingItem createMockShoppingItem(final String guid) {
		ShoppingItem shoppingItem = mock(ShoppingItem.class);
		when(shoppingItem.getGuid()).thenReturn(guid);
		return shoppingItem;
	}
}
