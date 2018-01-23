/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integration.epcommerce.promotion.impl;

import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

@RunWith(MockitoJUnitRunner.class)
public class AppliedItemPromotionsLookupStrategyImplTest {

	private static final String ITEM_ID = "itemId";
	private static final String SCOPE = "scope";
	private static final String PROMOTION_ID = "promoId";
	@Mock
	private PromotionRepository promotionRepository;
	@InjectMocks
	private AppliedItemPromotionsLookupStrategyImpl appliedItemPromotionsLookupStrategy;
	@Mock
	private ProductSku productSku;
	@Mock
	private Product product;

	@Test
	public void getAppliedPromotionsForItemWhenOnePresent() {
		when(productSku.getProduct()).thenReturn(product);
		when(promotionRepository.getAppliedPromotionsForItem(SCOPE, ITEM_ID)).thenReturn(Collections.singleton(PROMOTION_ID));

		ExecutionResult<Collection<String>> result = appliedItemPromotionsLookupStrategy.getAppliedPromotionsForItem(SCOPE, ITEM_ID);

		assertExecutionResult(result)
				.isSuccessful()
				.data(Collections.singleton(PROMOTION_ID));
	}

	@Test
	public void getAppliedPromotionsForItemWhenNonePresent() {
		when(productSku.getProduct()).thenReturn(product);
		when(promotionRepository.getAppliedPromotionsForItem(SCOPE, ITEM_ID)).thenReturn(Collections.<String>emptySet());

		ExecutionResult<Collection<String>> result = appliedItemPromotionsLookupStrategy.getAppliedPromotionsForItem(SCOPE, ITEM_ID);

		assertExecutionResult(result)
				.isSuccessful()
				.data(Collections.EMPTY_SET);
	}

}
