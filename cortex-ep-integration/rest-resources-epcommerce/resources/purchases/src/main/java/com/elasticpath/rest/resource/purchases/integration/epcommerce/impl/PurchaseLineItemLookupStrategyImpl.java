/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.integration.epcommerce.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.resource.purchases.integration.epcommerce.transform.OrderSkuTransformer;
import com.elasticpath.rest.resource.purchases.lineitems.integration.PurchaseLineItemLookupStrategy;

/**
 * EP Commerce purchase line item lookup strategy.
 */
@Singleton
@Named("purchaseLineItemLookupStrategy")
public class PurchaseLineItemLookupStrategyImpl implements PurchaseLineItemLookupStrategy {

	private static final String LINE_ITEM_NOT_FOUND = "Line item not found";

	private final ResourceOperationContext resourceOperationContext;
	private final OrderRepository orderRepository;
	private final OrderSkuTransformer orderSkuTransformer;
	private final ProductSkuRepository productSkuRepository;

	/**
	 * Constructor.
	 *
	 * @param resourceOperationContext the resource operation context.
	 * @param orderRepository          the order repository.
	 * @param orderSkuTransformer      the purchase line item transformer.
	 * @param productSkuRepository     product sku repository
	 */
	@Inject
	public PurchaseLineItemLookupStrategyImpl(
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("orderRepository")
			final OrderRepository orderRepository,
			@Named("orderSkuTransformer")
			final OrderSkuTransformer orderSkuTransformer,
			@Named("productSkuRepository")
			final ProductSkuRepository productSkuRepository) {

		this.resourceOperationContext = resourceOperationContext;
		this.orderRepository = orderRepository;
		this.orderSkuTransformer = orderSkuTransformer;
		this.productSkuRepository = productSkuRepository;
	}

	@Override
	public ExecutionResult<Collection<String>> findLineItemIds(final String storeCode, final String orderGuid) {

		Order order = Assign.ifSuccessful(orderRepository.findByGuid(storeCode, orderGuid));
		Collection<? extends ShoppingItem> lineItems = order.getRootShoppingItems();
		Collection<String> lineItemGuids = new ArrayList<>(lineItems.size());
		for (ShoppingItem lineItem : lineItems) {
			lineItemGuids.add(lineItem.getGuid());
		}
		return ExecutionResultFactory.createReadOK(lineItemGuids);

	}

	@Override
	public ExecutionResult<PurchaseLineItemEntity> getLineItem(
			final String storeCode, final String orderGuid, final String orderSkuGuid, final String parentOrderSkuGuid) {

		final ExecutionResult<PurchaseLineItemEntity> result;

		OrderSku orderSku = Assign.ifSuccessful(retrieveOrderSkuForOrder(storeCode, orderGuid, orderSkuGuid));
		// now check to see if we are getting the proper level in our object graph.
		if (isTopLevel(parentOrderSkuGuid, orderSku)) {
			result = createDtoForLineItem(orderSku);
		} else {
			Ensure.isTrue(isComponent(parentOrderSkuGuid, orderSku),
					OnFailure.returnNotFound(LINE_ITEM_NOT_FOUND));
			Ensure.isTrue(parentOrderSkuGuid.equals(orderSku.getParent().getGuid()),
					OnFailure.returnNotFound(LINE_ITEM_NOT_FOUND));
			result = createDtoForLineItem(orderSku);
		}

		return result;

	}

	private boolean isTopLevel(final String parentOrderSkuGuid, final OrderSku orderSku) {
		return parentOrderSkuGuid == null && orderSku.getParent() == null;
	}

	private boolean isComponent(final String parentOrderSkuGuid, final OrderSku orderSku) {
		return parentOrderSkuGuid != null && orderSku.getParent() != null;
	}

	private ExecutionResult<PurchaseLineItemEntity> createDtoForLineItem(final OrderSku orderSku) {

		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		PurchaseLineItemEntity lineItemDto = orderSkuTransformer.transformToEntity(orderSku, locale);
		return ExecutionResultFactory.createReadOK(lineItemDto);

	}

	@Override
	public ExecutionResult<Boolean> isLineItemBundle(final String storeCode, final String orderGuid, final String orderSkuGuid) {

		OrderSku lineItem = Assign.ifSuccessful(retrieveOrderSkuForOrder(storeCode, orderGuid, orderSkuGuid));
		Boolean isProductBundle = Assign.ifSuccessful(productSkuRepository.isProductBundle(lineItem.getSkuGuid()));
		return ExecutionResultFactory.createReadOK(isProductBundle);

	}

	private ExecutionResult<OrderSku> retrieveOrderSkuForOrder(final String storeCode, final String orderGuid, final String orderSkuGuid) {

		Order order = Assign.ifSuccessful(orderRepository.findByGuid(storeCode, orderGuid));
		ShoppingItem shoppingItem = Assign.ifNotNull(getShoppingItemByGuid(order, orderSkuGuid),
				OnFailure.returnNotFound(LINE_ITEM_NOT_FOUND));
		assert shoppingItem instanceof OrderSku;
		OrderSku orderSku = (OrderSku) shoppingItem;
		return ExecutionResultFactory.createReadOK(orderSku);

	}

	private ShoppingItem getShoppingItemByGuid(final Order order, final String shoppingItemGuid) {
		Collection<ShoppingItem> allShoppingItems = getAllShoppingItems(order.getRootShoppingItems());

		for (ShoppingItem shoppingItem : allShoppingItems) {
			if (shoppingItem.getGuid().equals(shoppingItemGuid)) {
				return shoppingItem;
			}
		}
		return null;
	}

	/**
	 * Remove this method when core provides a method to get a particular shopping item from an order given a guid. <br>
	 * We are using this because we cannot retrieve a nested bundle by guid.
	 *
	 * @param shoppingItems The collection of shopping items to examine.
	 * @return The processed collection.
	 */
	private Collection<ShoppingItem> getAllShoppingItems(final Collection<? extends ShoppingItem> shoppingItems) {
		Collection<ShoppingItem> allItems = new HashSet<>();
		for (ShoppingItem item : shoppingItems) {
			allItems.addAll(getAllShoppingItems(item.getChildren()));
			allItems.add(item);
		}
		return allItems;
	}

	@Override
	public ExecutionResult<Collection<String>> getComponentIdsForLineItemId(
			final String storeCode, final String orderGuid, final String orderSkuGuid) {

		OrderSku orderSku = Assign.ifSuccessful(retrieveOrderSkuForOrder(storeCode, orderGuid, orderSkuGuid));
		List<ShoppingItem> shoppingItems = orderSku.getChildren();
		Collection<String> componentGuidsForLineItem = new ArrayList<>(shoppingItems.size());
		for (ShoppingItem bundleItem : shoppingItems) {
			componentGuidsForLineItem.add(bundleItem.getGuid());
		}
		return ExecutionResultFactory.createReadOK(componentGuidsForLineItem);

	}
}
