/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.permissions;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.identity.util.PrincipalsUtil;
import com.elasticpath.rest.authorization.parameter.AbstractCollectionValueStrategy;
import com.elasticpath.rest.resource.purchases.PurchaseLookup;
import org.apache.shiro.subject.PrincipalCollection;


/**
 * Strategy for resolving the purchase id parameter.
 */
@Singleton
@Named("purchaseIdParameterStrategy")
public final class PurchaseIdParameterStrategy extends AbstractCollectionValueStrategy {

	private final PurchaseLookup purchaseLookup;


	/**
	 * Constructor.
	 *
	 * @param purchaseLookup purchase lookup
	 */
	@Inject
	PurchaseIdParameterStrategy(
			@Named("purchaseLookup")
			final PurchaseLookup purchaseLookup) {

		this.purchaseLookup = purchaseLookup;
	}

	@Override
	protected Collection<String> getParameterValues(final PrincipalCollection principals) {
		String scope = PrincipalsUtil.getScope(principals);
		String userId = PrincipalsUtil.getUserIdentifier(principals);
		return purchaseLookup.findPurchaseIds(scope, userId).getData();
	}
}
