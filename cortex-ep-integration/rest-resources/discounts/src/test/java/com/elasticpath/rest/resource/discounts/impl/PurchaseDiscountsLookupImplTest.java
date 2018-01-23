/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Mockito.when;

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
import com.elasticpath.rest.definition.discounts.DiscountEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.discounts.integration.PurchaseDiscountsLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Unit test for PurchaseDiscountsLookupImpl.
 */
@RunWith(MockitoJUnitRunner.class)
public class PurchaseDiscountsLookupImplTest {

	private static final String SCOPE = "scope";
	private static final String PURCHASE_GUID = "purchaseGuid";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private PurchaseDiscountsLookupStrategy purchaseDiscountsLookupStrategy;
	@Mock
	private TransformRfoToResourceState<DiscountEntity, DiscountEntity, PurchaseEntity> discountsCartTransformer;
	@InjectMocks
	private PurchaseDiscountsLookupImpl discountsLookup;

	@Mock
	private ResourceState<PurchaseEntity> purchaseRepresentation;
	@Mock
	private DiscountEntity discountEntity;
	@Mock
	private ResourceState<DiscountEntity> discountResourceState;


	@Test
	public void ensurePurchaseDiscountsCanBeRetrievedSuccessfully() {
		givenAValidPurchaseResourceState();

		when(purchaseDiscountsLookupStrategy.getPurchaseDiscounts(PURCHASE_GUID, SCOPE))
				.thenReturn(ExecutionResultFactory.createReadOK(discountEntity));
		when(discountsCartTransformer.transform(discountEntity, purchaseRepresentation))
				.thenReturn(discountResourceState);

		ExecutionResult<ResourceState<DiscountEntity>> result = discountsLookup.getPurchaseDiscounts(purchaseRepresentation);

		assertExecutionResult(result).data(discountResourceState);
	}

	@Test
	public void ensureNotFoundReturnedWhenPurchaseForDiscountsNotFound() {
		givenAValidPurchaseResourceState();

		when(purchaseDiscountsLookupStrategy.getPurchaseDiscounts(PURCHASE_GUID, SCOPE))
				.thenReturn(ExecutionResultFactory.<DiscountEntity>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		discountsLookup.getPurchaseDiscounts(purchaseRepresentation);
	}

	@Test
	public void ensureServerErrorReturnedWhenErrorOccursLookingUpPurchases() {
		givenAValidPurchaseResourceState();

		when(purchaseDiscountsLookupStrategy.getPurchaseDiscounts(PURCHASE_GUID, SCOPE))
				.thenReturn(ExecutionResultFactory.<DiscountEntity>createServerError(""));
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		discountsLookup.getPurchaseDiscounts(purchaseRepresentation);
	}

	private void givenAValidPurchaseResourceState() {
		PurchaseEntity purchaseEntity = PurchaseEntity.builder()
												.withPurchaseId(Base32Util.encode(PURCHASE_GUID))
												.build();

		when(purchaseRepresentation.getEntity()).thenReturn(purchaseEntity);
		when(purchaseRepresentation.getScope()).thenReturn(SCOPE);
	}
}