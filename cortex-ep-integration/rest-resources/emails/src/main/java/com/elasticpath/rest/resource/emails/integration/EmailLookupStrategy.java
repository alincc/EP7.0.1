/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.emails.EmailEntity;

/**
 * Service that provides lookup of email data from external systems.
 */
public interface EmailLookupStrategy {

	/**
	 * Find email.
	 *
	 * @param scope the scope
	 * @param decodedEmailId the email id
	 * @return the execution result
	 */
	ExecutionResult<EmailEntity> findEmail(String scope, String decodedEmailId);

	/**
	 * Finds the email IDs for the logged in user.
	 *
	 * @param scope the scope
	 * @param decodedUserId the decoded user id
	 * @return the collection of email IDs
	 */
	ExecutionResult<Collection<String>> findEmailIds(String scope, String decodedUserId);
}
