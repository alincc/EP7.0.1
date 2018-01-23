/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.schema.uri.EmailFormUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Build the URI for the email form.
 */
@Named("emailFormUriBuilder")
public class EmailFormUriBuilderImpl implements EmailFormUriBuilder {

	private String scope;
	private final String resourceServerName;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	EmailFormUriBuilderImpl(@Named("resourceServerName")
	final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}

	@Override
	public EmailFormUriBuilder setScope(final String scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public String build() {
		assert scope != null : "scope is required";
		return URIUtil.format(resourceServerName, scope, Form.URI_PART);
	}

}
