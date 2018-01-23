/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.transformer;

import com.elasticpath.rest.resource.promotions.AppliedPromotions;
import com.elasticpath.rest.resource.promotions.impl.PromotionsUriBuilderImpl;

/**
 * Test class for {@link AppliedPromotionsTransformer}.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class AppliedPromotionsTransformerTest extends AbstractPromotionsRFOTransformerContractTest {

	void createTransformerUnderTest(final PromotionsTransformer promotionsTransformer) {
		transformerUnderTest =  new AppliedPromotionsTransformer(promotionsUriBuilderFactory, promotionsTransformer);
	}

	String createExpectedUri() {
		return new PromotionsUriBuilderImpl(RESOURCE_SERVER_NAME)
				.setSourceUri(SOURCE_URI)
				.setPromotionType(AppliedPromotions.URI_PART)
				.build();
	}

}
