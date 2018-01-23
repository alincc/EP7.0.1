/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.impl;

import java.util.Collection;
import java.util.stream.Collectors;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository.FindCartOrder.BY_SHIPMENT_DETAILS_ID;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.ShippingServiceLevelRepository;

/**
 * Implementation of ShippingServiceLevelRepository.
 */
@Singleton
@Named("shippingServiceLevelRepository")
public class ShippingServiceLevelRepositoryImpl implements ShippingServiceLevelRepository {

	private static final String SHIPPING_SERVICE_LEVEL_SELECTION_OUT_OF_SYNC =
			"Cart order shipping service level and shopping cart selected shipping service level are no longer in sync.";
	private static final String SHIPPING_OPTION_NOT_FOUND = "Shipping option not found.";
	private static final String SHIPPING_OPTIONS_NOT_FOUND = "Shipping options not found.";
	private static final String SELECTED_METHOD_NOT_FOUND = "Cannot find selected shipping option for order";

	private final CartOrderRepository cartOrderRepository;

	/**
	 * Constructor.
	 *
	 * @param cartOrderRepository the cart order repository
	 */
	@Inject
	public ShippingServiceLevelRepositoryImpl(
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository) {

		this.cartOrderRepository = cartOrderRepository;
	}

	@Override
	@CacheResult
	public ExecutionResult<Collection<String>> findShippingServiceLevelGuidsForShipment(final String storeCode, final String shipmentDetailsId) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {

				final Address shippingAddress = findShippingAddressByStoreCodeAndShipmentDetails(storeCode, shipmentDetailsId);

				final Collection<ShippingServiceLevel> shippingServiceLevels = Assign.ifNotNull(
							cartOrderRepository.findShippingServiceLevels(storeCode, shippingAddress),
														OnFailure.returnServerError(SHIPPING_OPTIONS_NOT_FOUND));

				final Collection<String> serviceLevelGuids = shippingServiceLevels.stream().map(ShippingServiceLevel::getGuid)
																	.collect(Collectors.toList());

				return ExecutionResultFactory.createReadOK(serviceLevelGuids);
			}
		}.execute();
	}

	@Override
	@CacheResult(uniqueIdentifier = "findByGuid")
	public ExecutionResult<ShippingServiceLevel> findByGuid(final String storeCode, final String shipmentDetailsId,
															final String shippingServiceLevelGuid) {

		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {

				final Address shippingAddress = findShippingAddressByStoreCodeAndShipmentDetails(storeCode, shipmentDetailsId);

				final Collection<ShippingServiceLevel> shippingServiceLevels = Assign.ifNotEmpty(
						cartOrderRepository.findShippingServiceLevels(storeCode, shippingAddress),
												OnFailure.returnServerError(SHIPPING_OPTIONS_NOT_FOUND));

				final ShippingServiceLevel shippingServiceLevel = Assign.ifSuccessful(
							getShippingServiceLevel(shippingServiceLevels, shippingServiceLevelGuid));

				return ExecutionResultFactory.createReadOK(shippingServiceLevel);
			}
		}.execute();
	}

	@Override
	@CacheResult(uniqueIdentifier = "getShippingServiceLevel")
	public ExecutionResult<ShippingServiceLevel> getShippingServiceLevel(
			final Collection<ShippingServiceLevel> shippingServiceLevels, final String shippingServiceLevelGuid) {
		return new ExecutionResultChain() {
			protected ExecutionResult<?> build() {
				Optional<ShippingServiceLevel> shippingServiceLevel =
						Iterables.tryFind(shippingServiceLevels, level -> level.getGuid().equals(shippingServiceLevelGuid));
				Ensure.isTrue(shippingServiceLevel.isPresent(), OnFailure.returnNotFound(SHIPPING_OPTION_NOT_FOUND));
				return ExecutionResultFactory.createReadOK(shippingServiceLevel.get());
			}
		}.execute();
	}

	@Override
	@CacheResult
	public ExecutionResult<String> getSelectedShipmentOptionIdForShipmentDetails(final String storeCode, final String shipmentDetailsId) {

		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				//cached
				final Address shippingAddress = findShippingAddressByStoreCodeAndShipmentDetails(storeCode, shipmentDetailsId);

				final CartOrder cartOrder = Assign.ifSuccessful(cartOrderRepository.getCartOrder(storeCode, shipmentDetailsId,
					CartOrderRepository.FindCartOrder.BY_SHIPMENT_DETAILS_ID));

				final String selectedShippingServiceLevelGuid = Assign.ifNotNull(cartOrder.getShippingServiceLevelGuid(),
					OnFailure.returnNotFound(SELECTED_METHOD_NOT_FOUND));
				//cached
				final Collection<ShippingServiceLevel> shippingServiceLevels = Assign.ifNotEmpty(
						cartOrderRepository.findShippingServiceLevels(storeCode, shippingAddress),
							OnFailure.returnServerError(SHIPPING_OPTION_NOT_FOUND));

				final ShippingServiceLevel shippingServiceLevel = Assign.ifSuccessful(
						getShippingServiceLevel(shippingServiceLevels, selectedShippingServiceLevelGuid),
							OnFailure.returnServerError(SHIPPING_SERVICE_LEVEL_SELECTION_OUT_OF_SYNC));

				return ExecutionResultFactory.createReadOK(shippingServiceLevel.getGuid());
			}
		}.execute();
	}

	private Address findShippingAddressByStoreCodeAndShipmentDetails(final String storeCode, final String shipmentDetailsId) {
		//possibly cached
		final CartOrder cartOrder = Assign.ifSuccessful(cartOrderRepository.getCartOrder(storeCode, shipmentDetailsId, BY_SHIPMENT_DETAILS_ID));
		//also possibly cached
		return Assign.ifSuccessful(cartOrderRepository.getShippingAddress(cartOrder));
	}
}
