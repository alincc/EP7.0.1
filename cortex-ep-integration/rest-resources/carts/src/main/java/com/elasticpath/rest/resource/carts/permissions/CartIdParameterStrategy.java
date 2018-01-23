/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.permissions;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.authorization.parameter.AbstractCollectionValueStrategy;
import org.apache.shiro.subject.PrincipalCollection;

import com.elasticpath.rest.identity.util.PrincipalsUtil;
import com.elasticpath.rest.resource.carts.CartLookup;

/**
 * Strategy for resolving the cart ID parameter.
 */
@Singleton
@Named("cartIdParameterStrategy")
public final class CartIdParameterStrategy extends AbstractCollectionValueStrategy {

	private final CartLookup cartLookup;


	/**
	 * Constructor.
	 *
	 * @param cartLookup cart lookup
	 */
	@Inject
	CartIdParameterStrategy(
			@Named("cartLookup")
			final CartLookup cartLookup) {

		this.cartLookup = cartLookup;
	}

	@Override
	protected Collection<String> getParameterValues(final PrincipalCollection principals) {
		String scope = PrincipalsUtil.getScope(principals);
		String userId = PrincipalsUtil.getUserIdentifier(principals);
		return cartLookup.findCartIds(scope, userId).getData();
	}
}
