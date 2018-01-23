/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.addresses.transform;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.purchases.addresses.BillingAddress;
import com.elasticpath.rest.resource.purchases.addresses.rel.BillingAddressResourceRels;
import com.elasticpath.rest.resource.purchases.constants.PurchaseResourceConstants;
import com.elasticpath.rest.resource.purchases.rel.PurchaseResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Transforms a {@link AddressEntity} to a {@link ResourceState}.
 */
@Singleton
@Named("purchaseBillingAddressTransformer")
public final class PurchaseBillingAddressTransformer {

	private final String resourceServerName;

	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resource server name
	 *
	 */
	@Inject
	public PurchaseBillingAddressTransformer(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}


	/**
	 * Transforms a purchase billing address entity into a representation.
	 *
	 *
	 * @param scope the scope
	 * @param purchaseId the purchase id
	 * @param billingAddressEntity the billing address entity
	 * @return the address representation.
	 */
	public ResourceState<AddressEntity> transformToRepresentation(final String scope, final String purchaseId,
			final AddressEntity billingAddressEntity) {

		String purchaseUri = URIUtil.format(resourceServerName, scope, purchaseId);
		String selfUri = URIUtil.format(purchaseUri, BillingAddress.URI_PART);

		Self self = SelfFactory.createSelf(selfUri);

		String addressId = BillingAddress.URI_PART;

		ResourceLink purchaseLink = ResourceLinkFactory.create(purchaseUri, PurchasesMediaTypes.PURCHASE.id(),
				PurchaseResourceRels.PURCHASE_REL, BillingAddressResourceRels.BILLING_ADDRESS_REV);

		AddressEntity updatedEntity = AddressEntity.builderFrom(billingAddressEntity)
				.withAddressId(addressId)
				.build();

		return ResourceState.Builder.create(updatedEntity)
				.withSelf(self)
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(PurchaseResourceConstants.MAX_AGE)
						.build())
				.withScope(scope)
				.addingLinks(purchaseLink)
				.build();
	}
}
