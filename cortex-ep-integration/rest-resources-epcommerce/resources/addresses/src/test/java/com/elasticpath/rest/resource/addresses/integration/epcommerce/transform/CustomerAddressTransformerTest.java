/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.integration.epcommerce.transform;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.addresses.AddressDetailEntity;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.base.NameEntity;
import com.elasticpath.rest.resource.integration.commons.addresses.transform.AddressTransformer;

/**
 * The test class for {@link CustomerAddressTransformer}.
 */
public class CustomerAddressTransformerTest {

	private static final String ADDRESS_GUID = "address_guid";
	private static final String ZIP_CODE = "zipCode";
	private static final String SUB_COUNTRY = "BC";
	private static final String COUNTRY = "CA";
	private static final String CITY = "city";
	private static final String STREET2 = "street2";
	private static final String STREET1 = "street1";
	private static final String LAST_NAME = "last_name";
	private static final String FIRST_NAME = "first_name";

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();
	private final BeanFactory mockBeanFactory = context.mock(BeanFactory.class);
	private final AddressTransformer addressTransformer = context.mock(AddressTransformer.class);

	private final CustomerAddressTransformer transformer = new CustomerAddressTransformer(mockBeanFactory, addressTransformer);

	/**
	 * Test transform to entity.
	 */
	@Test
	public void testTransformToEntity() {
		context.checking(new Expectations() {
			{
				allowing(addressTransformer).transformAddressToEntity(with(any(Address.class)));
				will(returnValue(ResourceTypeFactory.createResourceEntity(AddressEntity.class)));
			}
		});

		CustomerAddress address = createAddress();

		AddressEntity addressEntity = transformer.transformToEntity(address);

		assertEquals(address.getGuid(), addressEntity.getAddressId());
	}

	/**
	 * Test transform to domain.
	 */
	@Test
	public void testTransformToDomain() {
		context.checking(new Expectations() {
			{
				allowing(mockBeanFactory).getBean(ContextIdNames.CUSTOMER_ADDRESS);
				will(returnValue(new CustomerAddressImpl()));
			}
		});

		AddressEntity addressEntity = createAddressEntity();

		CustomerAddress customerAddress = transformer.transformToDomain(addressEntity);

		assertEquals(FIRST_NAME, customerAddress.getFirstName());
		assertEquals(LAST_NAME, customerAddress.getLastName());
		assertEquals(STREET1, customerAddress.getStreet1());
		assertEquals(STREET2, customerAddress.getStreet2());
		assertEquals(CITY, customerAddress.getCity());
		assertEquals(COUNTRY, customerAddress.getCountry());
		assertEquals(SUB_COUNTRY, customerAddress.getSubCountry());
		assertEquals(ZIP_CODE, customerAddress.getZipOrPostalCode());
	}

	private CustomerAddress createAddress() {
		CustomerAddress address = new CustomerAddressImpl();

		address.setFirstName(FIRST_NAME);
		address.setLastName(LAST_NAME);
		address.setStreet1(STREET1);
		address.setStreet2(STREET2);
		address.setCity(CITY);
		address.setCountry(COUNTRY);
		address.setSubCountry(SUB_COUNTRY);
		address.setZipOrPostalCode(ZIP_CODE);
		address.setGuid(ADDRESS_GUID);

		return address;
	}

	private AddressEntity createAddressEntity() {
		AddressDetailEntity address = AddressDetailEntity.builder()
				.withStreetAddress(STREET1)
				.withExtendedAddress(STREET2)
				.withLocality(CITY)
				.withCountryName(COUNTRY)
				.withRegion(SUB_COUNTRY)
				.withPostalCode(ZIP_CODE)
				.build();

		NameEntity name = NameEntity.builder()
				.withFamilyName(LAST_NAME)
				.withGivenName(FIRST_NAME)
				.build();

		return AddressEntity.builder()
				.withAddress(address)
				.withName(name)
				.build();
	}

}
