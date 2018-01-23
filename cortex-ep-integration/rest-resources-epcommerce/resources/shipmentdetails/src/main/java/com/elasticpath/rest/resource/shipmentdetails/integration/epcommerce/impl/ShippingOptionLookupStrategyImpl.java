/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.integration.epcommerce.impl;

import java.util.Collection;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;


import org.apache.commons.lang3.StringUtils;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shoppingcart.ShippingPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.ShippingServiceLevelRepository;
import com.elasticpath.rest.resource.shipmentdetails.integration.epcommerce.ShipmentDetailsIntegrationProperties;
import com.elasticpath.rest.resource.shipmentdetails.integration.epcommerce.transform.ShippingServiceLevelTransformer;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.integration.ShippingOptionLookupStrategy;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.integration.dto.ShippingOptionDto;

/**
 * The Ep Commerce Engine implementation of {@link ShippingOptionLookupStrategy}.
 */
@Singleton
@Named("shippingOptionLookupStrategy")
public class ShippingOptionLookupStrategyImpl implements ShippingOptionLookupStrategy {

	private final ResourceOperationContext resourceOperationContext;
	private final CartOrderRepository cartOrderRepository;
	private final ShippingServiceLevelRepository shippingServiceLevelRepository;
	private final ShippingServiceLevelTransformer shippingServiceLevelTransformer;
	private final PricingSnapshotRepository pricingSnapshotRepository;

	/**
	 * Instantiates a new shipping option lookup strategy.
	 *
	 * @param resourceOperationContext        the resource operation context
	 * @param cartOrderRepository             the cart order repository
	 * @param shippingServiceLevelRepository  the shipping service level repository
	 * @param shippingServiceLevelTransformer the shipping service level transformer
	 * @param pricingSnapshotRepository		  the pricing snapshot repository
	 */
	@Inject
	public ShippingOptionLookupStrategyImpl(
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository,
			@Named("shippingServiceLevelRepository")
			final ShippingServiceLevelRepository shippingServiceLevelRepository,
			@Named("shippingServiceLevelTransformer")
			final ShippingServiceLevelTransformer shippingServiceLevelTransformer,
			@Named("pricingSnapshotRepository")
			final PricingSnapshotRepository pricingSnapshotRepository) {

		this.resourceOperationContext = resourceOperationContext;
		this.shippingServiceLevelRepository = shippingServiceLevelRepository;
		this.cartOrderRepository = cartOrderRepository;
		this.shippingServiceLevelTransformer = shippingServiceLevelTransformer;
		this.pricingSnapshotRepository = pricingSnapshotRepository;
	}

	@Override
	public ExecutionResult<ShippingOptionDto> getShippingOptionForShipmentDetails(
			final String storeCode,
			final String shipmentDetailsId,
			final String shippingOptionCode) {

		ShippingServiceLevel level = Assign.ifSuccessful(shippingServiceLevelRepository.findByGuid(storeCode, shipmentDetailsId,
				shippingOptionCode));

		final ShippingPricingSnapshot shippingPricingSnapshot = getShippingPricingSnapshot(storeCode, shipmentDetailsId, level);

		Subject subject = resourceOperationContext.getSubject();
		Locale locale = SubjectUtil.getLocale(subject);
		ShippingOptionDto dto = shippingServiceLevelTransformer.transformToEntity(level, shippingPricingSnapshot, locale);
		return ExecutionResultFactory.createReadOK(dto);
	}

	@Override
	public ExecutionResult<Collection<String>> getShippingOptionIdsForShipmentDetails(
			final String storeCode,
			final String shipmentDetailsId) {

		final Collection<String> levelGuids = Assign.ifSuccessful(
				shippingServiceLevelRepository.findShippingServiceLevelGuidsForShipment(storeCode, shipmentDetailsId));

		return ExecutionResultFactory.createReadOK(levelGuids);
	}

	@Override
	public ExecutionResult<String> getSelectedShipmentOptionIdForShipmentDetails(final String storeCode, final String shipmentDetailsId) {


		final String shipmentOptionId = Assign.ifSuccessful(shippingServiceLevelRepository
																	.getSelectedShipmentOptionIdForShipmentDetails(storeCode, shipmentDetailsId));

		return ExecutionResultFactory.createReadOK(shipmentOptionId);
	}


	@Override
	public ExecutionResult<Boolean> isShippingDestinationSelectedForShipmentDetails(
			final String storeCode,
			final String shipmentDetailsId) {

		CartOrder cartOrder = Assign.ifSuccessful(cartOrderRepository.findByShipmentDetailsId(storeCode, shipmentDetailsId));
		String shippingAddressGuid = cartOrder.getShippingAddressGuid();
		return ExecutionResultFactory.createReadOK(!StringUtils.isEmpty(shippingAddressGuid));
	}

	@Override
	public ExecutionResult<Boolean> isSupportedShippingOptionType(final String shippingOptionType) {
		return ExecutionResultFactory.createReadOK(
				ShipmentDetailsIntegrationProperties.PHYSICAL_SHIPMENT_IDENTIFIER.equals(shippingOptionType));
	}

	/**
	 * Retrieves the corresponding {@link ShippingPricingSnapshot} from the given inputs.
	 *
	 * @param storeCode the store code
	 * @param shipmentDetailsId the shipment details ID
	 * @param level the shipping service level
	 * @return a shipping pricing snapshot
	 */
	protected ShippingPricingSnapshot getShippingPricingSnapshot(final String storeCode, final String shipmentDetailsId, final ShippingServiceLevel
			level) {
		final ShoppingCart cart = Assign.ifSuccessful(
			cartOrderRepository.getEnrichedShoppingCart(storeCode, shipmentDetailsId, CartOrderRepository.FindCartOrder.BY_SHIPMENT_DETAILS_ID));

		final ShoppingCartPricingSnapshot cartPricingSnapshot = Assign.ifSuccessful(pricingSnapshotRepository.getShoppingCartPricingSnapshot(cart));

		return cartPricingSnapshot.getShippingPricingSnapshot(level);
	}

}
