/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.availabilities.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.OperationResultFactory;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.definition.availabilities.AvailabilityEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.resource.availabilities.integration.AvailabilityLookupStrategy;
import com.elasticpath.rest.resource.availabilities.rel.AvailabilityRepresentationRels;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Processes the resource operation on profiles.
 */
@Singleton
@Named("availabilitiesResourceOperator")
@Path(ResourceName.PATH_PART)
public final class AvailabilitiesResourceOperatorImpl implements ResourceOperator {

	private final String resourceServerName;
	private final AvailabilityLookupStrategy availabilityLookupStrategy;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName resource server name
	 * @param availabilityLookupStrategy availability lookup strategy.
	 */
	@Inject
	AvailabilitiesResourceOperatorImpl(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("availabilityLookupStrategy")
			final AvailabilityLookupStrategy availabilityLookupStrategy) {

		this.resourceServerName = resourceServerName;
		this.availabilityLookupStrategy = availabilityLookupStrategy;
	}


	/**
	 * Handles the READ operations for availability on items.
	 *
	 * @param item the item.
	 * @param operation the resource operation
	 * @return the result.
	 */
	@Path(AnyResourceUri.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadAvailabilityForItem(
			@AnyResourceUri
			final ResourceState<ItemEntity> item,
			final ResourceOperation operation) {

		String itemId = item.getEntity().getItemId();

		ResourceState<AvailabilityEntity> availability = getAvailability(item, itemId, AvailabilityRepresentationRels.ITEM_REL);

		return OperationResultFactory.createReadOK(availability, operation);
	}


	/**
	 * Handles the READ operations for availability on line items.
	 *
	 * @param lineItem the line item.
	 * @param operation the resource operation
	 * @return the result.
	 */
	@Path(AnyResourceUri.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadAvailabilityForLineItem(
			@AnyResourceUri
			final ResourceState<LineItemEntity> lineItem,
			final ResourceOperation operation) {

		String itemId = lineItem.getEntity().getItemId();

		ResourceState<AvailabilityEntity> availability = getAvailability(lineItem, itemId, AvailabilityRepresentationRels.LINE_ITEM_REL);

		return OperationResultFactory.createReadOK(availability, operation);
	}


	private ResourceState<AvailabilityEntity> getAvailability(final ResourceState<?> other, final String itemId, final String rel) {
		AvailabilityEntity availabilityEntity = Assign.ifSuccessful(availabilityLookupStrategy.getAvailability(other.getScope(), itemId));

		Self otherSelf = other.getSelf();
		String uri = URIUtil.format(resourceServerName, otherSelf.getUri());
		Self self = SelfFactory.createSelf(uri);

		ResourceLink linkToItem = ResourceLinkFactory.createFromSelf(
				otherSelf,
				rel,
				AvailabilityRepresentationRels.AVAILABILITY_REV);

		return ResourceState.Builder
				.create(availabilityEntity)
				.addingLinks(linkToItem)
				.withSelf(self)
				.build();
	}
}
