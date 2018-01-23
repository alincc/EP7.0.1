/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.emails.EmailEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Queries for email information.
 */
public interface EmailLookup {

	/**
	 * Gets the {@link ResourceState} for a given email id.
	 *
	 * @param scope the scope
	 * @param emailId the email id
	 * @return the {@link ResourceState}
	 */
	ExecutionResult<ResourceState<EmailEntity>> getEmail(String scope, String emailId);

	/**
	 * Find the email IDs.
	 *
	 * @param scope the scope
	 * @param userId the user id
	 * @return the email IDs
	 */
	ExecutionResult<Collection<String>> findEmailIds(String scope, String userId);
}
