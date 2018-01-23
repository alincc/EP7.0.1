/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.permissions;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.identity.util.PrincipalsUtil;
import com.elasticpath.rest.resource.addresses.integration.addresses.AddressLookupStrategy;
import com.elasticpath.rest.authorization.parameter.AbstractCollectionValueStrategy;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * Strategy to look up permission for Profile address resource.
 */
@Singleton
@Named("addressIdPermissionParameterStrategy")
public final class AddressIdParameterStrategy extends AbstractCollectionValueStrategy {

	private final AddressLookupStrategy addressLookupStrategy;

	/**
	 * Constructor.
	 *
	 * @param addressLookupStrategy    address lookup strategy
	 */
	@Inject
	AddressIdParameterStrategy(
			@Named("addressLookupStrategy")
			final AddressLookupStrategy addressLookupStrategy) {
		this.addressLookupStrategy = addressLookupStrategy;
	}

	@Override
	protected Collection<String> getParameterValues(final PrincipalCollection principals) {
		String scope = PrincipalsUtil.getScope(principals);
		String userId = PrincipalsUtil.getUserIdentifier(principals);
		ExecutionResult<Collection<String>> result = addressLookupStrategy.findIdsByUserId(scope, userId);
		return result.isSuccessful()
			? Base32Util.encodeAll(result.getData())
			: null;
	}
}
