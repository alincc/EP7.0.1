/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.slots.transform.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.slots.SlotEntity;
import com.elasticpath.rest.resource.slots.integration.dto.SlotDto;
import com.elasticpath.rest.resource.slots.integration.dto.SlotParameterDto;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Tests for {@link ImagePathParameterHandlerImpl}.
 */
public final class ImagePathParameterHandlerImplTest {

	private static final String IMAGE_RELATIVE_PATH = "relativePath/image.jpg";
	private static final String SCOPE = "scope";
	private static final String IMAGE_PARAMETER_TYPE = "imagePath";

	/**
	 * Tests handle image parameter with no asset server base url does not set the content location.
	 */
	@Test
	public void testHandleImageParameterWithNoAssetServerBaseUrl() {
		SlotEntity.Builder entityBuilder = SlotEntity.builder();
		Collection<ResourceLink> linkAccumulator = new ArrayList<>();
		SlotDto slotDto = createSlotDto(IMAGE_RELATIVE_PATH);
		ImagePathParameterHandlerImpl handler = new ImagePathParameterHandlerImpl();

		handler.handle(SCOPE, entityBuilder, linkAccumulator, slotDto);
		ResourceState<SlotEntity> representation = ResourceState.Builder.create(entityBuilder.build())
				.withLinks(linkAccumulator)
				.build();

		assertEquals(IMAGE_RELATIVE_PATH, representation.getEntity().getRelativeLocation());
		assertNull(representation.getEntity().getContentLocation());

	}

	/**
	 * Tests handle image parameter with an empty asset server base url does not set the content location.
	 */
	@Test
	public void testHandleImageParameterWithEmptyAssetServerBaseUrl() {
		SlotEntity.Builder entityBuilder = SlotEntity.builder();
		Collection<ResourceLink> linkAccumulator = new ArrayList<>();
		SlotDto slotDto = createSlotDto(IMAGE_RELATIVE_PATH);
		ImagePathParameterHandlerImpl handler = new ImagePathParameterHandlerImpl();

		handler.handle(SCOPE, entityBuilder, linkAccumulator, slotDto);
		ResourceState<SlotEntity> representation = ResourceState.Builder.create(entityBuilder.build())
				.withLinks(linkAccumulator)
				.build();

		assertEquals(IMAGE_RELATIVE_PATH, representation.getEntity().getRelativeLocation());
		assertNull(representation.getEntity().getContentLocation());
	}

	/**
	 * Tests the handle image parameter with a valid asset server base url.
	 */
	@Test
	public void testHandleImageParameterWithAnAssetServerBaseUrl() {
		SlotEntity.Builder entityBuilder = SlotEntity.builder();
		Collection<ResourceLink> linkAccumulator = new ArrayList<>();
		SlotDto slotDto = createSlotDto(IMAGE_RELATIVE_PATH);
		ImagePathParameterHandlerImpl handler = new ImagePathParameterHandlerImpl();

		handler.handle(SCOPE, entityBuilder, linkAccumulator, slotDto);
		ResourceState<SlotEntity> representation = ResourceState.Builder.create(entityBuilder.build())
				.withLinks(linkAccumulator)
				.build();

		assertEquals(IMAGE_RELATIVE_PATH, representation.getEntity().getRelativeLocation());
	}

	private SlotDto createSlotDto(final String assetRelativePath) {
		SlotDto slotDto = ResourceTypeFactory.createResourceEntity(SlotDto.class);
		Map<String, SlotParameterDto> imagePathMap = new HashMap<>();

		SlotParameterDto slotParameterDto = ResourceTypeFactory.createResourceEntity(SlotParameterDto.class);
		slotParameterDto.setType(IMAGE_PARAMETER_TYPE)
				.setValue(assetRelativePath);

		imagePathMap.put(IMAGE_PARAMETER_TYPE, slotParameterDto);
		slotDto.setParameters(imagePathMap);

		return slotDto;
	}
}
