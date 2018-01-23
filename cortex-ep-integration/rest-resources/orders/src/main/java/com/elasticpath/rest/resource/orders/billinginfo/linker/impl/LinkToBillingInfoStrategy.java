/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billinginfo.linker.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.needinfo.NeedInfoFromInfoCommand;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.orders.billinginfo.BillingAddressInfo;
import com.elasticpath.rest.resource.orders.billinginfo.rel.BillingInfoRepresentationRels;
import com.elasticpath.rest.resource.orders.rel.OrdersRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Create a link to billing info on the order representation.
 */
@Singleton
@Named("linkToBillingInfoStrategy")
public final class LinkToBillingInfoStrategy implements ResourceStateLinkHandler<OrderEntity> {

	private final Provider<NeedInfoFromInfoCommand.Builder> needInfoProvider;

	/**
	 * Constructor that has the required dependencies injected.
	 *
	 * @param needInfoProvider Providers a need info builder
	 */
	@Inject
	public LinkToBillingInfoStrategy(
			@Named("needInfoFromInfoCommandBuilder")
			final Provider<NeedInfoFromInfoCommand.Builder> needInfoProvider) {

		this.needInfoProvider = needInfoProvider;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<OrderEntity> orderResourceState) {

		Collection<ResourceLink> orderLinks = new ArrayList<>();

		String billingInfoLinkUri = URIUtil.format(orderResourceState.getSelf()
				.getUri(), BillingAddressInfo.URI_PART);

		ResourceLink billingInfoLink = ResourceLinkFactory.create(
				billingInfoLinkUri,
				ControlsMediaTypes.INFO
						.id(),
				BillingInfoRepresentationRels.BILLING_ADDRESS_INFO_REL,
				OrdersRepresentationRels.ORDER_REV);
		orderLinks.add(billingInfoLink);
		// billing address is required for orders
		Command<Collection<ResourceLink>> needInfoCmd = needInfoProvider.get()
				.setNeededRel(BillingInfoRepresentationRels.BILLING_ADDRESS_REL)
				.setInfoUri(billingInfoLinkUri)
				.setInfoRel(BillingInfoRepresentationRels.BILLING_ADDRESS_INFO_REL)
				.setResourceState(orderResourceState)
				.build();

		ExecutionResult<Collection<ResourceLink>> needInfoResult = needInfoCmd.execute();
		if (needInfoResult.isSuccessful()) {
			orderLinks.addAll(needInfoResult.getData());
		}

		return orderLinks;
	}
}
