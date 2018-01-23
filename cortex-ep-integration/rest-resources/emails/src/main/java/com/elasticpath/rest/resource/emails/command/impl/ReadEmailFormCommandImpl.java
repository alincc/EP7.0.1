/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.command.impl;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.emails.EmailEntity;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.resource.emails.command.ReadEmailFormCommand;
import com.elasticpath.rest.resource.emails.rel.EmailResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * The read email form command.
 */
@Named
public final class ReadEmailFormCommandImpl implements ReadEmailFormCommand {

	private final String resourceServerName;
	private String scope;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName resource server name
	 */
	@Inject
	ReadEmailFormCommandImpl(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}

	@Override
	public ExecutionResult<ResourceState<EmailEntity>> execute() {

		String baseUri = URIUtil.format(resourceServerName, scope);
		Self self = SelfFactory.createSelf(URIUtil.format(baseUri, Form.URI_PART));
		ResourceLink createEmailActionLink = ResourceLinkFactory.createUriRel(baseUri, EmailResourceRels.CREATE_EMAIL_ACTION_REL);

		ResourceState<EmailEntity> email = ResourceState.Builder
				.create(EmailEntity.builder()
						.withEmail(StringUtils.EMPTY)
						.build())
				.withSelf(self)
				.addingLinks(createEmailActionLink)
				.build();

		return ExecutionResultFactory.createReadOK(email);
	}

	/**
	 * Constructs a ReadEmailFormCommand.
	 */
	@Named("readEmailFormCommandBuilder")
	static class BuilderImpl implements Builder {

		private final ReadEmailFormCommandImpl command;

		/**
		 * Constructor.
		 *
		 * @param command Command instance.
		 */
		@Inject
		BuilderImpl(final ReadEmailFormCommandImpl command) {
			this.command = command;
		}

		@Override
		public Builder setScope(final String scope) {
			command.scope = scope;
			return this;
		}

		@Override
		public ReadEmailFormCommandImpl build() {
			assert command.scope != null : "scope required.";
			return command;
		}
	}

}
