/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.command.impl;

import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.emails.EmailsMediaTypes;
import com.elasticpath.rest.definition.profiles.ProfilesMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.emails.EmailLookup;
import com.elasticpath.rest.resource.emails.command.ReadEmailListCommand;
import com.elasticpath.rest.resource.emails.rel.EmailRepresentationRels;
import com.elasticpath.rest.resource.emails.rel.EmailResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.EmailFormUriBuilder;
import com.elasticpath.rest.schema.uri.EmailFormUriBuilderFactory;
import com.elasticpath.rest.schema.uri.ProfilesUriBuilder;
import com.elasticpath.rest.schema.uri.ProfilesUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests the {@link ReadEmailListCommandImpl}.
 */
public final class ReadEmailListCommandImplTest {

	private static final String EMAILS = "emails";
	private static final String TEST_EMAIL2 = "testEmail2";
	private static final String TEST_EMAIL1 = "testEmail1";
	private static final String SCOPE = "testScope";
	private static final String EMAIL_FORM_URI = "/emailFormUri";
	private static final String PROFILE_URI = "/profileUri";
	private static final String DECODED_USER_ID = "DECODED_USER_ID";
	private static final String USER_ID = Base32Util.encode(DECODED_USER_ID);

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	@Mock
	private EmailLookup emailLookup;
	@Mock
	private EmailFormUriBuilder emailFormUriBuilder;
	@Mock
	private ResourceOperationContext operationContext;
	@Mock
	private EmailFormUriBuilderFactory emailFormUriBuilderFactory;
	@Mock
	private ProfilesUriBuilderFactory profilesUriBuilderFactory;
	@Mock
	private ProfilesUriBuilder profilesUriBuilder;

	/**
	 * Set up common elements of unit test.
	 */
	@Before
	public void setUp() {
		context.checking(new Expectations() {
			{
				allowing(operationContext).getUserIdentifier();
				will(returnValue(DECODED_USER_ID));

				allowing(emailFormUriBuilderFactory).get();
				will(returnValue(emailFormUriBuilder));

				allowing(emailFormUriBuilder).setScope(SCOPE);
				will(returnValue(emailFormUriBuilder));

				allowing(emailFormUriBuilder).build();
				will(returnValue(EMAIL_FORM_URI));

				allowing(profilesUriBuilderFactory).get();
				will(returnValue(profilesUriBuilder));

				allowing(profilesUriBuilder).setProfileId(USER_ID);
				will(returnValue(profilesUriBuilder));

				allowing(profilesUriBuilder).setScope(SCOPE);
				will(returnValue(profilesUriBuilder));

				allowing(profilesUriBuilder).build();
				will(returnValue(PROFILE_URI));

				allowing(profilesUriBuilder).build();
				will(returnValue(PROFILE_URI));
			}
		});
	}

	/**
	 * Tests successful execution of reading a list of emails for a user.
	 */
	@Test
	public void testSuccessfulExecute() {
		List<String> testEmailIds = Arrays.asList(TEST_EMAIL1, TEST_EMAIL2);

		Collection<ResourceLink> expectedLinks = createExpectedLinks(testEmailIds);
		Self expectedSelfLink = createExpectedSelf();

		shouldReturnEmailIds(ExecutionResultFactory.<Collection<String>>createReadOK(testEmailIds));
		ReadEmailListCommand command = createCommand();

		ExecutionResult<ResourceState<LinksEntity>> result = command.execute();

		assertTrue("The result should be successful", result.isSuccessful());

		assertResourceState(result.getData())
				.self(SelfFactory.createSelf(expectedSelfLink.getUri()))
				.containsLinks(expectedLinks);
	}

	/**
	 * Test getting emails for a user when no emails are found.
	 */
	@Test
	public void testExecuteWithNoEmails() {
		Collection<ResourceLink> expectedLinks = createExpectedLinks(Collections.<String>emptyList());
		Self expectedSelfLink = createExpectedSelf();

		shouldReturnEmailIds(ExecutionResultFactory.<Collection<String>>createNotFound());
		ReadEmailListCommand command = createCommand();

		ExecutionResult<ResourceState<LinksEntity>> result = command.execute();

		assertTrue("The result should be successful", result.isSuccessful());

		assertResourceState(result.getData())
				.self(SelfFactory.createSelf(expectedSelfLink.getUri()))
				.containsLinks(expectedLinks);
	}

	private Collection<ResourceLink> createExpectedLinks(final Collection<String> emailIds) {
		ArrayList<ResourceLink> expectedLinks = new ArrayList<>();

		ResourceLink profileLink = ResourceLinkFactory.create(PROFILE_URI, ProfilesMediaTypes.PROFILE.id(),
				EmailResourceRels.PROFILE_REL, EmailResourceRels.EMAILS_REV);
		expectedLinks.add(profileLink);

		String emailUriPrefix = URIUtil.format(EMAILS, SCOPE);
		Collection<ResourceLink> emailLinks = ElementListFactory.createElementsOfList(emailUriPrefix, emailIds, EmailsMediaTypes.EMAIL.id());
		expectedLinks.addAll(emailLinks);

		ResourceLink emailFormLink = ResourceLinkFactory.createNoRev(EMAIL_FORM_URI, EmailsMediaTypes.EMAIL.id(),
				EmailRepresentationRels.EMAIL_FORM_REL);
		expectedLinks.add(emailFormLink);

		return expectedLinks;
	}

	private Self createExpectedSelf() {
		String selfUri = URIUtil.format(EMAILS, SCOPE);
		return SelfFactory.createSelf(selfUri, CollectionsMediaTypes.LINKS.id());
	}

	private void shouldReturnEmailIds(final ExecutionResult<Collection<String>> result) {
		context.checking(new Expectations() {
			{
				oneOf(emailLookup).findEmailIds(SCOPE, DECODED_USER_ID);
				will(returnValue(result));
			}
		});
	}

	private ReadEmailListCommand createCommand() {
		ReadEmailListCommandImpl command = new ReadEmailListCommandImpl(EMAILS,
				operationContext, profilesUriBuilderFactory, emailLookup, emailFormUriBuilderFactory);

		ReadEmailListCommand.Builder builder = new ReadEmailListCommandImpl.BuilderImpl(command);
		return builder.setScope(SCOPE)
				.build();
	}
}
