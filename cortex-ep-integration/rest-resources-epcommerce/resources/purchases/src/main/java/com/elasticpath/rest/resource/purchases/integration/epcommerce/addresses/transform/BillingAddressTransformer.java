/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.integration.epcommerce.addresses.transform;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.resource.integration.commons.addresses.transform.AddressTransformer;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transforms a {@link Address} into {@link AddressEntity}, and vice versa.
 */
@Singleton
@Named("billingAddressTransformer")
public class BillingAddressTransformer extends AbstractDomainTransformer<Address, AddressEntity> {

	private final AddressTransformer addressTransformer;

	/**
	 * Constructor.
	 * @param addressTransformer the address transformer
	 */
	@Inject
	public BillingAddressTransformer(
			@Named("addressTransformer")
			final AddressTransformer addressTransformer) {

		this.addressTransformer = addressTransformer;
	}

	@Override
	public Address transformToDomain(final AddressEntity addressEntity, final Locale locale) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public AddressEntity transformToEntity(final Address customerAddress, final Locale locale) {
		return addressTransformer.transformAddressToEntity(customerAddress);
	}
}
