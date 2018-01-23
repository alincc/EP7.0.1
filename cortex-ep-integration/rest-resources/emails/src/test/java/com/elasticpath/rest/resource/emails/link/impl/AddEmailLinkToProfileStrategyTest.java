/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.link.impl;

import static com.elasticpath.rest.test.AssertResourceLink.assertResourceLink;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import org.hamcrest.Matchers;

import com.elasticpath.rest.definition.profiles.ProfileEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.emails.constants.EmailTestConstants;
import com.elasticpath.rest.resource.emails.rel.EmailRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Test for {@link com.elasticpath.rest.resource.emails.link.impl.AddEmailLinkToProfileStrategy}.
 */
public final class AddEmailLinkToProfileStrategyTest {


	private static final String SCOPE = "scope";

	private final ResourceStateLinkHandler<ProfileEntity> addEmailLinkToProfileStrategy = new AddEmailLinkToProfileStrategy(
			EmailTestConstants.EMAIL_PATH);


	/**
	 * Tests creation of the link to email for profile.
	 */
	@Test
	public void ensureLinkFromProfileToEmailHasCorrectContent() {
		ResourceState<ProfileEntity> profile = ResourceState.Builder.create(ProfileEntity.builder().build())
				.withScope(SCOPE)
				.build();

		Iterable<ResourceLink> createdLinks = addEmailLinkToProfileStrategy.getLinks(profile);

		assertThat("There should only be one link created.", createdLinks, Matchers.<ResourceLink>iterableWithSize(1));
		ResourceLink createdLink = createdLinks.iterator().next();
		String expectedLinkUri = URIUtil.format(EmailTestConstants.EMAIL_PATH, SCOPE);
		assertResourceLink(createdLink)
				.rel(EmailRepresentationRels.EMAIL_REL)
				.rev(EmailRepresentationRels.PROFILE_REV)
				.type(CollectionsMediaTypes.LINKS.id())
				.uri(expectedLinkUri);
	}
}
