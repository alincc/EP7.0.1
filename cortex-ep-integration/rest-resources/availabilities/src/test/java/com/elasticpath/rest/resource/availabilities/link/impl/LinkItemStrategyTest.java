/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.availabilities.link.impl;

import static com.elasticpath.rest.resource.availabilities.link.impl.TestAvailabilityResponseFactory.buildExecutionResultWithAvailability;
import static com.elasticpath.rest.resource.availabilities.link.impl.TestAvailabilityResponseFactory.buildExecutionResultWithNoAvailability;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import org.hamcrest.Matchers;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.read.ReadResourceCommandBuilderProvider;
import com.elasticpath.rest.command.read.TestReadResourceCommandBuilderProvider;
import com.elasticpath.rest.definition.availabilities.AvailabilitiesMediaTypes;
import com.elasticpath.rest.definition.availabilities.AvailabilityEntity;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.resource.availabilities.rel.AvailabilityRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Test class for {@link LinkItemStrategy}.
 */
public final class LinkItemStrategyTest {

	private static final String RESOURCE = "availabilities";
	private static final String ITEM_RESOURCE = "items";
	private static final String WORKING_ENCODED_ITEM_ID = "10123=";
	private static final String SCOPE = "rockjam";


	/**
	 * Test that a valid item returns the correct availability resource link.
	 */
	@Test
	public void testLinkItemCommand() {
		ResourceState<ItemEntity> itemRep = createItemRepresentation(WORKING_ENCODED_ITEM_ID);
		ExecutionResult<ResourceState<AvailabilityEntity>> availabilityResult = buildExecutionResultWithAvailability();
		ReadResourceCommandBuilderProvider provider = TestReadResourceCommandBuilderProvider.mock(availabilityResult);
		LinkItemStrategy lineItemStrategy = new LinkItemStrategy(RESOURCE, provider);

		Collection<ResourceLink> resourceLinks = lineItemStrategy.getLinks(itemRep);

		String expectedItemUri = URIUtil.format(RESOURCE, ITEM_RESOURCE, SCOPE, WORKING_ENCODED_ITEM_ID);
		ResourceLink expectedLink = ResourceLinkFactory.create(expectedItemUri, AvailabilitiesMediaTypes.AVAILABILITY.id(),
				AvailabilityRepresentationRels.AVAILABILITY_REL, AvailabilityRepresentationRels.ITEM_REV);
		assertThat("The expected link should be contained within the collection of links.", resourceLinks, Matchers.hasItem(expectedLink));
	}

	/**
	 * Test that no links are added to item if there is no availability found.
	 */
	@Test
	public void testLinkItemCommandWithNoAvailability() {

		ResourceState<ItemEntity> itemRep = createItemRepresentation(WORKING_ENCODED_ITEM_ID);
		ExecutionResult<ResourceState<AvailabilityEntity>> availabilityResult = buildExecutionResultWithNoAvailability();
		ReadResourceCommandBuilderProvider provider = TestReadResourceCommandBuilderProvider.mock(availabilityResult);
		LinkItemStrategy lineItemStrategy = new LinkItemStrategy(RESOURCE, provider);

		Collection<ResourceLink> resourceLinks = lineItemStrategy.getLinks(itemRep);

		assertTrue(resourceLinks.isEmpty());
	}


	private ResourceState<ItemEntity> createItemRepresentation(final String itemId) {
		String itemUri = URIUtil.format(ITEM_RESOURCE, SCOPE, itemId);
		return ResourceState.Builder
				.create(ItemEntity.builder().build())
				.withSelf(SelfFactory.createSelf(itemUri))
				.build();
	}
}
