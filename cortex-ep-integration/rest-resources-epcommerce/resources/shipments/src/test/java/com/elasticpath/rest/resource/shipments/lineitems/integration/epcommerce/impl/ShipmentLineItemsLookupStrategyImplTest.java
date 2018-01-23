/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableSet;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * Test cases for {@link ShipmentLineItemsLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentLineItemsLookupStrategyImplTest {

	private static final String SCOPE = "TEST";
	private static final String ORDER_GUID = "test-order";
	private static final String SHIPMENT_GUID = "test-shipment";
	private static final String LINE_ITEM_GUID_1 = "test-line-item-1";
	private static final String LINE_ITEM_GUID_2 = "test-line-item-2";
	private static final String SKU_DISPLAY_NAME_1 = "SKU 1";
	private static final Integer SKU_QUANTITY_1 = 123;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private OrderSku orderSku1;
	@Mock
	private OrderSku orderSku2;
	@Mock
	private ShipmentRepository shipmentRepository;
	@Mock
	private PhysicalOrderShipment orderShipment;
	@Mock
	private MoneyTransformer moneyTransformer;
	@Mock
	@SuppressWarnings("PMD.UnusedPrivateField")
	private ResourceOperationContext resourceOperationContext;
	private final CostEntity purchasePrice = ResourceTypeFactory.createResourceEntity(CostEntity.class);

	@InjectMocks
	private ShipmentLineItemsLookupStrategyImpl shipmentLineItemsLookupStrategy;

	private final ShipmentLineItemEntity queryEntity = ShipmentLineItemEntity.builder()
			.withShipmentId(SHIPMENT_GUID)
			.withPurchaseId(ORDER_GUID)
			.withLineItemId(LINE_ITEM_GUID_1)
			.build();
	private Collection<OrderSku> orderSkus;

	@Before
	public void setUp() {
		Set<OrderSku> orderSkus = ImmutableSet.of(orderSku1, orderSku2);
		this.orderSkus = orderSkus;
		when(orderShipment.getShipmentOrderSkus()).thenReturn(orderSkus);
		when(orderSku1.getGuid()).thenReturn(LINE_ITEM_GUID_1);
		when(orderSku2.getGuid()).thenReturn(LINE_ITEM_GUID_2);
		when(orderSku1.getQuantity()).thenReturn(SKU_QUANTITY_1);
		when(orderSku1.getDisplayName()).thenReturn(SKU_DISPLAY_NAME_1);
		when(moneyTransformer.transformToEntity(any(Money.class), any(Locale.class))).thenReturn(purchasePrice);
	}

	@Test
	public void testGetShipmentLineItemIdsWhenShipmentNotFound() {
		when(shipmentRepository.getOrderSkusForShipment(SCOPE, ORDER_GUID, SHIPMENT_GUID))
				.thenReturn(ExecutionResultFactory.<Collection<OrderSku>>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shipmentLineItemsLookupStrategy.findLineItemIds(SCOPE, queryEntity);
	}

	@Test
	public void testGetShipmentLineItemIdsForSuccess() {
		when(shipmentRepository.getOrderSkusForShipment(SCOPE, ORDER_GUID, SHIPMENT_GUID))
				.thenReturn(ExecutionResultFactory.createReadOK(orderSkus));

		ExecutionResult<Collection<String>> executionResult = shipmentLineItemsLookupStrategy.findLineItemIds(SCOPE, queryEntity);

		assertExecutionResult(executionResult)
				.isSuccessful();
		assertThat(executionResult.getData(), hasItems(LINE_ITEM_GUID_1, LINE_ITEM_GUID_2));
	}

	@Test
	public void testGetOrderSkuWhenShipmentNotFound() {
		when(shipmentRepository.getOrderSku(SCOPE, ORDER_GUID, SHIPMENT_GUID, LINE_ITEM_GUID_1, null))
				.thenReturn(ExecutionResultFactory.<OrderSku> createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shipmentLineItemsLookupStrategy.find(SCOPE, queryEntity);
	}


	@Test
	public void testGetOrderSkuForSuccess() {
		when(shipmentRepository.getOrderSku(SCOPE, ORDER_GUID, SHIPMENT_GUID, LINE_ITEM_GUID_1, null))
				.thenReturn(ExecutionResultFactory.createReadOK(orderSku1));
		ExecutionResult<ShipmentLineItemEntity> executionResult = shipmentLineItemsLookupStrategy.find(SCOPE, queryEntity);

		assertExecutionResult(executionResult)
				.isSuccessful()
				.data(createExpectedEntity());
	}

	private ShipmentLineItemEntity createExpectedEntity() {
		return ShipmentLineItemEntity.builder()
				.withName(SKU_DISPLAY_NAME_1)
				.withShipmentId(SHIPMENT_GUID)
				.withPurchaseId(ORDER_GUID)
				.withLineItemId(LINE_ITEM_GUID_1)
				.withQuantity(SKU_QUANTITY_1)
				.build();
	}

}
