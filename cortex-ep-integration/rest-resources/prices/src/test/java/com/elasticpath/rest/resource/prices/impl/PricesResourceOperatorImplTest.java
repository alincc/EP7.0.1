/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertOperationResult.assertOperationResult;
import static org.mockito.BDDMockito.given;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.CartsMediaTypes;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemdefinitionsMediaTypes;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.definition.prices.CartLineItemPriceEntity;
import com.elasticpath.rest.definition.prices.ItemPriceEntity;
import com.elasticpath.rest.definition.prices.PriceRangeEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.resource.prices.CartLineItemPriceLookup;
import com.elasticpath.rest.resource.prices.ItemPriceLookup;
import com.elasticpath.rest.resource.prices.ShipmentLineItemPriceLookup;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.PricesUriBuilder;
import com.elasticpath.rest.schema.uri.PricesUriBuilderFactory;

/**
 * Tests the {@link PricesResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PricesResourceOperatorImplTest {
	private static final String ITEM_ID = "itemId";
	private static final String SCOPE = "scope";
	private static final String ITEM_URI = "/itemUri";
	private static final String CART_LINE_ITEM_URI = "/cartLineItemUri";
	private static final String ITEM_DEFINITION_URI = "/itemDefinitionUri";
	public static final String CART_ID = "cartId";
	public static final String LINE_ITEM_ID = "lineItemId";
	public static final String ITEM_PRICE_URI = "/itemPriceUri";
	public static final String LINE_ITEM_PRICE_URI = "lineItemPriceUri";
	public static final String ITEM_DEFINITION_PRICE_URI = "itemDefinitionPriceUri";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ItemPriceLookup itemPriceLookup;
	@Mock
	private CartLineItemPriceLookup cartLineItemPriceLookup;
	@Mock
	private PricesUriBuilderFactory pricesUriBuilderFactory;
	@Mock
	private ShipmentLineItemPriceLookup shipmentLineItemPriceLookup;
	@Mock
	private PricesUriBuilder pricesUriBuilder;
	@Mock
	private ResourceOperation operation;

	private ResourceState<ShipmentLineItemEntity> shipmentLineItemRepresentation;
	private ItemPriceEntity itemPriceEntity;

	private PriceRangeEntity priceRangeEntity;
	private CartLineItemPriceEntity cartLineItemPriceEntity;
	private ResourceState<ItemEntity> item;
	private ResourceState<LineItemEntity> lineItem;
	private ResourceState<ItemDefinitionEntity> itemDefinition;

	private PricesResourceOperatorImpl pricesResourceOperator;

	@Before
	public void setupCommonTestComponents() {
		given(pricesUriBuilderFactory.get()).willReturn(pricesUriBuilder);

		pricesResourceOperator
			= new PricesResourceOperatorImpl(itemPriceLookup,
				cartLineItemPriceLookup,
				shipmentLineItemPriceLookup,
				pricesUriBuilderFactory);

		Self itemSelf = SelfFactory.createSelf(ITEM_URI, ItemsMediaTypes.ITEM.id());

		item = ResourceState.Builder.create(
				ItemEntity.Builder
						.builder()
						.withItemId(ITEM_ID)
						.build())
				.withScope(SCOPE)
				.withSelf(itemSelf)
				.build();

		Self lineItemSelf = SelfFactory.createSelf(CART_LINE_ITEM_URI, CartsMediaTypes.LINE_ITEM.id());

		lineItem = ResourceState.Builder.create(
				LineItemEntity.builder()
						.withCartId(CART_ID)
						.withLineItemId(LINE_ITEM_ID)
						.build())
				.withScope(SCOPE)
				.withSelf(lineItemSelf)
				.build();

		Self itemDefinitionSelf = SelfFactory.createSelf(ITEM_DEFINITION_URI, ItemdefinitionsMediaTypes.ITEM_DEFINITION.id());

		itemDefinition = ResourceState.Builder.create(
				ItemDefinitionEntity.builder()
						.withItemId(ITEM_ID)
						.build())
				.withScope(SCOPE)
				.withSelf(itemDefinitionSelf)
				.build();

		itemPriceEntity = ItemPriceEntity.Builder
				.builder()
				.build();

		priceRangeEntity = PriceRangeEntity.Builder
				.builder()
				.build();

		cartLineItemPriceEntity = CartLineItemPriceEntity.Builder
				.builder()
				.build();
	}

	@Test
	public void ensureItemPriceCanBeRead() {
		given(itemPriceLookup.getItemPrice(SCOPE, ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(itemPriceEntity));

		given(operation.getUri()).willReturn(ITEM_PRICE_URI);

		OperationResult operationResult = pricesResourceOperator.processReadForItem(item, operation);


		Self expectedSelf = SelfFactory.createSelf(ITEM_PRICE_URI);
		ItemPriceEntity expectedAugmentedItemPriceEntity = ItemPriceEntity
				.builderFrom(itemPriceEntity)
				.withItemId(ITEM_ID)
				.build();

		assertOperationResult(operationResult).resourceState(
				ResourceState.Builder
						.create(expectedAugmentedItemPriceEntity)
						.withSelf(expectedSelf)
						.withScope(SCOPE)
						.build()
		);
	}

	@Test
	public void failedItemPriceReadThrowsBCE() {
		given(itemPriceLookup.getItemPrice(SCOPE, ITEM_ID))
				.willThrow(new BrokenChainException(ExecutionResultFactory.<ItemPriceEntity>createNotFound()));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		pricesResourceOperator.processReadForItem(item, operation);
	}

	@Test
	public void ensureCartLineitemCanBeRead() {
		given(cartLineItemPriceLookup.getLineItemPrice(lineItem)).willReturn(ExecutionResultFactory.createReadOK(cartLineItemPriceEntity));

		given(pricesUriBuilder.setSourceUri(CART_LINE_ITEM_URI)).willReturn(pricesUriBuilder);
		given(pricesUriBuilder.build()).willReturn(LINE_ITEM_PRICE_URI);

		OperationResult operationResult = pricesResourceOperator.processReadForCartLineItem(lineItem, operation);

		Self expectedSelf = SelfFactory.createSelf(LINE_ITEM_PRICE_URI);

		CartLineItemPriceEntity augmentedCartLineItemPrice = CartLineItemPriceEntity
				.builderFrom(cartLineItemPriceEntity)
				.withCartId(CART_ID)
				.withLineItemId(LINE_ITEM_ID)
				.build();

		assertOperationResult(operationResult).resourceState(
				ResourceState.Builder
						.create(augmentedCartLineItemPrice)
						.withSelf(expectedSelf)
						.withScope(SCOPE)
						.build()
		);
	}

	@Test
	public void failedCartLineItemPriceReadThrowsBCE() {
		given(cartLineItemPriceLookup.getLineItemPrice(lineItem))
				.willThrow(
						new BrokenChainException(ExecutionResultFactory.<CartLineItemPriceEntity>createNotFound()));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		pricesResourceOperator.processReadForCartLineItem(lineItem, operation);
	}

	@Test
	public void ensureItemDefinitionPriceRangeCanBeRead() {
		given(itemPriceLookup.getItemPriceRange(SCOPE, ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(priceRangeEntity));

		given(pricesUriBuilder.setSourceUri(ITEM_DEFINITION_URI)).willReturn(pricesUriBuilder);
		given(pricesUriBuilder.build()).willReturn(ITEM_DEFINITION_PRICE_URI);

		OperationResult operationResult = pricesResourceOperator.processReadForItemDefinition(itemDefinition, operation);

		Self expectedSelf = SelfFactory.createSelf(ITEM_DEFINITION_PRICE_URI);
		PriceRangeEntity expectedAugmentedPriceRangeEntity = PriceRangeEntity
				.builderFrom(priceRangeEntity)
				.withItemId(ITEM_ID)
				.build();

		assertOperationResult(operationResult).resourceState(
				ResourceState.Builder
						.create(expectedAugmentedPriceRangeEntity)
						.withSelf(expectedSelf)
						.withScope(SCOPE)
						.build()
		);
	}

	@Test
	public void failedItemDefinitionPriceRangeReadThrowsBCE() {
		given(itemPriceLookup.getItemPriceRange(SCOPE, ITEM_ID))
				.willThrow(new BrokenChainException(ExecutionResultFactory.<PriceRangeEntity>createNotFound()));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		pricesResourceOperator.processReadForItemDefinition(itemDefinition, operation);
	}

	@Test
	public void failedPriceLookupThrowsBCE() {
		given(shipmentLineItemPriceLookup.getPrice(shipmentLineItemRepresentation))
				.willThrow(new BrokenChainException(ExecutionResultFactory.createNotFound()));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		pricesResourceOperator.processReadForShipmentLineItem(shipmentLineItemRepresentation, operation);
	}
}
