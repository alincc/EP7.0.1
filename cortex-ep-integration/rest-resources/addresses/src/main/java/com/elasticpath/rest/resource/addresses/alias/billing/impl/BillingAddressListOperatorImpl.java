/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.alias.billing.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.OperationResultFactory;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.addresses.billing.Billing;
import com.elasticpath.rest.resource.addresses.helper.AddressLinkCreationHelper;
import com.elasticpath.rest.resource.addresses.integration.addresses.AddressLookupStrategy;
import com.elasticpath.rest.resource.addresses.integration.addresses.alias.DefaultAddressLookupStrategy;
import com.elasticpath.rest.resource.addresses.rel.AddressResourceRels;
import com.elasticpath.rest.resource.addresses.transform.BillingShippingAddressTransformer;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Resource Operator for billing address.
 */
@Singleton
@Named("billingAddressListOperator")
@Path({ResourceName.PATH_PART, Scope.PATH_PART, Billing.PATH_PART})
public class BillingAddressListOperatorImpl implements ResourceOperator {

	private final AddressLookupStrategy addressLookupStrategy;
	private final BillingShippingAddressTransformer billingShippingAddressTransformer;
	private final String resourceServerName;
	private final ResourceOperationContext resourceOperationContext;
	private final DefaultAddressLookupStrategy defaultBillingAddressLookupStrategy;
	private final AddressLinkCreationHelper addressLinkCreationHelper;

	/**
	 * Constructor.
	 *
	 * @param addressLookupStrategy address lookup strategy
	 * @param billingShippingAddressTransformer address transformer
	 * @param resourceServerName the resource server name
	 * @param resourceOperationContext used to look up user id
	 * @param defaultBillingAddressLookupStrategy get default billing address ids
	 * @param addressLinkCreationHelper link creation helper
	 */
	@Inject
	BillingAddressListOperatorImpl(
			@Named("addressLookupStrategy")
			final AddressLookupStrategy addressLookupStrategy,
			@Named("billingShippingAddressTransformer")
			final BillingShippingAddressTransformer billingShippingAddressTransformer,
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("defaultBillingAddressLookupStrategy")
			final DefaultAddressLookupStrategy defaultBillingAddressLookupStrategy,
			@Named("addressLinkCreationHelper")
			final AddressLinkCreationHelper addressLinkCreationHelper) {

		this.addressLookupStrategy = addressLookupStrategy;
		this.billingShippingAddressTransformer = billingShippingAddressTransformer;
		this.resourceServerName = resourceServerName;
		this.resourceOperationContext = resourceOperationContext;
		this.defaultBillingAddressLookupStrategy = defaultBillingAddressLookupStrategy;
		this.addressLinkCreationHelper = addressLinkCreationHelper;
	}


	/**
	 * Process read operator.
	 *
	 * @param scope the scope.
	 * @param operation the Resource Operation.
	 * @return the operation result.
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processRead(
			@Scope
			final String scope,
			final ResourceOperation operation) {

		String selfUri = URIUtil.format(resourceServerName, scope);

		String userIdentifier = resourceOperationContext.getUserIdentifier();
		Collection<String> decodedAddressIds = Assign.ifSuccessful(addressLookupStrategy.findBillingIdsByUserId(scope, userIdentifier));
		Collection<String> billingAddressIds = Base32Util.encodeAll(decodedAddressIds);

		return OperationResultFactory.createReadOK(
						billingShippingAddressTransformer.transform(
								scope,
								createBillingAddressLinks(billingAddressIds, scope, selfUri)),
						operation);
	}

	private Collection<ResourceLink> createBillingAddressLinks(final Collection<String> addressIds, final String scope, final String selfUri) {
		Collection<ResourceLink> resourceLinks = new ArrayList<>();

		resourceLinks.addAll(addressLinkCreationHelper.createAddressesLinks(scope, addressIds));
		ResourceLink preferredBillingAddressLink = addressLinkCreationHelper.
				createPreferredAddressLink(scope, selfUri, AddressResourceRels.DEFAULT_REL, null, defaultBillingAddressLookupStrategy);
		if (preferredBillingAddressLink != null) {
			resourceLinks.add(preferredBillingAddressLink);
		}
		resourceLinks.add(addressLinkCreationHelper.createProfileLink(scope, AddressResourceRels.PROFILE_REL, null));
		resourceLinks.add(addressLinkCreationHelper.createGenericLink(
				selfUri,
				AddressResourceRels.ADDRESSES_REL,
				AddressResourceRels.BILLING_ADDRESSES_REV));
		return resourceLinks;
	}
}
