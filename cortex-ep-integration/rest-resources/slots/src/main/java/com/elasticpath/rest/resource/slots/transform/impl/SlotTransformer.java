/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.slots.transform.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.slots.SlotEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.slots.integration.dto.SlotDto;
import com.elasticpath.rest.resource.slots.rel.SlotsResourceRels;
import com.elasticpath.rest.resource.slots.transform.SlotParameterHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Transforms between {@link ResourceState} and {@link SlotDto}.
 */
@Singleton
@Named("slotTransformer")
public class SlotTransformer {

	private final String resourceServerName;
	private final List<SlotParameterHandler> slotParameterHandlers;


	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resource server name
	 * @param slotParameterHandlers the slot parameter handlers
	 */
	@Inject
	@SuppressWarnings("PMD.LooseCoupling") // Suppressing warning because spring cannot autowire to the List interface
	public SlotTransformer(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("slotParameterHandlers")
			final ArrayList<SlotParameterHandler> slotParameterHandlers) {

		this.resourceServerName = resourceServerName;
		this.slotParameterHandlers = slotParameterHandlers;
	}


	/**
	 * Transforms a dto into a representation.
	 *
	 * @param scope the scope.
	 * @param slotDto the slot dto.
	 * @return a slot representation.
	 */
	public ResourceState<SlotEntity> transformToResourceState(final String scope, final SlotDto slotDto) {
		SlotEntity.Builder entityBuilder = SlotEntity.builder();
		Collection<ResourceLink> links = new ArrayList<>();

		String selfUri = URIUtil.format(resourceServerName, scope, Base32Util.encode(slotDto.getCorrelationId()));
		Self self = SelfFactory.createSelf(selfUri);

		String listUri = URIUtil.format(resourceServerName, scope);
		ResourceLink listLink = ElementListFactory.createListWithoutElement(listUri, CollectionsMediaTypes.LINKS.id());
		links.add(listLink);

		entityBuilder.withName(slotDto.getTargetId());

		if (CollectionUtil.isNotEmpty(slotDto.getParameters())) {
			for (SlotParameterHandler handler : slotParameterHandlers) {
				handler.handle(scope, entityBuilder, links, slotDto);
			}
		}

		return ResourceState.Builder.create(entityBuilder.build())
				.withSelf(self)
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(SlotsResourceRels.SLOT_MAX_AGE)
						.build())
				.withLinks(links)
				.build();
	}
}
