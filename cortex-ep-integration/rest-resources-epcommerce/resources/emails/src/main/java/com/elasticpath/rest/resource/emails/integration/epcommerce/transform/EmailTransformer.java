/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.integration.epcommerce.transform;

import java.util.Locale;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.emails.EmailEntity;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transforms between an email address and {@link EmailEntity}, and vice versa.
 */
@Singleton
@Named("emailTransformer")
public class EmailTransformer extends AbstractDomainTransformer<String, EmailEntity> {

	@Override
	public String transformToDomain(final EmailEntity emailEntity, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public EmailEntity transformToEntity(final String email, final Locale locale) {
		return EmailEntity.builder()
								.withEmailId(email)
								.withEmail(email)
								.build();
	}
}
