/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.impl;

import static com.elasticpath.rest.test.AssertOperationResult.assertOperationResult;
import static org.mockito.BDDMockito.given;


import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.carts.LineItemConfigurationEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.resource.carts.CartLookup;
import com.elasticpath.rest.resource.carts.lineitems.integration.LineItemLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;

@RunWith(MockitoJUnitRunner.class)
public class CartsResourceOperatorImplTest {
	private static final String SCOPE = "scope";
	private static final String CART_ID = "cartId";
	private static final String OPERATION_URI = "/operationUri";
	private static final String ITEM_ID = "itemId";

	@Mock
	private CartLookup cartLookup;

	@Mock
	private LineItemLookupStrategy lineItemLookupStrategy;

	@InjectMocks
	private CartsResourceOperatorImpl cartsResourceOperator;

	@Mock
	private ResourceState<CartEntity> cartResource;
	@Mock
	private ResourceOperation resourceOperation;
	@Mock
	private LineItemConfigurationEntity configuration;

	@Test
	public void ensureExistingCartCanBeReadSuccessfully() {
		given(cartLookup.findCart(SCOPE, CART_ID)).willReturn(ExecutionResultFactory.createReadOK(cartResource));

		OperationResult result = cartsResourceOperator.processCartRead(SCOPE, CART_ID, resourceOperation);

		assertOperationResult(result)
				.resourceState(cartResource)
				.resourceStatus(ResourceStatus.READ_OK);
	}

	@Test
	public void ensureNotFoundReturnedIfCartNotFound() {
		given(cartLookup.findCart(SCOPE, CART_ID)).willReturn(ExecutionResultFactory.<ResourceState<CartEntity>>createNotFound());

		OperationResult result = cartsResourceOperator.processCartRead(SCOPE, CART_ID, resourceOperation);

		assertOperationResult(result).resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void ensureServerErrorReturnedWhenCartLookupFails() {
		given(cartLookup.findCart(SCOPE, CART_ID)).willReturn(ExecutionResultFactory.<ResourceState<CartEntity>>createServerError(""));

		OperationResult result = cartsResourceOperator.processCartRead(SCOPE, CART_ID, resourceOperation);

		assertOperationResult(result).resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	@Test
	public void ensureCartFormIsReadCorrectly() {
		ResourceState<ItemEntity> itemRepresentation = createItemResourceState();

		given(cartLookup.findCart(SCOPE, CART_ID)).willReturn(ExecutionResultFactory.createReadOK(cartResource));
		given(resourceOperation.getUri()).willReturn(OPERATION_URI);
		given(lineItemLookupStrategy.getItemConfiguration(SCOPE, ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(configuration));

		OperationResult result = cartsResourceOperator.processReadForm(itemRepresentation, resourceOperation);

		ResourceState<LineItemEntity> expectedCartForm = createLineItemEntityBuilder()
			.build();

		assertOperationResult(result).resourceState(expectedCartForm);
	}

	private ResourceState<ItemEntity> createItemResourceState() {
		return ResourceState.Builder.create(ItemEntity.builder()
				.withItemId(ITEM_ID)
				.build())
			.withScope(SCOPE)
			.build();
	}

	private ResourceState.Builder<LineItemEntity> createLineItemEntityBuilder() {
		return ResourceState.Builder.create(LineItemEntity.builder()
				.withQuantity(0)
				.withItemId(ITEM_ID)
				.withConfiguration(configuration)
				.build())
			.withScope(SCOPE)
			.withSelf(SelfFactory.createSelf(OPERATION_URI));
	}
}
