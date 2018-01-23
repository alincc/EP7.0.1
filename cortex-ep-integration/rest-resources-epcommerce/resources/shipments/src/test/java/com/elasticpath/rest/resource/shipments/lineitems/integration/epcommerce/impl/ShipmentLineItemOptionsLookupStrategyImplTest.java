/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.command.ExecutionResultFactory.createReadOK;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableSet;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionValueEntity;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Test cases for {@link ShipmentLineItemOptionsLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentLineItemOptionsLookupStrategyImplTest {

	private static final String ORDER_GUID = "test-order";
	private static final String SHIPMENT_GUID = "test-shipment";
	private static final String LINE_ITEM_GUID_1 = "test-line-item-1";
	private static final String LINE_ITEM_GUID_2 = "test-line-item-2";
	private static final String LINE_ITEM_OPTION_GUID = "test-line-item-option-1";
	private static final String LINE_ITEM_OPTION_VALUE_GUID = "test-line-item-option-value";
	private static final String SKU_DISPLAY_NAME_1 = "SKU 1";
	private static final Integer SKU_QUANTITY_1 = 123;
	private static final String SKU_GUID = "skuGuid";
	private static final Set<String> OPTION_IDS = Sets.newSet(LINE_ITEM_OPTION_GUID);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ResourceOperationContext resourceOperationContext;
	@Mock
	private ShipmentRepository shipmentRepository;
	@Mock
	private ProductSkuRepository productSkuRepository;
	@Mock
	private AbstractDomainTransformer<SkuOptionValue, ShipmentLineItemOptionEntity> skuOptionTransformer;
	@Mock
	private AbstractDomainTransformer<SkuOptionValue, ShipmentLineItemOptionValueEntity> skuOptionValueTransformer;

	private ShipmentLineItemOptionsLookupStrategyImpl shipmentLineItemOptionsLookupStrategy;
	@Mock
	private OrderSku orderSku1;
	@Mock
	private OrderSku orderSku2;
	@Mock
	private PhysicalOrderShipment orderShipment;
	@Mock
	private ProductSku productSku1;
	@Mock
	private SkuOptionValue skuOptionValue;

	private final Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale("scope", "userId", Locale.ENGLISH);

	private final Map<String, SkuOptionValue> optionValueMap = new HashMap<>();

	private final ShipmentLineItemEntity queryLineItemEntity =  ShipmentLineItemEntity.builder()
			.withShipmentId(SHIPMENT_GUID)
			.withPurchaseId(ORDER_GUID)
			.withLineItemId(LINE_ITEM_GUID_1)
			.build();

	private final ShipmentLineItemOptionEntity queryOptionEntity = ShipmentLineItemOptionEntity.builder()
			.withShipmentId(SHIPMENT_GUID)
			.withPurchaseId(ORDER_GUID)
			.withLineItemId(LINE_ITEM_GUID_1)
			.withLineItemOptionId(LINE_ITEM_OPTION_GUID)
			.build();

	@Before
	public void setUp() {
		when(resourceOperationContext.getSubject()).thenReturn(subject);

		Set<OrderSku> orderSKUs = ImmutableSet.of(orderSku1, orderSku2);
		when(orderShipment.getShipmentOrderSkus()).thenReturn(orderSKUs);
		when(orderSku1.getGuid()).thenReturn(LINE_ITEM_GUID_1);
		when(orderSku1.getSkuGuid()).thenReturn(SKU_GUID);
		when(orderSku2.getGuid()).thenReturn(LINE_ITEM_GUID_2);
		when(orderSku2.getSkuGuid()).thenReturn(SKU_GUID);
		when(orderSku1.getQuantity()).thenReturn(SKU_QUANTITY_1);
		when(orderSku1.getDisplayName()).thenReturn(SKU_DISPLAY_NAME_1);
		optionValueMap.put(LINE_ITEM_OPTION_GUID, skuOptionValue);
		when(skuOptionValue.getGuid()).thenReturn(LINE_ITEM_OPTION_VALUE_GUID);
		shipmentLineItemOptionsLookupStrategy = new ShipmentLineItemOptionsLookupStrategyImpl(productSkuRepository, skuOptionTransformer,
				skuOptionValueTransformer, shipmentRepository, resourceOperationContext);
	}



	@Test
	public void testFindLineItemOptionIdsWhenShipmentNotFound() {
		when(shipmentRepository.find(ORDER_GUID, SHIPMENT_GUID))
				.thenReturn(ExecutionResultFactory.<PhysicalOrderShipment>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shipmentLineItemOptionsLookupStrategy.findLineItemOptionIds(queryLineItemEntity);
	}

	@Test
	public void testFindLineItemOptionIdsWhenSuccesful() {
		when(shipmentRepository.find(ORDER_GUID, SHIPMENT_GUID)).thenReturn(createReadOK(orderShipment));
		when(productSkuRepository.getProductSkuWithAttributesByGuid(anyString()))
				.thenReturn(createReadOK(productSku1));
		when(productSku1.getOptionValueCodes()).thenReturn(OPTION_IDS);


		ExecutionResult<Collection<String>> executionResult =
				shipmentLineItemOptionsLookupStrategy.findLineItemOptionIds(queryLineItemEntity);

		assertExecutionResult(executionResult)
				.isSuccessful()
				.data(OPTION_IDS);
	}

	@Test
	public void testFindLineItemOptionWhenShipmentNotFound() {
		when(shipmentRepository.find(ORDER_GUID, SHIPMENT_GUID)).thenReturn(ExecutionResultFactory.<PhysicalOrderShipment> createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shipmentLineItemOptionsLookupStrategy.findLineItemOption(queryLineItemEntity, LINE_ITEM_OPTION_GUID);
	}

	@Test
	public void testFindLineItemOptionForSuccess() {
		when(shipmentRepository.find(ORDER_GUID, SHIPMENT_GUID)).thenReturn(createReadOK(orderShipment));
		when(productSkuRepository.getProductSkuWithAttributesByGuid(anyString())).thenReturn(createReadOK(productSku1));
		when(productSku1.getOptionValueMap()).thenReturn(optionValueMap);
		when(skuOptionTransformer.transformToEntity(eq(skuOptionValue), any(Locale.class))).thenReturn(createExpectedOptionEntity());


		ExecutionResult<ShipmentLineItemOptionEntity> executionResult =
				shipmentLineItemOptionsLookupStrategy.findLineItemOption(queryLineItemEntity, LINE_ITEM_OPTION_GUID);

		assertExecutionResult(executionResult)
				.isSuccessful()
				.data(createExpectedOptionEntity());
	}

	@Test
	public void testFindOptionValuesWhenNoneFound() {
		when(shipmentRepository.find(ORDER_GUID, SHIPMENT_GUID)).thenReturn(createReadOK(orderShipment));
		when(productSkuRepository.getProductSkuWithAttributesByGuid(SKU_GUID)).thenReturn(createReadOK(productSku1));
		Map<String, SkuOptionValue> optionValueMap = Collections.emptyMap();
		when(productSku1.getOptionValueMap()).thenReturn(optionValueMap);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shipmentLineItemOptionsLookupStrategy.findLineItemOptionValue(queryOptionEntity, LINE_ITEM_OPTION_VALUE_GUID);
	}
	@Test
	public void testFindOptionValueForSuccess() {
		when(shipmentRepository.find(ORDER_GUID, SHIPMENT_GUID)).thenReturn(createReadOK(orderShipment));
		when(productSkuRepository.getProductSkuWithAttributesByGuid(SKU_GUID)).thenReturn(createReadOK(productSku1));
		Map<String, SkuOptionValue> optionValueMap = Collections.singletonMap(LINE_ITEM_OPTION_GUID, skuOptionValue);
		when(productSku1.getOptionValueMap()).thenReturn(optionValueMap);
		when(skuOptionValueTransformer.transformToEntity(eq(skuOptionValue), any(Locale.class))).thenReturn(createExpectedValueEntity());

		ExecutionResult<ShipmentLineItemOptionValueEntity> executionResult =
				shipmentLineItemOptionsLookupStrategy.findLineItemOptionValue(queryOptionEntity, LINE_ITEM_OPTION_VALUE_GUID);

		assertExecutionResult(executionResult)
				.isSuccessful()
				.data(createExpectedValueEntity());
	}

	private ShipmentLineItemOptionEntity createExpectedOptionEntity() {
		return ShipmentLineItemOptionEntity.builder()
				.withShipmentId(SHIPMENT_GUID)
				.withPurchaseId(ORDER_GUID)
				.withLineItemId(LINE_ITEM_GUID_1)
				.withLineItemOptionId(LINE_ITEM_OPTION_GUID)
				.withLineItemOptionValueId(LINE_ITEM_OPTION_VALUE_GUID)
				.build();
	}

	private ShipmentLineItemOptionValueEntity createExpectedValueEntity() {
		return ShipmentLineItemOptionValueEntity.builder()
				.withDisplayName(SHIPMENT_GUID)
				.withName(ORDER_GUID)
				.build();
	}
}
