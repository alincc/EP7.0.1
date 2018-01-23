/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.slots.transform.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.navigations.NavigationsMediaTypes;
import com.elasticpath.rest.definition.slots.SlotEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.slots.integration.dto.SlotDto;
import com.elasticpath.rest.resource.slots.integration.dto.SlotParameterDto;
import com.elasticpath.rest.resource.slots.rel.SlotsResourceRels;
import com.elasticpath.rest.resource.slots.transform.SlotParameterHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.uri.NavigationsUriBuilderFactory;


/**
 * Slots parameter handler to handle the category code parameter.
 */
@Singleton
@Named("categoryCodeParameterHandler")
public final class CategoryCodeParameterHandlerImpl implements SlotParameterHandler {

	private final NavigationsUriBuilderFactory navigationsUriBuilderFactory;


	/**
	 * Default Constructor.
	 *
	 * @param navigationsUriBuilderFactory the navigations uri builder factory
	 */
	@Inject
	public CategoryCodeParameterHandlerImpl(
			@Named("navigationsUriBuilderFactory")
			final NavigationsUriBuilderFactory navigationsUriBuilderFactory) {

		this.navigationsUriBuilderFactory = navigationsUriBuilderFactory;
	}


	@Override
	public void handle(final String scope, final SlotEntity.Builder entityBuilder, final Collection<ResourceLink> links, final SlotDto slotDto) {
		SlotParameterDto slotParameterDto = slotDto.getParameters().get("categoryCode");

		if (slotParameterDto != null) {
			String navigationNodeId = Base32Util.encode(slotParameterDto.getValue());
			String navigationNodeUri = navigationsUriBuilderFactory.get().setNavigationId(navigationNodeId)
					.setScope(scope)
					.build();

			ResourceLink navigationNodeLink = ResourceLinkFactory.createNoRev(navigationNodeUri, NavigationsMediaTypes.NAVIGATION.id(),
					SlotsResourceRels.ON_EVENT_REL);

			links.add(navigationNodeLink);
		}
	}
}
