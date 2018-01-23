/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.ShippingOptionWriter;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.integration.ShippingOptionWriterStrategy;
import com.elasticpath.rest.id.util.Base32Util;

/**
 * Implements  the {@link ShippingOptionWriter}.
 */
@Singleton
@Named("shippingOptionWriter")
public final class ShippingOptionWriterImpl implements ShippingOptionWriter {

	private final ShippingOptionWriterStrategy shippingOptionWriterStrategy;

	/**
	 * Default constructor.
	 *
	 * @param shippingOptionWriterStrategy the shipping option writer strategy
	 */
	@Inject
	public ShippingOptionWriterImpl(
			@Named("shippingOptionWriterStrategy")
			final ShippingOptionWriterStrategy shippingOptionWriterStrategy) {

		this.shippingOptionWriterStrategy = shippingOptionWriterStrategy;
	}

	@Override
	public ExecutionResult<Boolean> selectShippingOptionForShipment(final String scope, final String shipmentDetailsId,
			final String shippingOptionId) {
		String decodedShippingOptionId = Base32Util.decode(shippingOptionId);
		return shippingOptionWriterStrategy.selectShippingOptionForShipment(scope, shipmentDetailsId, decodedShippingOptionId);
	}
}
