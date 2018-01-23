/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.integration.epcommerce.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.emails.EmailEntity;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.emails.integration.EmailLookupStrategy;
import com.elasticpath.rest.resource.emails.integration.epcommerce.transform.EmailTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.common.authentication.AuthenticationConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;

/**
 * Service that provides lookup of email data from external systems.
 */
@Singleton
@Named("emailLookupStrategy")
public class EmailLookupStrategyImpl implements EmailLookupStrategy {

	private final CustomerRepository customerRepository;
	private final EmailTransformer emailTransformer;
	private final ResourceOperationContext resourceOperationContext;


	/**
	 * Constructor.
	 *
	 * @param customerRepository the customer repository
	 * @param emailTransformer the email transformer
	 * @param resourceOperationContext the resource operation context
	 */
	@Inject
	public EmailLookupStrategyImpl(
			@Named("customerRepository")
			final CustomerRepository customerRepository,
			@Named("emailTransformer")
			final EmailTransformer emailTransformer,
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext) {

		this.customerRepository = customerRepository;
		this.emailTransformer = emailTransformer;
		this.resourceOperationContext = resourceOperationContext;
	}


	@Override
	public ExecutionResult<EmailEntity> findEmail(final String storeCode, final String emailId) {
		Customer customer = Assign.ifSuccessful(customerRepository.findCustomerByGuid(resourceOperationContext.getUserIdentifier()));
		Ensure.isTrue(StringUtils.equals(emailId, customer.getEmail()),
				ExecutionResultFactory.createNotFound("Email does not match."));
		EmailEntity emailEntity = emailTransformer.transformToEntity(emailId);
		return ExecutionResultFactory.createReadOK(emailEntity);
	}

	/**
	 * Gets all email ids for a customer. This method is used specifically for constructing permissions.
	 *
	 * @param storeCode the store code
	 * @param customerGuid the customer guid
	 * @return collection of email ids for customer.
	 */
	@Override
	public ExecutionResult<Collection<String>> findEmailIds(final String storeCode, final String customerGuid) {
		Customer customer = Assign.ifSuccessful(customerRepository.findCustomerByGuid(customerGuid));
		String customerEmail = customer.getEmail();
		Collection<String> emailList;
		if (StringUtils.isNotEmpty(customerEmail)
				&& ObjectUtils.notEqual(customerEmail, AuthenticationConstants.ANONYMOUS_USER_ID)) {
			emailList = Collections.singletonList(customerEmail);
		} else {
			emailList = Collections.emptyList();
		}
		return ExecutionResultFactory.createReadOK(emailList);
	}
}
