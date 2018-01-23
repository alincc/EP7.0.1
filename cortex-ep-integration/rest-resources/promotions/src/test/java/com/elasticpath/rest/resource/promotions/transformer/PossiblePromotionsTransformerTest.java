/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.transformer;

import com.elasticpath.rest.resource.promotions.PossiblePromotions;
import com.elasticpath.rest.resource.promotions.impl.PromotionsUriBuilderImpl;

/**
 * Test class for {@link PossiblePromotionsTransformer}.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class PossiblePromotionsTransformerTest extends AbstractPromotionsRFOTransformerContractTest {

	void createTransformerUnderTest(final PromotionsTransformer promotionsTransformer) {
		transformerUnderTest =  new PossiblePromotionsTransformer(promotionsUriBuilderFactory, promotionsTransformer);
	}

	String createExpectedUri() {
		return new PromotionsUriBuilderImpl(RESOURCE_SERVER_NAME)
				.setSourceUri(SOURCE_URI)
				.setPromotionType(PossiblePromotions.URI_PART)
				.build();
	}
}
