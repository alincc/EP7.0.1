/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.link.impl;

import static com.elasticpath.rest.test.AssertResourceLink.assertResourceLink;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.emails.EmailEntity;
import com.elasticpath.rest.definition.profiles.ProfilesMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.emails.rel.EmailRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.ProfilesUriBuilder;
import com.elasticpath.rest.schema.uri.ProfilesUriBuilderFactory;

/**
 * Test for {@link AddProfileLinkToEmailStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class AddProfileLinkToEmailStrategyTest {

	private static final String SCOPE = "scope";
	private static final String USER_ID = "userid";
	private static final String PROFILES_URI = "/pr";

	@Mock
	ResourceOperationContext operationContext;
	@Mock
	ProfilesUriBuilderFactory profilesUriBuilderFactory;
	@Mock
	ProfilesUriBuilder profilesUriBuilder;

	@InjectMocks
	private AddProfileLinkToEmailStrategy addProfileLinkToEmailStrategy;

	/**
	 * Tests creation of the link to profile from email.
	 */
	@Test
	public void ensureLinkFromProfileToEmailHasCorrectContent() {
		when(operationContext.getUserIdentifier()).thenReturn(USER_ID);
		when(profilesUriBuilderFactory.get()).thenReturn(profilesUriBuilder);
		when(profilesUriBuilder.setProfileId(anyString())).thenReturn(profilesUriBuilder);
		when(profilesUriBuilder.setScope(anyString())).thenReturn(profilesUriBuilder);
		when(profilesUriBuilder.build()).thenReturn(PROFILES_URI);

		ResourceState<EmailEntity> email = ResourceState.Builder.create(EmailEntity.builder().build())
				.withScope(SCOPE)
				.build();

		Iterable<ResourceLink> createdLinks = addProfileLinkToEmailStrategy.getLinks(email);

		assertThat("There should only be one link created.", createdLinks, Matchers.<ResourceLink>iterableWithSize(1));
		ResourceLink createdLink = createdLinks.iterator().next();
		assertResourceLink(createdLink)
				.rel(EmailRepresentationRels.PROFILE_REL)
				.type(ProfilesMediaTypes.PROFILE.id())
				.uri(PROFILES_URI);
		verify(profilesUriBuilder).setProfileId(Base32Util.encode(USER_ID));
		verify(profilesUriBuilder).setScope(SCOPE);
	}
}
