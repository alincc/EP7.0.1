/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.transform;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionValueEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Values;
import com.elasticpath.rest.resource.purchases.constants.PurchaseResourceConstants;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.test.AssertResourceInfo;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests for {@link PurchaseLineItemOptionValueTransformer}.
 */
public final class PurchaseLineItemOptionValueTransformerTest {

	private static final String LINEITEMURI = "/lineitemuri";
	private static final String OPTION_ID = Base32Util.encode("optionId");
	private static final String VALUE_ID = Base32Util.encode("valueId");
	private static final String NAME = "HD";
	private static final String DISPLAY_NAME = "High Definition";

	private final PurchaseLineItemOptionValueTransformer transformer = new PurchaseLineItemOptionValueTransformer();

	/**
	 * Test transform to representation.
	 */
	@Test
	public void testTransformToRepresentation() {
		PurchaseLineItemOptionValueEntity optionValueDto = createDto();
		ResourceState<PurchaseLineItemOptionValueEntity> representation = transformer.transformToRepresentation(LINEITEMURI, OPTION_ID, VALUE_ID,
				optionValueDto);

		assertEquals("The representation should have the expected name", NAME, representation.getEntity().getName());
		assertEquals("The representation should have the expected display name", DISPLAY_NAME, representation.getEntity().getDisplayName());

		String optionValueUri = URIUtil.format(LINEITEMURI, Options.URI_PART, OPTION_ID, Values.URI_PART, VALUE_ID);
		Self expectedSelf = SelfFactory.createSelf(optionValueUri);
		assertEquals("The self representation should be as expected", expectedSelf, representation.getSelf());
		AssertResourceInfo.assertResourceInfo(representation.getResourceInfo())
			.maxAge(PurchaseResourceConstants.MAX_AGE);
	}

	private PurchaseLineItemOptionValueEntity createDto() {
		return PurchaseLineItemOptionValueEntity.builder()
				.withName(NAME)
				.withDisplayName(DISPLAY_NAME)
				.build();
	}
}
