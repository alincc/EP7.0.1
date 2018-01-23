/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.integration.epcommerce.transform;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.resource.integration.epcommerce.transform.DateTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;
import com.elasticpath.rest.resource.purchases.constants.PurchaseStatus;

/**
 * Transforms between a {@link Order} and {@link PurchaseEntity}, and vice versa.
 */
@Singleton
@Named("orderTransformer")
public class OrderTransformer extends AbstractDomainTransformer<Order, PurchaseEntity> {

	private static final Map<OrderStatus, PurchaseStatus> STATUS_MAP = createStatusMap();

	private final MoneyTransformer moneyTransformer;
	private final DateTransformer dateTransformer;

	/**
	 * Default constructor.
	 *
	 * @param moneyTransformer the money transformer
	 * @param dateTransformer the date transformer
	 */
	@Inject
	public OrderTransformer(
			@Named("moneyTransformer")
			final MoneyTransformer moneyTransformer,
			@Named("dateTransformer")
			final DateTransformer dateTransformer) {

		this.moneyTransformer = moneyTransformer;
		this.dateTransformer = dateTransformer;
	}

	private static Map<OrderStatus, PurchaseStatus> createStatusMap() {
		Map<OrderStatus, PurchaseStatus> statusMap = new HashMap<>();
		statusMap.put(OrderStatus.AWAITING_EXCHANGE, PurchaseStatus.IN_PROGRESS);
		statusMap.put(OrderStatus.FAILED, PurchaseStatus.IN_PROGRESS);
		statusMap.put(OrderStatus.IN_PROGRESS, PurchaseStatus.IN_PROGRESS);
		statusMap.put(OrderStatus.ONHOLD, PurchaseStatus.IN_PROGRESS);
		statusMap.put(OrderStatus.PARTIALLY_SHIPPED, PurchaseStatus.IN_PROGRESS);
		statusMap.put(OrderStatus.COMPLETED, PurchaseStatus.COMPLETED);
		statusMap.put(OrderStatus.CANCELLED, PurchaseStatus.CANCELED);
		return Collections.unmodifiableMap(statusMap);
	}

	@Override
	public Order transformToDomain(final PurchaseEntity purchaseEntity, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public PurchaseEntity transformToEntity(final Order order, final Locale locale) {
		return PurchaseEntity.builder()
				.withOrderId(order.getCartOrderGuid())
				.withPurchaseId(order.getGuid())
				.withStatus(STATUS_MAP.get(order.getStatus()).name())
				.withPurchaseDate(dateTransformer.transformToEntity(order.getCreatedDate(), locale))
				.withMonetaryTotal(Collections.singleton(moneyTransformer.transformToEntity(order.getTotalMoney(), locale)))
				.withTaxTotal(moneyTransformer.transformToEntity(order.getTotalTaxMoney(), locale))
				.build();
	}
}
