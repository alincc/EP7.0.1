/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.slots.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.OperationResultFactory;
import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.slots.SlotEntity;
import com.elasticpath.rest.definition.slots.SlotsMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.slots.integration.SlotLookupStrategy;
import com.elasticpath.rest.resource.slots.integration.dto.SlotDto;
import com.elasticpath.rest.resource.slots.rel.SlotsResourceRels;
import com.elasticpath.rest.resource.slots.transform.impl.SlotTransformer;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Processes the resource operation on Example.
 */
@Singleton
@Named("slotsResourceOperator")
@Path(ResourceName.PATH_PART)
public class SlotsResourceOperatorImpl implements ResourceOperator {

	private final SlotLookupStrategy slotLookupStrategy;
	private final SlotTransformer slotTransformer;
	private final String resourceServerName;


	/**
	 * Constructor.
	 * @param slotLookupStrategy the slot lookup strategy.
	 * @param slotTransformer the slot transformer.
	 * @param resourceServerName the resource server name.
	 */
	@Inject
	public SlotsResourceOperatorImpl(
			@Named("slotLookupStrategy")
			final SlotLookupStrategy slotLookupStrategy,
			@Named("slotTransformer")
			final SlotTransformer slotTransformer,
			@Named("resourceServerName")
			final String resourceServerName) {

		this.slotLookupStrategy = slotLookupStrategy;
		this.slotTransformer = slotTransformer;
		this.resourceServerName = resourceServerName;
	}


	/**
	 * Handles the READ operations for the slots list.
	 *
	 * @param scope the scope
	 * @param operation the Resource Operation.
	 * @return the operation result
	 */
	@Path(Scope.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadList(
			@Scope
			final String scope,
			final ResourceOperation operation) {

		Collection<String> slotIds = Assign.ifSuccessful(slotLookupStrategy.findAllSlotIds(scope));
		String selfUri = URIUtil.format(resourceServerName, scope);
		Self self = SelfFactory.createSelf(selfUri);

		Collection<ResourceLink> links = new ArrayList<>();
		for (String slotId : slotIds) {
			String elementUri = URIUtil.format(selfUri, Base32Util.encode(slotId));
			ResourceLink slotLink = ElementListFactory.createElementOfList(elementUri, SlotsMediaTypes.SLOT.id());
			links.add(slotLink);
		}

		ResourceState<LinksEntity> slots = ResourceState.Builder.create(LinksEntity.builder().build())
				.withSelf(self)
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(SlotsResourceRels.SLOT_LIST_MAX_AGE)
						.build())
				.withLinks(links)
				.build();
		return OperationResultFactory.createReadOK(slots, operation);
	}

	/**
	 * Handles the READ operations for the slot.
	 *
	 * @param scope the scope
	 * @param slotId the slot id
	 * @param operation the resource operation.
	 * @return the operation result
	 */
	@Path({Scope.PATH_PART, ResourceId.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadSlot(
			@Scope
			final String scope,
			@ResourceId
			final String slotId,
			final ResourceOperation operation) {

		String decodedSlotId = Base32Util.decode(slotId);
		SlotDto slotDto = Assign.ifSuccessful(slotLookupStrategy.getSlot(scope, decodedSlotId));
		ResourceState<SlotEntity> slot = slotTransformer.transformToResourceState(scope, slotDto);
		return OperationResultFactory.createReadOK(slot, operation);
	}
}
