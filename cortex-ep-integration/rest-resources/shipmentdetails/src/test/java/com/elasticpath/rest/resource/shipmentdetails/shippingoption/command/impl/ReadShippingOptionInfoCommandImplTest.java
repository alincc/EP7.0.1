/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.command.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.hamcrest.Matchers;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.definition.shipmentdetails.ShipmentdetailsMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.shipmentdetails.ShipmentDetail;
import com.elasticpath.rest.resource.shipmentdetails.ShipmentDetailsLookup;
import com.elasticpath.rest.resource.shipmentdetails.ShippingOption;
import com.elasticpath.rest.resource.shipmentdetails.ShippingOptionInfo;
import com.elasticpath.rest.resource.shipmentdetails.rel.ShipmentDetailsRels;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.ShippingOptionLookup;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.constants.ShippingOptionInfoConstants;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.common.selector.SelectorRepresentationRels;
import com.elasticpath.rest.schema.uri.DeliveryUriBuilder;
import com.elasticpath.rest.schema.uri.DeliveryUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests for {@link ReadShippingOptionInfoCommandImpl}.
 */
public final class ReadShippingOptionInfoCommandImplTest {

	private static final String NOT_FOUND = "not found";
	private static final String SHIPPING_OPTION_ID = "SHIPPING OPTION ID";
	private static final String DELIVERY_URI = "/deliveryUri";
	private static final String DELIVERY_ID = "DELIVERYID";
	private static final String ORDER_ID = "ORDER_ID";
	private static final String RESOURCE_SERVER = "RESOURCE_SERVER";
	private static final String SCOPE = "the scope";
	private static final String SHIPMENTDETAILS_ID = "shipment details id";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	@Mock
	private ShippingOptionLookup mockShippingOptionLookup;
	@Mock
	private DeliveryUriBuilderFactory mockDeliveryUriBuilderFactory;
	@Mock
	private ShipmentDetailsLookup mockShipmentDetailsLookup;


	/**
	 * Tests the read command.
	 */
	@Test
	public void testReadCommand() {

		ReadShippingOptionInfoCommandImpl command = createCommand();
		mockDeliveryUri(DELIVERY_URI);
		ShipmentDetail shipmentDetail = new ShipmentDetail()
				.setOrderId(ORDER_ID)
				.setDeliveryId(DELIVERY_ID);

		ExecutionResult<ShipmentDetail> shipmentDetailsResult = ExecutionResultFactory.createReadOK(shipmentDetail);

		mockShipmentDetailsResult(shipmentDetailsResult);

		context.checking(new Expectations() {
			{
				allowing(mockShippingOptionLookup).getSelectedShipmentOptionIdForShipmentDetails(SCOPE, SHIPMENTDETAILS_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(SHIPPING_OPTION_ID)));
			}
		});

		ExecutionResult<ResourceState<InfoEntity>> result = command.execute();

		assertTrue(result.isSuccessful());
		ResourceState<InfoEntity> infoRepresentation = result.getData();

		String expectedSelfUri = URIUtil.format(RESOURCE_SERVER, SCOPE, SHIPMENTDETAILS_ID, ShippingOptionInfo.URI_PART);
		String expectedSelectorUri = URIUtil.format(expectedSelfUri, Selector.URI_PART);
		ResourceLink expectedSelectorLink = ResourceLinkFactory.create(expectedSelectorUri, ControlsMediaTypes.SELECTOR.id(),
				SelectorRepresentationRels.SELECTOR, ShipmentDetailsRels.SHIPPING_OPTION_INFO_REV);

		ResourceLink expectedDeliveryLink = ResourceLinkFactory.create(DELIVERY_URI, OrdersMediaTypes.DELIVERY.id(), ShipmentDetailsRels.DELIVERY_REL,
				ShipmentDetailsRels.SHIPPING_OPTION_INFO_REV);

		String expectedShippingOptionUri = URIUtil.format(RESOURCE_SERVER, SCOPE, SHIPMENTDETAILS_ID, ShippingOption.URI_PART, SHIPPING_OPTION_ID);
		ResourceLink expectedShippingOptionLink = ResourceLinkFactory.createNoRev(expectedShippingOptionUri,
				ShipmentdetailsMediaTypes.SHIPPING_OPTION.id(), ShipmentDetailsRels.SHIPPINGOPTION_REL);

		assertEquals(expectedSelfUri, ResourceStateUtil.getSelfUri(infoRepresentation));
		assertEquals(ShippingOptionInfoConstants.SHIPPING_OPTION_INFO_NAME, infoRepresentation.getEntity().getName());

		assertThat("wrong links", infoRepresentation.getLinks(),
				Matchers.contains(expectedSelectorLink, expectedDeliveryLink, expectedShippingOptionLink));
	}

	/**
	 * Test read command with invalid shipment detail lookup.
	 */
	@Test
	public void testReadCommandWithInvalidShipmentDetailLookup() {

		ExecutionResult<ShipmentDetail> shipmentDetailsResult = ExecutionResultFactory.createNotFound(NOT_FOUND);
		mockShipmentDetailsResult(shipmentDetailsResult);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		ReadShippingOptionInfoCommandImpl command = createCommand();
		command.execute();
	}

	/**
	 * Test read command with no selected shipment option.
	 */
	@Test
	public void testReadCommandWithNoSelectedShipmentOption() {

		ReadShippingOptionInfoCommandImpl command = createCommand();
		mockDeliveryUri(DELIVERY_URI);
		ShipmentDetail shipmentDetail = new ShipmentDetail()
				.setOrderId(ORDER_ID)
				.setDeliveryId(DELIVERY_ID);

		ExecutionResult<ShipmentDetail> shipmentDetailsResult = ExecutionResultFactory.createReadOK(shipmentDetail);

		mockShipmentDetailsResult(shipmentDetailsResult);

		context.checking(new Expectations() {
			{
				allowing(mockShippingOptionLookup).getSelectedShipmentOptionIdForShipmentDetails(SCOPE, SHIPMENTDETAILS_ID);
				will(returnValue(ExecutionResultFactory.createNotFound(NOT_FOUND)));
			}
		});

		ExecutionResult<ResourceState<InfoEntity>> result = command.execute();

		assertTrue(result.isSuccessful());
		ResourceState<InfoEntity> infoRepresentation = result.getData();

		String expectedSelfUri = URIUtil.format(RESOURCE_SERVER, SCOPE, SHIPMENTDETAILS_ID, ShippingOptionInfo.URI_PART);
		String expectedSelectorUri = URIUtil.format(expectedSelfUri, Selector.URI_PART);
		ResourceLink expectedSelectorLink = ResourceLinkFactory.create(expectedSelectorUri, ControlsMediaTypes.SELECTOR.id(),
				SelectorRepresentationRels.SELECTOR, ShipmentDetailsRels.SHIPPING_OPTION_INFO_REV);

		ResourceLink expectedDeliveryLink = ResourceLinkFactory.create(DELIVERY_URI, OrdersMediaTypes.DELIVERY.id(), ShipmentDetailsRels.DELIVERY_REL,
				ShipmentDetailsRels.SHIPPING_OPTION_INFO_REV);

		assertEquals(expectedSelfUri, ResourceStateUtil.getSelfUri(infoRepresentation));
		assertEquals(ShippingOptionInfoConstants.SHIPPING_OPTION_INFO_NAME, infoRepresentation.getEntity().getName());

		assertThat("wrong links", infoRepresentation.getLinks(), Matchers.contains(expectedSelectorLink, expectedDeliveryLink));
	}

	private void mockShipmentDetailsResult(final ExecutionResult<ShipmentDetail> shipmentDetailsResult) {
		context.checking(new Expectations() {
			{
				allowing(mockShipmentDetailsLookup).getShipmentDetail(SCOPE, SHIPMENTDETAILS_ID);
				will(returnValue(shipmentDetailsResult));
			}
		});
	}

	private void mockDeliveryUri(final String deliveryUri) {

		final DeliveryUriBuilder mockDeliveryUriBuilder = context.mock(DeliveryUriBuilder.class);
		context.checking(new Expectations() {
			{
				allowing(mockDeliveryUriBuilderFactory).get();
				will(returnValue(mockDeliveryUriBuilder));

				allowing(mockDeliveryUriBuilder).setDeliveryId(DELIVERY_ID);
				will(returnValue(mockDeliveryUriBuilder));
				allowing(mockDeliveryUriBuilder).setOrderId(ORDER_ID);
				will(returnValue(mockDeliveryUriBuilder));
				allowing(mockDeliveryUriBuilder).setScope(SCOPE);
				will(returnValue(mockDeliveryUriBuilder));

				allowing(mockDeliveryUriBuilder).build();
				will(returnValue(deliveryUri));
			}
		});
	}

	private ReadShippingOptionInfoCommandImpl createCommand() {
		ReadShippingOptionInfoCommandImpl command = new ReadShippingOptionInfoCommandImpl(RESOURCE_SERVER, mockDeliveryUriBuilderFactory,
				mockShippingOptionLookup, mockShipmentDetailsLookup);
		ReadShippingOptionInfoCommandImpl.BuilderImpl builder = new ReadShippingOptionInfoCommandImpl.BuilderImpl(command);

		return (ReadShippingOptionInfoCommandImpl) builder
				.setScope(SCOPE)
				.setShipmentDetailsId(SHIPMENTDETAILS_ID)
				.build();
	}
}
