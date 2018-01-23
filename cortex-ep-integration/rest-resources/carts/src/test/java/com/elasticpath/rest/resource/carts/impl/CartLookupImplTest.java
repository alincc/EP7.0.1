/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.carts.integration.CartLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformToResourceState;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;

/**
 * Tests on {@link com.elasticpath.rest.resource.carts.impl.CartLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class CartLookupImplTest {

	private static final String SCOPE = "scope";
	private static final String CART_ID = "cart_id";
	private static final String USER_ID = "user_id";
	private static final String ENCODED_CART_ID = Base32Util.encode(CART_ID);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	ResourceState<CartEntity> expectedRepresentation;

	@Mock
	CartEntity expectedCartEntity;

	@Mock
	TransformToResourceState<CartEntity, CartEntity> defaultCartTransformer;

	@Mock
	private CartLookupStrategy mockLookupStrategy;

	@Mock
	private CartsUriBuilderFactory uriBuilderFactory;

	private CartLookupImpl lookupImpl;


	@Before
	public void setUp() {

		when(defaultCartTransformer.transform(SCOPE, expectedCartEntity))
				.thenReturn(expectedRepresentation);
		lookupImpl = new CartLookupImpl(mockLookupStrategy, defaultCartTransformer, uriBuilderFactory);
	}

	@Test
	public void testFindingAllCartsForUserAndScope() {
		when(mockLookupStrategy.getCartIds(SCOPE, USER_ID))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createReadOK(Collections.singleton(CART_ID)));

		ExecutionResult<Collection<String>> result = lookupImpl.findCartIds(SCOPE, USER_ID);

		assertTrue("Result is a success", result.isSuccessful());
		assertTrue("Collection should contain expected encoded id", result.getData().contains(ENCODED_CART_ID));

	}

	@Test
	public void testFindCartByIdWhenSuccessfullyFound() {
		when(mockLookupStrategy.getCart(SCOPE, CART_ID)).thenReturn(ExecutionResultFactory.createReadOK(expectedCartEntity));

		ExecutionResult<ResourceState<CartEntity>> result = lookupImpl.findCart(SCOPE, ENCODED_CART_ID);

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK)
				.data(expectedRepresentation);
	}


	@Test
	public void testFindCartByIdWhenCartIdNotFound() {
		when(mockLookupStrategy.getCart(SCOPE, CART_ID)).thenReturn(ExecutionResultFactory.<CartEntity>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookupImpl.findCart(SCOPE, ENCODED_CART_ID);
	}

}
