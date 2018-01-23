/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.addresses.AddressesMediaTypes;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.shipmentdetails.DestinationInfo;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.DestinationInfoLookup;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.common.selector.SelectorRepresentationRels;
import com.elasticpath.rest.schema.uri.AddressUriBuilder;
import com.elasticpath.rest.schema.uri.AddressUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Tests the {@link ReadShippingAddressChoiceResourceOperator}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ReadShippingAddressChoiceResourceOperatorTest {

	private static final String NOT_SELECTED_ADDRESS_ID = "not_selected_address_id";
	private static final String SELECTED_ADDRESS_ID = "address_id";
	private static final String SHIPMENT_ID = "shipment_id";
	private static final String RESOURCE = "shipmentdetails";
	private static final String SCOPE = "scope";
	private static final String SELECTOR_URI = URIUtil.format(RESOURCE, SCOPE, SHIPMENT_ID, DestinationInfo.URI_PART, Selector.URI_PART);
	private static final String SHIPPING_ADDRESS_URI = "/shipping_address_uri";

	private static final ResourceState<AddressEntity> ADDRESS = ResourceState.Builder.create(AddressEntity.builder().build())
			.withSelf(SelfFactory.createSelf(SHIPPING_ADDRESS_URI, AddressesMediaTypes.ADDRESS.id()))
			.build();
	private static final ResourceOperation READ_OP = TestResourceOperationFactory.createRead(SELECTOR_URI);

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private DestinationInfoLookup mockDestinationInfoLookup;
	private ReadShippingAddressChoiceResourceOperator readShippingAddressChoiceResourceOperator;

	@Before
	public void setUp() {
		readShippingAddressChoiceResourceOperator = new ReadShippingAddressChoiceResourceOperator(RESOURCE, mockDestinationInfoLookup,
				new TestAddressUriBuilderFactory());
	}


	@Test
	public void testReadShippingAddressChoiceWhenChoiceIsNotSelected() {
		when(mockDestinationInfoLookup.findSelectedAddressIdForShipment(SCOPE, SHIPMENT_ID))
				.thenReturn(ExecutionResultFactory.<String>createNotFound());

		OperationResult result = readShippingAddressChoiceResourceOperator.processReadShipmentDestinationInfoChoice(SCOPE,
				SHIPMENT_ID, ADDRESS, READ_OP);

		assertTrue(result.isSuccessful());

		String expectedSelfUri = URIUtil.format(SELECTOR_URI, SHIPPING_ADDRESS_URI);
		ResourceLink expectedSelectActionLink = ResourceLinkFactory.createUriRel(expectedSelfUri,
				SelectorRepresentationRels.SELECT_ACTION);

		ResourceLink expectedAddressLink = ResourceLinkFactory.createNoRev(URIUtil.format(SHIPPING_ADDRESS_URI), AddressesMediaTypes.ADDRESS.id(),
				SelectorRepresentationRels.DESCRIPTION);

		ResourceLink expectedDestinationInfoSelectorLink = ResourceLinkFactory.createNoRev(SELECTOR_URI,
				ControlsMediaTypes.SELECTOR.id(), SelectorRepresentationRels.SELECTOR);

		Self expectedSelfLink = SelfFactory.createSelf(expectedSelfUri);

		assertThat(result.getResourceState().getLinks(), Matchers.containsInAnyOrder(
				expectedAddressLink, expectedSelectActionLink, expectedDestinationInfoSelectorLink));
		assertEquals(result.getResourceState().getSelf(), expectedSelfLink);
	}


	@Test
	public void testReadShippingAddressChoice() {

		when(mockDestinationInfoLookup.findSelectedAddressIdForShipment(SCOPE, SHIPMENT_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(NOT_SELECTED_ADDRESS_ID));

		OperationResult result = readShippingAddressChoiceResourceOperator.processReadShipmentDestinationInfoChoice(SCOPE,
				SHIPMENT_ID, ADDRESS, READ_OP);

		assertTrue(result.isSuccessful());

		String expectedSelfUri = URIUtil.format(SELECTOR_URI, SHIPPING_ADDRESS_URI);
		ResourceLink expectedSelectActionLink = ResourceLinkFactory.createUriRel(expectedSelfUri,
				SelectorRepresentationRels.SELECT_ACTION);

		ResourceLink expectedAddressLink = ResourceLinkFactory.createNoRev(URIUtil.format(SHIPPING_ADDRESS_URI), AddressesMediaTypes.ADDRESS.id(),
				SelectorRepresentationRels.DESCRIPTION);

		ResourceLink expectedDestinationInfoSelectorLink = ResourceLinkFactory.createNoRev(SELECTOR_URI,
				ControlsMediaTypes.SELECTOR.id(), SelectorRepresentationRels.SELECTOR);

		Self expectedSelfLink = SelfFactory.createSelf(expectedSelfUri);

		assertThat(result.getResourceState().getLinks(), Matchers.containsInAnyOrder(
				expectedAddressLink, expectedSelectActionLink, expectedDestinationInfoSelectorLink));
		assertEquals(expectedSelfLink, result.getResourceState().getSelf());
	}


	/**
	 * A test factory for the expected address uri.
	 */
	private class TestAddressUriBuilderFactory implements AddressUriBuilderFactory, AddressUriBuilder {

		private static final String DIFF_ADDRESS = "diff_address";
		private String addressId;

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
			this.addressId = addressId;
			return this;
		}

		@Override
		public String build() {
			if (addressId.equals(SELECTED_ADDRESS_ID)) {
				return SHIPPING_ADDRESS_URI;
			}
			return DIFF_ADDRESS;
		}
	}
}
