/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.permissions;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.authorization.parameter.AbstractCollectionValueStrategy;
import com.elasticpath.rest.identity.util.PrincipalsUtil;
import com.elasticpath.rest.resource.emails.EmailLookup;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * Strategy for resolving the email id parameter.
 */
@Singleton
@Named("emailIdPermissionParameterStrategy")
public final class EmailIdParameterStrategy extends AbstractCollectionValueStrategy {

	private final EmailLookup emailLookup;

	/**
	 * Constructor.
	 *
	 * @param emailLookup the email lookup
	 */
	@Inject
	EmailIdParameterStrategy(
			@Named("emailLookup")
			final EmailLookup emailLookup) {

		this.emailLookup = emailLookup;
	}

	@Override
	protected Collection<String> getParameterValues(final PrincipalCollection principals) {
		String scope = PrincipalsUtil.getScope(principals);
		String userId = PrincipalsUtil.getUserIdentifier(principals);
		return emailLookup.findEmailIds(scope, userId).getData();
	}
}
