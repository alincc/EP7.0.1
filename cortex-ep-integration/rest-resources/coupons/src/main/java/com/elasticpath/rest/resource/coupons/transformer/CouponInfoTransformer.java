/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.transformer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.coupons.constant.CouponsConstants;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.schema.uri.CouponsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Transform a collection of decoded coupon ids to a links representation for the given representation.
 */
@Singleton
@Named("couponInfoTransformer")
public final class CouponInfoTransformer
		implements TransformRfoToResourceState<InfoEntity, Iterable<String>, OrderEntity> {

	private final CouponsUriBuilderFactory couponsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param couponsUriBuilderFactory coupons uri builder factory.
	 */
	@Inject
	public CouponInfoTransformer(
			@Named("couponsUriBuilderFactory")
			final CouponsUriBuilderFactory couponsUriBuilderFactory) {
		this.couponsUriBuilderFactory = couponsUriBuilderFactory;
	}

	@Override
	public ResourceState<InfoEntity> transform(final Iterable<String> decodedCouponIds,
			final ResourceState<OrderEntity> orderState) {
		String couponInfoSelfUri = createSelfUri(orderState);

		InfoEntity infoEntity = InfoEntity.builder()
				.withName(CouponsConstants.COUPON_INFO_NAME)
				.withInfoId(orderState.getEntity().getOrderId())
				.build();

		Self self = SelfFactory.createSelf(couponInfoSelfUri);
		return ResourceState.Builder
				.create(infoEntity)
				.withSelf(self)
				.withScope(orderState.getScope())
				.build();
	}

	private String createCouponInfoUri(final String otherUri) {
		return couponsUriBuilderFactory.get()
				.setSourceUri(otherUri)
				.setInfoUri()
				.build();
	}

	private String createSelfUri(final ResourceState<?> otherRepresentation) {
		String otherUri = ResourceStateUtil.getSelfUri(otherRepresentation);
		return createCouponInfoUri(otherUri);
	}
}
