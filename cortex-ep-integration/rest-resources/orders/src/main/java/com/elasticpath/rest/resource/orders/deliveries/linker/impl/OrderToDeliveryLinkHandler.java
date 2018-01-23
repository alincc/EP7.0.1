/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.deliveries.linker.impl;

import static com.elasticpath.rest.definition.collections.CollectionsMediaTypes.LINKS;
import static com.elasticpath.rest.resource.orders.deliveries.rel.DeliveryRepresentationRels.DELIVERIES_REL;
import static com.elasticpath.rest.resource.orders.rel.OrdersRepresentationRels.ORDER_REV;
import static com.elasticpath.rest.schema.ResourceLinkFactory.create;
import static com.google.common.collect.Lists.newArrayList;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.orders.deliveries.Deliveries;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Link strategy.
 */
@Singleton
@Named("orderToDeliveryLinkHandler")
public class OrderToDeliveryLinkHandler implements ResourceStateLinkHandler<OrderEntity> {

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<OrderEntity> representation) {

		String deliveriesUri = URIUtil.format(
				representation.getSelf().getUri(),
				Deliveries.URI_PART
		);

		return newArrayList(
				create(deliveriesUri,
						LINKS.id(),
						DELIVERIES_REL,
						ORDER_REV)
		);
	}
}
