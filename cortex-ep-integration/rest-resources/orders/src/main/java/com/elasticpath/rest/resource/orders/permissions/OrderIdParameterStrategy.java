/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.permissions;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.identity.util.PrincipalsUtil;
import com.elasticpath.rest.resource.orders.OrderLookup;
import com.elasticpath.rest.authorization.parameter.AbstractCollectionValueStrategy;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * Strategy for resolving the order ID parameter.
 */
@Singleton
@Named("orderIdParameterStrategy")
public final class OrderIdParameterStrategy extends AbstractCollectionValueStrategy {

	private final OrderLookup orderLookup;


	/**
	 * Constructor.
	 *
	 * @param orderLookup order lookup
	 */
	@Inject
	OrderIdParameterStrategy(
			@Named("orderLookup")
			final OrderLookup orderLookup) {

		this.orderLookup = orderLookup;
	}

	@Override
	protected Collection<String> getParameterValues(final PrincipalCollection principals) {
		String scope = PrincipalsUtil.getScope(principals);
		String userId = PrincipalsUtil.getUserIdentifier(principals);
		return orderLookup.findOrderIds(scope, userId).getData();
	}
}
