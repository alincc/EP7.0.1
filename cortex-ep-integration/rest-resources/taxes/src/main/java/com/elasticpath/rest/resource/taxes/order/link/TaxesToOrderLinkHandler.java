/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.order.link;

import static com.elasticpath.rest.definition.orders.OrdersMediaTypes.ORDER;
import static com.elasticpath.rest.resource.taxes.order.rel.OrderTaxesResourceRels.ORDER_REL;
import static com.elasticpath.rest.resource.taxes.rel.TaxesResourceRels.TAX_REV;
import static com.elasticpath.rest.schema.ResourceLinkFactory.create;

import java.util.Collections;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Link Handler.
 */
@Singleton
@Named("taxesToOrderLinkHandler")
public class TaxesToOrderLinkHandler implements ResourceStateLinkHandler<TaxesEntity> {

	private static final String ORDERS_RESOURCE_NAME = "/orders";

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<TaxesEntity> resourceState) {

		String selfUri = resourceState.getSelf()
				.getUri();

		if (!selfUri.contains(ORDERS_RESOURCE_NAME)) {
			return Collections.emptyList();
		}

		return Collections.singleton(createOrderLink(selfUri));
	}

	private ResourceLink createOrderLink(final String selfUri) {

		int pos = selfUri.indexOf(ORDERS_RESOURCE_NAME);
		String orderUri = selfUri.substring(pos);

		return create(
				orderUri,
				ORDER.id(),
				ORDER_REL,
				TAX_REV
		);
	}
}
