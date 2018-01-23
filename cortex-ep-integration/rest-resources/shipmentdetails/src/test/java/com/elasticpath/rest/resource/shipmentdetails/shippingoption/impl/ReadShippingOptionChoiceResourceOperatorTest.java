/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.hamcrest.Matchers;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.shipmentdetails.ShipmentdetailsMediaTypes;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.shipmentdetails.ShippingOptionInfo;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.ShippingOptionLookup;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.common.selector.SelectorRepresentationRels;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests the {@link com.elasticpath.rest.resource.shipmentdetails.shippingoption.impl.ReadShippingOptionChoiceResourceOperator}.
 */
public final class ReadShippingOptionChoiceResourceOperatorTest {

	private static final String NOT_FOUND_ERROR_MESSAGE = "not found";
	private static final String RESOURCE_SERVER_NAME = "RESOURCE SERVER NAME";
	private static final String SCOPE = "SCOPE";
	private static final String SHIPMENTDETAILS_ID = "shipment details id";
	private static final String SHIPPING_OPTION_URI = "/shipping/option/uri";
	private static final String SHIPPING_OPTION_ID = "shipping option id";
	private static final ResourceState<ShippingOptionEntity> SHIPPING_OPTION = ResourceState.Builder
			.create(ShippingOptionEntity.builder()
					.withShippingOptionId(SHIPPING_OPTION_ID)
					.build())
			.withSelf(SelfFactory.createSelf(SHIPPING_OPTION_URI))
			.build();
	private static final ResourceOperation READ_OP = TestResourceOperationFactory.createRead("/uri");

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();
	@Mock
	private ShippingOptionLookup mockShippingOptionLookup;
	private ReadShippingOptionChoiceResourceOperator classUnderTest;

	@Before
	public void setUp() {
		classUnderTest = new ReadShippingOptionChoiceResourceOperator(RESOURCE_SERVER_NAME, mockShippingOptionLookup);
	}


	@Test
	public void testRead() {
		context.checking(new Expectations() {
			{
				allowing(mockShippingOptionLookup).getSelectedShipmentOptionIdForShipmentDetails(SCOPE, SHIPMENTDETAILS_ID);
				will(returnValue(ExecutionResultFactory.createReadOK("other shipping option id")));
			}
		});

		OperationResult result = classUnderTest.processReadShippingOptionInfoChoice(SCOPE, SHIPMENTDETAILS_ID, SHIPPING_OPTION, READ_OP);

		assertTrue("result should return successful", result.isSuccessful());

		String expectedSelfUri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, SHIPMENTDETAILS_ID, ShippingOptionInfo.URI_PART, Selector.URI_PART,
				SHIPPING_OPTION_URI);

		assertEquals("wrong self uri", expectedSelfUri, ResourceStateUtil.getSelfUri(result.getResourceState()));
		ResourceLink expectedDescriptionLink = ResourceLinkFactory.createNoRev(SHIPPING_OPTION_URI,
				ShipmentdetailsMediaTypes.SHIPPING_OPTION.id(),
				SelectorRepresentationRels.DESCRIPTION);

		ResourceLink expectedSelectActionLink = ResourceLinkFactory.createUriRel(expectedSelfUri, SelectorRepresentationRels.SELECT_ACTION);
		String expectedSelectorUri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, SHIPMENTDETAILS_ID, ShippingOptionInfo.URI_PART, Selector.URI_PART);
		ResourceLink expectedSelectorLink = ResourceLinkFactory.createNoRev(expectedSelectorUri,
				ControlsMediaTypes.SELECTOR.id(),
				SelectorRepresentationRels.SELECTOR);

		assertThat("wrong links", result.getResourceState().getLinks(),
				Matchers.containsInAnyOrder(expectedDescriptionLink, expectedSelectActionLink, expectedSelectorLink));
	}

	/**
	 * Test read chosen will have no select action.
	 */
	@Test
	public void testReadChosenWillHaveNoSelectAction() {
		context.checking(new Expectations() {
			{
				allowing(mockShippingOptionLookup).getSelectedShipmentOptionIdForShipmentDetails(SCOPE, SHIPMENTDETAILS_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(SHIPPING_OPTION_ID)));
			}
		});

		OperationResult result = classUnderTest.processReadShippingOptionInfoChoice(SCOPE, SHIPMENTDETAILS_ID, SHIPPING_OPTION, READ_OP);

		assertTrue("result should return successful", result.isSuccessful());

		ResourceLink expectedDescriptionLink = ResourceLinkFactory.createNoRev(SHIPPING_OPTION_URI,
				ShipmentdetailsMediaTypes.SHIPPING_OPTION.id(),
				SelectorRepresentationRels.DESCRIPTION);

		String expectedSelectorUri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, SHIPMENTDETAILS_ID, ShippingOptionInfo.URI_PART, Selector.URI_PART);
		ResourceLink expectedSelectorLink = ResourceLinkFactory.createNoRev(expectedSelectorUri,
				ControlsMediaTypes.SELECTOR.id(),
				SelectorRepresentationRels.SELECTOR);

		assertThat("wrong links", result.getResourceState().getLinks(), Matchers.contains(expectedDescriptionLink, expectedSelectorLink));
	}


	/**
	 * Test read choice where no shipping option selected.
	 */
	@Test
	public void testReadChoiceWhereNoShippingOptionSelected() {
		context.checking(new Expectations() {
			{
				allowing(mockShippingOptionLookup).getSelectedShipmentOptionIdForShipmentDetails(SCOPE, SHIPMENTDETAILS_ID);
				will(returnValue(ExecutionResultFactory.createNotFound(NOT_FOUND_ERROR_MESSAGE)));
			}
		});

		OperationResult result = classUnderTest.processReadShippingOptionInfoChoice(SCOPE, SHIPMENTDETAILS_ID, SHIPPING_OPTION, READ_OP);

		assertTrue("result should return successful", result.isSuccessful());

		String expectedSelfUri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, SHIPMENTDETAILS_ID, ShippingOptionInfo.URI_PART, Selector.URI_PART,
				SHIPPING_OPTION_URI);

		assertEquals("wrong self uri", expectedSelfUri, ResourceStateUtil.getSelfUri(result.getResourceState()));
		ResourceLink expectedDescriptionLink = ResourceLinkFactory.createNoRev(SHIPPING_OPTION_URI,
				ShipmentdetailsMediaTypes.SHIPPING_OPTION.id(),
				SelectorRepresentationRels.DESCRIPTION);

		ResourceLink expectedSelectActionLink = ResourceLinkFactory.createUriRel(expectedSelfUri, SelectorRepresentationRels.SELECT_ACTION);
		String expectedSelectorUri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, SHIPMENTDETAILS_ID, ShippingOptionInfo.URI_PART, Selector.URI_PART);
		ResourceLink expectedSelectorLink = ResourceLinkFactory.createNoRev(expectedSelectorUri,
				ControlsMediaTypes.SELECTOR.id(),
				SelectorRepresentationRels.SELECTOR);

		assertThat("wrong links", result.getResourceState().getLinks(),
				Matchers.containsInAnyOrder(expectedDescriptionLink, expectedSelectActionLink, expectedSelectorLink));
	}
}
