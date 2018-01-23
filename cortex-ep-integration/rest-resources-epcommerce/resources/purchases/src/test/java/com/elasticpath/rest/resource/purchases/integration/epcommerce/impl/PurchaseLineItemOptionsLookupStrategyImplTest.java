/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionEntity;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionValueEntity;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.resource.purchases.integration.epcommerce.transform.SkuOptionTransformer;
import com.elasticpath.rest.resource.purchases.integration.epcommerce.transform.SkuOptionValueTransformer;

/**
 * Test that {@link PurchaseLineItemOptionsLookupStrategyImpl} behaves as expected.
 */
@RunWith(MockitoJUnitRunner.class)
public class PurchaseLineItemOptionsLookupStrategyImplTest {

	private static final String STORE_CODE = "STORE_CODE";
	private static final String ORDER_GUID = "ORDER_GUID";
	private static final String LINE_ITEM_GUID = "LINE_ITEM_GUID";
	private static final String OPTION_GUID = "OPTION_GUID";
	private static final String OPTION_VALUE_KEY = "OPTION_VALUE_KEY";
	private static final String USERID = "userid";
	private static final Locale LOCALE = Locale.ENGLISH;
	private static final String INVALID_OPTION_VALUE_KEY = "INVALID_OPTION_VALUE_KEY";
	private static final String SKU_GUID = "SKU_GUID";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ResourceOperationContext resourceOperationContext;
	@Mock
	private OrderRepository orderRepository;
	@Mock
	private SkuOptionTransformer skuOptionTransformer;
	@Mock
	private SkuOptionValueTransformer skuOptionValueTransformer;
	@Mock
	private ProductSkuRepository productSkuRepository;

	@InjectMocks
	private PurchaseLineItemOptionsLookupStrategyImpl purchaseLineItemsOptionsLookupStrategy;

	/**
	 * Test the happy case of findOptionIds().
	 */
	@Test
	public void testFindOptionIds() {
		SkuOptionValue skuOptionValue = createMockSkuOptionValue(OPTION_VALUE_KEY);
		ProductSku productSku = createMockProductSku(Collections.singleton(OPTION_GUID), Collections.singletonMap(OPTION_GUID, skuOptionValue));
		ShoppingItem shoppingItem = createMockShoppingItem(productSku);
		Order order = createMockOrder(shoppingItem);

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(order));

		ExecutionResult<Collection<String>> result = purchaseLineItemsOptionsLookupStrategy.findOptionIds(STORE_CODE, ORDER_GUID, LINE_ITEM_GUID);

		Collection<String> optionIds = result.getData();
		assertTrue("The operation should have been successful", result.isSuccessful());
		assertEquals("There should be one option ID.", 1, optionIds.size());
		assertEquals("The optionId should be the guid of the line item's option.", OPTION_GUID, optionIds.iterator().next());
	}

	/**
	 * Test findOptionIds() returns a not found result when the purchase cannot found.
	 */
	@Test
	public void testFindOptionIdsWhenPurchaseNotFound() {
		shouldFindByGuidWithResult(ExecutionResultFactory.<Order>createNotFound("purchase not found"));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseLineItemsOptionsLookupStrategy.findOptionIds(STORE_CODE, ORDER_GUID, LINE_ITEM_GUID);
	}

	/**
	 * Test findOptionIds() returns a not found result when the line item cannot found.
	 */
	@Test
	public void testFindOptionIdsWhenLineItemNotFound() {
		Order order = createMockOrder(null);

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(order));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseLineItemsOptionsLookupStrategy.findOptionIds(STORE_CODE, ORDER_GUID, LINE_ITEM_GUID);
	}

	/**
	 * Test findOptionIds() returns a not found result when the line item has no options.
	 */
	@Test
	public void testFindOptionIdsWhenNoOptionsOnLineItem() {
		ProductSku productSku = createMockProductSku(Collections.<String>emptySet(), null);
		ShoppingItem shoppingItem = createMockShoppingItem(productSku);
		Order order = createMockOrder(shoppingItem);

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(order));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseLineItemsOptionsLookupStrategy.findOptionIds(STORE_CODE, ORDER_GUID, LINE_ITEM_GUID);
	}

	/**
	 * Test the happy case of findOption().
	 */
	@Test
	public void testFindOption() {
		SkuOptionValue skuOptionValue = createMockSkuOptionValue(OPTION_VALUE_KEY);
		ProductSku productSku = createMockProductSku(Collections.singleton(OPTION_GUID), Collections.singletonMap(OPTION_GUID, skuOptionValue));
		ShoppingItem shoppingItem = createMockShoppingItem(productSku);
		Order order = createMockOrder(shoppingItem);
		PurchaseLineItemOptionEntity expectedOptionDto = mock(PurchaseLineItemOptionEntity.class);

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(order));
		shouldUseSubject(LOCALE);
		shouldTransformSkuOptionValueToPurchaseLineItemOptionDto(skuOptionValue, expectedOptionDto);

		ExecutionResult<PurchaseLineItemOptionEntity> result = purchaseLineItemsOptionsLookupStrategy.findOption(STORE_CODE, ORDER_GUID,
				LINE_ITEM_GUID,
				OPTION_GUID);

		assertTrue("The operation should have been successful.", result.isSuccessful());

		PurchaseLineItemOptionEntity optionDto = result.getData();
		assertNotNull("An optionDto should have been returned", optionDto);
		assertEquals("The DTO should be the one from the transformer", expectedOptionDto, optionDto);
	}

	/**
	 * Test findOption() returns a not found result when the option id cannot found.
	 */
	@Test
	public void testFindOptionWhenOptionIdNotFound() {
		Order order = createMockOrder(null);

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(order));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseLineItemsOptionsLookupStrategy.findOption(STORE_CODE, ORDER_GUID,
				LINE_ITEM_GUID,
				"Some option");
	}

	/**
	 * Test find option when option id returns null option from product sku.
	 */
	@Test
	public void testFindOptionWhenOptionIdReturnsNullOptionFromProductSku() {
		ProductSku productSku = createMockProductSku(null, Collections.<String, SkuOptionValue>emptyMap());
		ShoppingItem shoppingItem = createMockShoppingItem(productSku);
		Order order = createMockOrder(shoppingItem);

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(order));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseLineItemsOptionsLookupStrategy.findOption(STORE_CODE, ORDER_GUID,
				LINE_ITEM_GUID,
				"Some option");
	}

	/**
	 * Test the happy case of findOptionValue().
	 */
	@Test
	public void testFindOptionValue() {
		SkuOptionValue skuOptionValue = createMockSkuOptionValue(OPTION_VALUE_KEY);
		ProductSku productSku = createMockProductSku(Collections.singleton(OPTION_GUID), Collections.singletonMap(OPTION_GUID, skuOptionValue));
		ShoppingItem shoppingItem = createMockShoppingItem(productSku);
		Order order = createMockOrder(shoppingItem);
		PurchaseLineItemOptionValueEntity expectedOptionValueDto = mock(PurchaseLineItemOptionValueEntity.class);

		shouldUseSubject(LOCALE);
		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(order));
		shouldTransformSkuOptionValueToPurchaseLineItemOptionValueDto(skuOptionValue, expectedOptionValueDto);

		ExecutionResult<PurchaseLineItemOptionValueEntity> result = purchaseLineItemsOptionsLookupStrategy.findOptionValue(STORE_CODE, ORDER_GUID,
				LINE_ITEM_GUID,
				OPTION_GUID,
				OPTION_VALUE_KEY);

		assertTrue("The operation should have been successful.", result.isSuccessful());
		assertEquals("The DTO should be the one from the transformer", expectedOptionValueDto, result.getData());
	}

	/**
	 * Test findOptionValue() returns a not found result when the item cannot found.
	 */
	@Test
	public void testFindOptionValueWhenItemNotFound() {
		Order order = createMockOrder(null);

		shouldUseSubject(LOCALE);
		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(order));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseLineItemsOptionsLookupStrategy.findOptionValue(STORE_CODE, ORDER_GUID,
				LINE_ITEM_GUID,
				OPTION_GUID,
				OPTION_VALUE_KEY);
	}

	/**
	 * Test findOptionValue() returns a not found result when the value id cannot found.
	 */
	@Test
	public void testFindOptionValueWithInvalidValueId() {
		Order order = createMockOrder(null);

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(order));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseLineItemsOptionsLookupStrategy.findOptionValue(STORE_CODE, ORDER_GUID,
				LINE_ITEM_GUID,
				OPTION_GUID,
				INVALID_OPTION_VALUE_KEY);
	}

	/**
	 * Test find option value when option value not found.
	 */
	@Test
	public void testFindOptionValueWhenOptionValueNotFound() {
		SkuOptionValue skuOptionValue = createMockSkuOptionValue(OPTION_VALUE_KEY);
		ProductSku productSku = createMockProductSku(Collections.singleton(OPTION_GUID), Collections.singletonMap(OPTION_GUID, skuOptionValue));
		ShoppingItem shoppingItem = createMockShoppingItem(productSku);
		Order order = createMockOrder(shoppingItem);

		shouldFindByGuidWithResult(ExecutionResultFactory.createReadOK(order));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseLineItemsOptionsLookupStrategy.findOptionValue(STORE_CODE, ORDER_GUID,
				LINE_ITEM_GUID,
				OPTION_GUID,
				INVALID_OPTION_VALUE_KEY);
	}

	private void shouldFindByGuidWithResult(final ExecutionResult<Order> result) {
		when(orderRepository.findByGuid(STORE_CODE, ORDER_GUID)).thenReturn(result);
	}

	private void shouldUseSubject(final Locale locale) {
		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale(STORE_CODE, USERID, locale);
		when(resourceOperationContext.getSubject())
				.thenReturn(subject);
	}

	private void shouldTransformSkuOptionValueToPurchaseLineItemOptionDto(final SkuOptionValue skuOptionValue,
			final PurchaseLineItemOptionEntity expectedOptionDto) {

		when(skuOptionTransformer.transformToEntity(skuOptionValue, LOCALE)).thenReturn(expectedOptionDto);
	}

	private void shouldTransformSkuOptionValueToPurchaseLineItemOptionValueDto(final SkuOptionValue skuOptionValue,
			final PurchaseLineItemOptionValueEntity expectedOptionValueDto) {

		when(skuOptionValueTransformer.transformToEntity(skuOptionValue, LOCALE)).thenReturn(expectedOptionValueDto);
	}

	private Order createMockOrder(final ShoppingItem shoppingItem) {
		Order order = mock(Order.class);
		stub(order.getShoppingItemByGuid(LINE_ITEM_GUID)).toReturn(shoppingItem);
		return order;
	}

	private ShoppingItem createMockShoppingItem(final ProductSku mockProductSku) {
		ShoppingItem shoppingItem = mock(ShoppingItem.class);
		stub(shoppingItem.getSkuGuid()).toReturn(SKU_GUID);
		when(productSkuRepository.getProductSkuWithAttributesByGuid(SKU_GUID)).thenReturn(ExecutionResultFactory.createReadOK(mockProductSku));
		return shoppingItem;
	}

	private ProductSku createMockProductSku(final Set<String> optionValueCodes, final Map<String, SkuOptionValue> optionValueMap) {
		ProductSku productSku = mock(ProductSku.class);
		stub(productSku.getOptionValueCodes()).toReturn(optionValueCodes);
		stub(productSku.getOptionValueMap()).toReturn(optionValueMap);
		return productSku;
	}

	private SkuOptionValue createMockSkuOptionValue(final String guid) {
		SkuOptionValue skuOptionValue = mock(SkuOptionValue.class);
		stub(skuOptionValue.getGuid()).toReturn(guid);
		return skuOptionValue;
	}
}
