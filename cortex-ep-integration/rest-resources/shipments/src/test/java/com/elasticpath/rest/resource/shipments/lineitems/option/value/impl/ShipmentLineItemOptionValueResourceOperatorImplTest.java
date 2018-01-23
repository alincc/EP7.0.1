/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option.value.impl;

import static com.elasticpath.rest.test.AssertOperationResult.assertOperationResult;
import static org.mockito.Mockito.when;

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
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionValueEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.shipments.lineitems.option.ShipmentLineItemOptionLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Tests {@link ShipmentLineItemOptionValueResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentLineItemOptionValueResourceOperatorImplTest {

	private static final String OPTION_VALUE_ID = "lineItemOptionValueId";
	private static final String ENCODED_OPTION_VALUE_ID = Base32Util.encode(OPTION_VALUE_ID);

	private static final String TEST_URI = "/testuri";

	private static final ResourceOperation OPERATION = TestResourceOperationFactory.createRead(TEST_URI);

	private static final ResourceState<ShipmentLineItemOptionEntity> LINEITEM_OPTION_REPRESENTATION =
			ResourceState.Builder
					.create(ShipmentLineItemOptionEntity.builder().build())
					.build();

	private static final String SERVER_ERROR = "Server Error";

	@Mock
	private ShipmentLineItemOptionLookup mockShipmentLineItemOptionLookup;

	@Mock
	private ResourceState<ShipmentLineItemOptionValueEntity> mockShipmentLineItemOptionValue;

	@InjectMocks
	private ShipmentLineItemOptionValueResourceOperatorImpl resourceOperator;

	@Test
	public void testProcessReadShipmentLineItemOptionValueWhenLookupReturnsReadOkay() {
		when(mockShipmentLineItemOptionLookup.findOptionValues(LINEITEM_OPTION_REPRESENTATION, OPTION_VALUE_ID)).thenReturn(
				ExecutionResultFactory.createReadOK(mockShipmentLineItemOptionValue));

		OperationResult result =
				resourceOperator.processReadShipmentLineItemOptionValue(LINEITEM_OPTION_REPRESENTATION, ENCODED_OPTION_VALUE_ID, OPERATION);

		assertOperationResult(result)
				.resourceState(mockShipmentLineItemOptionValue)
				.resourceStatus(ResourceStatus.READ_OK);
	}

	@Test
	public void testProcessReadShipmentLineItemOptionValueWhenLookupReturnsNotFound() {
		when(mockShipmentLineItemOptionLookup.findOptionValues(LINEITEM_OPTION_REPRESENTATION, OPTION_VALUE_ID)).thenReturn(
				ExecutionResultFactory.<ResourceState<ShipmentLineItemOptionValueEntity>>createNotFound());

		OperationResult result =
				resourceOperator.processReadShipmentLineItemOptionValue(LINEITEM_OPTION_REPRESENTATION, ENCODED_OPTION_VALUE_ID, OPERATION);

		assertOperationResult(result).resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testProcessReadShipmentLineItemOptionValueWhenLookupReturnsServerError() {
		when(mockShipmentLineItemOptionLookup.findOptionValues(LINEITEM_OPTION_REPRESENTATION, OPTION_VALUE_ID)).thenReturn(
				ExecutionResultFactory.<ResourceState<ShipmentLineItemOptionValueEntity>>createServerError(SERVER_ERROR));

		OperationResult result =
				resourceOperator.processReadShipmentLineItemOptionValue(LINEITEM_OPTION_REPRESENTATION, ENCODED_OPTION_VALUE_ID, OPERATION);

		assertOperationResult(result).resourceStatus(ResourceStatus.SERVER_ERROR);
	}

}
