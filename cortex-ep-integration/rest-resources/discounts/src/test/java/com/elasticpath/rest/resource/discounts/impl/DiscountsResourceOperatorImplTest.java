/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.impl;

import static com.elasticpath.rest.schema.SelfFactory.createSelf;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.carts.CartsMediaTypes;
import com.elasticpath.rest.definition.discounts.DiscountEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.discounts.CartDiscountsLookup;
import com.elasticpath.rest.resource.discounts.PurchaseDiscountsLookup;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.type.ResourceEntityImpl;

/**
 * Tests {@link com.elasticpath.rest.resource.discounts.impl.DiscountsResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class DiscountsResourceOperatorImplTest {

	@Mock
	ResourceOperation readDiscountsOperation;

	@Mock
	CartDiscountsLookup cartDiscountsLookup;

	@Mock
	PurchaseDiscountsLookup purchaseDiscountsLookup;

	ExecutionResult<ResourceState<DiscountEntity>> executionResult =
			ExecutionResultFactory.createNotFound(); // actual data irrelevant
	DiscountsResourceOperatorImpl resourceOperator;

	@Before
	public void setUp() {

		resourceOperator = new DiscountsResourceOperatorImpl(
				cartDiscountsLookup,
				purchaseDiscountsLookup
		);

		given(cartDiscountsLookup.getCartDiscounts(Matchers.<ResourceState<CartEntity>>any()))
				.willReturn(executionResult);

		given(purchaseDiscountsLookup.getPurchaseDiscounts(Matchers.<ResourceState<PurchaseEntity>>any()))
				.willReturn(executionResult);
	}

	@Test
	public void givenCartRepresentationWhenReadingFromOtherShouldProcessCartDiscounts() {

		ResourceEntityImpl resourceEntity = new ResourceEntityImpl();
		ResourceState<CartEntity> otherRepresentation =
				ResourceState.Builder
									.create(ResourceTypeFactory.adaptResourceEntity(resourceEntity, CartEntity.class))
									.withSelf(createSelf("", CartsMediaTypes.CART.id()))
									.build();

		resourceOperator.processReadForCart(otherRepresentation, readDiscountsOperation);

		verify(cartDiscountsLookup).getCartDiscounts(otherRepresentation);
	}

	@Test
	public void givenPurchaseRepresentationWhenReadingFromOtherShouldProcessPurchaseDiscounts() {

		ResourceEntityImpl resourceEntity = new ResourceEntityImpl();
		ResourceState<PurchaseEntity> otherRepresentation =
				ResourceState.Builder
									.create(ResourceTypeFactory.adaptResourceEntity(resourceEntity, PurchaseEntity.class))
									.withSelf(createSelf("", PurchasesMediaTypes.PURCHASE.id()))
									.build();

		resourceOperator.processReadForPurchase(otherRepresentation, readDiscountsOperation);

		verify(purchaseDiscountsLookup).getPurchaseDiscounts(otherRepresentation);
	}
}
