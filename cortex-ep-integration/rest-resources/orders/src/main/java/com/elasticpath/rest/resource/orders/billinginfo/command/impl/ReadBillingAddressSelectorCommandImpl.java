/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billinginfo.command.impl;

import static com.elasticpath.rest.definition.collections.CollectionsMediaTypes.LINKS;
import static com.elasticpath.rest.definition.orders.OrdersMediaTypes.ORDER;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.chain.BrokenChainException;
import com.google.common.collect.ImmutableSet;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.common.selector.SelectorResourceStateBuilder;
import com.elasticpath.rest.definition.addresses.AddressesMediaTypes;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.controls.SelectorEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.orders.OrderLookup;
import com.elasticpath.rest.resource.orders.billinginfo.BillingAddressInfo;
import com.elasticpath.rest.resource.orders.billinginfo.BillingInfoConstants;
import com.elasticpath.rest.resource.orders.billinginfo.BillingInfoLookup;
import com.elasticpath.rest.resource.orders.billinginfo.command.ReadBillingAddressSelectorCommand;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.AddressUriBuilderFactory;
import com.elasticpath.rest.schema.uri.BillingAddressListUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceLinkUtil;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Command to read an order's billing information.
 */
@Named
public final class ReadBillingAddressSelectorCommandImpl implements ReadBillingAddressSelectorCommand {

	private final AddressUriBuilderFactory addressUriBuilderFactory;
	private final SelectorResourceStateBuilder selectorBuilder;
	private final BillingAddressListUriBuilderFactory billingAddressUriBuilderFactory;
	private final BillingInfoLookup billingInfoLookup;
	private final OrderLookup orderLookup;

	private ResourceState<LinksEntity> billingAddressLinks;

	private String orderId;

	private String scope;

	/**
	 * Constructor for injection.
	 *
	 * @param addressUriBuilderFactory the address uri builder factory
	 * @param selectorBuilder the selector builder
	 * @param billingAddressUriBuilderFactory the profiles billing address uri builder factory
	 * @param billingInfoLookup the billing info lookup
	 * @param orderLookup the order entity lookup
	 */
	@Inject
	public ReadBillingAddressSelectorCommandImpl(
			@Named("addressUriBuilderFactory")
			final AddressUriBuilderFactory addressUriBuilderFactory,
			@Named("singleSelectorResourceStateBuilder")
			final SelectorResourceStateBuilder selectorBuilder,
			@Named("billingAddressListUriBuilderFactory")
			final BillingAddressListUriBuilderFactory billingAddressUriBuilderFactory,
			@Named("billingInfoLookup")
			final BillingInfoLookup billingInfoLookup,
			@Named("orderLookup")
			final OrderLookup orderLookup) {

		this.addressUriBuilderFactory = addressUriBuilderFactory;
		this.selectorBuilder = selectorBuilder;
		this.billingAddressUriBuilderFactory = billingAddressUriBuilderFactory;
		this.billingInfoLookup = billingInfoLookup;
		this.orderLookup = orderLookup;
	}


	@Override
	public ExecutionResult<ResourceState<SelectorEntity>> execute() {

		ResourceState<OrderEntity> order = Assign.ifSuccessful(orderLookup.findOrderByOrderId(scope, orderId));

		// Filter the non-address links then remove the duplicates.
		List<ResourceLink> addressResourceLinks = ResourceLinkUtil.findLinksByType(billingAddressLinks, AddressesMediaTypes.ADDRESS.id());
		addressResourceLinks = ImmutableSet.copyOf(addressResourceLinks).asList();

		String orderUri = order.getSelf().getUri();
		String addressParentUri = URIUtil.format(orderUri, BillingAddressInfo.URI_PART, Selector.URI_PART);
		String billingAddressListUri = billingAddressUriBuilderFactory.get().setScope(scope).build();
		String selfUri = URIUtil.format(addressParentUri, billingAddressListUri);

		ResourceLink selectedAddressLink = null;
		String billingAddressId;
		try {
			billingAddressId = Assign.ifSuccessful(billingInfoLookup.findAddressForOrder(scope, orderId));
		} catch (BrokenChainException bce) {
			billingAddressId = Assign.ifBrokenChainExceptionStatus(bce, ResourceStatus.NOT_FOUND, StringUtils.EMPTY);
		}

		if (StringUtils.isNotEmpty(billingAddressId)) {
			selectedAddressLink = buildSelectedBillingAddressLink(scope, addressResourceLinks, billingAddressId, addressParentUri);
		}
		ResourceState<SelectorEntity> billingInfo = createBillingInfo(
				selfUri,
				addressParentUri,
				addressResourceLinks,
				selectedAddressLink
		);
		return ExecutionResultFactory.createReadOK(billingInfo);
	}

	private ResourceState<SelectorEntity> createBillingInfo(final String selfUri,
															final String addressParentUri,
															final Collection<ResourceLink> billingAddresses,
															final ResourceLink selectedAddress) {

		selectorBuilder.setSelectorId(orderId);
		selectorBuilder.setSelectorType(ORDER.entityClass());
		selectorBuilder.setScope(scope);

		selectorBuilder.setName(BillingInfoConstants.BILLING_ADDRESS_SELECTOR_NAME)
				.setSelfUri(selfUri);

		for (ResourceLink billingAddress : billingAddresses) {
			ResourceLink billingAddressChoiceLink = ResourceLinkFactory.createUriType(URIUtil.format(addressParentUri,
					billingAddress.getUri()),
					LINKS.id());
			selectorBuilder.addChoice(billingAddressChoiceLink);
		}

		// Only set the selection if the address is in the billing addresses
		if (selectedAddress != null) {
			selectorBuilder.setSelection(selectedAddress);
		}

		return selectorBuilder.build();
	}

	private ResourceLink buildSelectedBillingAddressLink(final String scope,
			final Collection<ResourceLink> billingAddressLinks,
			final String selectedBillingAddressId,
			final String addressParentUri) {

		ResourceLink result = null;
		String selectedAddressUri = addressUriBuilderFactory.get()
				.setScope(scope)
				.setAddressId(selectedBillingAddressId)
				.build();

		for (ResourceLink billingAddress : billingAddressLinks) {
			String billingAddressUri = billingAddress.getUri();
			if (billingAddressUri.equalsIgnoreCase(selectedAddressUri)) {
				result = ResourceLinkFactory.createUriType(URIUtil.format(addressParentUri, billingAddress.getUri()), LINKS.id());
				break;
			}
		}

		return result;
	}

	/**
	 * Read billing information command builder.
	 */
	@Named("readBillingInfoSelectorCommandBuilder")
	public static class BuilderImpl implements ReadBillingAddressSelectorCommand.Builder {

		private final ReadBillingAddressSelectorCommandImpl command;

		/**
		 * Instantiates a new builder.
		 *
		 * @param command the command
		 */
		@Inject
		public BuilderImpl(final ReadBillingAddressSelectorCommandImpl command) {
			this.command = command;
		}

		@Override
		public Builder setBillingAddressLinks(final ResourceState<LinksEntity> billingAddressLinks) {
			command.billingAddressLinks = billingAddressLinks;
			return this;
		}

		@Override
		public Builder setOrderId(final String orderId) {
			command.orderId = orderId;
			return this;
		}

		@Override
		public Builder setScope(final String scope) {
			command.scope = scope;
			return this;
		}

		@Override
		public ReadBillingAddressSelectorCommand build() {
			assert command.billingAddressLinks != null : "billingAddressLinks must be set.";
			assert command.orderId != null : "orderId must be set.";
			assert command.scope != null : "scope must be set.";
			return command;
		}
	}
}
