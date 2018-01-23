/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.links.impl;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.profiles.ProfileEntity;
import com.elasticpath.rest.resource.addresses.rel.AddressResourceRels;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Profile to addresses list link Handler.
 */
@Singleton
@Named("profileToAddressesLinkHandler")
public class ProfileToAddressesLinkHandler implements ResourceStateLinkHandler<ProfileEntity> {

	private final String resourceServerName;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	public ProfileToAddressesLinkHandler(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<ProfileEntity> resourceState) {
		return Collections.singletonList(
				ResourceLinkFactory.create(
						URIUtil.format(resourceServerName, resourceState.getScope()),
						CollectionsMediaTypes.LINKS.id(),
						AddressResourceRels.ADDRESSES_REL,
						AddressResourceRels.PROFILE_REV
				)
		);
	}
}
