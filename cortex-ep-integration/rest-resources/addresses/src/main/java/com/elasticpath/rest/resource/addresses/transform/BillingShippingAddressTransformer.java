/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.transform;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformToResourceState;

/**
 * A base links representation transformer for the billing and shipping addresses.
 */
@Singleton
@Named("billingShippingAddressTransformer")
public class BillingShippingAddressTransformer implements TransformToResourceState<LinksEntity, Collection<ResourceLink>> {

	private final ResourceOperationContext operationContext;

	/**
	 * Constructor.
	 *
	 * @param operationContext get user identifier
	 */
	@Inject
	public BillingShippingAddressTransformer(
			@Named("resourceOperationContext")
			final ResourceOperationContext operationContext) {

		this.operationContext = operationContext;
	}

	@Override
	public ResourceState<LinksEntity> transform(final String scope, final Collection<ResourceLink> addressLinks) {
		Self self = SelfFactory.createSelf(operationContext.getResourceOperation().getUri());
		LinksEntity linksEntity = LinksEntity.builder().build();
		return ResourceState.Builder
				.create(linksEntity)
				.withScope(scope)
				.withLinks(addressLinks)
				.withSelf(self)
				.build();
	}
}
