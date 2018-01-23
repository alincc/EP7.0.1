/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.resource.purchases.constants.PurchaseResourceConstants;
import com.elasticpath.rest.resource.purchases.lineitems.PurchaseLineItemOptionsLookup;
import com.elasticpath.rest.resource.purchases.lineitems.rel.PurchaseLineItemsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests the {@link com.elasticpath.rest.resource.purchases.lineitems.impl.PurchaseLineItemOptionsResourceOperator}.
 */
public final class PurchaseLineItemOptionsResourceOperatorTest {

	private static final String PURCHASE_ID = "testPurchaseId";
	private static final String LINE_ITEM_ID = "testLineItemId";
	private static final String SCOPE = "testScope";
	private static final String PURCHASE_LINE_ITEM_URI = "/testLineItemUri";
	private static final String OPTION_ID = "optionId";
	private static final ResourceOperation READ = TestResourceOperationFactory.createRead(PURCHASE_LINE_ITEM_URI);

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final PurchaseLineItemOptionsLookup mockPurchaseLineItemOptionsLookup = context.mock(PurchaseLineItemOptionsLookup.class);

	private PurchaseLineItemOptionsResourceOperator classUnderTest;

	@Before
	public void setUp() {
		classUnderTest = new PurchaseLineItemOptionsResourceOperator(mockPurchaseLineItemOptionsLookup);
	}

	/**
	 * Test read purchase line item options.
	 */
	@Test
	public void testReadPurchaseLineItemOptions() {
		context.checking(new Expectations() {
			{
				allowing(mockPurchaseLineItemOptionsLookup).findOptionIdsForLineItem(SCOPE, PURCHASE_ID, LINE_ITEM_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(Collections.singleton(OPTION_ID))));
			}
		});
		ResourceState<LinksEntity> expectedLinksRepresentation = createExpectedLinksRepresentation(PURCHASE_LINE_ITEM_URI);

		OperationResult result = classUnderTest.processReadOptions(createPurchaseLineItemRepresentation(), READ);

		assertTrue("The result returned should be successful", result.isSuccessful());
		assertEquals("The links representation returned should be the same as the expected links representation",
				expectedLinksRepresentation, result.getResourceState());
	}

	/**
	 * Test read purchase line item options with error.
	 */
	@Test
	public void testReadPurchaseLineItemOptionsWithNotFound() {
		context.checking(new Expectations() {
			{
				allowing(mockPurchaseLineItemOptionsLookup).findOptionIdsForLineItem(SCOPE, PURCHASE_ID, LINE_ITEM_ID);
				will(returnValue(ExecutionResultFactory.createNotFound()));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		OperationResult result = classUnderTest.processReadOptions(createPurchaseLineItemRepresentation(), READ);

		assertTrue("The result returned should be successful", result.isSuccessful());
		assertEquals("The requested item should be not found", ResourceStatus.NOT_FOUND, result.getResourceStatus());
	}

	private ResourceState<PurchaseLineItemEntity> createPurchaseLineItemRepresentation() {
		Self self = SelfFactory.createSelf(PURCHASE_LINE_ITEM_URI, PurchasesMediaTypes.PURCHASE_LINE_ITEM.id());

		return ResourceState.Builder
				.create(PurchaseLineItemEntity.builder()
						.withPurchaseId(PURCHASE_ID)
						.withLineItemId(LINE_ITEM_ID)
						.build())
				.withScope(SCOPE)
				.withSelf(self)
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(PurchaseResourceConstants.MAX_AGE)
						.build())
				.build();
	}

	private ResourceState<LinksEntity> createExpectedLinksRepresentation(final String parentUri) {
		String optionsUri = URIUtil.format(parentUri, Options.URI_PART);

		Self self = SelfFactory.createSelf(optionsUri);

		String optionUri = URIUtil.format(optionsUri, OPTION_ID);
		ResourceLink optionLink = ElementListFactory.createElementOfList(optionUri, PurchasesMediaTypes.PURCHASE_LINE_ITEM_OPTION.id());

		ResourceLink lineItemLink = ResourceLinkFactory.create(parentUri, PurchasesMediaTypes.PURCHASE_LINE_ITEM.id(),
				PurchaseLineItemsResourceRels.PURCHASE_LINEITEM_REL, PurchaseLineItemsResourceRels.OPTIONS_REL);

		return ResourceState.Builder.create(LinksEntity.builder().build())
				.withSelf(self)
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(PurchaseResourceConstants.MAX_AGE)
						.build())
			.addingLinks(lineItemLink, optionLink)
				.build();
	}
}
