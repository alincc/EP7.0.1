/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.impl;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.resource.dispatch.operator.AbstractResourceOperatorUriTest;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.shipmentdetails.DestinationInfo;
import com.elasticpath.rest.resource.shipmentdetails.ShippingOption;
import com.elasticpath.rest.resource.shipmentdetails.ShippingOptionInfo;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.impl.DestinationInfoResourceOperatorImpl;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.impl.ShippingOptionInfoResourceOperatorImpl;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Test functionality of ShipmentDetailsResourceUri.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ShippingOptionInfoResourceOperatorImpl.class,
		DestinationInfoResourceOperatorImpl.class})
public final class ShipmentDetailsResourceUriTest extends AbstractResourceOperatorUriTest {

	private static final String RESOURCENAME = "shipmentdetails";
	private static final String SCOPE = "scope";
	private static final String SHIPMENTID = "52xw4ztjm52xeylunfxw4s52=";
	private static final String SHIPPINGOPTIONID = "aaaw4ztjm52xeylunfxw4s52=";

	@Spy
	private final ShippingOptionInfoResourceOperatorImpl shippingOptionInfoResourceOperator =
			new ShippingOptionInfoResourceOperatorImpl(null, null, null);
	@Spy
	private final DestinationInfoResourceOperatorImpl destinationInfoResourceOperator =
			new DestinationInfoResourceOperatorImpl(null, null);
	@Mock
	private OperationResult mockOperationResult;

	/**
	 * Test read destination info.
	 */
	@Test
	public void testReadDestinationInfo() {
		String uri = URIUtil.format(RESOURCENAME, SCOPE, SHIPMENTID, DestinationInfo.URI_PART);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(destinationInfoResourceOperator)
				.processReadShipmentDestinationInfo(SCOPE, SHIPMENTID, operation);

		dispatch(operation);

		verify(destinationInfoResourceOperator).processReadShipmentDestinationInfo(SCOPE, SHIPMENTID, operation);
	}


	/**
	 * Test read destination info selector.
	 */
	@Test
	public void testReadDestinationInfoSelector() {
		String uri = URIUtil.format(RESOURCENAME, SCOPE, SHIPMENTID, DestinationInfo.URI_PART, Selector.URI_PART);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(destinationInfoResourceOperator).processReadShipmentDestinationInfoSelector(SCOPE, SHIPMENTID, operation);

		dispatch(operation);

		verify(destinationInfoResourceOperator).processReadShipmentDestinationInfoSelector(SCOPE, SHIPMENTID, operation);
	}


	/**
	 * Test read shipping option info.
	 */
	public void testReadShippingOptionInfo() {
		String uri = URIUtil.format(RESOURCENAME, SCOPE, SHIPMENTID, ShippingOptionInfo.URI_PART);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(shippingOptionInfoResourceOperator)
				.processReadShippingOptionInfo(SCOPE, SHIPMENTID, operation);

		dispatch(operation);

		verify(shippingOptionInfoResourceOperator).processReadShippingOptionInfo(SCOPE, SHIPMENTID, operation);
	}

	/**
	 * Test read shipping option info selector.
	 */
	@Test
	public void testReadShippingOptionInfoSelector() {
		String uri = URIUtil.format(RESOURCENAME, SCOPE, SHIPMENTID, ShippingOptionInfo.URI_PART, Selector.URI_PART);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(shippingOptionInfoResourceOperator)
				.processReadShippingOptionInfoSelector(SCOPE, SHIPMENTID, operation);

		dispatch(operation);

		verify(shippingOptionInfoResourceOperator).processReadShippingOptionInfoSelector(SCOPE, SHIPMENTID, operation);
	}


	/**
	 * Test read shipping option.
	 */
	@Test
	public void testReadShippingOption() {
		String uri = URIUtil.format(RESOURCENAME, SCOPE, SHIPMENTID, ShippingOption.PATH_PART, SHIPPINGOPTIONID);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(shippingOptionInfoResourceOperator)
				.processReadShippingOption(SCOPE, SHIPMENTID, SHIPPINGOPTIONID, operation);

		dispatch(operation);

		verify(shippingOptionInfoResourceOperator).processReadShippingOption(SCOPE, SHIPMENTID, SHIPPINGOPTIONID, operation);
	}

	private void dispatch(final ResourceOperation operation) {
		dispatchMethod(operation, shippingOptionInfoResourceOperator, destinationInfoResourceOperator);
	}
}
