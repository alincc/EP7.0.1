/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.transform;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.addresses.helper.AddressLinkCreationHelper;
import com.elasticpath.rest.resource.addresses.rel.AddressResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformToResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Transforms an address entity to resource state.
 */
@Singleton
@Named("addressTransformer")
public class AddressTransformer implements TransformToResourceState<AddressEntity, AddressEntity> {

	private final String resourceServerName;
	private final AddressLinkCreationHelper addressLinkCreationHelper;

	/**
	 * Default constructor.
	 * @param resourceServerName the resource server name
	 * @param addressLinkCreationHelper link creation helper
	 *
	 */
	@Inject
	public AddressTransformer(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("addressLinkCreationHelper")
			final AddressLinkCreationHelper addressLinkCreationHelper) {

		this.resourceServerName = resourceServerName;
		this.addressLinkCreationHelper = addressLinkCreationHelper;
	}

	/**
	 * Transform an address entity to address resource state.
	 *
	 * @param scope store scope
	 * @param addressEntity address entity
	 * @return the address resource state
	 */
	@Override
	public ResourceState<AddressEntity> transform(final String scope, final AddressEntity addressEntity) {
		String addressUri = URIUtil.format(resourceServerName, scope);
		String selfUri = URIUtil.format(addressUri, Base32Util.encode(addressEntity.getAddressId()));
		Self self = SelfFactory.createSelf(selfUri);
		return ResourceState.Builder.create(addressEntity)
				.withSelf(self)
				.withScope(scope)
				.withLinks(createLinksForAddressElement(scope, addressUri))
				.build();
	}

	/**
	 * Transform an address resource state to address entity.
	 *
	 * @param addressResourceState the address resource state
	 * @return the address entity
	 */
	public AddressEntity transformToResourceEntity(final ResourceState<AddressEntity> addressResourceState) {
		return addressResourceState.getEntity();
	}

	private Collection<ResourceLink> createLinksForAddressElement(final String scope, final String addressUri) {
		Collection<ResourceLink> resourceLinks = new ArrayList<>();
		resourceLinks.add(addressLinkCreationHelper.createProfileLink(scope, AddressResourceRels.PROFILE_REL, AddressResourceRels.ADDRESSES_REV));
		resourceLinks.add(addressLinkCreationHelper.createAddressLink(addressUri));
		return resourceLinks;
	}
}
