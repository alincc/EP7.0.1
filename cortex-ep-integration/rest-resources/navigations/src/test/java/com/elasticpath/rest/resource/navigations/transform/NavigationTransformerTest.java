/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.navigations.transform;

import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.junit.Test;

import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.base.DetailsEntity;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.navigations.NavigationEntity;
import com.elasticpath.rest.definition.navigations.NavigationsMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.navigations.constants.NavigationsResourceConstants;
import com.elasticpath.rest.resource.navigations.integration.dto.NavigationDto;
import com.elasticpath.rest.resource.navigations.rel.NavigationsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;
import com.elasticpath.rest.util.collection.CollectionUtil;


/**
 * Tests {@link NavigationTransformer}.
 */
public final class NavigationTransformerTest {

	private static final String TEST_CHILD_ID = "test child id";
	private static final String TEST_SCOPE = "test scope";
	private static final String TEST_RESOURCE = "test resource";
	private static final String TEST_NAME = "test name";
	private static final String TEST_DISPLAY_NAME = "test display name";
	private static final String TEST_NAVIGATION_ID = "test navigation id";
	private static final String TEST_PARENT_ID = "test parent id";
	private static final String TEST_ATTRIBUTE_DESCRIPTION = "descr";
	private static final String TEST_ATTRIBUTE_DISPLAY_VALUE = "display value";
	private static final String TEST_ATTRIBUTE_KEY = "attribute key";
	private static final Object TEST_ATTRIBUTE_VALUE = "attribute value";

	private final NavigationTransformer navigationTransformer = new NavigationTransformer(TEST_RESOURCE);


	/**
	 * Test transform to representation happy path.
	 */
	@Test
	public void testTransformToRepresentation() {
		NavigationDto navigationDto = ResourceTypeFactory.createResourceEntity(NavigationDto.class)
				.setName(TEST_NAME)
				.setDisplayName(TEST_DISPLAY_NAME)
				.setChildNavigationCorrelationIds(Collections.singleton(TEST_CHILD_ID))
				.setNavigationCorrelationId(TEST_NAVIGATION_ID)
				.setParentNavigationCorrelationId(TEST_PARENT_ID);

		ResourceState<NavigationEntity> navigationRepresentation = navigationTransformer.transformToRepresentation(TEST_SCOPE, navigationDto);

		String baseUri = URIUtil.format(TEST_RESOURCE, TEST_SCOPE);

		String selfUri = URIUtil.format(baseUri, Base32Util.encode(TEST_NAVIGATION_ID));
		Self expectedSelf = SelfFactory.createSelf(selfUri);

		Collection<ResourceLink> expectedLinks = new ArrayList<>();
		addParentLink(baseUri, expectedLinks);
		addChildLink(baseUri, expectedLinks);
		addTopLink(baseUri, expectedLinks);

		assertResourceState(navigationRepresentation)
				.self(expectedSelf)
				.resourceInfoMaxAge(NavigationsResourceConstants.DEFAULT_MAX_AGE)
				.linkCount(expectedLinks.size())
				.containsLinks(expectedLinks);

		assertEquals(TEST_NAME, navigationRepresentation.getEntity().getName());
		assertEquals(TEST_DISPLAY_NAME, navigationRepresentation.getEntity().getDisplayName());
		assertEquals(Base32Util.encode(TEST_NAVIGATION_ID), navigationRepresentation.getEntity().getNodeId());
	}

	/**
	 * Test transform to representation with no child nodes.
	 */
	@Test
	public void testTransformToRepresentationWithNoChildNodes() {
		NavigationDto navigationDto = ResourceTypeFactory.createResourceEntity(NavigationDto.class)
				.setName(TEST_NAME)
				.setNavigationCorrelationId(TEST_NAVIGATION_ID)
				.setParentNavigationCorrelationId(TEST_PARENT_ID);

		ResourceState<NavigationEntity> navigationRepresentation = navigationTransformer.transformToRepresentation(TEST_SCOPE, navigationDto);

		String baseUri = URIUtil.format(TEST_RESOURCE, TEST_SCOPE);

		String selfUri = URIUtil.format(baseUri, Base32Util.encode(TEST_NAVIGATION_ID));
		Self expectedSelf = SelfFactory.createSelf(selfUri);

		Collection<ResourceLink> expectedLinks = new ArrayList<>();
		addParentLink(baseUri, expectedLinks);
		addTopLink(baseUri, expectedLinks);

		assertResourceState(navigationRepresentation)
				.self(expectedSelf)
				.resourceInfoMaxAge(NavigationsResourceConstants.DEFAULT_MAX_AGE)
				.linkCount(expectedLinks.size())
				.containsLinks(expectedLinks);
	}

	/**
	 * Test transform to representation with no navigation nodes.
	 */
	@Test
	public void testTransformToRepresentationWithNoNavigationNodes() {
		NavigationDto navigationDto = ResourceTypeFactory.createResourceEntity(NavigationDto.class)
				.setName(TEST_NAME)
				.setNavigationCorrelationId(TEST_NAVIGATION_ID);

		ResourceState<NavigationEntity> navigationRepresentation = navigationTransformer.transformToRepresentation(TEST_SCOPE, navigationDto);

		String baseUri = URIUtil.format(TEST_RESOURCE, TEST_SCOPE);

		String selfUri = URIUtil.format(baseUri, Base32Util.encode(TEST_NAVIGATION_ID));
		Self expectedSelf = SelfFactory.createSelf(selfUri);

		Collection<ResourceLink> expectedLinks = new ArrayList<>();
		addTopLink(baseUri, expectedLinks);

		assertResourceState(navigationRepresentation)
				.self(expectedSelf)
				.resourceInfoMaxAge(NavigationsResourceConstants.DEFAULT_MAX_AGE)
				.linkCount(expectedLinks.size())
				.containsLinks(expectedLinks);
	}

	/**
	 * Test transform to representation with attributes.
	 */
	@Test
	public void testTransformToRepresentationWithAttributes() {
		Collection<DetailsEntity> attributes = new HashSet<>();

		DetailsEntity attribute = DetailsEntity.builder()
				.withDisplayName(TEST_ATTRIBUTE_DESCRIPTION)
				.withDisplayValue(TEST_ATTRIBUTE_DISPLAY_VALUE)
				.withName(TEST_ATTRIBUTE_KEY)
				.withValue(TEST_ATTRIBUTE_VALUE)
				.build();

		attributes.add(attribute);
		NavigationDto navigationDto = ResourceTypeFactory.createResourceEntity(NavigationDto.class)
				.setName(TEST_NAME)
				.setNavigationCorrelationId(TEST_NAVIGATION_ID)
				.setAttributes(attributes);

		ResourceState<NavigationEntity> navigationRepresentation = navigationTransformer.transformToRepresentation(TEST_SCOPE, navigationDto);
		Collection<DetailsEntity> navigationAttributes = navigationRepresentation.getEntity().getDetails();

		assertEquals(attributes.size(), navigationAttributes.size());

		DetailsEntity navigationAttribute = CollectionUtil.first(navigationAttributes);

		assertEquals(TEST_ATTRIBUTE_DESCRIPTION, navigationAttribute.getDisplayName());
		assertEquals(TEST_ATTRIBUTE_DISPLAY_VALUE, navigationAttribute.getDisplayValue());
		assertEquals(TEST_ATTRIBUTE_KEY, navigationAttribute.getName());
		assertEquals(TEST_ATTRIBUTE_VALUE, navigationAttribute.getValue());
	}

	private void addTopLink(final String baseUri, final Collection<ResourceLink> expectedLinks) {
		ResourceLink expectedTopNodeLink = ResourceLinkFactory.createNoRev(baseUri, CollectionsMediaTypes.LINKS.id(),
				NavigationsResourceRels.TOP_REL);
		expectedLinks.add(expectedTopNodeLink);
	}

	private void addChildLink(final String baseUri, final Collection<ResourceLink> expectedLinks) {
		String childNodeUri = URIUtil.format(baseUri, Base32Util.encode(TEST_CHILD_ID));
		ResourceLink expectedChildLink = ResourceLinkFactory.createNoRev(childNodeUri, NavigationsMediaTypes.NAVIGATION.id(),
				NavigationsResourceRels.CHILD_REL);
		expectedLinks.add(expectedChildLink);
	}

	private void addParentLink(final String baseUri, final Collection<ResourceLink> expectedLinks) {
		String parentNodeUri = URIUtil.format(baseUri, Base32Util.encode(TEST_PARENT_ID));
		ResourceLink expectedParentLink = ResourceLinkFactory.createNoRev(parentNodeUri, NavigationsMediaTypes.NAVIGATION.id(),
				NavigationsResourceRels.PARENT_REL);
		expectedLinks.add(expectedParentLink);
	}
}
