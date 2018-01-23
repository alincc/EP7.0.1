/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.transformer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.discounts.DiscountEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.schema.uri.DiscountsUriBuilderFactory;

/**
 * Transform a discount entity (RFO on Purchase) to a discount representation.
 */
@Singleton
@Named("discountsPurchaseTransformer")
public final class PurchaseDiscountsRfoResourceStateTransformerImpl implements TransformRfoToResourceState<DiscountEntity, DiscountEntity,
		PurchaseEntity> {

	private final DiscountsUriBuilderFactory discountsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param discountsUriBuilderFactory the URI builder factory.
	 */
	@Inject
	PurchaseDiscountsRfoResourceStateTransformerImpl(
			@Named("discountsUriBuilderFactory")
			final DiscountsUriBuilderFactory discountsUriBuilderFactory) {

		this.discountsUriBuilderFactory = discountsUriBuilderFactory;
	}

	@Override
	public ResourceState<DiscountEntity> transform(final DiscountEntity discountForPurchase,
			final ResourceState<PurchaseEntity> otherRepresentation) {

		String otherUri = otherRepresentation.getSelf()
											.getUri();
		String selfUri = discountsUriBuilderFactory.get()
												.setSourceUri(otherUri)
												.build();
		Self self = SelfFactory.createSelf(selfUri);

		return ResourceState.Builder.create(DiscountEntity.builderFrom(discountForPurchase)
															.withPurchaseId(otherRepresentation.getEntity().getPurchaseId())
			.build())
			.withSelf(self)
									.withScope(otherRepresentation.getScope())
									.build();

	}
}
