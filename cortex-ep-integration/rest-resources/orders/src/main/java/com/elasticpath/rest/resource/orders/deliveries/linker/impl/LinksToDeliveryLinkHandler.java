/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.deliveries.linker.impl;

import static com.elasticpath.rest.definition.collections.CollectionsMediaTypes.LINKS;
import static com.elasticpath.rest.schema.ResourceLinkFactory.createNoRev;
import static com.elasticpath.rest.rel.ListElementRels.LIST;
import static com.google.common.collect.Lists.newArrayList;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.orders.DeliveryEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.orders.deliveries.Deliveries;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Link handler.
 */
@Singleton
@Named("linksToDeliveryLinkHandler")
public class LinksToDeliveryLinkHandler implements ResourceStateLinkHandler<DeliveryEntity> {

	@Inject
	@Named("resourceServerName")
	private String resourceServerName;

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<DeliveryEntity> resourceState) {
		DeliveryEntity deliveryEntity = resourceState.getEntity();

		String deliveriesUri = URIUtil.format(resourceServerName, resourceState.getScope(), deliveryEntity.getOrderId(), Deliveries.URI_PART);

		return newArrayList(
				createNoRev(deliveriesUri, LINKS.id(), LIST)
		);
	}
}
