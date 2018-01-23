/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.rates.link.impl;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.definition.rates.RatesMediaTypes;
import com.elasticpath.rest.resource.rates.integration.ItemRateLookupStrategy;
import com.elasticpath.rest.resource.rates.rel.RateRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Test {@link com.elasticpath.rest.resource.rates.link.impl.LinkItemStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public class LinkItemStrategyTest {

	private static final String RESOURCE_NAME = "resource_name";
	private static final String ITEMS_RESOURCE = "items";
	private static final String SCOPE = "scope";
	private static final String ITEM_ID = "item_id";
	private static final String ITEM_URI = URIUtil.format(ITEMS_RESOURCE, SCOPE, ITEM_ID);
	private static final String RATE_ITEM_URI = URIUtil.format(RESOURCE_NAME, ITEM_URI);

	@Mock
	private ItemRateLookupStrategy itemRateLookup;

	private LinkItemStrategy linkItemStrategy;


	@Before
	public void setUp() {
		linkItemStrategy = new LinkItemStrategy(RESOURCE_NAME, itemRateLookup);
	}

	@Test
	public void testCreateLinksWhenRateLookupFound() {
		ResourceState<ItemEntity> itemRepresentation = createItemRepresentation(ITEM_URI);
		ResourceLink expectedLink = createResourceLink(RATE_ITEM_URI);
		when(itemRateLookup.rateExists(SCOPE, ITEM_ID)).thenReturn(ExecutionResultFactory.createReadOK(true));

		Collection<ResourceLink> result = linkItemStrategy.getLinks(itemRepresentation);

		assertThat("There should have been only one link created.", result, Matchers.hasSize(1));
		assertThat("The a link should have been created.", result, Matchers.hasItem(expectedLink));
	}


	@Test
	public void testCreateLinksWhenRateLookupNotFound() {
		ResourceState<ItemEntity> itemRepresentation = createItemRepresentation(ITEM_URI);
		when(itemRateLookup.rateExists(SCOPE, ITEM_ID)).thenReturn(ExecutionResultFactory.<Boolean>createNotFound());

		Collection<ResourceLink> result = linkItemStrategy.getLinks(itemRepresentation);

		assertThat("The result should be an empty collection", result, Matchers.empty());
	}

	private ResourceState<ItemEntity> createItemRepresentation(final String itemUri) {
		Self itemSelf = SelfFactory.createSelf(itemUri, ItemsMediaTypes.ITEM.id());
		return ResourceState.Builder
				.create(ItemEntity.builder()
						.withItemId(ITEM_ID)
						.build())
				.withSelf(itemSelf)
				.withScope(SCOPE)
				.build();
	}

	private ResourceLink createResourceLink(final String rateItemUri) {
		return ResourceLinkFactory.create(rateItemUri, RatesMediaTypes.RATE.id(), RateRepresentationRels.RATE_REL, RateRepresentationRels.ITEM_REV);
	}

}
