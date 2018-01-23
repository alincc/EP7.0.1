/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billinginfo.linker.impl;

import static com.elasticpath.rest.definition.addresses.AddressesMediaTypes.ADDRESS;
import static com.elasticpath.rest.definition.controls.ControlsMediaTypes.SELECTOR;
import static com.elasticpath.rest.definition.orders.OrdersMediaTypes.ORDER;
import static com.elasticpath.rest.resource.orders.billinginfo.BillingInfoConstants.BILLING_ADDRESS_INFO_NAME;
import static com.elasticpath.rest.resource.orders.billinginfo.rel.BillingInfoRepresentationRels.ADDRESS_FORM_REL;
import static com.elasticpath.rest.resource.orders.billinginfo.rel.BillingInfoRepresentationRels.BILLING_ADDRESS_INFO_REV;
import static com.elasticpath.rest.resource.orders.billinginfo.rel.BillingInfoRepresentationRels.BILLING_ADDRESS_REL;
import static com.elasticpath.rest.resource.orders.rel.OrdersRepresentationRels.ORDER_REL;
import static com.elasticpath.rest.schema.ResourceLinkFactory.create;
import static com.elasticpath.rest.schema.ResourceLinkFactory.createNoRev;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.BrokenChainException;
import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.orders.billinginfo.BillingInfoLookup;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.common.selector.SelectorRepresentationRels;
import com.elasticpath.rest.schema.uri.BillingAddressListUriBuilderFactory;
import com.elasticpath.rest.schema.uri.OrdersUriBuilderFactory;
import com.elasticpath.rest.schema.uri.AddressFormUriBuilderFactory;
import com.elasticpath.rest.schema.uri.AddressUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Link handler.
 */
@Named("billingAddressInfoLinkHandler")
@Singleton
public class BillingAddressInfoLinkHandler implements ResourceStateLinkHandler<InfoEntity> {

	@Inject
	@Named("billingInfoLookup")
	private BillingInfoLookup billingInfoLookup;

	@Inject
	@Named("ordersUriBuilderFactory")
	private OrdersUriBuilderFactory ordersUriBuilderFactory;

	@Inject
	@Named("billingAddressListUriBuilderFactory")
	private BillingAddressListUriBuilderFactory billingAddressListUriBuilderFactory;

	@Inject
	@Named("addressUriBuilderFactory")
	private AddressUriBuilderFactory addressUriBuilderFactory;

	@Inject
	@Named("addressFormUriBuilderFactory")
	private AddressFormUriBuilderFactory addressFormUriBuilderFactory;

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<InfoEntity> resourceState) {

		InfoEntity entity = resourceState.getEntity();

		if (!BILLING_ADDRESS_INFO_NAME.equals(entity.getName())) {
			return Collections.emptyList();
		}

		Collection<ResourceLink> resourceLinks = newArrayList(
				createOrderLink(entity.getInfoId(), resourceState.getScope()),
				createSelectorLink(resourceState.getScope(), resourceState.getSelf().getUri())
		);

		createAddressLink(resourceLinks, resourceState.getScope(), entity.getInfoId());

		createAddressForm(resourceLinks, resourceState.getScope());

		return resourceLinks;
	}

	private void createAddressForm(final Collection<ResourceLink> resourceLinks, final String scope) {

		String billingAddressFormUri = addressFormUriBuilderFactory.get()
				.setScope(scope)
				.build();

		resourceLinks.add(createNoRev(billingAddressFormUri,
				ADDRESS.id(),
				ADDRESS_FORM_REL));
	}

	private void createAddressLink(final Collection<ResourceLink> resourceLinks,
									final String scope,
									final String orderId) {

		String selectedAddressId;
		try {
			selectedAddressId = Assign.ifSuccessful(billingInfoLookup.findAddressForOrder(scope, orderId));
		} catch (BrokenChainException bce) {
			selectedAddressId = Assign.ifBrokenChainExceptionStatus(bce, ResourceStatus.NOT_FOUND, StringUtils.EMPTY);
		}
		if (!isNullOrEmpty(selectedAddressId)) {
			String billingAddressUri = addressUriBuilderFactory.get()
					.setAddressId(selectedAddressId)
					.setScope(scope)
					.build();

			resourceLinks.add(createNoRev(billingAddressUri,
					ADDRESS.id(),
					BILLING_ADDRESS_REL));
		}
	}

	private ResourceLink createOrderLink(final String infoId,
										final String scope) {

		String orderUri = ordersUriBuilderFactory.get()
				.setOrderId(infoId)
				.setScope(scope)
				.build();

		return create(
				orderUri,
				ORDER.id(),
				ORDER_REL,
				BILLING_ADDRESS_INFO_REV
		);
	}

	private ResourceLink createSelectorLink(final String scope, final String selfUri) {

		String billingAddressListUri = billingAddressListUriBuilderFactory.get()
				.setScope(scope)
				.build();
		String selectorUri = URIUtil.format(selfUri, Selector.URI_PART, billingAddressListUri);

		return create(
				selectorUri,
				SELECTOR.id(),
				SelectorRepresentationRels.SELECTOR,
				BILLING_ADDRESS_INFO_REV
		);
	}
}
