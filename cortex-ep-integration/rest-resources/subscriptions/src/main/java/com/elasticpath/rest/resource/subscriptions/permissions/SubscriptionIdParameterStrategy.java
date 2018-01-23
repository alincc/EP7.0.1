/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.subscriptions.permissions;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.identity.util.PrincipalsUtil;
import com.elasticpath.rest.authorization.parameter.AbstractCollectionValueStrategy;
import com.elasticpath.rest.resource.subscriptions.integration.SubscriptionLookupStrategy;
import org.apache.shiro.subject.PrincipalCollection;


/**
 * Strategy for resolving the subscription ID parameter.
 */
@Singleton
@Named("subscriptionIdParameterStrategy")
public final class SubscriptionIdParameterStrategy extends AbstractCollectionValueStrategy {

	private final SubscriptionLookupStrategy subscriptionLookupStrategy;

	/**
	 * Constructor.
	 *
	 * @param subscriptionLookupStrategy subscription lookup
	 */
	@Inject
	SubscriptionIdParameterStrategy(
			@Named("subscriptionLookupStrategy")
			final SubscriptionLookupStrategy subscriptionLookupStrategy) {

		this.subscriptionLookupStrategy = subscriptionLookupStrategy;
	}

	@Override
	protected Collection<String> getParameterValues(final PrincipalCollection principals) {
		String scope = PrincipalsUtil.getScope(principals);
		String userId = PrincipalsUtil.getUserIdentifier(principals);
		ExecutionResult<Collection<String>> result = subscriptionLookupStrategy.getSubscriptionIds(scope, userId);
		return result.isSuccessful()
			? Base32Util.encodeAll(result.getData())
			: null;
	}
}
