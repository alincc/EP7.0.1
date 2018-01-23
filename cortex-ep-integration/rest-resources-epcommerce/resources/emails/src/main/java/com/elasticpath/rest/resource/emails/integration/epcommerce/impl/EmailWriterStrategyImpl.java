/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.integration.epcommerce.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.emails.EmailEntity;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.emails.integration.EmailWriterStrategy;
import com.elasticpath.rest.resource.integration.epcommerce.common.validator.EpDomainValidator;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;

/**
 * Service that provides integration of email data with external systems.
 */
@Singleton
@Named("emailWriterStrategy")
public class EmailWriterStrategyImpl implements EmailWriterStrategy {

	private final CustomerRepository customerRepository;
	private final EpDomainValidator customerValidator;
	private final ResourceOperationContext resourceOperationContext;


	/**
	 * The Constructor.
	 *
	 * @param customerRepository the customer repository.
	 * @param customerValidator the customer validator.
	 * @param resourceOperationContext the resource operation context
	 */
	@Inject
	public EmailWriterStrategyImpl(
			@Named("customerRepository")
			final CustomerRepository customerRepository,
			@Named("epDomainCustomerValidator")
			final EpDomainValidator customerValidator,
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext) {

		this.customerRepository = customerRepository;
		this.customerValidator = customerValidator;
		this.resourceOperationContext = resourceOperationContext;
	}

	@Override
	public ExecutionResult<Void> createEmail(final EmailEntity emailEntity) {
		String customerGuid = resourceOperationContext.getUserIdentifier();
		Customer customer = Assign.ifSuccessful(customerRepository.findCustomerByGuid(customerGuid));

		String email = StringUtils.trimToNull(emailEntity.getEmail());
		customer.setEmail(email);

		if (customer.isAnonymous()) {
			Ensure.isTrue(StringUtils.isNotEmpty(email), ExecutionResultFactory.createBadRequestBody("Email is missing."));
			Ensure.successful(customerValidator.validateProperty(customer, "email"));
		}

		return customerRepository.updateCustomer(customer);
	}
}