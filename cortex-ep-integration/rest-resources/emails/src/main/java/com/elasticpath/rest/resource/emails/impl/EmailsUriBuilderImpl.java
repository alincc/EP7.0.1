/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.schema.uri.EmailsUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Implementation of the {@link EmailsUriBuilder}.
 */
@Named("emailsUriBuilder")
public class EmailsUriBuilderImpl implements EmailsUriBuilder {

	private String scope;
	private String emailId;
	private final String resourceServerName;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	EmailsUriBuilderImpl(
			@Named("resourceServerName")
			final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}

	@Override
	public EmailsUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public EmailsUriBuilder setEmailId(final String emailId) {
		this.emailId = emailId;
		return this;
	}

	@Override
	public String build() {
		return URIUtil.format(resourceServerName, scope, emailId);
	}

}
