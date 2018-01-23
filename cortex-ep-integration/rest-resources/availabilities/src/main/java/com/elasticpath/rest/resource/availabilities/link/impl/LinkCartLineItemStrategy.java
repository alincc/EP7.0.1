/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.availabilities.link.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.read.ReadResourceCommand;
import com.elasticpath.rest.command.read.ReadResourceCommandBuilderProvider;
import com.elasticpath.rest.definition.availabilities.AvailabilitiesMediaTypes;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.resource.availabilities.rel.AvailabilityRepresentationRels;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Create a Link to Availability on an Item Representation.
 */
@Singleton
@Named("linkCartLineItemStrategy")
public final class LinkCartLineItemStrategy implements ResourceStateLinkHandler<LineItemEntity> {

	private final String resourceServerName;
	private final Provider<ReadResourceCommand.Builder> readResourceCommandBuilder;


	/**
	 * Constructor.
	 *
	 * @param resourceServerName resource server name.
	 * @param readResourceCommandBuilder read resource.
	 */
	@Inject
	LinkCartLineItemStrategy(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("readResourceCommandBuilderProvider")
			final ReadResourceCommandBuilderProvider readResourceCommandBuilder) {

		this.resourceServerName = resourceServerName;
		this.readResourceCommandBuilder = readResourceCommandBuilder;
	}


	@Override
	public Collection<ResourceLink> getLinks(final ResourceState<LineItemEntity> lineItem) {

		String resourceUri = URIUtil.format(resourceServerName, ResourceStateUtil.getSelfUri(lineItem));
		Collection<ResourceLink> linksToAdd;

		ExecutionResult<ResourceState<?>> availabilityResult = readResourceCommandBuilder.get()
				.setReadLinks(false)
				.setResourceUri(resourceUri)
				.setExpectedType(AvailabilitiesMediaTypes.AVAILABILITY.id())
				.build()
				.execute();

		if (availabilityResult.isSuccessful()) {
			ResourceLink link = ResourceLinkFactory.create(resourceUri, AvailabilitiesMediaTypes.AVAILABILITY.id(),
					AvailabilityRepresentationRels.AVAILABILITY_REL, AvailabilityRepresentationRels.LINE_ITEM_REV);
			linksToAdd = Collections.singleton(link);
		} else {
			linksToAdd = Collections.emptyList();
		}

		return linksToAdd;
	}
}
