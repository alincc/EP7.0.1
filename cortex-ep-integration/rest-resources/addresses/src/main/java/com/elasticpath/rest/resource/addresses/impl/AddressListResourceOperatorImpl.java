/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.impl;

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
import com.elasticpath.rest.resource.addresses.helper.AddressLinkCreationHelper;
import com.elasticpath.rest.resource.addresses.integration.addresses.AddressLookupStrategy;
import com.elasticpath.rest.resource.addresses.rel.AddressResourceRels;
import com.elasticpath.rest.resource.addresses.transform.AddressListTransformer;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.schema.ResourceLink;

/**
 * Resource operator for the sub resource Address.
 */
@Singleton
@Named("addressListResourceOperator")
@Path({ResourceName.PATH_PART, Scope.PATH_PART})
public class AddressListResourceOperatorImpl implements ResourceOperator {

	private final AddressLookupStrategy addressLookupStrategy;
	private final AddressListTransformer addressListTransformer;
	private final ResourceOperationContext resourceOperationContext;
	private final AddressLinkCreationHelper addressLinkCreationHelper;

	/**
	 * Constructor.
	 * @param addressLookupStrategy address lookup strategy
	 * @param addressListTransformer address list transformer
	 * @param resourceOperationContext used to look up user id
	 * @param addressLinkCreationHelper link creation helper
	 */
	@Inject
	public AddressListResourceOperatorImpl(
			@Named("addressLookupStrategy")
			final AddressLookupStrategy addressLookupStrategy,
			@Named("addressListTransformer")
			final AddressListTransformer addressListTransformer,
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("addressLinkCreationHelper")
			final AddressLinkCreationHelper addressLinkCreationHelper) {
		this.addressLookupStrategy = addressLookupStrategy;
		this.addressListTransformer = addressListTransformer;
		this.resourceOperationContext = resourceOperationContext;
		this.addressLinkCreationHelper = addressLinkCreationHelper;
	}

	/**
	 * Handles the READ operations for reading current user's addresses.<br>
	 * A sample URI: "http://mymachine/cortex/addresses/rockjam/"
	 *
	 * @param scope the scope
	 * @param operation the Resource Operation
	 * @return the operation result
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processReadAddresses(
			@Scope
			final String scope,
			final ResourceOperation operation) {

		String userIdentifier = resourceOperationContext.getUserIdentifier();
		Collection<String> decodedAddressIds = Assign.ifSuccessful(addressLookupStrategy.findIdsByUserId(scope, userIdentifier));
		Collection<String> addressIds = Base32Util.encodeAll(decodedAddressIds);

		return OperationResultFactory.createReadOK(
						addressListTransformer.transform(
								scope,
								createAddressLinks(addressIds, scope, operation.getUri())),
				operation);
	}

	private Collection<ResourceLink> createAddressLinks(final Collection<String> addressIds, final String scope, final String selfUri) {
		Collection<ResourceLink> resourceLinks = new ArrayList<>();

		resourceLinks.addAll(addressLinkCreationHelper.createAddressesLinks(scope, addressIds));
		resourceLinks.add(addressLinkCreationHelper.createProfileLink(
				scope,
				AddressResourceRels.PROFILE_REL,
				AddressResourceRels.ADDRESSES_REV));
		resourceLinks.add(addressLinkCreationHelper.createShippingAddressesLink(
				selfUri,
				AddressResourceRels.SHIPPING_ADDRESSES_REL,
				AddressResourceRels.ADDRESSES_REV));
		resourceLinks.add(addressLinkCreationHelper.createBillingAddressesLink(
				selfUri,
				AddressResourceRels.BILLING_ADDRESSES_REL,
				AddressResourceRels.ADDRESSES_REV));
		resourceLinks.add(addressLinkCreationHelper.createAddressFormLink(scope, AddressResourceRels.ADDRESS_FORM_REL, null));
		return resourceLinks;
	}
}
