/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.integration.epcommerce.transform;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.rest.definition.addresses.AddressDetailEntity;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.base.NameEntity;
import com.elasticpath.rest.resource.integration.commons.addresses.transform.AddressTransformer;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transforms a {@link CustomerAddress} into {@link AddressDetailEntity}, and vice versa.
 */
@Singleton
@Named("customerAddressTransformer")
public class CustomerAddressTransformer extends AbstractDomainTransformer<CustomerAddress, AddressEntity> {

	private final BeanFactory coreBeanFactory;
	private final AddressTransformer addressTransformer;

	/**
	 * Default constructor.
	 *
	 * @param coreBeanFactory the core bean factory
	 * @param addressTransformer the address transformer
	 */
	@Inject
	public CustomerAddressTransformer(
			@Named("coreBeanFactory")
			final BeanFactory coreBeanFactory,
			@Named("addressTransformer")
			final AddressTransformer addressTransformer) {

		this.coreBeanFactory = coreBeanFactory;
		this.addressTransformer = addressTransformer;
	}

	@Override
	public CustomerAddress transformToDomain(final AddressEntity addressEntity, final Locale locale) {
		CustomerAddress domainAddress = coreBeanFactory.getBean(ContextIdNames.CUSTOMER_ADDRESS);

		AddressDetailEntity address = addressEntity.getAddress();
		if (address != null) {
			domainAddress.setStreet1(address.getStreetAddress());
			domainAddress.setStreet2(address.getExtendedAddress());
			domainAddress.setCity(address.getLocality());
			domainAddress.setSubCountry(address.getRegion());
			domainAddress.setCountry(address.getCountryName());
			domainAddress.setZipOrPostalCode(address.getPostalCode());
		}

		NameEntity nameEntity = addressEntity.getName();

		if (nameEntity != null) {
			domainAddress.setFirstName(nameEntity.getGivenName());
			domainAddress.setLastName(nameEntity.getFamilyName());
		}

		return domainAddress;
	}

	/**
	 * Transform to entity.
	 *
	 * @param customerAddress the customer address
	 * @return the address entity
	 */
	public AddressEntity transformToEntity(final CustomerAddress customerAddress) {
		AddressEntity addressEntity = super.transformToEntity(customerAddress);
		return AddressEntity.builderFrom(addressEntity)
				.withAddressId(customerAddress.getGuid())
				.build();
	}

	@Override
	public AddressEntity transformToEntity(final CustomerAddress customerAddress, final Locale locale) {
		return addressTransformer.transformAddressToEntity(customerAddress);
	}
}
