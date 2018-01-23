/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.impl;

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
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.shipments.lineitems.ShipmentLineItemsLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Tests {@link ShipmentLineItemsResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentLineItemsResourceOperatorImplTest {

	private static final String LINE_ITEM_ID = "lineItemId";
	private static final String ENCODED_LINE_ITEM_ID = Base32Util.encode(LINE_ITEM_ID);
	private static final String TEST_URI = "/testuri";
	private static final ResourceOperation OPERATION = TestResourceOperationFactory.createRead(TEST_URI);
	private static final String SERVER_ERROR = "Server Error";

	@Mock
	private ResourceState<ShipmentEntity> shipment;
	@Mock
	private ShipmentEntity shipmentEntity;
	@Mock
	private ShipmentLineItemsLookup mockShipmentLineItemsLookup;
	@Mock
	private ResourceState<ShipmentLineItemEntity> mockShipmentLineItem;
	@Mock
	private ResourceState<LinksEntity> mockLinksRepresentation;
	@InjectMocks
	private ShipmentLineItemsResourceOperatorImpl resourceOperator;

	@Before
	public void setUp() {
		when(shipment.getEntity()).thenReturn(shipmentEntity);
	}

	@Test
	public void testProcessReadShipmentLineItemsWhenLookupReturnsReadOkay() {
		when(mockShipmentLineItemsLookup.findAll(shipment))
				.thenReturn(ExecutionResultFactory.createReadOK(mockLinksRepresentation));

		OperationResult result = resourceOperator.processReadShipmentLineItems(shipment, OPERATION);

		assertOperationResult(result)
				.resourceState(mockLinksRepresentation)
				.resourceStatus(ResourceStatus.READ_OK);
	}

	@Test
	public void testProcessReadShipmentLineItemsWhenLookupReturnsNotFound() {
		when(mockShipmentLineItemsLookup.findAll(shipment))
				.thenReturn(ExecutionResultFactory.<ResourceState<LinksEntity>>createNotFound());

		OperationResult result = resourceOperator.processReadShipmentLineItems(shipment, OPERATION);

		assertOperationResult(result).resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testProcessReadShipmentLineItemsWhenLookupReturnsServerError() {
		when(mockShipmentLineItemsLookup.findAll(shipment))
				.thenReturn(ExecutionResultFactory.<ResourceState<LinksEntity>>createServerError(SERVER_ERROR));

		OperationResult result = resourceOperator.processReadShipmentLineItems(shipment, OPERATION);

		assertOperationResult(result).resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	@Test
	public void testProcessReadShipmentLineItemWhenLookupReturnsReadOkay() {
		when(mockShipmentLineItemsLookup.find(shipment, LINE_ITEM_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(mockShipmentLineItem));

		OperationResult result = resourceOperator.processReadShipmentLineItem(shipment, ENCODED_LINE_ITEM_ID, OPERATION);

		assertOperationResult(result)
				.resourceState(mockShipmentLineItem)
				.resourceStatus(ResourceStatus.READ_OK);
	}

	@Test
	public void testProcessReadShipmentLineItemWhenLookupReturnsNotFound() {
		when(mockShipmentLineItemsLookup.find(shipment, LINE_ITEM_ID))
				.thenReturn(ExecutionResultFactory.<ResourceState<ShipmentLineItemEntity>>createNotFound());

		OperationResult result = resourceOperator.processReadShipmentLineItem(shipment, ENCODED_LINE_ITEM_ID, OPERATION);

		assertOperationResult(result).resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testProcessReadShipmentLineItemWhenLookupReturnsServerError() {
		when(mockShipmentLineItemsLookup.find(shipment, LINE_ITEM_ID))
				.thenReturn(ExecutionResultFactory.<ResourceState<ShipmentLineItemEntity>>createServerError(SERVER_ERROR));

		OperationResult result = resourceOperator.processReadShipmentLineItem(shipment, ENCODED_LINE_ITEM_ID, OPERATION);

		assertOperationResult(result).resourceStatus(ResourceStatus.SERVER_ERROR);
	}

}
