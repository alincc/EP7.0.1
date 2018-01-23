/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.helper;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.addresses.AddressesMediaTypes;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.profiles.ProfilesMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.addresses.billing.Billing;
import com.elasticpath.rest.resource.addresses.integration.addresses.alias.DefaultAddressLookupStrategy;
import com.elasticpath.rest.resource.addresses.rel.AddressResourceRels;
import com.elasticpath.rest.resource.addresses.shipping.Shipping;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.uri.AddressFormUriBuilderFactory;
import com.elasticpath.rest.schema.uri.ProfilesUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Helper methods to create Resource Links for Address resource. Do not follow Secret Admirer strategy so this class
 * should eventually be deprecated.
 */
@Singleton
@Named("addressLinkCreationHelper")
public class AddressLinkCreationHelper {

	private final ResourceOperationContext operationContext;
	private final AddressFormUriBuilderFactory addressFormUriBuilderFactory;
	private final ProfilesUriBuilderFactory profilesUriBuilderFactory;
	private final String resourceServerName;

	/**
	 * Constructor.
	 *
	 * @param operationContext get user identifier
	 * @param addressFormUriBuilderFactory the form uri builder factory
	 * @param profilesUriBuilderFactory the profiles uri builder factory
	 * @param resourceServerName the resource server name
	 */
	@Inject
	public AddressLinkCreationHelper(
			@Named("resourceOperationContext")
			final ResourceOperationContext operationContext,
			@Named("addressFormUriBuilderFactory")
			final AddressFormUriBuilderFactory addressFormUriBuilderFactory,
			@Named("profilesUriBuilderFactory")
			final ProfilesUriBuilderFactory profilesUriBuilderFactory,
			@Named("resourceServerName")
			final String resourceServerName) {

		this.operationContext = operationContext;
		this.addressFormUriBuilderFactory = addressFormUriBuilderFactory;
		this.profilesUriBuilderFactory = profilesUriBuilderFactory;
		this.resourceServerName = resourceServerName;
	}

	/**
	 * Creates a generic link given the uri, rel and rev.
	 * @param uri uri
	 * @param relation rel
	 * @param reverse rev
	 * @return a generic resource link
	 */
	public ResourceLink createGenericLink(final String uri, final String relation, final String reverse) {
		return ResourceLinkFactory.create(uri, CollectionsMediaTypes.LINKS.id(), relation, reverse);
	}

	/**
	 * Creates a link to addresses list.
	 *
	 * @param addressUri the addresses uri
	 * @return a resource link to addresses
	 */
	public ResourceLink createAddressLink(final String addressUri) {
		return ElementListFactory.createListWithoutElement(addressUri, CollectionsMediaTypes.LINKS.id());
	}

	/**
	 * Creates a collection of address element links with scope and address ids.
	 * @param scope scope
	 * @param addressIds collection of address ids
	 * @return address links
	 */
	public Collection<ResourceLink> createAddressesLinks(final String scope, final Collection<String> addressIds) {
		String addressPrefixUri = URIUtil.format(resourceServerName, scope);

		return ElementListFactory.createElementsOfList(addressPrefixUri,
				addressIds,
				AddressesMediaTypes.ADDRESS.id());
	}

	/**
	 * Creates a collection of address element links with scope and address ids but without a rev link.
	 * @param scope scope
	 * @param addressIds collection of address ids
	 * @return address links
	 */
	public Collection<? extends ResourceLink> createElementLinks(final String scope, final Collection<String> addressIds) {
		String addressPrefixUri = URIUtil.format(resourceServerName, scope);

		return ElementListFactory.createElements(addressPrefixUri,
				addressIds,
				AddressesMediaTypes.ADDRESS.id());
	}

	/**
	 * Creates a shipping address link.
	 * @param selfUri self uri
	 * @param relation rel
	 * @param reverse rev
	 * @return shipping address link
	 */
	public ResourceLink createShippingAddressesLink(final String selfUri, final String relation, final String reverse) {
		String shippingAddressesUri = URIUtil.format(selfUri, Shipping.URI_PART);
		return createGenericLink(shippingAddressesUri, relation, reverse);
	}

	/**
	 * Creates a billing address link with scope, rel and rev.
	 * @param selfUri self uri
	 * @param relation rel
	 * @param reverse rev
	 * @return billing address link
	 */
	public ResourceLink createBillingAddressesLink(final String selfUri, final String relation, final String reverse) {
		String billingAddressesUri = URIUtil.format(selfUri, Billing.URI_PART);
		return createGenericLink(billingAddressesUri, relation, reverse);
	}

	/**
	 * Creates a preferred billing address link with scope, rel, rev and a default address lookup strategy.
	 * @param scope scope
	 * @param addressUri address uri
	 * @param relation rel
	 * @param reverse rev
	 * @param addressLookupStrategy the default address lookup strategy
	 * @return preferred billing address link
	 */
	public ResourceLink createPreferredAddressLink(final String scope,
													final String addressUri,
													final String relation,
													final String reverse,
													final DefaultAddressLookupStrategy addressLookupStrategy) {
		String userIdentifier = operationContext.getUserIdentifier();
		ExecutionResult<String> preferredAddressId;
		try {
			preferredAddressId = addressLookupStrategy.findPreferredAddressId(scope, userIdentifier);

			String defaultBillingAddressUri = URIUtil.format(addressUri, Base32Util.encode(preferredAddressId.getData()));
			return ResourceLinkFactory.create(
					defaultBillingAddressUri,
					AddressesMediaTypes.ADDRESS.id(),
					relation,
					reverse);
		} catch (BrokenChainException bce) {
			//do nothing if no addresses found
			return null;
		}
	}

	/**
	 * Creates a profile link with scope, rel and rev.
	 * @param scope scope
	 * @param relation rel
	 * @param reverse rev
	 * @return profile link
	 */
	public ResourceLink createProfileLink(final String scope, final String relation, final String reverse) {
		String userIdentifier = operationContext.getUserIdentifier();
		String profileUri = profilesUriBuilderFactory.get()
				.setProfileId(Base32Util.encode(userIdentifier))
				.setScope(scope)
				.build();
		return ResourceLinkFactory.create(profileUri, ProfilesMediaTypes.PROFILE.id(),
				relation, reverse);
	}

	/**
	 * Creates an address form link with scope, rel and rev.
	 * @param scope scope
	 * @param relation rel
	 * @param reverse rev
	 * @return address form link
	 */
	public ResourceLink createAddressFormLink(final String scope, final String relation, final String reverse) {
		String addressFormUri = addressFormUriBuilderFactory.get().setScope(scope).build();
		return ResourceLinkFactory.create(
				addressFormUri,
				AddressesMediaTypes.ADDRESS.id(),
				relation,
				reverse);
	}

	/**
	 * Creates an address creation URI relation resource link.
	 * @param baseUri base uri
	 * @return address creation link
	 */
	public ResourceLink createSubmitCreateAddressLink(final String baseUri) {
		return ResourceLinkFactory.createUriRel(baseUri, AddressResourceRels.CREATE_ADDRESS_ACTION_REL);
	}
}
