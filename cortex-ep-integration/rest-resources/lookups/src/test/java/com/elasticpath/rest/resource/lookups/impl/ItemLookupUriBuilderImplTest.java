/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.resource.lookups.Items;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests Uris constructed by {@link ItemLookupUriBuilderImpl}.
 */
public class ItemLookupUriBuilderImplTest {

	private static final String RESOURCE_SERVER = "lookups";
	private static final String SCOPE = "SCOPE";
	private static final String RFO_URI_PART = "/blah/blah";

	@Test
	public void testBuilduriWhenEverythingSet() {
		String uri = new ItemLookupUriBuilderImpl(RESOURCE_SERVER)
				.setScope(SCOPE)
				.setFormPart()
				.build();

		assertEquals("Incorrect URI generated: ", URIUtil.format(RESOURCE_SERVER, SCOPE, Items.PATH_PART, Form.PATH_PART), uri);
	}

	@Test(expected = AssertionError.class)
	public void testBuilduriWhenScopeMissing() {
		new ItemLookupUriBuilderImpl(RESOURCE_SERVER)
				.setFormPart()
				.build();
	}

	@Test
	public void testBuildItemSearchUriWhenEverythingSet() {
		String uri = new ItemLookupUriBuilderImpl(RESOURCE_SERVER)
				.setScope(SCOPE)
				.setItemsPart()
				.build();

		assertEquals("Incorrect URI generated: ", URIUtil.format(RESOURCE_SERVER, SCOPE, Items.PATH_PART), uri);
	}

	@Test(expected = AssertionError.class)
	public void testBuildItemSearchUriWhenScopeMissing() {
		new ItemLookupUriBuilderImpl(RESOURCE_SERVER)
				.setItemsPart()
				.build();
	}

	@Test
	public void testBuildItemRfoUriWhenEverythingSet() {
		String uri = new ItemLookupUriBuilderImpl(RESOURCE_SERVER)
				.setSourceUri(RFO_URI_PART)
				.build();

		assertEquals("Incorrect URI generated: ", URIUtil.format(RESOURCE_SERVER, RFO_URI_PART), uri);
	}
}
