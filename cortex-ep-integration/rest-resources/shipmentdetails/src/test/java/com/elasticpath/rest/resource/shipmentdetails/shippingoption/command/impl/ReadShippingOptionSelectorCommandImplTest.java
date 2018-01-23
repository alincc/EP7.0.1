/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.command.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.controls.SelectorEntity;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.shipmentdetails.ShippingOption;
import com.elasticpath.rest.resource.shipmentdetails.ShippingOptionInfo;
import com.elasticpath.rest.resource.shipmentdetails.rel.ShipmentDetailsRels;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.ShippingOptionLookup;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.command.ReadShippingOptionSelectorCommand;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.command.impl.ReadShippingOptionSelectorCommandImpl.BuilderImpl;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.common.selector.SelectorRepresentationRels;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests for {@link ReadShippingOptionSelectorCommandImpl}.
 */
public final class ReadShippingOptionSelectorCommandImplTest {

	private static final String NOT_FOUND = "not Found";
	private static final String SHIPPING_OPTION_ID = "OPTION_ID";
	private static final String SHIPMENT_DETAILS_ID = "SHIPMENT DETAILS ID";
	private static final String SCOPE = "SCOPE";
	private static final String RESOURCE_SERVER_NAME = "RESOURCE SERVER NAME";
	private static final String CHOSEN_ID = "chosen Id";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	@Mock
	private ShippingOptionLookup mockShippingOptionLookup;


	/**
	 * Test read selector.
	 */
	@Test
	public void testReadSelector() {
		ReadShippingOptionSelectorCommand command = createCommand();

		context.checking(new Expectations() {
			{
				allowing(mockShippingOptionLookup).getSelectedShipmentOptionIdForShipmentDetails(SCOPE, SHIPMENT_DETAILS_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(CHOSEN_ID)));

				allowing(mockShippingOptionLookup).getShippingOptionIdsForShipmentDetail(SCOPE, SHIPMENT_DETAILS_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(Arrays.asList(SHIPPING_OPTION_ID, CHOSEN_ID))));
			}
		});

		ExecutionResult<ResourceState<SelectorEntity>> result = command.execute();

		assertTrue("Result should be successful", result.isSuccessful());
		String expectedSelectorUri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, SHIPMENT_DETAILS_ID, ShippingOptionInfo.URI_PART, Selector.URI_PART);
		assertEquals("Wrong Selector Uri", expectedSelectorUri, ResourceStateUtil.getSelfUri(result.getData()));

		String expectedChoiceShippingOptionUri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, SHIPMENT_DETAILS_ID,
				ShippingOption.URI_PART, SHIPPING_OPTION_ID);
		String expectedChoiceUri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, SHIPMENT_DETAILS_ID, ShippingOptionInfo.URI_PART,
				Selector.URI_PART, expectedChoiceShippingOptionUri);

		String expectedChosenShippingOptionUri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, SHIPMENT_DETAILS_ID,
				ShippingOption.URI_PART, CHOSEN_ID);
		String expectedChosenUri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, SHIPMENT_DETAILS_ID, ShippingOptionInfo.URI_PART,
				Selector.URI_PART, expectedChosenShippingOptionUri);

		ResourceLink expectedChoiceLink = ResourceLinkFactory.create(expectedChoiceUri, CollectionsMediaTypes.LINKS.id(),
				SelectorRepresentationRels.CHOICE, SelectorRepresentationRels.SELECTOR);

		ResourceLink expectedChosenLink = ResourceLinkFactory.create(expectedChosenUri, CollectionsMediaTypes.LINKS.id(),
				SelectorRepresentationRels.CHOSEN, SelectorRepresentationRels.SELECTOR);


		String expectedInfoUri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, SHIPMENT_DETAILS_ID, ShippingOptionInfo.PATH_PART);
		ResourceLink expectedInfoLink = ResourceLinkFactory.create(expectedInfoUri, ControlsMediaTypes.INFO.id(),
				ShipmentDetailsRels.SHIPPING_OPTION_INFO_REL, SelectorRepresentationRels.SELECTOR);

		assertThat("wrong links", result.getData().getLinks(), containsInAnyOrder(expectedInfoLink, expectedChoiceLink, expectedChosenLink));
	}

	/**
	 * Test read selector without chosen.
	 */
	@Test
	public void testReadSelectorWithoutChosen() {
		ReadShippingOptionSelectorCommand command = createCommand();

		context.checking(new Expectations() {
			{
				allowing(mockShippingOptionLookup).getSelectedShipmentOptionIdForShipmentDetails(SCOPE, SHIPMENT_DETAILS_ID);
				will(returnValue(ExecutionResultFactory.createNotFound(NOT_FOUND)));

				allowing(mockShippingOptionLookup).getShippingOptionIdsForShipmentDetail(SCOPE, SHIPMENT_DETAILS_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(Arrays.asList(SHIPPING_OPTION_ID, CHOSEN_ID))));
			}
		});

		ExecutionResult<ResourceState<SelectorEntity>> result = command.execute();

		assertTrue("Result should be successful", result.isSuccessful());

		String expectedChoiceShippingOptionUri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, SHIPMENT_DETAILS_ID,
				ShippingOption.URI_PART, SHIPPING_OPTION_ID);
		String expectedChoiceUri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, SHIPMENT_DETAILS_ID, ShippingOptionInfo.URI_PART,
				Selector.URI_PART, expectedChoiceShippingOptionUri);

		String expectedSecondChoiceShippingOptionUri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, SHIPMENT_DETAILS_ID,
				ShippingOption.URI_PART, CHOSEN_ID);
		String expectedSecondChoiceUri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, SHIPMENT_DETAILS_ID, ShippingOptionInfo.URI_PART,
				Selector.URI_PART, expectedSecondChoiceShippingOptionUri);

		ResourceLink expectedChoiceLink = ResourceLinkFactory.create(expectedChoiceUri, CollectionsMediaTypes.LINKS.id(),
				SelectorRepresentationRels.CHOICE, SelectorRepresentationRels.SELECTOR);

		ResourceLink expectedSecondChoiceLink = ResourceLinkFactory.create(expectedSecondChoiceUri, CollectionsMediaTypes.LINKS.id(),
				SelectorRepresentationRels.CHOICE, SelectorRepresentationRels.SELECTOR);

		String expectedInfoUri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, SHIPMENT_DETAILS_ID, ShippingOptionInfo.PATH_PART);
		ResourceLink expectedInfoLink = ResourceLinkFactory.create(expectedInfoUri, ControlsMediaTypes.INFO.id(),
				ShipmentDetailsRels.SHIPPING_OPTION_INFO_REL, SelectorRepresentationRels.SELECTOR);

		assertThat("wrong links", result.getData().getLinks(), containsInAnyOrder(expectedInfoLink, expectedChoiceLink, expectedSecondChoiceLink));
	}

	/**
	 * Test read selector with error getting shipping options.
	 */
	@Test
	public void testReadSelectorWithErrorGettingShippingOptions() {
		ReadShippingOptionSelectorCommand command = createCommand();

		context.checking(new Expectations() {
			{
				allowing(mockShippingOptionLookup).getShippingOptionIdsForShipmentDetail(SCOPE, SHIPMENT_DETAILS_ID);
				will(returnValue(ExecutionResultFactory.createNotFound(NOT_FOUND)));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		command.execute();
	}


	private ReadShippingOptionSelectorCommand createCommand() {
		ReadShippingOptionSelectorCommandImpl command = new ReadShippingOptionSelectorCommandImpl(RESOURCE_SERVER_NAME, mockShippingOptionLookup);
		ReadShippingOptionSelectorCommandImpl.BuilderImpl builder = new BuilderImpl(command);

		return builder.setScope(SCOPE)
				.setShipmentDetailsId(SHIPMENT_DETAILS_ID)
				.build();
	}
}
