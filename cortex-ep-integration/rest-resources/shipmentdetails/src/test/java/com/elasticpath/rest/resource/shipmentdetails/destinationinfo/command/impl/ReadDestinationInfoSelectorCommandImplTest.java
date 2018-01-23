/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.command.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.command.read.ReadResourceCommandBuilderProvider;
import com.elasticpath.rest.command.read.TestReadResourceCommandBuilderProvider;
import com.elasticpath.rest.definition.addresses.AddressesMediaTypes;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.controls.SelectorEntity;
import com.elasticpath.rest.rel.ListElementRels;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.shipmentdetails.DestinationInfo;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.DestinationInfoLookup;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.command.ReadDestinationInfoSelectorCommand;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.constants.DestinationInfoConstants;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.rel.DestinationInfoRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.common.selector.SelectorRepresentationRels;
import com.elasticpath.rest.schema.uri.AddressUriBuilder;
import com.elasticpath.rest.schema.uri.AddressUriBuilderFactory;
import com.elasticpath.rest.schema.uri.ShippingAddressListUriBuilder;
import com.elasticpath.rest.schema.uri.ShippingAddressListUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Tests for {@link ReadDestinationInfoSelectorCommandImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ReadDestinationInfoSelectorCommandImplTest {

	private static final String SHIPMENT_ID = "shipment_id";
	private static final String RESOURCE = "shipmentdetails";
	private static final String SCOPE = "scope";
	private static final String SHIPPING_ADDRESS_URI = "/shippingaddress";
	private static final String ADDRESS_URI = "/addressuri";
	private static final String SELECTED_ADDRESS_ID = "selected_address_id";
	private static final String SELECTED_ADDRESS_URI = "/selectedaddressuri";
	private static final String DESTINATION_INFO_URI = URIUtil.format(RESOURCE, SCOPE, SHIPMENT_ID, DestinationInfo.URI_PART);

	@Mock
	private DestinationInfoLookup mockDestinationInfoLookup;


	/**
	 * Test read destination info selector.
	 */
	@Test
	public void testReadDestinationInfoSelector() {

		ResourceState<LinksEntity> linksRepresentation = createAddressLinksRepresentation();
		ExecutionResult<ResourceState<LinksEntity>> addressesResult = ExecutionResultFactory.createReadOK(linksRepresentation);
		ReadDestinationInfoSelectorCommand command = createReadDestinationInfoSelectorCommand(addressesResult);

		when(mockDestinationInfoLookup.findSelectedAddressIdForShipment(SCOPE, SHIPMENT_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(SELECTED_ADDRESS_ID));

		ExecutionResult<ResourceState<SelectorEntity>> result = command.execute();

		assertTrue(result.isSuccessful());
		ResourceState<SelectorEntity> selectorRepresentation = result.getData();
		assertEquals(DestinationInfoConstants.DESTINATION_SELECTOR_NAME, selectorRepresentation.getEntity().getName());

		String selectorUri = URIUtil.format(DESTINATION_INFO_URI, Selector.URI_PART);
		Self expectedSelf = SelfFactory.createSelf(selectorUri);
		assertEquals(expectedSelf, selectorRepresentation.getSelf());

		ResourceLink expectedChoiceLink = createChoiceLink(URIUtil.format(selectorUri, ADDRESS_URI));
		ResourceLink expectedChosenLink = createChosenLink(URIUtil.format(selectorUri, SELECTED_ADDRESS_URI));
		ResourceLink expectedDestinationInfoLink = createDestinationInfoLink();

		assertTrue(CollectionUtil.containsOnly(selectorRepresentation.getLinks(),
				Arrays.asList(expectedChoiceLink, expectedChosenLink, expectedDestinationInfoLink)));
	}

	/**
	 * Test read destination info selector when selected address not found.
	 */
	@Test
	public void testReadDestinationInfoSelectorWhenSelectedAddressNotFound() {

		ResourceState<LinksEntity> linksRepresentation = createAddressLinksRepresentation();
		ExecutionResult<ResourceState<LinksEntity>> addressesResult = ExecutionResultFactory.createReadOK(linksRepresentation);
		ReadDestinationInfoSelectorCommand command = createReadDestinationInfoSelectorCommand(addressesResult);

		when(mockDestinationInfoLookup.findSelectedAddressIdForShipment(SCOPE, SHIPMENT_ID))
				.thenReturn(ExecutionResultFactory.<String>createNotFound(StringUtils.EMPTY));

		ExecutionResult<ResourceState<SelectorEntity>> result = command.execute();

		assertTrue(result.isSuccessful());
		ResourceState<SelectorEntity> selectorRepresentation = result.getData();
		assertEquals(DestinationInfoConstants.DESTINATION_SELECTOR_NAME, selectorRepresentation.getEntity().getName());

		String selectorUri = URIUtil.format(DESTINATION_INFO_URI, Selector.URI_PART);
		Self expectedSelf = SelfFactory.createSelf(selectorUri);
		assertEquals(expectedSelf, selectorRepresentation.getSelf());

		ResourceLink expectedChoiceLink = createChoiceLink(URIUtil.format(selectorUri, ADDRESS_URI));

		ResourceLink expectedChoiceLink2 = createChoiceLink(URIUtil.format(selectorUri, SELECTED_ADDRESS_URI));

		ResourceLink expectedDestinationInfoLink = createDestinationInfoLink();

		assertThat(selectorRepresentation.getLinks(), Matchers.contains(expectedChoiceLink, expectedChoiceLink2, expectedDestinationInfoLink));
	}

	private ResourceLink createDestinationInfoLink() {
		return ResourceLinkFactory.create(DESTINATION_INFO_URI, ControlsMediaTypes.INFO.id(),
				DestinationInfoRepresentationRels.DESTINATION_INFO_REL, SelectorRepresentationRels.SELECTOR);
	}

	private ResourceLink createChosenLink(final String choiceUri) {
		return ResourceLinkFactory.create(choiceUri, CollectionsMediaTypes.LINKS.id(),
				SelectorRepresentationRels.CHOSEN, SelectorRepresentationRels.SELECTOR);
	}

	private ResourceLink createChoiceLink(final String choiceUri) {
		return ResourceLinkFactory.create(choiceUri, CollectionsMediaTypes.LINKS.id(),
				SelectorRepresentationRels.CHOICE, SelectorRepresentationRels.SELECTOR);
	}

	private ResourceState<LinksEntity> createAddressLinksRepresentation() {
		return ResourceState.Builder.create(LinksEntity.builder().build())
				.addingLinks(
						ResourceLinkFactory.createNoRev(ADDRESS_URI, AddressesMediaTypes.ADDRESS.id(), ListElementRels.ELEMENT),
						ResourceLinkFactory.createNoRev(SELECTED_ADDRESS_URI, AddressesMediaTypes.ADDRESS.id(), ListElementRels.ELEMENT))
				.build();
	}

	private ReadDestinationInfoSelectorCommand createReadDestinationInfoSelectorCommand(
			final ExecutionResult<ResourceState<LinksEntity>> expectedAddressesResult) {

		ReadResourceCommandBuilderProvider rrcProvider =
				TestReadResourceCommandBuilderProvider.mock(expectedAddressesResult);

		ReadDestinationInfoSelectorCommandImpl command =
				new ReadDestinationInfoSelectorCommandImpl(RESOURCE,
						rrcProvider,
						new TestShippingAddressListUriBuilderFactory(),
						new TestAddressUriBuilderFactory(), mockDestinationInfoLookup);

		ReadDestinationInfoSelectorCommandImpl.BuilderImpl builder =
				new ReadDestinationInfoSelectorCommandImpl.BuilderImpl(command);

		return builder.setScope(SCOPE)
				.setShipmentDetailsId(SHIPMENT_ID)
				.build();
	}


	/**
	 * A test factory for the expected address uri.
	 */
	private class TestAddressUriBuilderFactory implements AddressUriBuilderFactory, AddressUriBuilder {

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
			return SELECTED_ADDRESS_URI;
		}
	}

	/**
	 * A test factory for the expected shipping address uri.
	 */
	private class TestShippingAddressListUriBuilderFactory
			implements ShippingAddressListUriBuilderFactory, ShippingAddressListUriBuilder {

		@Override
		public ShippingAddressListUriBuilder get() {
			return this;
		}

		@Override
		public ShippingAddressListUriBuilder setScope(final String scope) {
			return this;
		}

		@Override
		public String build() {
			return SHIPPING_ADDRESS_URI;
		}
	}
}
