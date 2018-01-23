/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.linker.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.orders.OrderLookup;
import com.elasticpath.rest.resource.orders.rel.OrdersRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Strategy to add order link to purchase.
 */
@Singleton
@Named("linkToPurchaseStrategy")
public final class LinkToPurchaseStrategy implements ResourceStateLinkHandler<PurchaseEntity> {

	private final String resourceServerName;
	private final OrderLookup orderLookup;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName resource server name
	 * @param orderLookup        order lookup
	 */
	@Inject
	LinkToPurchaseStrategy(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("orderLookup")
			final OrderLookup orderLookup) {

		this.resourceServerName = resourceServerName;
		this.orderLookup = orderLookup;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<PurchaseEntity> purchaseResourceState) {

		Collection<ResourceLink> linksToAdd;

		PurchaseEntity purchaseEntity = purchaseResourceState.getEntity();
		String scope = purchaseResourceState.getScope();
		String orderId = purchaseEntity.getOrderId();

		if (orderId == null) {
			linksToAdd = Collections.emptyList();
		} else {
			ExecutionResult<ResourceState<OrderEntity>> findOrderResult;

			try {
				findOrderResult = orderLookup.findOrderByOrderId(scope, orderId);
				ResourceState<OrderEntity> orderRepresentation = findOrderResult.getData();
				OrderEntity orderEntity = orderRepresentation.getEntity();
				String encodedOrderId = Base32Util.encode(orderEntity.getOrderId());
				String orderUri = URIUtil.format(resourceServerName, scope, encodedOrderId);
				ResourceLink orderLink = ResourceLinkFactory.createNoRev(orderUri, OrdersMediaTypes.ORDER
						.id(), OrdersRepresentationRels.ORDER_REL);
				linksToAdd = Collections.singleton(orderLink);
			} catch (BrokenChainException bce) {
				linksToAdd = Collections.emptyList();
			}
		}

		return linksToAdd;
	}
}
