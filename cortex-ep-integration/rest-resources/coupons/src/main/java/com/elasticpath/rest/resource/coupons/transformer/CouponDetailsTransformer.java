/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.transformer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.coupons.constant.CouponsConstants;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.schema.uri.CouponsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Transform to a {@link ResourceState}.
 */
@Singleton
@Named("couponDetailsTransformer")
public final class CouponDetailsTransformer implements TransformRfoToResourceState<CouponEntity, CouponEntity, ResourceEntity> {

	private final CouponsUriBuilderFactory couponsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param couponsUriBuilderFactory the coupons URI builder factory.
	 */
	@Inject
	CouponDetailsTransformer(
			@Named("couponsUriBuilderFactory")
			final CouponsUriBuilderFactory couponsUriBuilderFactory) {
		this.couponsUriBuilderFactory = couponsUriBuilderFactory;
	}

	/**
	 * Transform from a coupon entity, using the scoped representation to create a coupon representation.
	 *
	 * @param couponEntity coupon entity to use as a base for the coupon representation.
	 * @param otherRepresentation other representation to use for uri building.
	 * @return coupon representation.
	 */
	public ResourceState<CouponEntity> transform(final CouponEntity couponEntity, final ResourceState<ResourceEntity> otherRepresentation) {

		String encodedCouponId = Base32Util.encode(couponEntity.getCouponId());
		String otherUri = ResourceStateUtil.getSelfUri(otherRepresentation);
		Self self = buildSelf(otherUri, encodedCouponId);
		String scope = otherRepresentation.getScope();
		ResourceInfo info = ResourceInfo.builder()
			.withMaxAge(CouponsConstants.PURCHASE_COUPON_MAX_AGE)
			.build();


		return ResourceState.Builder.create(couponEntity)
				.withSelf(self)
				.withResourceInfo(info)
				.withScope(scope)
				.build();
	}

	private Self buildSelf(final String otherUri, final String encodedCouponId) {
		String selfUri = couponsUriBuilderFactory.get()
				.setSourceUri(otherUri)
				.setCouponId(encodedCouponId)
				.build();
		return createSelf(selfUri);
	}

	private Self createSelf(final String selfUri) {
		return SelfFactory.createSelf(selfUri);
	}
}
