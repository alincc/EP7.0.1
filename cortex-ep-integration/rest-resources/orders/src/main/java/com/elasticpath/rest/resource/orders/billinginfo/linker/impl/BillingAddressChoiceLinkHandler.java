/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billinginfo.linker.impl;

import static com.elasticpath.rest.ResourceStatus.NOT_FOUND;
import static com.elasticpath.rest.definition.addresses.AddressesMediaTypes.ADDRESS;
import static com.elasticpath.rest.definition.controls.ControlsMediaTypes.SELECTOR;
import static com.elasticpath.rest.resource.orders.billinginfo.BillingInfoConstants.BILLING_ADDRESS_LIST_NAME;
import static com.elasticpath.rest.schema.ResourceLinkFactory.createNoRev;
import static com.elasticpath.rest.schema.ResourceLinkFactory.createUriRel;
import static com.elasticpath.rest.common.selector.SelectorRepresentationRels.DESCRIPTION;
import static com.elasticpath.rest.common.selector.SelectorRepresentationRels.SELECT_ACTION;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.ExecutionResultFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.orders.billinginfo.BillingAddressInfo;
import com.elasticpath.rest.resource.orders.billinginfo.BillingInfoLookup;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.common.selector.SelectorRepresentationRels;
import com.elasticpath.rest.schema.uri.BillingAddressListUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Link handler.
 */
@Singleton
@Named("billingAddressChoiceLinkHandler")
public class BillingAddressChoiceLinkHandler implements ResourceStateLinkHandler<LinksEntity> {

	private static final Logger LOG = LoggerFactory.getLogger(BillingAddressChoiceLinkHandler.class);

	@Inject
	@Named("resourceServerName")
	private String resourceServerName;

	@Inject
	@Named("billingInfoLookup")
	private BillingInfoLookup billingInfoLookup;

	@Inject
	@Named("billingAddressListUriBuilderFactory")
	private BillingAddressListUriBuilderFactory billingAddressUriBuilderFactory;


	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<LinksEntity> resourceState) {
		LinksEntity listEntity = resourceState.getEntity();
		if (!BILLING_ADDRESS_LIST_NAME.equals(listEntity.getName())) {
			return Collections.emptyList();
		}

		String scope = resourceState.getScope();
		String orderId = listEntity
				.getElementListId();

		String billingAddressListUri = billingAddressUriBuilderFactory.get()
				.setScope(scope)
				.build();

		String orderSelectorUri = URIUtil.format(
				resourceServerName,
				scope,
				orderId,
				BillingAddressInfo.URI_PART,
				Selector.URI_PART,
				billingAddressListUri
		);

		Collection<ResourceLink> resourceLinks = newArrayList(
				createResourceLink(orderSelectorUri),
				createDescriptionLink(resourceState.getSelf().getUri())
		);

		createActionLink(resourceLinks, orderId, scope, resourceState.getSelf().getUri());

		return resourceLinks;
	}

	private ResourceLink createResourceLink(final String orderSelectorUri) {
		return createNoRev(
				orderSelectorUri,
				SELECTOR.id(),
				SelectorRepresentationRels.SELECTOR
		);
	}

	private ResourceLink createDescriptionLink(final String uri) {

		String billingAddressListUri = StringUtils.substringAfter(uri, Selector.PATH_PART);

		return createNoRev(
				billingAddressListUri,
				ADDRESS.id(),
				DESCRIPTION
		);
	}

	private void createActionLink(final Collection<ResourceLink> resourceLinks,
									final String orderId,
									final String scope,
									final String uri) {

		String billingAddressId = StringUtils.substringAfter(uri, "addresses/");

		ExecutionResult<String> findSelectedAddressForOrderResult;

		try {
			findSelectedAddressForOrderResult = billingInfoLookup.findAddressForOrder(scope, orderId);
		} catch (BrokenChainException bce) {
			findSelectedAddressForOrderResult = Assign.ifBrokenChainExceptionStatus(
					bce,
					NOT_FOUND,
					ExecutionResultFactory.createCreateOKWithData(StringUtils.EMPTY, false));
		}
		if (addressIsNotSelected(billingAddressId, findSelectedAddressForOrderResult)) {
			ResourceLink selectAddressActionLink = createUriRel(
					uri,
					SELECT_ACTION
			);
			resourceLinks.add(selectAddressActionLink);
		}
	}

	private boolean addressIsNotSelected(final String addressId, final ExecutionResult<String> selectedAddressResult) {
		ResourceStatus resourceStatus = selectedAddressResult.getResourceStatus();

		if (selectedAddressResult.isFailure() && !resourceStatus.equals(NOT_FOUND)) {
			LOG.warn("Unexpected error - {} {}", resourceStatus, selectedAddressResult.getErrorMessage());
		}

		return selectedAddressResult.isFailure() || !addressId.equals(selectedAddressResult.getData());
	}
}
