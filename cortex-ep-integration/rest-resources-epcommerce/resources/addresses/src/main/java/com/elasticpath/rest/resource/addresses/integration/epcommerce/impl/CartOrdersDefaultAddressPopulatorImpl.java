/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.integration.epcommerce.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.addresses.integration.epcommerce.CartOrdersDefaultAddressPopulator;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;

/**
 * Populate cart order default address fields.
 */
@Singleton
@Named("cartOrdersDefaultAddressPopulator")
public class CartOrdersDefaultAddressPopulatorImpl implements CartOrdersDefaultAddressPopulator {

	private static final Logger LOG = LoggerFactory.getLogger(CartOrdersDefaultAddressPopulatorImpl.class);
	
	private final CartOrderRepository cartOrderRepository;

	/**
	 * Constructor.
	 *
	 * @param cartOrderRepository cart order repository.
	 */
	@Inject
	public CartOrdersDefaultAddressPopulatorImpl(
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository) {
		this.cartOrderRepository = cartOrderRepository;
	}

	@Override
	public void updateAllCartOrdersAddresses(final Customer customer, final CustomerAddress address, final String storeCode,
			final boolean updateBillingAddress, final boolean updateShippingAddress) {

		ExecutionResult<Collection<String>> customerCartOrderGuidsResult = cartOrderRepository.findCartOrderGuidsByCustomer(storeCode,
				customer.getGuid());
		if (customerCartOrderGuidsResult.isFailure()) {
			LOG.warn("Customer has no cart orders to default addresses with.");
			return;
		}
		Collection<String> cartOrderGuids = customerCartOrderGuidsResult.getData();
		for (String cartOrderGuid : cartOrderGuids) {
			updateCartOrderAddresses(address, updateBillingAddress, updateShippingAddress, storeCode, cartOrderGuid);
		}
	}

	private void updateCartOrderAddresses(final CustomerAddress address, final boolean updatedPreferredBillingAddress,
			final boolean updatedPreferredShippingAddress, final String storeCode, final String cartOrderGuid) {
		ExecutionResult<CartOrder> cartOrderResult = cartOrderRepository.findByGuid(storeCode, cartOrderGuid);
		if (cartOrderResult.isFailure()) {
			LOG.warn("Cart order was not found for guid.");
			return;
		}

		CartOrder cartOrder = cartOrderResult.getData();
		if (updatedPreferredBillingAddress && cartOrder.getBillingAddressGuid() == null) {
			cartOrder.setBillingAddressGuid(address.getGuid());
			cartOrderRepository.saveCartOrder(cartOrder);
		}

		if (updatedPreferredShippingAddress && cartOrder.getShippingAddressGuid() == null) {
			cartOrderRepository.updateShippingAddressOnCartOrder(address.getGuid(), cartOrder.getGuid(), storeCode);
		}
	}
}
