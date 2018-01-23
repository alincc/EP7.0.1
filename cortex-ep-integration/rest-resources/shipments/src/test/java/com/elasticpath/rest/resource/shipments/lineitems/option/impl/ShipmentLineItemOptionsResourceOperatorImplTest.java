/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option.impl;

import static com.elasticpath.rest.test.AssertOperationResult.assertOperationResult;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.shipments.lineitems.option.ShipmentLineItemOptionLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Tests {@link ShipmentLineItemOptionsResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentLineItemOptionsResourceOperatorImplTest {

	private static final String LINE_ITEM_OPTION_ID = "lineItemOptionId";
	private static final String ENCODED_LINE_ITEM_OPTION_ID = Base32Util.encode(LINE_ITEM_OPTION_ID);
	private static final String TEST_URI = "/testuri";
	private static final ResourceOperation OPERATION = TestResourceOperationFactory.createRead(TEST_URI);
	private static final String SERVER_ERROR = "Server Error";
	@Mock
	private ResourceState<ShipmentLineItemEntity> shipmentLineItem;
	@Mock
	private ShipmentLineItemEntity shipmentLineItemEntity;
	@Mock
	private ShipmentLineItemOptionLookup mockShipmentLineItemOptionLookup;
	@Mock
	private ResourceState<ShipmentLineItemOptionEntity> mockShipmentLineItemOption;
	@Mock
	private ResourceState<LinksEntity> mockLinksResource;
	@InjectMocks
	private ShipmentLineItemOptionsResourceOperatorImpl resourceOperator;

	@Before
	public void setUp() {
		when(shipmentLineItem.getEntity()).thenReturn(shipmentLineItemEntity);
	}

	@Test
	public void testProcessReadShipmentLineItemOptionsWhenLookupReturnsReadOkay() {
		when(mockShipmentLineItemOptionLookup.findAll(shipmentLineItem)).thenReturn(
				ExecutionResultFactory.createReadOK(mockLinksResource));

		OperationResult result = resourceOperator.processReadShipmentLineItemOptions(shipmentLineItem, OPERATION);

		assertOperationResult(result)
				.resourceState(mockLinksResource)
				.resourceStatus(ResourceStatus.READ_OK);
	}

	@Test
	public void testProcessReadShipmentLineItemOptionsWhenLookupReturnsNotFound() {
		when(mockShipmentLineItemOptionLookup.findAll(shipmentLineItem)).thenReturn(
				ExecutionResultFactory.<ResourceState<LinksEntity>>createNotFound());

		OperationResult result = resourceOperator.processReadShipmentLineItemOptions(shipmentLineItem, OPERATION);

		assertOperationResult(result).resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testProcessReadShipmentLineItemOptionsWhenLookupReturnsServerError() {
		when(mockShipmentLineItemOptionLookup.findAll(shipmentLineItem)).thenReturn(
				ExecutionResultFactory.<ResourceState<LinksEntity>>createServerError(SERVER_ERROR));

		OperationResult result = resourceOperator.processReadShipmentLineItemOptions(shipmentLineItem, OPERATION);

		assertOperationResult(result).resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	@Test
	public void testProcessReadShipmentLineItemOptionWhenLookupReturnsReadOkay() {
		when(mockShipmentLineItemOptionLookup.find(shipmentLineItem, LINE_ITEM_OPTION_ID)).thenReturn(
				ExecutionResultFactory.createReadOK(mockShipmentLineItemOption));

		OperationResult result = resourceOperator.processReadShipmentLineItemOption(shipmentLineItem, ENCODED_LINE_ITEM_OPTION_ID, OPERATION);

		assertOperationResult(result)
				.resourceState(mockShipmentLineItemOption)
				.resourceStatus(ResourceStatus.READ_OK);
	}

	@Test
	public void testProcessReadShipmentLineItemOptionWhenLookupReturnsNotFound() {
		when(mockShipmentLineItemOptionLookup.find(shipmentLineItem, LINE_ITEM_OPTION_ID)).thenReturn(
				ExecutionResultFactory.<ResourceState<ShipmentLineItemOptionEntity>>createNotFound());

		OperationResult result = resourceOperator.processReadShipmentLineItemOption(shipmentLineItem, ENCODED_LINE_ITEM_OPTION_ID, OPERATION);

		assertOperationResult(result).resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testProcessReadShipmentLineItemOptionWhenLookupReturnsServerError() {
		when(mockShipmentLineItemOptionLookup.find(shipmentLineItem, LINE_ITEM_OPTION_ID)).thenReturn(
				ExecutionResultFactory.<ResourceState<ShipmentLineItemOptionEntity>>createServerError(SERVER_ERROR));

		OperationResult result = resourceOperator.processReadShipmentLineItemOption(shipmentLineItem, ENCODED_LINE_ITEM_OPTION_ID, OPERATION);

		assertOperationResult(result).resourceStatus(ResourceStatus.SERVER_ERROR);
	}

}
