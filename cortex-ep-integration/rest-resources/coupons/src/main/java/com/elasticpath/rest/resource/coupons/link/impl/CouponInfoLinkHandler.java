/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.link.impl;

import static com.elasticpath.rest.definition.coupons.CouponsMediaTypes.COUPON;
import static com.elasticpath.rest.definition.orders.OrdersMediaTypes.ORDER;
import static com.elasticpath.rest.id.util.Base32Util.decode;
import static com.elasticpath.rest.id.util.Base32Util.encodeAll;
import static com.elasticpath.rest.resource.coupons.constant.CouponsConstants.COUPON_INFO_NAME;
import static com.elasticpath.rest.resource.coupons.rels.CouponsResourceRels.APPLY_COUPON_FORM_REL;
import static com.elasticpath.rest.resource.coupons.rels.CouponsResourceRels.ORDER_REL;
import static com.elasticpath.rest.schema.ResourceLinkFactory.createNoRev;
import static com.google.common.collect.Iterables.transform;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.resource.coupons.integration.OrderCouponsLookupStrategy;
import com.elasticpath.rest.resource.coupons.rels.CouponsResourceRels;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.CouponsUriBuilderFactory;
import com.elasticpath.rest.schema.uri.OrdersUriBuilderFactory;
import com.google.common.collect.ImmutableList;

/**
 * Link Handler.
 */
@Singleton
@Named("couponInfoLinkHandler")
public class CouponInfoLinkHandler implements ResourceStateLinkHandler<InfoEntity> {

	@Inject
	@Named("couponsUriBuilderFactory")
	private CouponsUriBuilderFactory couponsUriBuilderFactory;

	@Inject
	@Named("orderCouponsLookupStrategy")
	private OrderCouponsLookupStrategy orderCouponsLookupStrategy;

	@Inject
	@Named("ordersUriBuilderFactory")
	private OrdersUriBuilderFactory ordersUriBuilderFactory;

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<InfoEntity> resourceState) {

		if (!COUPON_INFO_NAME.equals(resourceState.getEntity()
				.getName())) {
			return ImmutableList.of();
		}

		String scope = resourceState.getScope();

		String orderId = resourceState.getEntity()
				.getInfoId();

		Collection<String> encodedCouponIds = encodeAll(
				orderCouponsLookupStrategy.findCouponIdsForOrder(scope, decode(orderId))
						.getData()
		);

		String orderUri = ordersUriBuilderFactory.get()
				.setOrderId(orderId)
				.setScope(scope)
				.build();

		return ImmutableList.<ResourceLink>builder()
				.add(createApplyCouponLink(orderUri))
				.add(createOrderLink(orderUri))
				.addAll(createCouponLinks(orderUri, encodedCouponIds))
				.build();
	}

	private ResourceLink createOrderLink(final String orderUri) {
		return createNoRev(
				orderUri,
				ORDER.id(),
				ORDER_REL
		);
	}

	private ResourceLink createApplyCouponLink(final String orderUri) {
		String couponFormUri = couponsUriBuilderFactory.get()
				.setSourceUri(orderUri)
				.setFormUri()
				.build();
		return createNoRev(
				couponFormUri,
				COUPON.id(),
				APPLY_COUPON_FORM_REL
		);
	}

	private Iterable<ResourceLink> createCouponLinks(final String orderUri, final Iterable<String> couponIds) {
		return transform(couponIds, couponId ->
			createNoRev(
				createCouponUri(orderUri, couponId),
				COUPON.id(),
				CouponsResourceRels.COUPON_REL
		));
	}

	private String createCouponUri(final String orderUri, final String couponId) {
		return couponsUriBuilderFactory.get()
				.setSourceUri(orderUri)
				.setCouponId(couponId)
				.build();
	}
}
