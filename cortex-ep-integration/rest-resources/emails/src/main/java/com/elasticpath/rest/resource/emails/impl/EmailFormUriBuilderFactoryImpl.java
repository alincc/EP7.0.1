/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.EmailFormUriBuilder;
import com.elasticpath.rest.schema.uri.EmailFormUriBuilderFactory;

/**
 * A factory for creating EmailFormUriBuilder objects.
 */
@Singleton
@Named("emailFormUriBuilderFactory")
public final class EmailFormUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<EmailFormUriBuilder>
		implements EmailFormUriBuilderFactory {

	/**
	 * Constructor.
	 *
	 * @param provider email form uri builder provider.
	 */
	@Inject
	EmailFormUriBuilderFactoryImpl(
			@Named("emailFormUriBuilder")
			final Provider<EmailFormUriBuilder> provider) {

		super(provider);
	}

}
