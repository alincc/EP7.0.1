/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.command.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.common.selector.SelectorRepresentationRels;
import com.elasticpath.rest.definition.addresses.AddressesMediaTypes;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.shipmentdetails.DestinationInfo;
import com.elasticpath.rest.resource.shipmentdetails.ShipmentDetail;
import com.elasticpath.rest.resource.shipmentdetails.ShipmentDetailsLookup;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.DestinationInfoLookup;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.command.ReadDestinationInfoCommand;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.constants.DestinationInfoConstants;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.rel.DestinationInfoRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.AddressFormUriBuilder;
import com.elasticpath.rest.schema.uri.AddressFormUriBuilderFactory;
import com.elasticpath.rest.schema.uri.AddressUriBuilder;
import com.elasticpath.rest.schema.uri.AddressUriBuilderFactory;
import com.elasticpath.rest.schema.uri.DeliveryUriBuilder;
import com.elasticpath.rest.schema.uri.DeliveryUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Test class for ReadDestinationInfoCommand.
 */
public final class ReadDestinationInfoCommandImplTest {

	private static final String DELIVERY_ID = "delivery_id";
	private static final String ORDER_ID = "order_id";
	private static final String ADDRESS_ID = "address_id";
	private static final String SHIPMENT_ID = "shipment_id";
	private static final String SCOPE = "scope";
	private static final String SHIPMENTDETAILS = "shpimentdetails";
	private static final String ADDRESS_URI = "/addressuri";
	private static final String DELIVERY_URI = "/deliveryuri";
	private static final String SHIPMENT_URI = URIUtil.format(SHIPMENTDETAILS, SCOPE, SHIPMENT_ID);
	private static final String ADDRESS_FORM_URI = "/addressFormUri";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final DestinationInfoLookup mockDestinationInfoLookup = context.mock(DestinationInfoLookup.class);
	private final ShipmentDetailsLookup mockShipmentDetailsLookup = context.mock(ShipmentDetailsLookup.class);


	/**
	 * Test read destination info.
	 */
	@Test
	public void testReadDestinationInfo() {
		ReadDestinationInfoCommand command = createReadDestinationInfoCommand();
		final ShipmentDetail shipmentDetail = createShipmentDetail();

		context.checking(new Expectations() {
			{
				allowing(mockDestinationInfoLookup).findSelectedAddressIdForShipment(SCOPE, SHIPMENT_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(ADDRESS_ID)));

				allowing(mockShipmentDetailsLookup).getShipmentDetail(SCOPE, SHIPMENT_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(shipmentDetail)));
			}
		});

		ExecutionResult<ResourceState<InfoEntity>> result = command.execute();
		assertTrue(result.isSuccessful());

		ResourceState<InfoEntity> representation = result.getData();

		ResourceLink expectedSelectedAddressLink = ResourceLinkFactory.createNoRev(ADDRESS_URI, AddressesMediaTypes.ADDRESS.id(),
				DestinationInfoRepresentationRels.DESTINATION_REL);

		final int expectedLinkSize = 4;
		assertResourceState(representation)
				.self(SelfFactory.createSelf(URIUtil.format(SHIPMENT_URI, DestinationInfo.URI_PART)))
				.linkCount(expectedLinkSize)
				.containsLink(expectedSelectedAddressLink)
				.containsLink(createExpectedAddressInfoSelectorLink())
				.containsLink(createExpectedDeliveriesLink())
				.containsLink(createExpectedAddressFormLink());


		assertEquals(DestinationInfoConstants.DESTINATION_INFO_NAME, representation.getEntity().getName());
	}


	/**
	 * Test read destination info when no address already selected.
	 */
	@Test
	public void testReadDestinationInfoWhenNoAddressAlreadySelected() {
		ReadDestinationInfoCommand command = createReadDestinationInfoCommand();
		final ShipmentDetail shipmentDetail = createShipmentDetail();

		context.checking(new Expectations() {
			{
				allowing(mockDestinationInfoLookup).findSelectedAddressIdForShipment(SCOPE, SHIPMENT_ID);
				will(returnValue(ExecutionResultFactory.createNotFound(StringUtils.EMPTY)));

				allowing(mockShipmentDetailsLookup).getShipmentDetail(SCOPE, SHIPMENT_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(shipmentDetail)));
			}
		});

		ExecutionResult<ResourceState<InfoEntity>> result = command.execute();
		assertTrue(result.isSuccessful());

		ResourceState<InfoEntity> representation = result.getData();

		final int expectedLinkSize = 3;
		assertResourceState(representation)
				.self(SelfFactory.createSelf(URIUtil.format(SHIPMENT_URI, DestinationInfo.URI_PART)))
				.linkCount(expectedLinkSize)
				.containsLink(createExpectedAddressInfoSelectorLink())
				.containsLink(createExpectedDeliveriesLink())
				.containsLink(createExpectedAddressFormLink());

		assertEquals(DestinationInfoConstants.DESTINATION_INFO_NAME, representation.getEntity().getName());
	}

	/**
	 * Test read destination info when error on getting shipment detail.
	 */
	@Test
	public void testReadDestinationInfoWhenErrorOnGettingShipmentDetail() {
		ReadDestinationInfoCommand command = createReadDestinationInfoCommand();

		context.checking(new Expectations() {
			{
				allowing(mockDestinationInfoLookup).findSelectedAddressIdForShipment(SCOPE, SHIPMENT_ID);
				will(returnValue(ExecutionResultFactory.createNotFound(StringUtils.EMPTY)));

				allowing(mockShipmentDetailsLookup).getShipmentDetail(SCOPE, SHIPMENT_ID);
				will(returnValue(ExecutionResultFactory.createServerError(StringUtils.EMPTY)));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		command.execute();
	}

	/**
	 * Test read destination info when error on getting selected address.
	 */
	@Test
	public void testReadDestinationInfoWhenErrorOnGettingSelectedAddess() {
		ReadDestinationInfoCommand command = createReadDestinationInfoCommand();

		context.checking(new Expectations() {
			{
				allowing(mockDestinationInfoLookup).findSelectedAddressIdForShipment(SCOPE, SHIPMENT_ID);
				will(returnValue(ExecutionResultFactory.createServerError(StringUtils.EMPTY)));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		command.execute();
	}

	private ResourceLink createExpectedAddressInfoSelectorLink() {
		String addressInfoSelectorUri = URIUtil.format(SHIPMENT_URI, DestinationInfo.URI_PART, Selector.URI_PART);
		return ResourceLinkFactory.create(addressInfoSelectorUri, ControlsMediaTypes.SELECTOR.id(), SelectorRepresentationRels.SELECTOR,
				DestinationInfoRepresentationRels.DESTINATION_INFO_REL);
	}

	private ResourceLink createExpectedDeliveriesLink() {
		return ResourceLinkFactory.create(DELIVERY_URI, OrdersMediaTypes.DELIVERY.id(), DestinationInfoRepresentationRels.DELIVERY_REL,
				DestinationInfoRepresentationRels.DESTINATION_INFO_REL);
	}

	private ShipmentDetail createShipmentDetail() {
		return new ShipmentDetail()
				.setOrderId(ORDER_ID)
				.setDeliveryId(DELIVERY_ID);
	}

	private ResourceLink createExpectedAddressFormLink() {
		return ResourceLinkFactory.createNoRev(ADDRESS_FORM_URI, AddressesMediaTypes.ADDRESS.id(),
				DestinationInfoRepresentationRels.ADDRESS_FORM_REL);
	}

	private ReadDestinationInfoCommand createReadDestinationInfoCommand() {

		ReadDestinationInfoCommandImpl command = new ReadDestinationInfoCommandImpl(SHIPMENTDETAILS,
				mockDestinationInfoLookup, mockShipmentDetailsLookup, new TestProfileAddressUriBuilderFactory(),
				new TestAddressFormUriBuilderFactory(), new TestDeliveryUriBuilderFactory());

		ReadDestinationInfoCommand.Builder builder = new ReadDestinationInfoCommandImpl.BuilderImpl(command);

		return builder.setScope(SCOPE)
				.setShipmentDetailsId(SHIPMENT_ID)
				.build();
	}

	/**
	 * A test factory for the expected address uri.
	 */
	private static class TestProfileAddressUriBuilderFactory implements AddressUriBuilderFactory, AddressUriBuilder {

		@Override
		public AddressUriBuilder get() {
			return this;
		}

		@Override
		public AddressUriBuilder setScope(final String scope) {
			return this;
		}

		@Override
		public AddressUriBuilder setAddressId(final String addressId) {
			return this;
		}

		@Override
		public String build() {
			return ADDRESS_URI;
		}
	}

	/**
	 * A test factory for the expected delivery uri.
	 */
	private static class TestDeliveryUriBuilderFactory implements DeliveryUriBuilderFactory, DeliveryUriBuilder {

		@Override
		public DeliveryUriBuilder get() {
			return this;
		}

		@Override
		public DeliveryUriBuilder setScope(final String scope) {
			return this;
		}

		@Override
		public DeliveryUriBuilder setOrderId(final String orderId) {
			return this;
		}

		@Override
		public DeliveryUriBuilder setDeliveryId(final String deliveryId) {
			return this;
		}

		@Override
		public String build() {
			return DELIVERY_URI;
		}
	}

	/**
	 * A test factory for the expected  address form uri.
	 */
	private static class TestAddressFormUriBuilderFactory implements AddressFormUriBuilderFactory, AddressFormUriBuilder {


		@Override
		public AddressFormUriBuilder get() {
			return this;
		}

		@Override
		public AddressFormUriBuilder setScope(final String scope) {
			return this;
		}

		@Override
		public String build() {
			return ADDRESS_FORM_URI;
		}
	}
}
