/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.slots.transform.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.slots.SlotEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.rel.ListElementRels;
import com.elasticpath.rest.resource.slots.integration.dto.SlotDto;
import com.elasticpath.rest.resource.slots.integration.dto.SlotParameterDto;
import com.elasticpath.rest.resource.slots.rel.SlotsResourceRels;
import com.elasticpath.rest.resource.slots.transform.SlotParameterHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests for {@link SlotTransformer}.
 */
public final class SlotTransformerTest {

	private static final String TEST_CONTENT_LOCATION = "image1.jpg";
	private static final String TEST_PRODUCT_URI = "/test product uri";
	private static final ResourceLink TEST_RESOURCE_LINK = ResourceLinkFactory.createUriOnly(TEST_PRODUCT_URI);
	private static final String RESOURCE_NAME = "slots";
	private static final String SCOPE = "scope";
	private static final String TARGET_ID = "TARGET ID";
	private static final String DECODED_SLOT_ID = "decodedSlotId";
	private static final String ENCODED_SLOT_ID = Base32Util.encode(DECODED_SLOT_ID);


	/**
	 * Tests the transformer.
	 */
	@Test
	public void testSlotTransformer() {
		ArrayList<SlotParameterHandler> emptyList = new ArrayList<>();
		SlotTransformer slotTransformer = new SlotTransformer(RESOURCE_NAME, emptyList);
		SlotDto slotDto = createSlotDto(TARGET_ID);

		ResourceState<SlotEntity> representation = slotTransformer.transformToResourceState(SCOPE, slotDto);

		String expectedUri = URIUtil.format(RESOURCE_NAME, SCOPE, ENCODED_SLOT_ID);
		assertEquals(TARGET_ID, representation.getEntity().getName());
		Self self = representation.getSelf();
		assertEquals(expectedUri, self.getUri());
		assertEquals(SlotsResourceRels.SLOT_MAX_AGE, representation.getResourceInfo().getMaxAge());
		Collection<ResourceLink> links = representation.getLinks();
		assertEquals("size of links should be 1", 1, links.size());
		String expectedListUri = URIUtil.format(RESOURCE_NAME, SCOPE);
		ResourceLink expectedListLink = ResourceLinkFactory.createNoRev(expectedListUri, CollectionsMediaTypes.LINKS.id(), ListElementRels.LIST);
		assertTrue(links.contains(expectedListLink));
	}

	/**
	 * Tests the transformer with a product code parameter handler.
	 */
	@Test
	public void testSlotTransformerWithProductCodeParameterHandler() {
		ArrayList<SlotParameterHandler> slotParameterHandlerList = new ArrayList<>();
		addProductCodeParameterHandler(slotParameterHandlerList);
		SlotTransformer slotTransformer = new SlotTransformer(RESOURCE_NAME, slotParameterHandlerList);
		SlotDto slotDto = createSlotDto(TARGET_ID);

		ResourceState<SlotEntity> representation = slotTransformer.transformToResourceState(SCOPE, slotDto);

		String expectedUri = URIUtil.format(RESOURCE_NAME, SCOPE, ENCODED_SLOT_ID);
		assertEquals(TARGET_ID, representation.getEntity().getName());
		Self self = representation.getSelf();
		assertEquals(expectedUri, self.getUri());
		assertEquals(SlotsResourceRels.SLOT_MAX_AGE, representation.getResourceInfo().getMaxAge());
		Collection<ResourceLink> links = representation.getLinks();
		assertEquals("size of links should be 2", 2, links.size());
		String expectedListUri = URIUtil.format(RESOURCE_NAME, SCOPE);
		ResourceLink expectedListLink = ResourceLinkFactory.createNoRev(expectedListUri, CollectionsMediaTypes.LINKS.id(), ListElementRels.LIST);
		assertTrue(links.contains(TEST_RESOURCE_LINK));
		assertTrue(links.contains(expectedListLink));
	}

	/**
	 * Tests the transformer with a image path parameter handler.
	 */
	@Test
	public void testSlotTransformerWithImagePathParameterHandler() {
		ArrayList<SlotParameterHandler> slotParameterHandlerList = new ArrayList<>();
		addProductImagePathParameterHandler(slotParameterHandlerList);
		SlotTransformer slotTransformer = new SlotTransformer(RESOURCE_NAME, slotParameterHandlerList);
		SlotDto slotDto = createSlotDto(TARGET_ID);

		ResourceState<SlotEntity> representation = slotTransformer.transformToResourceState(SCOPE, slotDto);

		String expectedUri = URIUtil.format(RESOURCE_NAME, SCOPE, ENCODED_SLOT_ID);
		assertEquals(TARGET_ID, representation.getEntity().getName());
		Self self = representation.getSelf();
		assertEquals(expectedUri, self.getUri());
		assertEquals(SlotsResourceRels.SLOT_MAX_AGE, representation.getResourceInfo().getMaxAge());
		Collection<ResourceLink> links = representation.getLinks();
		assertEquals("size of links should be 1", 1, links.size());
		String expectedListUri = URIUtil.format(RESOURCE_NAME, SCOPE);
		ResourceLink expectedListLink = ResourceLinkFactory.createNoRev(expectedListUri, CollectionsMediaTypes.LINKS.id(), ListElementRels.LIST);
		assertEquals(TEST_CONTENT_LOCATION, representation.getEntity().getContentLocation());
		assertTrue(links.contains(expectedListLink));
	}


	private void addProductCodeParameterHandler(final List<SlotParameterHandler> slotParameterHandlerList) {
		SlotParameterHandler mockProductCodeParameterHandler =
			(scope, entityBuilder, links, slotDto) -> links.add(TEST_RESOURCE_LINK);
		slotParameterHandlerList.add(mockProductCodeParameterHandler);
	}

	private void addProductImagePathParameterHandler(final List<SlotParameterHandler> slotParameterHandlerList) {
		SlotParameterHandler mockImageSlotParameterHandler =
			(scope, entityBuilder, links, slotDto) -> entityBuilder.withContentLocation(TEST_CONTENT_LOCATION);
		slotParameterHandlerList.add(mockImageSlotParameterHandler);
	}

	private SlotDto createSlotDto(final String targetId) {
		SlotDto slotDto = ResourceTypeFactory.createResourceEntity(SlotDto.class);
		Map<String, SlotParameterDto> parameters = new HashMap<>();
		SlotParameterDto testSlotParameter = ResourceTypeFactory.createResourceEntity(SlotParameterDto.class);
		parameters.put("test", testSlotParameter);
		slotDto.setTargetId(targetId)
				.setCorrelationId(DECODED_SLOT_ID)
				.setParameters(parameters);
		return slotDto;
	}
}
