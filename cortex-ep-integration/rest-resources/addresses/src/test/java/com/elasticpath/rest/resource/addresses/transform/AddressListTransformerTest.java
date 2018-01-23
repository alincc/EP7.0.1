/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.definition.addresses.AddressesMediaTypes;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.addresses.helper.AddressLinkCreationHelper;
import com.elasticpath.rest.resource.addresses.rel.AddressResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.AddressUriBuilder;
import com.elasticpath.rest.schema.uri.AddressUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests the {@link com.elasticpath.rest.resource.addresses.transform.AddressListTransformerTest}.
 */
public class AddressListTransformerTest {
	private static final String TEST_SELF_URI = "/addresses/mobee";
	private static final String SCOPE = "MOBEE";
	private static final String ADDRESS = "Address1";
	private static final String RESOURCE_SERVER_NAME = AddressResourceRels.ADDRESSES_REL;
	private static final String ADDRESS_URI_PREFIX = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE);

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final ResourceOperationContext mockResourceOperationContext = context.mock(ResourceOperationContext.class);
	private final ResourceOperation mockResourceOperation = context.mock(ResourceOperation.class);
	private final AddressUriBuilderFactory mockAddressUriBuilderFactory = context.mock(AddressUriBuilderFactory.class);
	private final AddressUriBuilder mockAddressUriBuilder = context.mock(AddressUriBuilder.class);

	private final AddressListTransformer addressListTransformer =
			new AddressListTransformer(mockResourceOperationContext);

	private final AddressLinkCreationHelper linkHelper = new AddressLinkCreationHelper(
			mockResourceOperationContext,
			null,
			null,
			RESOURCE_SERVER_NAME
	);

	@Before
	public void setUp() {
		context.checking(new Expectations() {
			{
				allowing(mockResourceOperationContext).getResourceOperation();
				will(returnValue(mockResourceOperation));
				allowing(mockResourceOperation).getUri();
				will(returnValue(TEST_SELF_URI));

				allowing(mockAddressUriBuilderFactory).get();
				will(returnValue(mockAddressUriBuilder));
				allowing(mockAddressUriBuilder).setScope(SCOPE);
				will(returnValue(mockAddressUriBuilder));
				allowing(mockAddressUriBuilder).setAddressId(with(any(String.class)));
				will(returnValue(mockAddressUriBuilder));
				allowing(mockAddressUriBuilder).build();
				will(returnValue(ADDRESS_URI_PREFIX));
			}
		});
	}

	/**
	 * Tests that the full address list transformer adds the links specific to that level.
	 * The additional links are to:
	 * shipping addresses
	 * billing addresses
	 * create address form
	 */
	@Test
	public void testTransformToRepresentationWithCorrectLinks() {

		Collection<String> addressIds = new ArrayList<String>();
		addressIds.add(ADDRESS);

		Collection<ResourceLink> testLinks = new ArrayList<ResourceLink>();
		testLinks.addAll(linkHelper.createAddressesLinks(SCOPE, addressIds));
		ResourceState<LinksEntity> linksResourceState = addressListTransformer.transform(SCOPE, testLinks);

		String addressPrefixUri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE);
		Collection<ResourceLink> expectedAddressLink =
				ElementListFactory.createElementsOfList(addressPrefixUri, addressIds, AddressesMediaTypes.ADDRESS.id());
		assertEquals(expectedAddressLink.iterator().next(), getFirstLink(linksResourceState));
	}

	private ResourceLink getFirstLink(final ResourceState<LinksEntity> linksResourceState) {
		ResourceLink first = linksResourceState.getLinks().get(0);
		assertNotNull(first);
		return first;
	}
}
