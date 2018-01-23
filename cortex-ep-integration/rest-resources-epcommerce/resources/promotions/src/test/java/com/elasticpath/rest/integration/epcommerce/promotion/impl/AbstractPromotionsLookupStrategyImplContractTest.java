/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integration.epcommerce.promotion.impl;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.test.AssertExecutionResult;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractPromotionsLookupStrategyImplContractTest {

	public static final String SCOPE = "SCOPE";

	public static final String DECODED_ID = "DECODED_ID";

	public static final String PROMO_1 = "PROMO_1";

	public static final String PROMO_2 = "PROMO_2";

	@Test
	public void testGetNoPromotions() {
		Collection<String> expectedPromotions = setUpToReturnNoPromotions();

		ExecutionResult<Collection<String>> result = getPromotions();

		AssertExecutionResult.assertExecutionResult(result)
				.isSuccessful()
				.data(expectedPromotions);
	}

	@Test
	public void testGetOnePromotions() {
		Collection<String> expectedPromotions = setUpToReturnOnePromotion();

		ExecutionResult<Collection<String>> result = getPromotions();

		AssertExecutionResult.assertExecutionResult(result)
				.isSuccessful()
				.data(expectedPromotions);
	}

	@Test
	public void testGetMultiplePromotions() {
		Collection<String> expectedPromotions = setUpToReturnMultiplePromotions();

		ExecutionResult<Collection<String>> result = getPromotions();

		AssertExecutionResult.assertExecutionResult(result)
				.isSuccessful()
				.data(expectedPromotions);
	}

	@Test(expected = BrokenChainException.class)
	public void testGetPromotionsHasServerError() {
		setUpToReturnNotFoundWhenGettingPromotions();

		getPromotions();
	}

	abstract ExecutionResult<Collection<String>> getPromotions();

	abstract Collection<String> setUpToReturnNoPromotions();

	abstract Collection<String> setUpToReturnOnePromotion();

	abstract Collection<String> setUpToReturnMultiplePromotions();

	abstract void setUpToReturnNotFoundWhenGettingPromotions();
}
