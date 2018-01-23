/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.commons.lang3.StringUtils;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.uri.URIUtil;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Info;

/**
 * Test.
 */
@RunWith(MockitoJUnitRunner.class)
public class CouponsUriBuilderImplTest {

	private static final String RESOURCE_NAME = "coupons";
	private static final String ROOT = URIUtil.format(RESOURCE_NAME);
	private static final String COUPON_ID = "/couponid";
	private static final String OTHER_URI = "/resource/scope/id";
	private static final String URI_FAIL_MESSAGE = "Unexpected URI.";

	private final CouponsUriBuilderImpl uriBuilder = new CouponsUriBuilderImpl(RESOURCE_NAME);

	@Test
	public void testGoodOtherUri() {
		String uri = uriBuilder.setSourceUri(OTHER_URI).build();
		assertEquals("Unexpected uri", URIUtil.format(ROOT, OTHER_URI), uri);
	}

	@Test
	public void testGoodOtherUriWithId() {
		String uri = uriBuilder.setSourceUri(OTHER_URI).setCouponId(COUPON_ID).build();
		assertEquals("Unexpected uri", URIUtil.format(ROOT, OTHER_URI, COUPON_ID), uri);
	}

	@Test
	public void testEmptyOtherUri() {
		String uri = uriBuilder.setSourceUri(StringUtils.EMPTY).build();
		assertEquals(URI_FAIL_MESSAGE, ROOT, uri);
	}

	@Test
	public void testGoodInfoUri() {
		String uri = uriBuilder
				.setSourceUri(OTHER_URI)
				.setInfoUri()
				.build();
		assertEquals(URI_FAIL_MESSAGE, URIUtil.format(ROOT, OTHER_URI, Info.URI_PART), uri);
	}

	@Test
	public void testCorrectFormUri() {
		String uri = uriBuilder
				.setSourceUri(OTHER_URI)
				.setFormUri()
				.build();
		assertEquals(URI_FAIL_MESSAGE, URIUtil.format(ROOT, OTHER_URI, Form.URI_PART), uri);
	}

	@Test(expected = AssertionError.class)
	public void testSourceUriCannotBeNull() {
		uriBuilder.build();
	}

	@Test(expected = AssertionError.class)
	public void testInfoUriAndCouponIdCannotBothBeSet() {
		uriBuilder.setCouponId(COUPON_ID).setInfoUri().build();
	}

	@Test(expected = AssertionError.class)
	public void testInfoUriAndFormUriCannotBothBeSet() {
		uriBuilder.setFormUri().setInfoUri().build();
	}

	@Test(expected = AssertionError.class)
	public void testFormUriAndCouponIdCannotBothBeSet() {
		uriBuilder.setCouponId(COUPON_ID).setFormUri().build();
	}

	@Test(expected = AssertionError.class)
	public void testFormUriAndCouponIdAndInfoUriCannotBothBeSet() {
		uriBuilder.setCouponId(COUPON_ID).setFormUri().setInfoUri().build();
	}
}
