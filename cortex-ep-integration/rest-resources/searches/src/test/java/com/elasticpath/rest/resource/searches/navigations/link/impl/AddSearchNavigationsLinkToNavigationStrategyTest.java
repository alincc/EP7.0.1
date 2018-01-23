/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.navigations.link.impl;

import static com.elasticpath.rest.test.AssertResourceLink.assertResourceLink;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import org.hamcrest.Matchers;

import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.navigations.NavigationEntity;
import com.elasticpath.rest.definition.navigations.NavigationsMediaTypes;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.searches.TestSearchesResourceConstants;
import com.elasticpath.rest.resource.searches.keywords.Items;
import com.elasticpath.rest.resource.searches.navigations.Navigations;
import com.elasticpath.rest.resource.searches.rel.SearchesResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Test for {@link com.elasticpath.rest.resource.searches.navigations.link.impl.AddSearchNavigationsLinkToNavigationStrategy}.
 */
public final class AddSearchNavigationsLinkToNavigationStrategyTest {

	private static final String SCOPE = "testScope";
	private static final String TEST_NODE_ID = "testNodeId";

	private final ResourceStateLinkHandler<NavigationEntity> addSearchNavigationLinkToNavigationStrategy =
			new AddSearchNavigationsLinkToNavigationStrategy(TestSearchesResourceConstants.RESOURCE_NAME);


	/**
	 * Tests creation of the link to searches/navigations on the navigation representation.
	 */
	@Test
	public void testCreateLinkToSearchNavigationsForNavigation() {

		ResourceState<NavigationEntity> navigationRepresentation = ResourceState.Builder
				.create(NavigationEntity.builder()
				.withNodeId(TEST_NODE_ID)
				.build())
				.withScope(SCOPE)
				.withSelf(SelfFactory.createSelf("navUri", NavigationsMediaTypes.NAVIGATION.id()))
				.build();

		Iterable<ResourceLink> createdLinks = addSearchNavigationLinkToNavigationStrategy.getLinks(navigationRepresentation);
		assertThat("There should only be one link created.", createdLinks, Matchers.<ResourceLink>iterableWithSize(1));

		ResourceLink createdLink = createdLinks.iterator().next();
		String expectedLinkUri = URIUtil.format(TestSearchesResourceConstants.RESOURCE_NAME, SCOPE, Navigations.URI_PART,
				Items.URI_PART, navigationRepresentation.getEntity().getNodeId());

		assertResourceLink(createdLink)
				.rel(SearchesResourceRels.ITEMS_REL)
				.type(CollectionsMediaTypes.PAGINATED_LINKS.id())
				.uri(expectedLinkUri);
	}
}
