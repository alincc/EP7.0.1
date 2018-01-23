/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.emailinfo.linker.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.rel.NeedInfoRels;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.orders.emailinfo.EmailInfo;
import com.elasticpath.rest.resource.orders.emailinfo.EmailInfoLookup;
import com.elasticpath.rest.resource.orders.emailinfo.EmailInfoRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Strategy to add email info needinfo links onto the order.
 */
@Singleton
@Named("addEmailInfoLinksToOrderStrategy")
public final class AddEmailInfoLinksToOrderStrategy implements ResourceStateLinkHandler<OrderEntity> {

	private final EmailInfoLookup emailInfoLookup;

	/**
	 * Constructor that has the required dependencies injected.
	 *
	 * @param emailInfoLookup the email info lookup
	 */
	@Inject
	public AddEmailInfoLinksToOrderStrategy(
			@Named("emailInfoLookup")
			final EmailInfoLookup emailInfoLookup) {

		this.emailInfoLookup = emailInfoLookup;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<OrderEntity> orderResourceState) {

		Collection<ResourceLink> orderLinks = new ArrayList<>();

		OrderEntity orderEntity = orderResourceState.getEntity();
		String emailInfoUri = URIUtil.format(orderResourceState.getSelf()
				.getUri(), EmailInfo.URI_PART);

		ResourceLink emailInfoLink = ResourceLinkFactory.create(emailInfoUri,
				ControlsMediaTypes.INFO
						.id(),
				EmailInfoRepresentationRels.EMAIL_INFO_REL,
				EmailInfoRepresentationRels.ORDER_REV);

		orderLinks.add(emailInfoLink);

		String scope = orderResourceState.getScope();
		String orderId = orderEntity.getOrderId();

		try {
			emailInfoLookup.findEmailIdForOrder(scope, orderId);
		} catch (BrokenChainException bce) {
			ResourceLink emailNeedInfoLink = ResourceLinkFactory.createNoRev(emailInfoUri,
					ControlsMediaTypes.INFO
							.id(),
					NeedInfoRels.NEEDINFO);

			orderLinks.add(emailNeedInfoLink);
		}
		return orderLinks;
	}
}
