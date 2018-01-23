/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.integration.epcommerce.impl;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;
import com.elasticpath.rest.resource.purchases.integration.PurchaseLookupStrategy;
import com.elasticpath.rest.resource.purchases.integration.epcommerce.transform.OrderTransformer;

/**
 * EP Commerce implementation of the purchase lookup strategy.
 */
@Singleton
@Named("purchaseLookupStrategy")
public class PurchaseLookupStrategyImpl implements PurchaseLookupStrategy {

	private final ResourceOperationContext resourceOperationContext;
	private final CartOrderRepository cartOrderRepository;
	private final OrderRepository orderRepository;
	private final OrderTransformer orderTransformer;
	private final ShoppingCartRepository shoppingCartRepository;
	private final StoreProductRepository storeProductRepository;


	/**
	 * Constructor.
	 *
	 * @param resourceOperationContext the resource operation context
	 * @param cartOrderRepository      the cart order repository
	 * @param orderRepository          the order repository
	 * @param shoppingCartRepository   the shopping cart repository
	 * @param storeProductRepository   the store product repository
	 * @param orderTransformer         the purchase transformer
	 */
	@Inject
	public PurchaseLookupStrategyImpl(
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository,
			@Named("orderRepository")
			final OrderRepository orderRepository,
			@Named("shoppingCartRepository")
			final ShoppingCartRepository shoppingCartRepository,
			@Named("storeProductRepository")
			final StoreProductRepository storeProductRepository,
			@Named("orderTransformer")
			final OrderTransformer orderTransformer) {

		this.resourceOperationContext = resourceOperationContext;
		this.cartOrderRepository = cartOrderRepository;
		this.orderRepository = orderRepository;
		this.shoppingCartRepository = shoppingCartRepository;
		this.storeProductRepository = storeProductRepository;
		this.orderTransformer = orderTransformer;
	}


	@Override
	public ExecutionResult<PurchaseEntity> getPurchase(final String storeCode, final String orderGuid) {

		Order order = Assign.ifSuccessful(orderRepository.findByGuid(storeCode, orderGuid));
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		PurchaseEntity purchaseEntity = orderTransformer.transformToEntity(order, locale);
		return ExecutionResultFactory.createReadOK(purchaseEntity);
	}

	@Override
	public ExecutionResult<Collection<String>> getPurchaseIds(final String storeCode, final String customerGuid) {
		return orderRepository.findOrderIdsByCustomerGuid(storeCode, customerGuid);
	}


	@Override
	public ExecutionResult<Boolean> isOrderPurchasable(final String storeCode, final String cartOrderGuid) {

		CartOrder cartOrder = Assign.ifSuccessful(cartOrderRepository.findByGuid(storeCode, cartOrderGuid));
		String cartGuid = cartOrder.getShoppingCartGuid();
		ShoppingCart shoppingCart = Assign.ifSuccessful(shoppingCartRepository.getShoppingCart(cartGuid));
		List<ShoppingItem> cartItems = shoppingCart.getCartItems();

		StoreProduct storeProduct;
		String skuGuid;
		for (ShoppingItem cartItem : cartItems) {
			skuGuid = cartItem.getSkuGuid();
			storeProduct = Assign.ifSuccessful(storeProductRepository.findDisplayableStoreProductWithAttributesBySkuGuid(storeCode, skuGuid));

			if (!storeProduct.isSkuAvailable(storeProduct.getSkuByGuid(skuGuid).getSkuCode())) {
				return ExecutionResultFactory.createReadOK(false);
			}
		}

		return ExecutionResultFactory.createReadOK(!shoppingCart.getCartItems().isEmpty());
	}
}
