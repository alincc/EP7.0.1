/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.emails.EmailEntity;

/**
 * Service that provides integration of email data with external systems.
 */
public interface EmailWriterStrategy {

	/**
	 * Creates the given email.
	 *
	 * @param email the email to create
	 * @return an ExecutionResult with ResourceStatus.Success if successful.
	 */
	ExecutionResult<Void> createEmail(EmailEntity email);
}
