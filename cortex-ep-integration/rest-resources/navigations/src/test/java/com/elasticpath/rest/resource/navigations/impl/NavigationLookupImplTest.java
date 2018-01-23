/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.navigations.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.navigations.NavigationEntity;
import com.elasticpath.rest.definition.navigations.NavigationsMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.navigations.constants.NavigationsResourceConstants;
import com.elasticpath.rest.resource.navigations.integration.NavigationLookupStrategy;
import com.elasticpath.rest.resource.navigations.integration.dto.NavigationDto;
import com.elasticpath.rest.resource.navigations.rel.NavigationsResourceRels;
import com.elasticpath.rest.resource.navigations.transform.NavigationTransformer;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests {@link NavigationLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class NavigationLookupImplTest {

	private static final String CORRELATED_NAVIGATION_ID = "CORRELATED_NAVIGATION_ID";
	private static final String SCOPE_NOT_FOUND = "Scope not found";
	private static final String CATEGORY_ID = "CATEGORY_ID";
	private static final String ENCODED_CATEGORY_ID = Base32Util.encode(CATEGORY_ID);
	private static final String SCOPE = "SCOPE";
	private static final String INVALID_SCOPE = "INVALID_SCOPE";
	private static final String TEST_RESOURCE_NAME = "test resource name";

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private NavigationLookupStrategy navigationLookupStrategy;
	@Mock
	private NavigationTransformer navigationTransformer;

	private NavigationLookupImpl navigationLookup;

	@Before
	public void setUp() {
		navigationLookup = new NavigationLookupImpl(TEST_RESOURCE_NAME, navigationLookupStrategy, navigationTransformer);
	}


	@Test
	public void testGetRootNavigationNodesWithValidScope() {
		shouldFindRootNodeIdsWithResult(SCOPE, ExecutionResultFactory.<Collection<String>>createReadOK(Collections.singleton(CATEGORY_ID)));

		ExecutionResult<ResourceState<LinksEntity>> result = navigationLookup.getRootNavigationNodes(SCOPE);

		assertTrue("There should be a successful result.", result.isSuccessful());
		assertResourceState(result.getData())
				.self(createExpectedSelf())
				.resourceInfoMaxAge(NavigationsResourceConstants.DEFAULT_MAX_AGE)
				.containsLink(createExpectedNodeLink());
	}


	@Test
	public void testGetRootNavigationNodesWithInvalidScope() {
		shouldFindRootNodeIdsWithResult(INVALID_SCOPE, ExecutionResultFactory.<Collection<String>>createNotFound(SCOPE_NOT_FOUND));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		navigationLookup.getRootNavigationNodes(INVALID_SCOPE);
	}


	@Test
	public void testGetNavigationNode() {
		NavigationDto navigationDto = ResourceTypeFactory.createResourceEntity(NavigationDto.class);
		ResourceState<NavigationEntity> expectedNavigation = ResourceState.Builder.create(NavigationEntity.builder().build()).build();
		String navigationId = Base32Util.encode(CORRELATED_NAVIGATION_ID);

		shouldFindWithResult(SCOPE, ExecutionResultFactory.createReadOK(navigationDto));
		shouldTransformDtoToRepresentation(navigationDto, expectedNavigation);

		ExecutionResult<ResourceState<NavigationEntity>> result = navigationLookup.getNavigationNode(SCOPE, navigationId);

		assertTrue("There should be a successful result.", result.isSuccessful());
		assertEquals("The result should contain the navigation representation.", expectedNavigation, result.getData());
	}


	@Test
	public void testGetNavigationNodeWithInvalidScope() {
		final String navigationId = Base32Util.encode(CORRELATED_NAVIGATION_ID);

		shouldFindWithResult(INVALID_SCOPE, ExecutionResultFactory.<NavigationDto>createNotFound(SCOPE_NOT_FOUND));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		navigationLookup.getNavigationNode(INVALID_SCOPE, navigationId);
	}

	private void shouldFindRootNodeIdsWithResult(final String scope, final ExecutionResult<Collection<String>> result) {
		when(navigationLookupStrategy.findRootNodeIds(scope))
				.thenReturn(result);
	}

	private void shouldFindWithResult(final String scope, final ExecutionResult<NavigationDto> result) {
		when(navigationLookupStrategy.find(scope, CORRELATED_NAVIGATION_ID))
				.thenReturn(result);
	}

	private void shouldTransformDtoToRepresentation(final NavigationDto navigationDto, final ResourceState<NavigationEntity> expected) {
		when(navigationTransformer.transformToRepresentation(SCOPE, navigationDto))
				.thenReturn(expected);
	}

	private Self createExpectedSelf() {
		String selfLinkUri = URIUtil.format(TEST_RESOURCE_NAME, SCOPE);
		return SelfFactory.createSelf(selfLinkUri);
	}

	private ResourceLink createExpectedNodeLink() {
		String nodeLinkUri = URIUtil.format(TEST_RESOURCE_NAME, SCOPE, ENCODED_CATEGORY_ID);
		return ElementListFactory.createElementWithRev(nodeLinkUri, NavigationsMediaTypes.NAVIGATION.id(), NavigationsResourceRels.TOP_REL);
	}
}
