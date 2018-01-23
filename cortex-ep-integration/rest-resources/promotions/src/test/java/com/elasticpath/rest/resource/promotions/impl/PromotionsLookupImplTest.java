/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.promotions.PromotionEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.promotions.integration.PromotionsLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformToResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Test class for {@link PromotionsLookupImplTest}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class PromotionsLookupImplTest {

	private static final String SCOPE = "scope";
	private static final String PROMOTION_ID = "12345";
	private static final String ENCODED_PROMOTION_ID = Base32Util.encode(PROMOTION_ID);
	public static final String PURCHASE_ID = "1234";
	private static final String ENCODED_PURCHASE_ID = Base32Util.encode(PURCHASE_ID);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private PromotionsLookupStrategy mockLookupStrategy;
	@Mock
	private TransformToResourceState<PromotionEntity, PromotionEntity> mockPromotionDetailsTransformer;
	@Mock
	private TransformRfoToResourceState<PromotionEntity, PromotionEntity, PurchaseEntity> mockReadFromOtherPromotionDetailsTransformer;
	@Mock
	private ResourceState<PurchaseEntity> mockPurchaseRepresentation;
	@Mock
	private PurchaseEntity mockPurchaseEntity;
	@Mock
	private ResourceState<PromotionEntity> expectedRepresentation;


	private PromotionsLookupImpl promotionsLookup;


	/**
	 * Setup Mocks.
	 * Not using @InjectMocks, as Mockito has a Java 7 bug that causes confusion with parameters of same type.
	 */
	@Before
	public void setUp() {
		promotionsLookup = new PromotionsLookupImpl(
				mockLookupStrategy,
				mockPromotionDetailsTransformer,
				mockReadFromOtherPromotionDetailsTransformer
		);

		when(mockPurchaseRepresentation.getEntity()).thenReturn(mockPurchaseEntity);
		when(mockPurchaseEntity.getPurchaseId()).thenReturn(ENCODED_PURCHASE_ID);
	}

	@Test
	public void testGetPromotionDetailsWhenStrategyReturnsSuccessfully() {
		ResourceState<PromotionEntity> expectedRepresentation = setupMocksToReturnExpectedPromotion();

		ExecutionResult<ResourceState<PromotionEntity>> result = promotionsLookup.getPromotionDetails(SCOPE, ENCODED_PROMOTION_ID);

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK)
				.data(expectedRepresentation);
	}

	@Test
	public void testGetPromotionDetailsWhenFailureResultFromStrategy() {
		when(mockLookupStrategy.getPromotionById(SCOPE, PROMOTION_ID))
				.thenReturn(ExecutionResultFactory.<PromotionEntity>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		promotionsLookup.getPromotionDetails(SCOPE, ENCODED_PROMOTION_ID);

	}

	@Test
	public void testGetPurchasePromotionDetailsWhenStrategyReturnsSuccessfully() {
		ResourceState<PromotionEntity> expectedRepresentation = setupPurchaseDetailsMocksToReturnExpectedPromotion();

		ExecutionResult<ResourceState<PromotionEntity>> result = promotionsLookup.getPurchasePromotionDetails(SCOPE, ENCODED_PROMOTION_ID,
				PURCHASE_ID,
				mockPurchaseRepresentation);

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK)
				.data(expectedRepresentation);
	}

	@Test
	public void testGetPurchasePromotionDetailsWhenFailureResultFromStrategy() {
		when(mockLookupStrategy.getPromotionForPurchase(SCOPE, PROMOTION_ID, PURCHASE_ID))
				.thenReturn(ExecutionResultFactory.<PromotionEntity>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		promotionsLookup.getPurchasePromotionDetails(SCOPE, ENCODED_PROMOTION_ID,
				PURCHASE_ID,
				mockPurchaseRepresentation);
	}


	private ResourceState<PromotionEntity> setupMocksToReturnExpectedPromotion() {
		PromotionEntity mockDto = Mockito.mock(PromotionEntity.class);
		when(mockLookupStrategy.getPromotionById(SCOPE, PROMOTION_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(mockDto));

		when(mockPromotionDetailsTransformer.transform(SCOPE, mockDto)).thenReturn(expectedRepresentation);

		return expectedRepresentation;
	}

	private ResourceState<PromotionEntity> setupPurchaseDetailsMocksToReturnExpectedPromotion() {
		PromotionEntity mockDto = Mockito.mock(PromotionEntity.class);
		when(mockLookupStrategy.getPromotionForPurchase(SCOPE, PROMOTION_ID, PURCHASE_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(mockDto));

		when(mockReadFromOtherPromotionDetailsTransformer.transform(mockDto, mockPurchaseRepresentation))
				.thenReturn(expectedRepresentation);

		return expectedRepresentation;
	}
}
