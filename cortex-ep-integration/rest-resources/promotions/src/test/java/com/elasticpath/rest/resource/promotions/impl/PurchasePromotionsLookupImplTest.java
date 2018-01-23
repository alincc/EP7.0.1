/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.promotions.integration.AppliedPurchasePromotionsLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Test class for {@link PurchasePromotionsLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class PurchasePromotionsLookupImplTest {

	private static final String SCOPE = "scope";

	private static final String PROMOTION_ID = "12345";

	private static final String PURCHASE_ID = "abcde";

	private static final String ENCODED_PURCHASE_ID = Base32Util.encode(PURCHASE_ID);

	private static final String SOURCE_URI = "/source/abcd=";

	private final Collection<String> promotionIds = Collections.singleton(PROMOTION_ID);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ResourceState<LinksEntity> expectedLinksRepresentation;

	private final ResourceState<PurchaseEntity> purchaseRepresentation = createPurchaseRepresentation();

	@Mock
	private AppliedPurchasePromotionsLookupStrategy mockAppliedPromosLookupStrategy;

	@Mock
	private TransformRfoToResourceState<LinksEntity, Collection<String>, PurchaseEntity> mockPurchaseAppliedPromotionsTransformer;

	@InjectMocks
	private PurchasePromotionsLookupImpl purchasePromotionsLookup;


	@Test
	public void testGetAppliedPromotionsForPurchaseWhenSuccess() {
		when(mockAppliedPromosLookupStrategy.getAppliedPromotionsForPurchase(SCOPE, PURCHASE_ID))
			.thenReturn(ExecutionResultFactory.createReadOK(promotionIds));

		when(mockPurchaseAppliedPromotionsTransformer.transform(promotionIds, purchaseRepresentation))
				.thenReturn(expectedLinksRepresentation);

		ExecutionResult<ResourceState<LinksEntity>> result = purchasePromotionsLookup.getAppliedPromotionsForPurchase(purchaseRepresentation);

		assertExecutionResult(result).isSuccessful().resourceStatus(ResourceStatus.READ_OK).data(expectedLinksRepresentation);
	}

	@Test
	public void testGetAppliedPromotionsForPurchaseWhenFailure() {
		when(mockAppliedPromosLookupStrategy.getAppliedPromotionsForPurchase(SCOPE, PURCHASE_ID))
			.thenReturn(ExecutionResultFactory.<Collection<String>> createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchasePromotionsLookup.getAppliedPromotionsForPurchase(purchaseRepresentation);
	}

	private ResourceState<PurchaseEntity> createPurchaseRepresentation() {
		PurchaseEntity purchaseEntity = PurchaseEntity.builder()
				.withPurchaseId(ENCODED_PURCHASE_ID)
				.build();
		Self self = SelfFactory.createSelf(SOURCE_URI);

		return ResourceState.<PurchaseEntity>builder()
				.withEntity(purchaseEntity)
				.withSelf(self)
				.withScope(SCOPE)
				.build();
	}

}
