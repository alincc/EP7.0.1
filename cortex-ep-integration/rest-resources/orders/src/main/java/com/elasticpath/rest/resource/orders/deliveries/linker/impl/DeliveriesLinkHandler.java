/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.deliveries.linker.impl;

import static com.elasticpath.rest.definition.orders.OrdersMediaTypes.DELIVERY;
import static com.elasticpath.rest.definition.orders.OrdersMediaTypes.ORDER;
import static com.elasticpath.rest.resource.orders.deliveries.DeliveryConstants.DELIVERY_LIST_NAME;
import static com.elasticpath.rest.resource.orders.deliveries.rel.DeliveryRepresentationRels.DELIVERIES_REV;
import static com.elasticpath.rest.resource.orders.rel.OrdersRepresentationRels.ORDER_REL;
import static com.elasticpath.rest.schema.ResourceLinkFactory.create;
import static com.elasticpath.rest.schema.util.ElementListFactory.createElementsOfList;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.collect.ImmutableList;

import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.orders.deliveries.DeliveryLookup;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.DeliveryListUriBuilderFactory;
import com.elasticpath.rest.schema.uri.OrdersUriBuilderFactory;

/**
 * Link handler.
 */
@Singleton
@Named("deliveriesLinkHandler")
public class DeliveriesLinkHandler implements ResourceStateLinkHandler<LinksEntity> {

	@Inject
	@Named("deliveryLookup")
	private DeliveryLookup deliveryLookup;

	@Inject
	@Named("deliveryListUriBuilderFactory")
	private DeliveryListUriBuilderFactory deliveryListUriBuilderFactory;

	@Inject
	@Named("ordersUriBuilderFactory")
	private OrdersUriBuilderFactory ordersUriBuilderFactory;

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<LinksEntity> resourceState) {
		LinksEntity linksEntity = resourceState.getEntity();

		if (!DELIVERY_LIST_NAME.equals(linksEntity
				.getName())) {
			return ImmutableList.of();
		}

		String orderId = linksEntity.getElementListId();
		String scope = resourceState.getScope();

		String orderUri = ordersUriBuilderFactory.get()
				.setOrderId(orderId)
				.setScope(scope)
				.build();

		return ImmutableList.<ResourceLink>builder()
				.addAll(createDeliveryLinks(orderId, scope, orderUri))
				.add(createOrderLink(orderUri))
				.build();
	}

	private ResourceLink createOrderLink(final String orderUri) {
		return create(
				orderUri,
				ORDER.id(),
				ORDER_REL,
				DELIVERIES_REV
		);
	}

	private Iterable<ResourceLink> createDeliveryLinks(final String orderId,
														final String scope,
														final String orderUri) {

		Collection<String> deliveryIds = deliveryLookup.getDeliveryIds(scope, orderId)
				.getData();

		String deliveriesSelfUri = deliveryListUriBuilderFactory.get()
				.setSourceUri(orderUri)
				.build();
		return createElementsOfList(
				deliveriesSelfUri,
				deliveryIds,
				DELIVERY.id()
		);
	}
}
