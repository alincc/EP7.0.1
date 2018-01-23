/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.order.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.search.query.CustomerSearchCriteria;
import com.elasticpath.service.search.query.OrderSearchCriteria;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * The facade for {@link Order} related operations.
 */
@Singleton
@Named("orderRepository")
public class OrderRepositoryImpl implements OrderRepository {

	private static final String PURCHASE_NOT_FOUND = "No purchase with GUID %s was found in store %s.";
	private final OrderService orderService;
	private final BeanFactory coreBeanFactory;

	/**
	 * Default constructor.
	 *
	 * @param orderService the order service
	 * @param coreBeanFactory the core bean factory
	 */
	@Inject
	public OrderRepositoryImpl(
			@Named("orderService")
			final OrderService orderService,
			@Named("coreBeanFactory")
			final BeanFactory coreBeanFactory) {

		this.orderService = orderService;
		this.coreBeanFactory = coreBeanFactory;
	}

	@Override
	@CacheResult
	public ExecutionResult<Order> findByGuid(final String storeCode, final String orderGuid) {
		return new ExecutionResultChain() {
			@Override
			public ExecutionResult<?> build() {
				Order order = Assign.ifNotNull(orderService.findOrderByOrderNumber(orderGuid),
						OnFailure.returnNotFound(PURCHASE_NOT_FOUND, orderGuid, storeCode));

				return ExecutionResultFactory.createReadOK(order);
			}
		}.execute();
	}

	@Override
	@CacheResult
	public ExecutionResult<Collection<String>> findOrderIdsByCustomerGuid(final String storeCode, final String customerGuid) {
		CustomerSearchCriteria customerSearchCriteria = createCustomerSearchCriteria(customerGuid);
		OrderSearchCriteria orderSearchCriteria = createOrderSearchCriteria(customerSearchCriteria, storeCode);
		Collection<String> orderGuidList = orderService.findOrderNumbersBySearchCriteria(orderSearchCriteria, 0, Integer.MAX_VALUE);

		return ExecutionResultFactory.createReadOK(orderGuidList);
	}


	private OrderSearchCriteria createOrderSearchCriteria(final CustomerSearchCriteria customerSearchCriteria, final String validatedStoreCode) {
		OrderSearchCriteria orderSearchCriteria = coreBeanFactory.getBean(ContextIdNames.ORDER_SEARCH_CRITERIA);
		orderSearchCriteria.setExcludedOrderStatus(OrderStatus.FAILED);
		Set<String> storeCodes = Collections.singleton(validatedStoreCode);
		orderSearchCriteria.setStoreCodes(storeCodes);
		orderSearchCriteria.setCustomerSearchCriteria(customerSearchCriteria);
		orderSearchCriteria.setSortingType(StandardSortBy.DATE);
		orderSearchCriteria.setSortingOrder(SortOrder.DESCENDING);
		return orderSearchCriteria;
	}

	private CustomerSearchCriteria createCustomerSearchCriteria(final String customerGuid) {
		CustomerSearchCriteria customerSearchCriteria = coreBeanFactory.getBean(ContextIdNames.CUSTOMER_SEARCH_CRITERIA);
		customerSearchCriteria.setGuid(customerGuid);
		return customerSearchCriteria;
	}
}
