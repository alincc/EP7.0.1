/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.alias.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertOperationResult.assertOperationResult;
import static org.mockito.BDDMockito.given;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.carts.CartLookup;
import com.elasticpath.rest.resource.carts.alias.integration.DefaultCartLookupStrategy;
import com.elasticpath.rest.resource.carts.lineitems.LineItemWriter;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Tests the {@link DefaultLineItemsResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultLineItemsResourceOperatorImplTest {
	public static final String SCOPE = "scope";
	public static final String DECODED_DEFAULT_CART_ID = "defaultCartId";
	public static final String ITEM_ID = "itemId";
	public static final String QUANTITY_IS_EITHER_MISSING_OR_IS_NOT_AN_INTEGER = "Quantity is either missing or is not an integer";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private DefaultCartLookupStrategy defaultCartLookupStrategy;
	@Mock
	private CartLookup cartLookup;
	@Mock
	private LineItemWriter lineItemWriter;
	@InjectMocks
	private DefaultLineItemsResourceOperatorImpl defaultLineItemsResourceOperator;
	@Mock
	private ResourceState<CartEntity> cartRepresentation;
	@Mock
	private ResourceOperation operation;

	private LineItemEntity postedEntity;

	@Before
	public void setUp() {
		postedEntity = LineItemEntity.builder().build();
		ResourceState postedRepresentation = ResourceState.Builder.create(postedEntity).build();
		given(operation.getResourceState()).willReturn(postedRepresentation);
	}

	@Test
	public void ensureLineItemCanBeCreatedForDefaultCartSuccessfully() {
		given(defaultCartLookupStrategy.getDefaultCartId(SCOPE)).willReturn(ExecutionResultFactory.createReadOK(DECODED_DEFAULT_CART_ID));
		given(cartLookup.findCart(SCOPE, Base32Util.encode(DECODED_DEFAULT_CART_ID)))
				.willReturn(ExecutionResultFactory.createReadOK(cartRepresentation));

		given(lineItemWriter.addLineItemToCart(cartRepresentation, ITEM_ID, postedEntity))
				.willReturn(ExecutionResultFactory.<ResourceState<?>>createCreateOK("/uri", false));

		OperationResult result = defaultLineItemsResourceOperator.processCreateLineItem(SCOPE, ITEM_ID, operation);

		assertOperationResult(result).resourceStatus(ResourceStatus.CREATE_OK);
	}

	@Test
	public void failedAddLineItemToCartThrowsBCE() {
		given(defaultCartLookupStrategy.getDefaultCartId(SCOPE)).willReturn(ExecutionResultFactory.createReadOK(DECODED_DEFAULT_CART_ID));
		given(cartLookup.findCart(SCOPE, Base32Util.encode(DECODED_DEFAULT_CART_ID)))
				.willReturn(ExecutionResultFactory.createReadOK(cartRepresentation));

		given(lineItemWriter.addLineItemToCart(cartRepresentation, ITEM_ID, postedEntity))
				.willThrow(new BrokenChainException(ExecutionResultFactory.createBadRequestBody(QUANTITY_IS_EITHER_MISSING_OR_IS_NOT_AN_INTEGER)));

		thrown.expect(containsResourceStatus(ResourceStatus.BAD_REQUEST_BODY));

		defaultLineItemsResourceOperator.processCreateLineItem(SCOPE, ITEM_ID, operation);
	}

	@Test
	public void failedFindCartThrowsBCE() {
		given(defaultCartLookupStrategy.getDefaultCartId(SCOPE)).willReturn(ExecutionResultFactory.createReadOK(DECODED_DEFAULT_CART_ID));
		given(cartLookup.findCart(SCOPE, Base32Util.encode(DECODED_DEFAULT_CART_ID)))
				.willReturn(ExecutionResultFactory.<ResourceState<CartEntity>>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		defaultLineItemsResourceOperator.processCreateLineItem(SCOPE, ITEM_ID, operation);
	}

	@Test
	public void failedCartLookupThrowsBCE() {
		given(defaultCartLookupStrategy.getDefaultCartId(SCOPE)).willReturn(ExecutionResultFactory.<String>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		defaultLineItemsResourceOperator.processCreateLineItem(SCOPE, ITEM_ID, operation);
	}
}
