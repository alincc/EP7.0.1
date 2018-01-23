/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.command.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
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
import com.elasticpath.rest.schema.uri.EmailFormUriBuilderFactory;
import com.elasticpath.rest.schema.uri.ProfilesUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Implementation of {@link ReadEmailListCommand}.
 */
@Named
public final class ReadEmailListCommandImpl implements ReadEmailListCommand {

	private final String resourceServerName;
	private final EmailLookup emailLookup;
	private final ResourceOperationContext operationContext;
	private final EmailFormUriBuilderFactory emailFormUriBuilderFactory;
	private final ProfilesUriBuilderFactory profilesUriBuilderFactory;

	private String scope;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName resource server name
	 * @param operationContext the operation context
	 * @param profilesUriBuilderFactory the profiles uri builder factory
	 * @param emailLookup the email lookup
	 * @param emailFormUriBuilderFactory the email form uri builder factory
	 */
	@Inject
	ReadEmailListCommandImpl(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("resourceOperationContext")
			final ResourceOperationContext operationContext,
			@Named("profilesUriBuilderFactory")
			final ProfilesUriBuilderFactory profilesUriBuilderFactory,
			@Named("emailLookup")
			final EmailLookup emailLookup,
			@Named("emailFormUriBuilderFactory")
			final EmailFormUriBuilderFactory emailFormUriBuilderFactory) {

		this.resourceServerName = resourceServerName;
		this.operationContext = operationContext;
		this.profilesUriBuilderFactory = profilesUriBuilderFactory;
		this.emailLookup = emailLookup;
		this.emailFormUriBuilderFactory = emailFormUriBuilderFactory;
	}

	@Override
	public ExecutionResult<ResourceState<LinksEntity>> execute() {
		Collection<ResourceLink> links = new ArrayList<>();
		String userIdentifier = operationContext.getUserIdentifier();
		String emailPrefixUri = URIUtil.format(resourceServerName, scope);

		String selfUri = URIUtil.format(resourceServerName, scope);
		Self self = SelfFactory.createSelf(selfUri);

		// add profile link
		String profileUri = profilesUriBuilderFactory.get()
				.setProfileId(Base32Util.encode(userIdentifier))
				.setScope(scope)
				.build();
		ResourceLink profileLink = ResourceLinkFactory.create(profileUri, ProfilesMediaTypes.PROFILE.id(), EmailResourceRels.PROFILE_REL,
				EmailResourceRels.EMAILS_REV);
		links.add(profileLink);

		String emailFormUri = emailFormUriBuilderFactory.get()
				.setScope(scope)
				.build();
		ResourceLink emailFormLink = ResourceLinkFactory.createNoRev(emailFormUri, EmailsMediaTypes.EMAIL.id(),
				EmailRepresentationRels.EMAIL_FORM_REL);
		links.add(emailFormLink);

		Collection<String> emailIds;
		try {
			emailIds = Assign.ifSuccessful(emailLookup.findEmailIds(scope, userIdentifier));
		} catch (BrokenChainException bce) {
			emailIds = Assign.ifBrokenChainExceptionStatus(bce, ResourceStatus.NOT_FOUND, Collections.<String>emptyList());
		}
		Collection<ResourceLink> emailLinks =
				ElementListFactory.createElementsOfList(emailPrefixUri, emailIds, EmailsMediaTypes.EMAIL.id());
		links.addAll(emailLinks);

		ResourceState<LinksEntity> linksEntityResourceState = ResourceState.Builder.create(LinksEntity.builder().build())
				.withSelf(self)
				.addingLinks(links)
				.build();

		return ExecutionResultFactory.createReadOK(linksEntityResourceState);
	}

	/**
	 * Constructs a ReadEmailCommand.
	 */
	@Named("readEmailListCommandBuilder")
	static class BuilderImpl implements ReadEmailListCommand.Builder {

		private final ReadEmailListCommandImpl command;

		/**
		 * Constructor.
		 *
		 * @param command Command instance.
		 */
		@Inject
		BuilderImpl(final ReadEmailListCommandImpl command) {
			this.command = command;
		}

		@Override
		public Builder setScope(final String scope) {
			command.scope = scope;
			return this;
		}

		@Override
		public ReadEmailListCommandImpl build() {
			assert command.scope != null : "scope required.";
			return command;
		}
	}
}
