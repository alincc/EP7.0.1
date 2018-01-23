/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.transform;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformToResourceState;

/**
 * Transforms an entity to representation.
 */
@Singleton
@Named("shipmentTransformer")
public final class ShipmentTransformer implements TransformToResourceState<ShipmentEntity, ShipmentEntity> {

	private final ResourceOperationContext operationContext;

	/**
	 * Constructor.
	 *
	 * @param operationContext the resource operation context
	 */
	@Inject
	ShipmentTransformer(
			@Named("resourceOperationContext")
			final ResourceOperationContext operationContext) {
		this.operationContext = operationContext;
	}


	@Override
	public ResourceState<ShipmentEntity> transform(final String scope, final ShipmentEntity shipmentEntity) {
		String selfUri = operationContext.getResourceOperation().getUri();
		Self self = SelfFactory.createSelf(selfUri);

		return ResourceState.Builder.create(shipmentEntity)
				.withSelf(self)
				.withScope(scope)
				.build();
	}
}