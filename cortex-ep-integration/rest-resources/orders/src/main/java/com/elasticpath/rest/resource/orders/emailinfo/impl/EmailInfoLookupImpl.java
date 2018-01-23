/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.emailinfo.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.orders.emailinfo.EmailInfoLookup;
import com.elasticpath.rest.resource.orders.integration.emailinfo.EmailInfoLookupStrategy;
import com.elasticpath.rest.id.util.Base32Util;

/**
 * Implementation of the {@link EmailInfoLookup}.
 */
@Singleton
@Named("emailInfoLookup")
public class EmailInfoLookupImpl implements EmailInfoLookup {

	private final EmailInfoLookupStrategy emailInfoLookupStrategy;

	/**
	 * Constructor.
	 *
	 * @param emailInfoLookupStrategy the email info lookup strategy
	 */
	@Inject
	EmailInfoLookupImpl(
			@Named("emailInfoLookupStrategy")
			final EmailInfoLookupStrategy emailInfoLookupStrategy) {
		this.emailInfoLookupStrategy = emailInfoLookupStrategy;
	}

	@Override
	public ExecutionResult<String> findEmailIdForOrder(final String scope, final String orderId) {
		String decodedOrderId = Base32Util.decode(orderId);
		String emailId = Assign.ifSuccessful(emailInfoLookupStrategy.getEmailIdForOrder(scope, decodedOrderId));
		return ExecutionResultFactory.createReadOK(Base32Util.encode(emailId));
	}
}
