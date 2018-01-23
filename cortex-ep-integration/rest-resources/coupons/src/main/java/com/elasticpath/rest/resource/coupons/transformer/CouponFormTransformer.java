/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.transformer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.schema.uri.CouponsUriBuilderFactory;

/**
 * Transform coupon entity into a coupon form.
 */
@Singleton
@Named("couponFormTransformer")
public final class CouponFormTransformer implements TransformRfoToResourceState<CouponEntity, CouponEntity, ResourceEntity> {

	private final CouponsUriBuilderFactory couponsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param couponsUriBuilderFactory coupons uri builder factory.
	 */
	@Inject
	CouponFormTransformer(
			@Named("couponsUriBuilderFactory")
			final CouponsUriBuilderFactory couponsUriBuilderFactory) {
		this.couponsUriBuilderFactory = couponsUriBuilderFactory;
	}

	@Override
	public ResourceState<CouponEntity> transform(final CouponEntity couponEntity,
			final ResourceState<ResourceEntity> otherRepresentation) {
		String otherUri = otherRepresentation.getSelf().getUri();

		String formUri = createFormUri(otherUri);
		Self self = SelfFactory.createSelf(formUri);

		return ResourceState.Builder.create(couponEntity)
				.withSelf(self)
				.build();
	}

	private String createFormUri(final String resourceUri) {
		return couponsUriBuilderFactory.get()
				.setSourceUri(resourceUri)
				.setFormUri()
				.build();
	}
}
