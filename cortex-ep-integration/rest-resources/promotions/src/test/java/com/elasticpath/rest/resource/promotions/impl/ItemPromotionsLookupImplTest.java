/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
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
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.resource.promotions.integration.AppliedItemPromotionsLookupStrategy;
import com.elasticpath.rest.resource.promotions.integration.PossibleItemPromotionsLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Test class for {@link ItemPromotionsLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ItemPromotionsLookupImplTest {

	private static final String SCOPE = "scope";
	private static final String PROMOTION_ID = "12345";
	private static final String ITEM_ID = "noodles";
	private static final String SOURCE_URI = "/source/abcd=";

	private final Collection<String> promotionIds = Collections.singleton(PROMOTION_ID);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ResourceState<LinksEntity> expectedLinksRepresentation;
	private final ResourceState<ItemEntity> itemRepresentation = createItemRepresentation();

	@Mock
	private AppliedItemPromotionsLookupStrategy mockAppliedPromosLookupStrategy;
	@Mock
	private PossibleItemPromotionsLookupStrategy mockPossiblePromosLookupStrategy;
	@Mock
	private TransformRfoToResourceState<LinksEntity, Collection<String>, ItemEntity> mockAppliedPromotionsTransformer;
	@Mock
	private TransformRfoToResourceState<LinksEntity, Collection<String>, ItemEntity> mockPossiblePromotionsTransformer;

	private ItemPromotionsLookupImpl itemPromotionsLookup;

	@Mock
	private ResourceState<ItemEntity> resourceState;

	/**
	 * Setup Mocks.
	 * Not using @InjectMocks, as Mockito has a Java 7 bug that causes confusion with parameters of same type.
	 */
	@Before
	public void setUp() {
		itemPromotionsLookup = new ItemPromotionsLookupImpl(
				mockAppliedPromosLookupStrategy,
				mockPossiblePromosLookupStrategy,
				mockAppliedPromotionsTransformer,
				mockPossiblePromotionsTransformer
		);
	}


	@Test
	public void testGetPromotionsForItemWhenSuccessful() {
		when(mockAppliedPromosLookupStrategy.getAppliedPromotionsForItem(SCOPE, ITEM_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(promotionIds));
		when(mockAppliedPromotionsTransformer.transform(promotionIds, itemRepresentation)).thenReturn(expectedLinksRepresentation);

		ExecutionResult<ResourceState<LinksEntity>> result =
				itemPromotionsLookup.getAppliedPromotionsForItem(itemRepresentation);

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK)
				.data(expectedLinksRepresentation);
	}

	@Test
	public void testGetPromotionsForItemWhenFailure() {
		when(mockAppliedPromosLookupStrategy.getAppliedPromotionsForItem(SCOPE, ITEM_ID))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		itemPromotionsLookup.getAppliedPromotionsForItem(itemRepresentation);
	}

	@Test
	public void testGetPossiblePromotionsForItemWhenSuccessful() {
		when(mockPossiblePromosLookupStrategy.getPossiblePromotionsForItem(SCOPE, ITEM_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(promotionIds));
		when(mockPossiblePromotionsTransformer.transform(promotionIds, itemRepresentation)).thenReturn(expectedLinksRepresentation);

		ExecutionResult<ResourceState<LinksEntity>> result =
				itemPromotionsLookup.getPossiblePromotionsForItem(itemRepresentation);

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK)
				.data(expectedLinksRepresentation);
	}

	@Test
	public void testGetPossiblePromotionsForItemWhenFailure() {
		when(mockPossiblePromosLookupStrategy.getPossiblePromotionsForItem(SCOPE, ITEM_ID))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		itemPromotionsLookup.getPossiblePromotionsForItem(itemRepresentation);
	}

	@Test
	public void testItemHasPossiblePromotionsWhenSuccessful() {
		when(resourceState.getScope()).thenReturn(SCOPE);
		ItemEntity itemEntity = mock(ItemEntity.class);
		when(resourceState.getEntity()).thenReturn(itemEntity);
		when(itemEntity.getItemId()).thenReturn(ITEM_ID);
		when(mockPossiblePromosLookupStrategy.itemHasPossiblePromotions(SCOPE, ITEM_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(true));

		ExecutionResult<Boolean> result = itemPromotionsLookup.itemHasPossiblePromotions(resourceState);

		assertExecutionResult(result)
				.isSuccessful()
				.data(Boolean.TRUE);
	}

	@Test
	public void testItemHasPossiblePromotionsWhenFailure() {
		when(resourceState.getScope()).thenReturn(SCOPE);
		ItemEntity itemEntity = mock(ItemEntity.class);
		when(resourceState.getEntity()).thenReturn(itemEntity);
		when(itemEntity.getItemId()).thenReturn(ITEM_ID);
		when(mockPossiblePromosLookupStrategy.itemHasPossiblePromotions(SCOPE, ITEM_ID))
				.thenReturn(ExecutionResultFactory.<Boolean>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		itemPromotionsLookup.itemHasPossiblePromotions(resourceState);
	}


	private ResourceState<ItemEntity> createItemRepresentation() {
		ItemEntity itemEntity = ItemEntity.builder()
				.withItemId(ITEM_ID)
				.build();
		Self self = SelfFactory.createSelf(SOURCE_URI);

		return ResourceState.<ItemEntity>builder()
				.withEntity(itemEntity)
				.withSelf(self)
				.withScope(SCOPE)
				.build();
	}
}
