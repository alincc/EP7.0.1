/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.alias.shipping.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.OperationResultFactory;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.addresses.helper.AddressLinkCreationHelper;
import com.elasticpath.rest.resource.addresses.integration.addresses.AddressLookupStrategy;
import com.elasticpath.rest.resource.addresses.integration.addresses.alias.DefaultAddressLookupStrategy;
import com.elasticpath.rest.resource.addresses.rel.AddressResourceRels;
import com.elasticpath.rest.resource.addresses.shipping.Shipping;
import com.elasticpath.rest.resource.addresses.transform.BillingShippingAddressTransformer;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.uri.URIUtil;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Resource Operator for shipping address.
 */
@Singleton
@Named("shippingAddressListOperator")
@Path({ResourceName.PATH_PART, Scope.PATH_PART, Shipping.PATH_PART})
public class ShippingAddressListOperatorImpl implements ResourceOperator {

	private final DefaultAddressLookupStrategy defaultShippingAddressLookupStrategy;
	private final AddressLookupStrategy addressLookupStrategy;
	private final String resourceServerName;
	private final ResourceOperationContext resourceOperationContext;
	private final BillingShippingAddressTransformer billingShippingAddressTransformer;
	private final AddressLinkCreationHelper addressLinkCreationHelper;

	/**
	 * Constructor.
	 *
	 * @param defaultShippingAddressLookupStrategy lookup default address
	 * @param addressLookupStrategy address lookup
	 * @param resourceServerName the resource server name
	 * @param resourceOperationContext used to look up user id
	 * @param billingShippingAddressTransformer address list transformer
	 * @param addressLinkCreationHelper link creation helper
	 */
	@Inject
	ShippingAddressListOperatorImpl(
			@Named("defaultShippingAddressLookupStrategy")
			final DefaultAddressLookupStrategy defaultShippingAddressLookupStrategy,
			@Named("addressLookupStrategy")
			final AddressLookupStrategy addressLookupStrategy,
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("billingShippingAddressTransformer")
			final BillingShippingAddressTransformer billingShippingAddressTransformer,
			@Named("addressLinkCreationHelper")
			final AddressLinkCreationHelper addressLinkCreationHelper) {

		this.defaultShippingAddressLookupStrategy = defaultShippingAddressLookupStrategy;
		this.addressLookupStrategy = addressLookupStrategy;
		this.resourceServerName = resourceServerName;
		this.resourceOperationContext = resourceOperationContext;
		this.billingShippingAddressTransformer = billingShippingAddressTransformer;
		this.addressLinkCreationHelper = addressLinkCreationHelper;
	}


	/**
	 * Process read operator.
	 *
	 * @param scope the scope
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
		Collection<String> decodedAddressIds = Assign.ifSuccessful(addressLookupStrategy.findShippingIdsByUserId(scope, userIdentifier));
		Collection<String> shippingAddressIds = Base32Util.encodeAll(decodedAddressIds);

		return OperationResultFactory.create(operation,
				ResourceStatus.READ_OK,
				billingShippingAddressTransformer.transform(
						scope,
						createShippingAddressLinks(shippingAddressIds, scope, selfUri)
				));
	}

	private Collection<ResourceLink> createShippingAddressLinks(final Collection<String> addressIds, final String scope, final String selfUri) {

		Collection<ResourceLink> resourceLinks = new ArrayList<>();

		resourceLinks.addAll(addressLinkCreationHelper.createElementLinks(scope, addressIds));
		ResourceLink preferredShippingAddressLink = addressLinkCreationHelper.createPreferredAddressLink(scope,
				selfUri, AddressResourceRels.DEFAULT_REL, null, defaultShippingAddressLookupStrategy);
		if (preferredShippingAddressLink != null) {
			resourceLinks.add(preferredShippingAddressLink);
		}
		resourceLinks.add(addressLinkCreationHelper.createProfileLink(scope, AddressResourceRels.PROFILE_REL, null));
		resourceLinks.add(addressLinkCreationHelper.createGenericLink(
				selfUri,
				AddressResourceRels.ADDRESSES_REL,
				AddressResourceRels.SHIPPING_ADDRESSES_REV));
		return resourceLinks;
	}
}
