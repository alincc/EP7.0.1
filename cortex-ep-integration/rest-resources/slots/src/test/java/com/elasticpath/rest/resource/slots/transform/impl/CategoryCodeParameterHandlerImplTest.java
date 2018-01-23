/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.slots.transform.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.navigations.NavigationsMediaTypes;
import com.elasticpath.rest.definition.slots.SlotEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.slots.integration.dto.SlotDto;
import com.elasticpath.rest.resource.slots.integration.dto.SlotParameterDto;
import com.elasticpath.rest.resource.slots.rel.SlotsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.NavigationsUriBuilder;
import com.elasticpath.rest.schema.uri.NavigationsUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;
import com.elasticpath.rest.util.collection.CollectionUtil;


/**
 * Contains tests for {@link CategoryCodeParameterHandlerImpl}.
 */
public final class CategoryCodeParameterHandlerImplTest {

	private static final String TEST_CATEGORY_ID = "TEST2";
	private static final String SCOPE = "scope";

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	@Mock
	private NavigationsUriBuilderFactory mockNavigationsUriBuilderFactory;
	@Mock
	private NavigationsUriBuilder mockNavigationsUriBuilder;


	/**
	 * Tests the handle method.
	 */
	@Test
	public void testCategoryCodeParameterHandler() {
		CategoryCodeParameterHandlerImpl handler = new CategoryCodeParameterHandlerImpl(mockNavigationsUriBuilderFactory);
		SlotEntity.Builder entityBuilder = SlotEntity.builder();
		Collection<ResourceLink> linkAccumulator = new ArrayList<>();
		SlotDto dto = ResourceTypeFactory.createResourceEntity(SlotDto.class);
		SlotParameterDto value = ResourceTypeFactory.createResourceEntity(SlotParameterDto.class);
		value.setValue(TEST_CATEGORY_ID);
		dto.setParameters(Collections.singletonMap("categoryCode", value));
		final String expectedEncodedNavigationId = Base32Util.encode(TEST_CATEGORY_ID);
		final String expectedUri = URIUtil.format("navigations", SCOPE, expectedEncodedNavigationId);
		context.checking(new Expectations() {
			{
				allowing(mockNavigationsUriBuilderFactory).get();
				will(returnValue(mockNavigationsUriBuilder));

				allowing(mockNavigationsUriBuilder).setNavigationId(expectedEncodedNavigationId);
				will(returnValue(mockNavigationsUriBuilder));

				allowing(mockNavigationsUriBuilder).setScope(SCOPE);
				will(returnValue(mockNavigationsUriBuilder));

				allowing(mockNavigationsUriBuilder).build();
				will(returnValue(expectedUri));
			}
		});

		handler.handle(SCOPE, entityBuilder, linkAccumulator, dto);
		ResourceState<SlotEntity> representation = ResourceState.Builder.create(entityBuilder.build())
				.withLinks(linkAccumulator)
				.build();

		Collection<ResourceLink> generatedLinks = representation.getLinks();
		assertEquals(1, generatedLinks.size());
		ResourceLink navigationLink = CollectionUtil.first(generatedLinks);
		ResourceLink expectedLink = ResourceLinkFactory.createNoRev(expectedUri, NavigationsMediaTypes.NAVIGATION.id(),
				SlotsResourceRels.ON_EVENT_REL);
		assertEquals(expectedLink, navigationLink);
	}
}
