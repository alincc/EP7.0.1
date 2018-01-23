/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.addresses.AddressesMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.shipmentdetails.DestinationInfo;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.DestinationInfoWriter;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Test class for {@link SelectShippingAddressResourceOperator}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class SelectShippingAddressResourceOperatorTest {

	private static final String RESOURCE = "shipmentdetails";
	private static final String SCOPE = "scope";
	private static final String SHIPMENT_ID = "shipment_id";
	private static final String SHIPMENT_URI = URIUtil.format(RESOURCE, SCOPE, SHIPMENT_ID);
	private static final String ADDRESS_URI = "/address_uri";
	private static final ResourceState<AddressEntity> ADDRESS = ResourceState.Builder.create(AddressEntity.builder().build())
			.withSelf(SelfFactory.createSelf(ADDRESS_URI, AddressesMediaTypes.ADDRESS.id()))
			.build();
	private static final ResourceOperation CREATE_OP = TestResourceOperationFactory.createCreate(ADDRESS_URI, ADDRESS);

	@Mock
	private DestinationInfoWriter mockDestinationInfoWriter;

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	private SelectShippingAddressResourceOperator selectShippingAddressCommand;
	@Before
	public void setUp() {
		selectShippingAddressCommand = new SelectShippingAddressResourceOperator(RESOURCE, mockDestinationInfoWriter);
	}


	@Test
	public void testSelectShippingAddress() {
		when(mockDestinationInfoWriter.updateShippingAddressForShipment(SCOPE, SHIPMENT_ID, ADDRESS))
				.thenReturn(ExecutionResultFactory.<Void>createReadOK(null));

		OperationResult result = selectShippingAddressCommand.processSelectShipmentDestinationInfoChoice(SCOPE, SHIPMENT_ID, ADDRESS, CREATE_OP);

		assertTrue(result.isSuccessful());
		String expectedUri = URIUtil.format(SHIPMENT_URI, DestinationInfo.URI_PART, Selector.URI_PART);
		assertEquals(expectedUri, ResourceStateUtil.getSelfUri(result.getResourceState()));
	}


	@Test
	public void testSelectShippingAddressWithErrorUpdatingAddress() {
		when(mockDestinationInfoWriter.updateShippingAddressForShipment(SCOPE, SHIPMENT_ID, ADDRESS))
				.thenReturn(ExecutionResultFactory.<Void>createNotFound("error"));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		selectShippingAddressCommand.processSelectShipmentDestinationInfoChoice(SCOPE, SHIPMENT_ID,	ADDRESS, CREATE_OP);
	}

}
