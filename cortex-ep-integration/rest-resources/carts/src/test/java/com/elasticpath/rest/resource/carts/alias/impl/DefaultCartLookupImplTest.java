/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.alias.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

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
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.carts.alias.integration.DefaultCartLookupStrategy;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilder;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilderFactory;
import com.elasticpath.rest.schema.uri.CartsUriBuilder;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;

/**
 * Tests on {@link DefaultCartLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class DefaultCartLookupImplTest {
	private static final String SCOPE = "scope";
	private static final String EXPECTED_CART_URI = "/cart/asd";
	private static final String EXPECTED_LINE_ITEMS_URI = "/asda/asd";
	private static final String CART_ID = "cart_id";
	private static final String ENCODED_CART_ID = Base32Util.encode(CART_ID);

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	CartLineItemsUriBuilderFactory cartLineItemsUriBuilderFactory;
	@Mock
	CartLineItemsUriBuilder cartLineItemsUriBuilder;
	@Mock
	CartsUriBuilderFactory cartsUriBuilderFactory;
	@Mock
	CartsUriBuilder cartsUriBuilder;
	@Mock
	private DefaultCartLookupStrategy mockDefaultLookupStrategy;

	private DefaultCartLookupImpl lookupImpl;

	@Before
	public void setUp() {
		when(cartLineItemsUriBuilderFactory.get()).thenReturn(cartLineItemsUriBuilder);
		when(cartLineItemsUriBuilder.setSourceUri(EXPECTED_CART_URI)).thenReturn(cartLineItemsUriBuilder);
		when(cartLineItemsUriBuilder.build()).thenReturn(EXPECTED_LINE_ITEMS_URI);

		when(cartsUriBuilderFactory.get()).thenReturn(cartsUriBuilder);
		when(cartsUriBuilder.setScope(SCOPE)).thenReturn(cartsUriBuilder);
		when(cartsUriBuilder.setCartId(ENCODED_CART_ID)).thenReturn(cartsUriBuilder);
		when(cartsUriBuilder.build()).thenReturn(EXPECTED_CART_URI);

		lookupImpl = new DefaultCartLookupImpl(mockDefaultLookupStrategy, cartsUriBuilderFactory);
	}

	@Test
	public void testFindDefaultCartId() {
		ExecutionResult<ResourceState<ResourceEntity>> seeOtherResult = arrangeToReturnSeeOtherCartExecutionResult();

		ExecutionResult<ResourceState<ResourceEntity>> result = lookupImpl.getDefaultCartSeeOtherRepresentation(SCOPE);

		assertResultWasExpectedRedirect(seeOtherResult, result);
	}

	@Test
	public void testFindDefaultCartIdWhenCartIdNotFound() {
		when(mockDefaultLookupStrategy.getDefaultCartId(SCOPE)).thenReturn(ExecutionResultFactory.<String>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookupImpl.getDefaultCartSeeOtherRepresentation(SCOPE);

	}

	private void assertResultWasExpectedRedirect(final ExecutionResult<ResourceState<ResourceEntity>> seeOtherResult,
												final ExecutionResult<ResourceState<ResourceEntity>> result) {
		assertTrue("The operation should be a redirect.", result.isRedirect());
		assertEquals(ResourceStatus.SEE_OTHER, result.getResourceStatus());
		assertEquals("Result should be the expected", seeOtherResult, result);
	}

	private ExecutionResult<ResourceState<ResourceEntity>> arrangeToReturnSeeOtherCartExecutionResult() {
		when(mockDefaultLookupStrategy.getDefaultCartId(SCOPE)).thenReturn(ExecutionResultFactory.createReadOK(CART_ID));

		ResourceState<ResourceEntity> redirectRepresentation = ResourceState.builder()
				.withSelf(SelfFactory.createSelf(EXPECTED_CART_URI))
				.build();
		return ExecutionResultFactory.create(null, ResourceStatus.SEE_OTHER, redirectRepresentation);
	}
}
