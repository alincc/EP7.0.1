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
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.discounts.DiscountEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.discounts.integration.CartDiscountsLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Unit test for CartDiscountsLookupImpl.
 */
@RunWith(MockitoJUnitRunner.class)
public class CartDiscountsLookupImplTest {
	public static final String SCOPE = "scope";
	public static final String CART_GUID = "cartGuid";

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private CartDiscountsLookupStrategy cartDiscountsLookupStrategy;
	@Mock
	private TransformRfoToResourceState<DiscountEntity, DiscountEntity, CartEntity> discountsCartTransformer;
	@InjectMocks
	private CartDiscountsLookupImpl discountsLookup;

	@Mock
	private ResourceState<CartEntity> cartRepresentation;
	@Mock
	private DiscountEntity discountEntity;
	@Mock
	private ResourceState<DiscountEntity> discountResourceState;

	@Test
	public void ensureCartDiscountsCanBeRetrievedSuccessfully() {
		givenAValidCartResourceState();

		when(cartDiscountsLookupStrategy.getCartDiscounts(CART_GUID, SCOPE)).thenReturn(ExecutionResultFactory.createReadOK(discountEntity));
		when(discountsCartTransformer.transform(discountEntity, cartRepresentation)).thenReturn(discountResourceState);

		ExecutionResult<ResourceState<DiscountEntity>> result = discountsLookup.getCartDiscounts(cartRepresentation);

		assertExecutionResult(result).data(discountResourceState);
	}

	@Test
	public void ensureNotFoundReturnedWhenCartForDiscountsNotFound() {
		givenAValidCartResourceState();

		when(cartDiscountsLookupStrategy.getCartDiscounts(CART_GUID, SCOPE)).thenReturn(ExecutionResultFactory.<DiscountEntity>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		discountsLookup.getCartDiscounts(cartRepresentation);
	}

	@Test
	public void ensureServerErrorReturnedWhenErrorOccursLookingUpDiscounts() {
		givenAValidCartResourceState();

		when(cartDiscountsLookupStrategy.getCartDiscounts(CART_GUID, SCOPE)).thenReturn(ExecutionResultFactory.<DiscountEntity>createServerError(""));
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		discountsLookup.getCartDiscounts(cartRepresentation);
	}

	private void givenAValidCartResourceState() {
		CartEntity cartEntity = CartEntity.builder()
												.withCartId(Base32Util.encode(CART_GUID))
												.build();

		when(cartRepresentation.getEntity()).thenReturn(cartEntity);
		when(cartRepresentation.getScope()).thenReturn(SCOPE);
	}
}