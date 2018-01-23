/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.subscriptions.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.subscriptions.SubscriptionEntity;

/**
 * A strategy used for looking up subscriptions.
 */
public interface SubscriptionLookupStrategy {

	/**
	 * Get a single subscription.
	 *
	 * @param scope scope
	 * @param decodedSubscriptionId the decoded subscription ID
	 * @return the subscription DTO
	 */
	ExecutionResult<SubscriptionEntity> getSubscription(String scope, String decodedSubscriptionId);

	/**
	 * Gets a list of subscription IDs for a user.
	 *
	 * @param scope scope
	 * @param userId the user ID
	 * @return the collection of subscription IDs
	 */
	ExecutionResult<Collection<String>> getSubscriptionIds(String scope, String userId);
}