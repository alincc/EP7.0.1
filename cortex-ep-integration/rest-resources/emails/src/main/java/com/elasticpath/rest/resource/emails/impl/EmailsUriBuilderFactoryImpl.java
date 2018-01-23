/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.EmailsUriBuilder;
import com.elasticpath.rest.schema.uri.EmailsUriBuilderFactory;

/**
 * A factory for creating EmailsUriBuilder objects.
 */
@Singleton
@Named("emailsUriBuilderFactory")
public final class EmailsUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<EmailsUriBuilder>
		implements EmailsUriBuilderFactory {

	/**
	 * Constructor.
	 *
	 * @param provider emails uri builder provider.
	 */
	@Inject
	EmailsUriBuilderFactoryImpl(
			@Named("emailsUriBuilder")
			final Provider<EmailsUriBuilder> provider) {

		super(provider);
	}

}
