/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.link.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.emails.EmailEntity;
import com.elasticpath.rest.definition.profiles.ProfilesMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.emails.rel.EmailRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.ProfilesUriBuilderFactory;

/**
 * Create a link to the profile on the email representation.
 */
@Singleton
@Named("addProfileLinkToEmailStrategy")
public final class AddProfileLinkToEmailStrategy implements ResourceStateLinkHandler<EmailEntity> {

	private final ResourceOperationContext resourceOperationContext;
	private final ProfilesUriBuilderFactory profilesUriBuilderFactory;


	/**
	 * Constructor for injection.
	 *
	 * @param resourceOperationContext the resource operation context
	 * @param profilesUriBuilderFactory profiles URI builder factory
	 */
	@Inject
	public AddProfileLinkToEmailStrategy(
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("profilesUriBuilderFactory")
			final ProfilesUriBuilderFactory profilesUriBuilderFactory) {

		this.resourceOperationContext = resourceOperationContext;
		this.profilesUriBuilderFactory = profilesUriBuilderFactory;
	}


	@Override
	public Collection<ResourceLink> getLinks(final ResourceState<EmailEntity> emailRepresentation) {
		String scope = emailRepresentation.getScope();
		String profileId = Base32Util.encode(resourceOperationContext.getUserIdentifier());
		String profileUri = profilesUriBuilderFactory.get()
			.setProfileId(profileId)
			.setScope(scope)
			.build();

		ResourceLink profileLink = ResourceLinkFactory.createNoRev(
			profileUri,
			ProfilesMediaTypes.PROFILE.id(),
			EmailRepresentationRels.PROFILE_REL);

		return Collections.singleton(profileLink);
	}
}
