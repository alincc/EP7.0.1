/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.availabilities.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.availabilities.AvailabilityEntity;
import com.elasticpath.rest.definition.carts.CartsMediaTypes;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.resource.availabilities.integration.AvailabilityLookupStrategy;
import com.elasticpath.rest.resource.availabilities.rel.AvailabilityRepresentationRels;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests URI-related annotations on {@link AvailabilitiesResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class AvailabilitiesResourceOperatorImplTest {

	private static final String AVAILABILITIES = "availabilities";
	private static final String ITEM_ID = "123";
	private static final String SCOPE = "siu";
	private static final String ITEMS = "items";
	private static final String ITEM_URI = URIUtil.format(ITEMS, SCOPE, ITEM_ID).substring(1);
	private static final String ITEM_AVAIL_URI = URIUtil.format(AVAILABILITIES, ITEM_URI);
	private static final String LINE_ITEM_URI = URIUtil.format(ITEMS, SCOPE, ITEM_ID).substring(1);
	private static final String LINE_ITEM_AVAIL_URI = URIUtil.format(AVAILABILITIES, LINE_ITEM_URI);
	private static final ResourceOperation READ_FOR_ITEM_OP = TestResourceOperationFactory.createRead(ITEM_AVAIL_URI);
	private static final ResourceOperation READ_FOR_LINE_ITEM_OP = TestResourceOperationFactory.createRead(LINE_ITEM_AVAIL_URI);
	private static final AvailabilityEntity AVAILABILITY_ENTITY = AvailabilityEntity.builder().build();

	@Mock
	private AvailabilityLookupStrategy availabilityLookupStrategy;
	private AvailabilitiesResourceOperatorImpl classUnderTest;

	@Before
	public void setUp() {
		classUnderTest = new AvailabilitiesResourceOperatorImpl(AVAILABILITIES, availabilityLookupStrategy);
	}

	@Test
	public void testProcessReadAvailabilityForItemWhenAvailable() {
		Self itemSelf = SelfFactory.createSelf(LINE_ITEM_URI, ItemsMediaTypes.ITEM.id());
		ResourceState<ItemEntity> item = ResourceState.Builder
				.create(ItemEntity.builder()
						.withItemId(ITEM_ID)
						.build())
				.withSelf(itemSelf)
				.withScope(SCOPE)
				.build();
		ResourceState<AvailabilityEntity> expectedAvailability = ResourceState.Builder.create(AVAILABILITY_ENTITY)
				.withSelf(SelfFactory.createSelf(ITEM_AVAIL_URI))
				.addingLinks(ResourceLinkFactory.createFromSelf(itemSelf, AvailabilityRepresentationRels.ITEM_REL,
						AvailabilityRepresentationRels.AVAILABILITY_REV))
				.build();
		when(availabilityLookupStrategy.getAvailability(SCOPE, ITEM_ID)).thenReturn(ExecutionResultFactory.createReadOK(AVAILABILITY_ENTITY));

		OperationResult result = classUnderTest.processReadAvailabilityForItem(item, READ_FOR_ITEM_OP);

		assertEquals(expectedAvailability, result.getResourceState());
	}
	@Test
	public void testProcessReadAvailabilityForLineItemWhenAvailable() {
		Self lineItemSelf = SelfFactory.createSelf(LINE_ITEM_URI, CartsMediaTypes.LINE_ITEM.id());
		ResourceState<LineItemEntity> lineItem = ResourceState.Builder
				.create(LineItemEntity.builder()
						.withItemId(ITEM_ID)
						.build())
				.withSelf(lineItemSelf)
				.withScope(SCOPE)
				.build();
		ResourceState<AvailabilityEntity> expectedAvailability = ResourceState.Builder.create(AVAILABILITY_ENTITY)
				.withSelf(SelfFactory.createSelf(LINE_ITEM_AVAIL_URI))
				.addingLinks(ResourceLinkFactory.createFromSelf(lineItemSelf, AvailabilityRepresentationRels.LINE_ITEM_REL,
						AvailabilityRepresentationRels.AVAILABILITY_REV))
				.build();
		when(availabilityLookupStrategy.getAvailability(SCOPE, ITEM_ID)).thenReturn(ExecutionResultFactory.createReadOK(AVAILABILITY_ENTITY));

		OperationResult result = classUnderTest.processReadAvailabilityForLineItem(lineItem, READ_FOR_LINE_ITEM_OP);

		assertEquals(expectedAvailability, result.getResourceState());
	}
}
