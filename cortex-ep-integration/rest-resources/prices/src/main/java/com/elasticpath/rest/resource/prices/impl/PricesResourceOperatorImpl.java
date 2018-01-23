/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.impl;

import static com.elasticpath.rest.schema.SelfFactory.createSelf;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionEntity;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.prices.CartLineItemPriceEntity;
import com.elasticpath.rest.definition.prices.ItemPriceEntity;
import com.elasticpath.rest.definition.prices.PriceRangeEntity;
import com.elasticpath.rest.definition.prices.ShipmentLineItemPriceEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.SingleResourceUri;
import com.elasticpath.rest.resource.prices.CartLineItemPriceLookup;
import com.elasticpath.rest.resource.prices.ItemPriceLookup;
import com.elasticpath.rest.resource.prices.ShipmentLineItemPriceLookup;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.uri.PricesUriBuilderFactory;

/**
 * Class for handling Price Resource.
 */
@Singleton
@Named("pricesResourceOperator")
@Path(ResourceName.PATH_PART)
public final class PricesResourceOperatorImpl implements ResourceOperator {

	private final ItemPriceLookup itemPriceLookup;
	private final CartLineItemPriceLookup cartLineItemPriceLookup;
	private final ShipmentLineItemPriceLookup shipmentLineItemPriceLookup;
	private final PricesUriBuilderFactory pricesUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param itemPriceLookup             the {@link ItemPriceLookup}
	 * @param cartLineItemPriceLookup     the {@link CartLineItemPriceLookup}
	 * @param shipmentLineItemPriceLookup The ShipmentLineItemPriceLookup.
	 * @param pricesUriBuilderFactory     the {@link PricesUriBuilderFactory}
	 */
	@Inject
	PricesResourceOperatorImpl(
			@Named("itemPriceLookup")
			final ItemPriceLookup itemPriceLookup,
			@Named("cartLineItemPriceLookup")
			final CartLineItemPriceLookup cartLineItemPriceLookup,
			@Named("shipmentLineItemPriceLookup")
			final ShipmentLineItemPriceLookup shipmentLineItemPriceLookup,
			@Named("pricesUriBuilderFactory")
			final PricesUriBuilderFactory pricesUriBuilderFactory) {
		this.itemPriceLookup = itemPriceLookup;
		this.cartLineItemPriceLookup = cartLineItemPriceLookup;
		this.shipmentLineItemPriceLookup = shipmentLineItemPriceLookup;
		this.pricesUriBuilderFactory = pricesUriBuilderFactory;
	}


	/**
	 * Handles the READ operations for prices on items.
	 *
	 * @param item      the item {@link ResourceState}
	 * @param operation the resource operation
	 * @return the result
	 */
	@Path(SingleResourceUri.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadForItem(
			@SingleResourceUri
			final ResourceState<ItemEntity> item,
			final ResourceOperation operation) {

		String scope = item.getScope();

		String itemId = item.getEntity()
				.getItemId();
		ItemPriceEntity itemPriceEntity = Assign.ifSuccessful(itemPriceLookup.getItemPrice(scope, itemId));


		String priceSelfUri = operation.getUri();
		Self self = createSelf(priceSelfUri);
		ResourceState<ItemPriceEntity> result = ResourceState.Builder.create(
				ItemPriceEntity
						.builderFrom(itemPriceEntity)
						.withItemId(itemId)
						.build())
				.withSelf(self)
				.withScope(scope)
				.build();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory
				.create(ExecutionResultFactory.createReadOK(result), operation);
	}

	/**
	 * Handles the READ operations for prices on cart line items.
	 *
	 * @param cartLineItem the cart line item {@link ResourceState}
	 * @param operation    the resource operation
	 * @return the result
	 */
	@Path(AnyResourceUri.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadForCartLineItem(
			@AnyResourceUri
			final ResourceState<LineItemEntity> cartLineItem,
			final ResourceOperation operation) {

		Self lineItemSelf = cartLineItem.getSelf();


		CartLineItemPriceEntity cartLineitemPriceEntity = Assign.ifSuccessful(cartLineItemPriceLookup.getLineItemPrice(cartLineItem));

		// self link of price
		Self self = createSelf(pricesUriBuilderFactory.get().setSourceUri(lineItemSelf.getUri()).build());
		CartLineItemPriceEntity augmentedCartLineItem = CartLineItemPriceEntity.builderFrom(cartLineitemPriceEntity)
				.withCartId(cartLineItem.getEntity().getCartId())
				.withLineItemId(cartLineItem.getEntity().getLineItemId())
				.build();
		ResourceState<CartLineItemPriceEntity> result = ResourceState.Builder.create(augmentedCartLineItem)
				.withSelf(self)
				.withScope(cartLineItem.getScope())
				.build();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory
				.create(ExecutionResultFactory.createReadOK(result), operation);
	}

	/**
	 * Handles the READ operations for prices on cart line items.
	 *
	 * @param itemDefinition the cart line item {@link ResourceState}
	 * @param operation      the resource operation
	 * @return the result
	 */
	@Path(SingleResourceUri.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadForItemDefinition(
			@SingleResourceUri
			final ResourceState<ItemDefinitionEntity> itemDefinition,
			final ResourceOperation operation) {

		String scope = itemDefinition.getScope();
		String itemId = itemDefinition.getEntity().getItemId();
		PriceRangeEntity priceRangeEntity = Assign.ifSuccessful(itemPriceLookup.getItemPriceRange(scope, itemId));

		String priceSelfUri = pricesUriBuilderFactory.get().setSourceUri(itemDefinition.getSelf().getUri()).build();
		Self self = createSelf(priceSelfUri);
		ResourceState<PriceRangeEntity> result = ResourceState.Builder.create(PriceRangeEntity.builderFrom(priceRangeEntity)
				.withItemId(itemId)
				.build())
				.withSelf(self)
				.withScope(scope)
				.build();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory
				.create(ExecutionResultFactory.createReadOK(result), operation);
	}


	/**
	 * Handles the READ operations for prices on a shipment line item.
	 *
	 * @param shipmentLineItemRepresentation the line item {@link ResourceState}
	 * @param operation                      the resource operation
	 * @return the result
	 */
	@Path(AnyResourceUri.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadForShipmentLineItem(
			@AnyResourceUri
			final ResourceState<ShipmentLineItemEntity> shipmentLineItemRepresentation,
			final ResourceOperation operation) {

		ShipmentLineItemPriceEntity shipmentLineitemPriceEntity
				= Assign.ifSuccessful(shipmentLineItemPriceLookup.getPrice(shipmentLineItemRepresentation));

		// self link of price
		Self lineItemSelf = shipmentLineItemRepresentation.getSelf();
		Self self = createSelf(pricesUriBuilderFactory.get().setSourceUri(lineItemSelf.getUri()).build());
		ShipmentLineItemEntity shipmentEntity = shipmentLineItemRepresentation.getEntity();
		ShipmentLineItemPriceEntity augmentedShipmentLineItem
				= ShipmentLineItemPriceEntity.builderFrom(shipmentLineitemPriceEntity)
				.withPurchaseId(shipmentEntity.getPurchaseId())
				.withShipmentId(shipmentEntity.getShipmentId())
				.withShipmentLineItemId(shipmentEntity.getLineItemId())
				.build();
		ResourceState<ShipmentLineItemPriceEntity> result = ResourceState.Builder.create(augmentedShipmentLineItem)
				.withSelf(self)
				.withScope(shipmentLineItemRepresentation.getScope())
				.build();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory
				.create(ExecutionResultFactory.createReadOK(result), operation);
	}
}
