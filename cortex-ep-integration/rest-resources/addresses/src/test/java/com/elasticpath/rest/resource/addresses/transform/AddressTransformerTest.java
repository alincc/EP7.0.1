/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.transform;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.definition.addresses.AddressDetailEntity;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.base.NameEntity;
import com.elasticpath.rest.definition.profiles.ProfilesMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.addresses.helper.AddressLinkCreationHelper;
import com.elasticpath.rest.resource.addresses.rel.AddressResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Test class for {@link AddressTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class AddressTransformerTest {

	private static final String EXPECTED_ADDRESS_ENTITY = "The returned address entity should be the same as expected.";
	private static final String DECODED_ADDRESS_ID = "DECODED_ADDRESS_ID";
	private static final String ADDRESS_ID = Base32Util.encode(DECODED_ADDRESS_ID);
	private static final String ZIP_CODE = "ZIP_CODE";
	private static final String SUB_COUNTRY = "SUB_COUNTRY";
	private static final String COUNTRY = "COUNTRY";
	private static final String CITY = "CITY";
	private static final String STREET2 = "STREET2";
	private static final String STREET1 = "STREET1";
	private static final String LAST_NAME = "LAST_NAME";
	private static final String FIRST_NAME = "FIRST_NAME";
	private static final String SCOPE = "SCOPE";
	private static final String PROFILES_URI = URIUtil.format(AddressResourceRels.PROFILE_REL, SCOPE);
	private static final String ADDRESS_URI = URIUtil.format(AddressResourceRels.ADDRESSES_REL, SCOPE);

	private AddressTransformer addressTransformer;

	@Rule
	public JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final AddressLinkCreationHelper addressLinkCreationHelper = context.mock(AddressLinkCreationHelper.class);

	@Before
	public void setup() {
		addressTransformer = new AddressTransformer(
				AddressResourceRels.ADDRESSES_REL,
				addressLinkCreationHelper);
	}

	/**
	 * Test transform to representation.
	 */
	@Test
	public void testTransformToRepresentation() {
		context.checking(new Expectations() {
			{
				allowing(addressLinkCreationHelper).createProfileLink(SCOPE,
						AddressResourceRels.PROFILE_REL, AddressResourceRels.ADDRESSES_REV);
				will(returnValue(ResourceLinkFactory.create(PROFILES_URI, ProfilesMediaTypes.PROFILE.id(),
						AddressResourceRels.PROFILE_REL, AddressResourceRels.ADDRESSES_REV)));
				allowing(addressLinkCreationHelper).createAddressLink(ADDRESS_URI);
				will(returnValue(ElementListFactory.createListWithoutElement(ADDRESS_URI, CollectionsMediaTypes.LINKS.id())));
			}
		});
		AddressEntity addressEntity = AddressEntity
				.builderFrom(createAddressEntity(createAddressDetailEntity(), createNameEntity()))
				.withAddressId(ADDRESS_ID)
				.build();
		ResourceState<AddressEntity> expectedState = createAddressResourceState(addressEntity);

		ResourceState<AddressEntity> addressResourceState = addressTransformer.transform(
				SCOPE, addressEntity);

		assertEquals("The returned address resource state should be the same as expected.", expectedState, addressResourceState);
	}

	/**
	 * Test transform to resource entity.
	 */
	@Test
	public void testTransformToResourceEntity() {
		AddressEntity addressEntity = createAddressEntity(createAddressDetailEntity(), createNameEntity());

		ResourceState<AddressEntity> addressResourceState = createAddressResourceState(AddressEntity.builder()
				.withAddress(addressEntity.getAddress())
				.withName(addressEntity.getName())
				.build());

		AddressEntity actualAddressEntity = addressTransformer.transformToResourceEntity(addressResourceState);

		assertEquals(EXPECTED_ADDRESS_ENTITY, addressEntity, actualAddressEntity);
	}

	/**
	 * Test transform to resource entity with address id.
	 */
	@Test
	public void testTransformToResourceEntityWithProfileAndAddressId() {
		AddressEntity addressEntity = AddressEntity
				.builderFrom(createAddressEntity(createAddressDetailEntity(), createNameEntity()))
				.build();

		ResourceState<AddressEntity> addressResourceState = createAddressResourceState(AddressEntity.builder()
				.withAddress(addressEntity.getAddress())
				.withName(addressEntity.getName())
				.build());

		AddressEntity actualAddressEntity =
				addressTransformer.transformToResourceEntity(addressResourceState);

		assertEquals(EXPECTED_ADDRESS_ENTITY, addressEntity, actualAddressEntity);
	}

	/**
	 * Test transform to resource entity with no address entity on representation.
	 */
	@Test
	public void testTransformToResourceEntityWithNoAddressEntityOnRepresentation() {
		AddressEntity addressEntity = createAddressEntity(null, createNameEntity());

		ResourceState<AddressEntity> addressResourceState = createAddressResourceState(addressEntity);

		AddressEntity actualAddressEntity = addressTransformer.transformToResourceEntity(addressResourceState);

		assertEquals(EXPECTED_ADDRESS_ENTITY, addressEntity, actualAddressEntity);
	}

	/**
	 * Test transform to resource entity with no name entity on representation.
	 */
	@Test
	public void testTransformToResourceEntityWithNoNameEntityOnRepresentation() {

		AddressEntity addressEntity = createAddressEntity(createAddressDetailEntity(), null);

		ResourceState<AddressEntity> addressResourceState = createAddressResourceState(addressEntity);
		AddressEntity actualAddressEntity = addressTransformer.transformToResourceEntity(addressResourceState);

		assertEquals(EXPECTED_ADDRESS_ENTITY, addressEntity, actualAddressEntity);
	}

	private ResourceState<AddressEntity> createAddressResourceState(final AddressEntity addressEntity) {
		String addressUri = URIUtil.format(AddressResourceRels.ADDRESSES_REL, SCOPE);
		String selfUri = URIUtil.format(addressUri, Base32Util.encode(ADDRESS_ID));

		Self self = SelfFactory.createSelf(selfUri);

		ResourceLink profileLink = ResourceLinkFactory.create(PROFILES_URI, ProfilesMediaTypes.PROFILE.id(),
				AddressResourceRels.PROFILE_REL, AddressResourceRels.ADDRESSES_REV);
		ResourceLink addressesLink = ElementListFactory.createListWithoutElement(addressUri, CollectionsMediaTypes.LINKS.id());

		Collection<ResourceLink> resourceLinks = new ArrayList<ResourceLink>();
		resourceLinks.add(profileLink);
		resourceLinks.add(addressesLink);
		return ResourceState.Builder.create(addressEntity)
				.withSelf(self)
				.withScope(SCOPE)
				.withLinks(resourceLinks)
				.build();
	}

	private AddressEntity createAddressEntity(final AddressDetailEntity addressDetailEntity, final NameEntity nameEntity) {
		return AddressEntity.builder()
				.withAddress(addressDetailEntity)
				.withName(nameEntity)
				.build();
	}

	private NameEntity createNameEntity() {
		return NameEntity.builder()
				.withGivenName(FIRST_NAME)
				.withFamilyName(LAST_NAME)
				.build();
	}

	private AddressDetailEntity createAddressDetailEntity() {
		return AddressDetailEntity.builder()
					.withStreetAddress(STREET1)
					.withExtendedAddress(STREET2)
					.withLocality(CITY)
					.withCountryName(COUNTRY)
					.withRegion(SUB_COUNTRY)
					.withPostalCode(ZIP_CODE)
					.build();
	}
}
