/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.link.impl;

import static com.elasticpath.rest.resource.coupons.rels.CouponsResourceRels.APPLY_COUPON_ACTION_REL;
import static com.elasticpath.rest.schema.ResourceLinkFactory.createUriRel;

import java.util.Collections;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.RestConstants;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.resource.dispatch.linker.FormLinkHandler;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Link Handler.
 */
@Singleton
@Named("couponFormLinkHandler")
public class CouponFormLinkHandler implements FormLinkHandler<CouponEntity> {

	private static final String SLASH_FORM = RestConstants.SLASH_CHAR + Form.URI_PART;

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<CouponEntity> resourceState) {

		return Collections.singleton(createApplyCouponLink(resourceState.getSelf().getUri()));
	}

	private ResourceLink createApplyCouponLink(final String selfUri) {

		String applyCouponUri = StringUtils.substringBefore(selfUri, SLASH_FORM);

		return createUriRel(
				applyCouponUri,
				APPLY_COUPON_ACTION_REL
		);
	}
}
