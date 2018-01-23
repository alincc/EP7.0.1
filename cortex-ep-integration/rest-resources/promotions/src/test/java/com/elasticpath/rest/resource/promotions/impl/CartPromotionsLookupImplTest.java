/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Mockito.mock;
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
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.promotions.integration.AppliedCartPromotionsLookupStrategy;
import com.elasticpath.rest.resource.promotions.integration.PossibleCartPromotionsLookupStrategy;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Test class for {@link CartPromotionsLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class CartPromotionsLookupImplTest {

	private static final String SCOPE = "scope";
	private static final String PROMOTION_ID = "12345";
	private static final String CART_ID = "abcde";
	private static final String CART_LINE_ITEM_ID = "noodles";
	private static final String ENCODED_CART_ID = Base32Util.encode(CART_ID);
	private static final String ENCODED_CART_LINE_ITEM_ID = Base32Util.encode(CART_LINE_ITEM_ID);
	private static final String SOURCE_URI = "/source/abcd=";
	private static final int QUANTITY = 1;

	private final Collection<String> promotionIds = Collections.singleton(PROMOTION_ID);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ResourceState<LinksEntity> expectedLinksRepresentation;
	@Mock
	private ResourceState<CartEntity> cartEntityResourceState;
	private final ResourceState<LineItemEntity> lineItemRepresentation = createLineItemRepresentation();
	private final ResourceState<CartEntity> cartRepresentation = createCartRepresentation();

	@Mock
	private AppliedCartPromotionsLookupStrategy mockAppliedPromosLookupStrategy;
	@Mock
	private PossibleCartPromotionsLookupStrategy mockPossiblePromosLookupStrategy;
	@Mock
	private TransformRfoToResourceState<LinksEntity, Collection<String>, ResourceEntity> mockAppliedPromotionsTransformer;
	@Mock
	private TransformRfoToResourceState<LinksEntity, Collection<String>, CartEntity> mockPossiblePromotionsTransformer;

	private CartPromotionsLookupImpl cartPromotionsLookup;

	/**
	 * Setup Mocks.
	 * Not using @InjectMocks, as Mockito has a Java 7 bug that causes confusion with parameters of same type.
	 */
	@Before
	public void setUp() {
		cartPromotionsLookup = new CartPromotionsLookupImpl(
				mockAppliedPromosLookupStrategy,
				mockPossiblePromosLookupStrategy,
				mockAppliedPromotionsTransformer,
				mockPossiblePromotionsTransformer
		);
	}


	@Test
	public void testGetAppliedPromotionsForItemInCartWhenSuccessful() {
		when(mockAppliedPromosLookupStrategy.getAppliedPromotionsForItemInCart(SCOPE, CART_ID, CART_LINE_ITEM_ID, QUANTITY))
				.thenReturn(ExecutionResultFactory.createReadOK(promotionIds));
		when(mockAppliedPromotionsTransformer.transform(promotionIds, (ResourceState) lineItemRepresentation))
				.thenReturn(expectedLinksRepresentation);

		ExecutionResult<ResourceState<LinksEntity>> result =
				cartPromotionsLookup.getAppliedPromotionsForItemInCart(lineItemRepresentation);

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK)
				.data(expectedLinksRepresentation);
	}

	@Test
	public void testGetAppliedPromotionsForItemInCartWhenFailure() {
		when(mockAppliedPromosLookupStrategy.getAppliedPromotionsForItemInCart(SCOPE, CART_ID, CART_LINE_ITEM_ID, QUANTITY))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		cartPromotionsLookup.getAppliedPromotionsForItemInCart(lineItemRepresentation);
	}

	@Test
	public void testGetAppliedPromotionsForCartWhenSuccessful() {
		when(mockAppliedPromosLookupStrategy.getAppliedPromotionsForCart(SCOPE, CART_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(promotionIds));
		when(mockAppliedPromotionsTransformer.transform(promotionIds, (ResourceState) cartRepresentation))
				.thenReturn(expectedLinksRepresentation);

		ExecutionResult<ResourceState<LinksEntity>> result = cartPromotionsLookup.getAppliedPromotionsForCart(cartRepresentation);

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK)
				.data(expectedLinksRepresentation);
	}

	@Test
	public void testGetAppliedPromotionsForCartWhenFailure() {
		when(mockAppliedPromosLookupStrategy.getAppliedPromotionsForCart(SCOPE, CART_ID))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		cartPromotionsLookup.getAppliedPromotionsForCart(cartRepresentation);
	}

	@Test
	public void testGetPossiblePromotionsForCartWhenSuccessful() {
		when(mockPossiblePromosLookupStrategy.getPossiblePromotionsForCart(SCOPE, CART_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(promotionIds));
		when(mockPossiblePromotionsTransformer.transform(promotionIds, cartRepresentation)).thenReturn(expectedLinksRepresentation);

		ExecutionResult<ResourceState<LinksEntity>> result = cartPromotionsLookup.getPossiblePromotionsForCart(cartRepresentation);

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK)
				.data(expectedLinksRepresentation);
	}

	@Test
	public void testGetPossiblePromotionsForCartWhenFailure() {
		when(mockPossiblePromosLookupStrategy.getPossiblePromotionsForCart(SCOPE, CART_ID))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		cartPromotionsLookup.getPossiblePromotionsForCart(cartRepresentation);
	}

	@Test
	public void testCartHasPossiblePromotionsWhenTrue() {
		when(cartEntityResourceState.getScope()).thenReturn(SCOPE);
		CartEntity cartEntity = mock(CartEntity.class);
		when(cartEntityResourceState.getEntity()).thenReturn(cartEntity);
		when(cartEntity.getCartId()).thenReturn(ENCODED_CART_ID);
		when(mockPossiblePromosLookupStrategy.cartHasPossiblePromotions(SCOPE, CART_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(true));

		ExecutionResult<Boolean> result = cartPromotionsLookup.cartHasPossiblePromotions(cartEntityResourceState);

		assertExecutionResult(result)
				.isSuccessful()
				.data(Boolean.TRUE);
	}

	@Test
	public void testCartHasPossiblePromotionsWhenFailure() {
		when(cartEntityResourceState.getScope()).thenReturn(SCOPE);
		CartEntity cartEntity = mock(CartEntity.class);
		when(cartEntityResourceState.getEntity()).thenReturn(cartEntity);
		when(cartEntity.getCartId()).thenReturn(ENCODED_CART_ID);
		when(mockPossiblePromosLookupStrategy.cartHasPossiblePromotions(SCOPE, CART_ID))
				.thenReturn(ExecutionResultFactory.<Boolean>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		cartPromotionsLookup.cartHasPossiblePromotions(cartEntityResourceState);
	}


	private ResourceState<LineItemEntity> createLineItemRepresentation() {
		LineItemEntity lineItemEntity = LineItemEntity.builder()
				.withCartId(ENCODED_CART_ID)
				.withLineItemId(ENCODED_CART_LINE_ITEM_ID)
				.withQuantity(QUANTITY)
				.build();
		Self self = SelfFactory.createSelf(SOURCE_URI);

		return ResourceState.<LineItemEntity>builder()
				.withEntity(lineItemEntity)
				.withSelf(self)
				.withScope(SCOPE)
				.build();
	}


	private ResourceState<CartEntity> createCartRepresentation() {
		CartEntity cartEntity = CartEntity.builder()
				.withCartId(ENCODED_CART_ID)
				.build();
		Self self = SelfFactory.createSelf(SOURCE_URI);

		return ResourceState.<CartEntity>builder()
				.withEntity(cartEntity)
				.withSelf(self)
				.withScope(SCOPE)
				.build();
	}

}
