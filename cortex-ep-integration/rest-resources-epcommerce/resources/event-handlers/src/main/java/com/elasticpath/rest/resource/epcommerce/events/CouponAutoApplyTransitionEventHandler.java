/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.epcommerce.events;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.relos.rs.events.RoleTransitionEvent;
import com.elasticpath.rest.relos.rs.events.ScopedEventEntityHandler;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;

/**
 * Automatically applies coupons to new customer.
 */
@Component(property = {
		Constants.SERVICE_RANKING + ":Integer=50",
		"eventType=" + RoleTransitionEvent.EVENT_TYPE })
public class CouponAutoApplyTransitionEventHandler implements ScopedEventEntityHandler<RoleTransitionEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(CouponAutoApplyTransitionEventHandler.class);

	@Reference
	private StoreRepository storeRepository;
	@Reference
	private CartOrderRepository cartOrderRepository;
	@Reference
	private ShoppingCartRepository shoppingCartRepository;


	@Override
	public void handleEvent(final String scope, final RoleTransitionEvent event) {
		String customerGuid = event.getNewUserGuid();
		ExecutionResult<Void> autoApplyResult = filterAndAutoApplyCoupons(scope, customerGuid);

		if (autoApplyResult.isFailure()) {
			LOG.error("Error auto apply coupons to cart order: {}", autoApplyResult.getErrorMessage());
		}
	}

	private ExecutionResult<Void> filterAndAutoApplyCoupons(final String storeCode, final String customerGuid) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				ShoppingCart shoppingCart = Assign.ifSuccessful(shoppingCartRepository.getShoppingCart(customerGuid, storeCode));
				Customer customer = shoppingCart.getShopper().getCustomer();

				CartOrder cartOrder = Assign.ifSuccessful(cartOrderRepository.findByCartGuid(shoppingCart.getGuid()));

				String customerEmailAddress = customer.getEmail();
				Store store = Assign.ifSuccessful(storeRepository.findStore(storeCode));
				boolean isCartOrderUpdated = Assign.ifSuccessful(
						cartOrderRepository.filterAndAutoApplyCoupons(cartOrder, store, customerEmailAddress));
				if (isCartOrderUpdated) {
					Assign.ifSuccessful(cartOrderRepository.saveCartOrder(cartOrder));
				}
				return ExecutionResultFactory.createUpdateOK();
			}

		}.execute();
	}

}
