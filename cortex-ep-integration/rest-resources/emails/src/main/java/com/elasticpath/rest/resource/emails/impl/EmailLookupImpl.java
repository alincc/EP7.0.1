/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.emails.EmailEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.emails.EmailLookup;
import com.elasticpath.rest.resource.emails.integration.EmailLookupStrategy;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Loads a user's email address based on their email ID.
 */
@Singleton
@Named("emailLookup")
public final class EmailLookupImpl implements EmailLookup {

	private final EmailLookupStrategy emailLookupStrategy;
	private final String resourceServerName;

	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resource server name
	 * @param emailLookupStrategy the email lookup strategy
	 */
	@Inject
	EmailLookupImpl(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("emailLookupStrategy")
			final EmailLookupStrategy emailLookupStrategy) {

		this.resourceServerName = resourceServerName;
		this.emailLookupStrategy = emailLookupStrategy;
	}

	@Override
	public ExecutionResult<ResourceState<EmailEntity>> getEmail(final String scope, final String emailId) {

		String decodedEmailId = Base32Util.decode(emailId);
		EmailEntity emailEntity = Assign.ifSuccessful(emailLookupStrategy.findEmail(scope, decodedEmailId));

		String selfUri = URIUtil.format(resourceServerName, scope, emailId);
		Self self = SelfFactory.createSelf(selfUri);

		String emailsListUri = URIUtil.format(resourceServerName, scope);
		ResourceLink emailListLink = ElementListFactory.createListWithoutElement(emailsListUri, CollectionsMediaTypes.LINKS.id());

		ResourceState<EmailEntity> email = ResourceState.Builder
				.create(emailEntity)
				.withScope(scope)
				.withSelf(self)
				.addingLinks(emailListLink)
				.build();

		return ExecutionResultFactory.createReadOK(email);
	}

	@Override
	public ExecutionResult<Collection<String>> findEmailIds(final String scope, final String decodedUserId) {

		Collection<String> emailIds = Assign.ifSuccessful(emailLookupStrategy.findEmailIds(scope, decodedUserId));
		return ExecutionResultFactory.createReadOK(Base32Util.encodeAll(emailIds));
	}

}
