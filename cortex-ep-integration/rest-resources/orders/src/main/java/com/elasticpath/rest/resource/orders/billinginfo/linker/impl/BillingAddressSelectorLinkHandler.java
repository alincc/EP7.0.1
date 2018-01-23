/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billinginfo.linker.impl;

import static com.elasticpath.rest.definition.controls.ControlsMediaTypes.INFO;
import static com.elasticpath.rest.resource.orders.billinginfo.BillingInfoConstants.BILLING_ADDRESS_SELECTOR_NAME;
import static com.elasticpath.rest.resource.orders.billinginfo.rel.BillingInfoRepresentationRels.BILLING_ADDRESS_INFO_REL;
import static com.elasticpath.rest.schema.ResourceLinkFactory.create;
import static com.elasticpath.rest.common.selector.SelectorRepresentationRels.SELECTOR;
import static com.google.common.collect.Lists.newArrayList;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.controls.SelectorEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.orders.billinginfo.BillingAddressInfo;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Link handler.
 */
@Singleton
@Named("billingAddressSelectorLinkHandler")
public class BillingAddressSelectorLinkHandler implements ResourceStateLinkHandler<SelectorEntity> {

	@Inject
	@Named("resourceServerName")
	private String resourceServerName;

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<SelectorEntity> resourceState) {

		SelectorEntity selector = resourceState.getEntity();
		if (!BILLING_ADDRESS_SELECTOR_NAME.equals(selector.getName())) {
			return newArrayList();
		}

		String addressInfoUri = URIUtil.format(resourceServerName, resourceState.getScope(), selector.getSelectorId(), BillingAddressInfo.URI_PART);

		ResourceLink infoLink = create(
				addressInfoUri,
				INFO.id(),
				BILLING_ADDRESS_INFO_REL,
				SELECTOR
		);

		return newArrayList(infoLink);
	}
}
