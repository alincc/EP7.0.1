/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.availabilities.link.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.rel.NeedInfoRels;
import com.elasticpath.rest.resource.availabilities.integration.AvailabilityLookupStrategy;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformToResourceState;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Link strategy which adds {@code needinfo} links for {@code destinationinfo} and {@code shippingoptioninfo} to the order resource.
 */
@Singleton
@Named("addAvailabilityNeedInfoLinksToOrderStrategy")
public final class AddAvailabilityNeedInfoLinksToOrderStrategy implements ResourceStateLinkHandler<OrderEntity> {
	private final String resourceServerName;
	private final AvailabilityLookupStrategy availabilityLookupStrategy;
	private final TransformToResourceState<LineItemEntity, LineItemEntity> lineItemDetailsTransformer;

	/**
	 * Default constructor.
	 * @param resourceServerName the resource server name
	 * @param availabilityLookupStrategy the availability lookup strategy
	 * @param lineItemDetailsTransformer the line item details transformer
	 */
	@Inject
	public AddAvailabilityNeedInfoLinksToOrderStrategy(
						@Named("resourceServerName")
						final String resourceServerName,
						@Named("availabilityLookupStrategy")
						final AvailabilityLookupStrategy availabilityLookupStrategy,
						@Named("lineItemDetailsTransformer")
						final TransformToResourceState<LineItemEntity, LineItemEntity> lineItemDetailsTransformer) {

		this.resourceServerName = resourceServerName;
		this.availabilityLookupStrategy = availabilityLookupStrategy;
		this.lineItemDetailsTransformer = lineItemDetailsTransformer;
	}


	@Override
	public Collection<ResourceLink> getLinks(final ResourceState<OrderEntity> order) {

		final String cartId = Base32Util.decode(order.getEntity().getCartId());
		final String scope = order.getScope();

		final Collection<ResourceLink> needInfoLinks = new ArrayList<>();

		final Collection<LineItemEntity> unavailableLineItemEntities = Assign.ifSuccessful(availabilityLookupStrategy
				.getUnavailableLineItems(scope, cartId));

		for (LineItemEntity unavailableLineItemEntity : unavailableLineItemEntities) {

			final ResourceState<LineItemEntity> lineItem = lineItemDetailsTransformer.transform(scope, unavailableLineItemEntity);

			final String resourceUri = URIUtil.format(resourceServerName, ResourceStateUtil.getSelfUri(lineItem));

			final ResourceLink availabilityNeedInfoLink = ResourceLinkFactory.createNoRev(resourceUri,
																						   ControlsMediaTypes.INFO.id(),
																						   NeedInfoRels.NEEDINFO);

			needInfoLinks.add(availabilityNeedInfoLink);
		}

		return needInfoLinks;
	}
}
