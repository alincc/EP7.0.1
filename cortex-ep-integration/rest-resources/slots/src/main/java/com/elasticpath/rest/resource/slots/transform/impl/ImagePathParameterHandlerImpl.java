/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.slots.transform.impl;

import java.util.Collection;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.slots.SlotEntity;
import com.elasticpath.rest.resource.slots.integration.dto.SlotDto;
import com.elasticpath.rest.resource.slots.integration.dto.SlotParameterDto;
import com.elasticpath.rest.resource.slots.transform.SlotParameterHandler;
import com.elasticpath.rest.schema.ResourceLink;

/**
 * Slot parameter handler to handle the image path and construct content location.
 */
@Singleton
@Named("imagePathParameterHandler")
public final class ImagePathParameterHandlerImpl implements SlotParameterHandler {

	@Override
	public void handle(final String scope, final SlotEntity.Builder entityBuilder, final Collection<ResourceLink> links, final SlotDto slotDto) {

		SlotParameterDto slotParameterDto = slotDto.getParameters().get("imagePath");

		if (slotParameterDto != null) {
			String relativeLocation = slotParameterDto.getValue();
			entityBuilder.withRelativeLocation(relativeLocation);
		}
	}
}
