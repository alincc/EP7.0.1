/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integration.epcommerce.promotion.impl;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository.FindCartOrder.BY_SHIPMENT_DETAILS_ID;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.ShippingServiceLevelRepository;
import com.elasticpath.rest.resource.promotions.integration.AppliedShippingOptionPromotionsLookupStrategy;

/**
 * Service that provides lookup applied shipping option promotions data from external systems.
 */
@Singleton
@Named("appliedShippingOptionPromotionsLookupStrategy")
public class AppliedShippingOptionPromotionsLookupStrategyImpl implements AppliedShippingOptionPromotionsLookupStrategy {

	private final CartOrderRepository cartOrderRepository;
	private final PromotionRepository promotionRepository;
	private final ShippingServiceLevelRepository shippingServiceLevelRepository;
	private final PricingSnapshotRepository pricingSnapshotRepository;

	/**
	 * Constructs.
	 * @param cartOrderRepository cartOrderRepository.
	 * @param promotionRepository promotionRepository.
	 * @param shippingServiceLevelRepository shippingServiceLevelRepository.
	 * @param pricingSnapshotRepository pricingSnapshotRepository.
	 */
	@Inject
	public AppliedShippingOptionPromotionsLookupStrategyImpl(
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository,
			@Named("promotionRepository")
			final PromotionRepository promotionRepository,
			@Named("shippingServiceLevelRepository")
			final ShippingServiceLevelRepository shippingServiceLevelRepository,
			@Named("pricingSnapshotRepository")
			final PricingSnapshotRepository pricingSnapshotRepository) {
		this.cartOrderRepository = cartOrderRepository;
		this.promotionRepository = promotionRepository;
		this.shippingServiceLevelRepository = shippingServiceLevelRepository;
		this.pricingSnapshotRepository = pricingSnapshotRepository;
	}

	/**
	 * <p>
	 * {@inheritDoc}
	 * </p>
	 * <p>
	 * NOTE: There are some funky assumptions baked into the parameters here so tread carefully.
	 * </p>
	 * @param scope the scope.
	 * @param shipmentDetailsId the decoded, composite shipment id.
	 * 			The composite contains the (cart)order ID which allows you to get backs.
	 * 			See ShipmentDetailsLookupStrategyImpl#getShipmentDetailsIdForOrderAndDelivery(...)
	 * 			or ShipmentDetailsLookupStrategyImpl#getShipmentDetail(...)
	 * 			or CartOrderRepository#findByShipmentDetailsId(...) for usages of this thing.
	 * @param shippingOptionId the decoded shipping option id.
	 * @return Applied promotion ids.
	 */
	@Override
	public ExecutionResult<Collection<String>> getAppliedPromotionsForShippingOption(
			final String scope, final String shipmentDetailsId, final String shippingOptionId) {

		ShoppingCart shoppingCart = Assign.ifSuccessful(
				cartOrderRepository.getEnrichedShoppingCart(scope, shipmentDetailsId, BY_SHIPMENT_DETAILS_ID));
		List<ShippingServiceLevel> shippingServiceLevelList = shoppingCart.getShippingServiceLevelList();
		ShippingServiceLevel shippingServiceLevel = Assign.ifSuccessful(
				shippingServiceLevelRepository.getShippingServiceLevel(shippingServiceLevelList, shippingOptionId));

		final ShoppingCartPricingSnapshot pricingSnapshot = Assign.ifSuccessful(
				pricingSnapshotRepository.getShoppingCartPricingSnapshot(shoppingCart));

		Collection<String> appliedPromotions
				= promotionRepository.getAppliedShippingPromotions(pricingSnapshot, shippingServiceLevel);
		return ExecutionResultFactory.createReadOK(appliedPromotions);
	}

}
