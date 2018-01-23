/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.ShipmentShippingServiceLevelRepository;
import com.elasticpath.service.shipping.ShippingServiceLevelService;

/**
 * Default implementation of {@link ShipmentShippingServiceLevelRepository}.
 */
@Named("shipmentShippingServiceLevelRepository")
public class ShipmentShippingServiceLevelRepositoryImpl implements ShipmentShippingServiceLevelRepository {

	private static final String SHIPPING_OPTION_NOT_FOUND = "Shipping option not found.";

	private final ShippingServiceLevelService shippingServiceLevelService;

	/**
	 * Constructor.
	 * 
	 * @param shippingServiceLevelService a {@link ShippingServiceLevelService}
	 */
	@Inject
	public ShipmentShippingServiceLevelRepositoryImpl(
		@Named("shippingServiceLevelService")
		final ShippingServiceLevelService shippingServiceLevelService) {
		this.shippingServiceLevelService = shippingServiceLevelService;
	}

	@Override
	@CacheResult
	public ExecutionResult<ShippingServiceLevel> findByGuid(final String shippingServiceLevelGuid) {
		return new ExecutionResultChain() {
			@Override
			protected ExecutionResult<?> build() {
				ShippingServiceLevel shippingServiceLevel = Assign.ifNotNull(shippingServiceLevelService.findByGuid(shippingServiceLevelGuid),
						OnFailure.returnNotFound(SHIPPING_OPTION_NOT_FOUND));
				return ExecutionResultFactory.createReadOK(shippingServiceLevel);
			}
		}.execute();
	}

}
