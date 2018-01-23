/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.subscriptions.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.OperationResultFactory;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.subscriptions.SubscriptionEntity;
import com.elasticpath.rest.definition.subscriptions.SubscriptionsMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.subscriptions.integration.SubscriptionLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;

/**
 * Resource Operator for Subscriptions.
 */
@Singleton
@Named("subscriptionsResourceOperator")
@Path(ResourceName.PATH_PART)
public final class SubscriptionResourceOperatorImpl implements ResourceOperator {


	private final ResourceOperationContext resourceOperationContext;
	private final SubscriptionLookupStrategy subscriptionLookupStrategy;

	/**
	 * Default constructor.
	 * @param resourceOperationContext the resource operation context.
	 * @param subscriptionLookupStrategy the lookup strategy.
	 */
	@Inject
	SubscriptionResourceOperatorImpl(
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("subscriptionLookupStrategy")
			final SubscriptionLookupStrategy subscriptionLookupStrategy) {
		this.resourceOperationContext = resourceOperationContext;
		this.subscriptionLookupStrategy = subscriptionLookupStrategy;
	}

	/**
	 * Handles the READ/READ_NOLINKS operations to get a list of subscriptions.
	 *
	 * @param scope scope
	 * @param operation resource operation you are responding to
	 * @return operation result
	 */
	@Path(Scope.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadSubscriptionList(
			@Scope
			final String scope,
			final ResourceOperation operation) {

		String userId = resourceOperationContext.getUserIdentifier();
		Collection<String> subscriptionIds = Assign.ifSuccessful(subscriptionLookupStrategy.getSubscriptionIds(scope, userId));
		Collection<String> encodedIds = Base32Util.encodeAll(subscriptionIds);
		String selfUri = operation.getUri();
		Self self = SelfFactory.createSelf(selfUri);
		ResourceState<LinksEntity> subscriptions = ResourceState.Builder
				.create(LinksEntity.builder().build())
				.withSelf(self)
				.withLinks(ElementListFactory.createElementsOfList(selfUri, encodedIds, SubscriptionsMediaTypes.SUBSCRIPTION.id()))
				.build();

		return OperationResultFactory.createReadOK(subscriptions, operation);
	}

	/**
	 * Process READ operation for a single subscription.
	 *
	 * @param scope the scope
	 * @param subscriptionId the subscription ID
	 * @param operation the Resource Operation
	 * @return the {@link OperationResult} with a subscription {@link ResourceState}
	 */
	@Path({Scope.PATH_PART, ResourceId.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadSubscription(
			@Scope
			final String scope,
			@ResourceId
			final String subscriptionId,
			final ResourceOperation operation) {

		String decodedSubscriptionId = Base32Util.decode(subscriptionId);
		SubscriptionEntity subscriptionEntity = Assign.ifSuccessful(subscriptionLookupStrategy.getSubscription(scope, decodedSubscriptionId));
		ResourceState<SubscriptionEntity> subscription =  ResourceState.Builder.create(subscriptionEntity)
				.withSelf(SelfFactory.createSelf(operation.getUri()))
				.withScope(scope)
				.build();

		return OperationResultFactory.createReadOK(subscription, operation);
	}
}
