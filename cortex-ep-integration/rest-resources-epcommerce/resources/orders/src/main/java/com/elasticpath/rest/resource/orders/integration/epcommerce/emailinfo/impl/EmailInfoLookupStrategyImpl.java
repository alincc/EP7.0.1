/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.integration.epcommerce.emailinfo.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.common.authentication.AuthenticationConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;
import com.elasticpath.rest.resource.orders.integration.emailinfo.EmailInfoLookupStrategy;

/**
 * Implementation of the {@link EmailInfoLookupStrategy}.
 */
@Singleton
@Named("emailInfoLookupStrategy")
public class EmailInfoLookupStrategyImpl implements EmailInfoLookupStrategy {

	private final CustomerRepository customerRepository;
	private final ResourceOperationContext resourceOperationContext;


	/**
	 * Constructor.
	 *
	 * @param customerRepository the customer repository
	 * @param resourceOperationContext the resource operation context
	 */
	@Inject
	EmailInfoLookupStrategyImpl(
			@Named("customerRepository")
			final CustomerRepository customerRepository,
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext) {

		this.customerRepository = customerRepository;
		this.resourceOperationContext = resourceOperationContext;
	}


	@Override
	public ExecutionResult<String> getEmailIdForOrder(final String scope, final String cartOrderGuid) {
		Customer customer = Assign.ifSuccessful(
				customerRepository.findCustomerByGuid(
						resourceOperationContext.getUserIdentifier()));
		String customerEmail = customer.getEmail();
		Ensure.isTrue(StringUtils.isNotEmpty(customerEmail)
						&& ObjectUtils.notEqual(AuthenticationConstants.ANONYMOUS_USER_ID, customerEmail),
				OnFailure.returnNotFound("The email was not found"));
		return ExecutionResultFactory.createReadOK(customerEmail);
	}
}
