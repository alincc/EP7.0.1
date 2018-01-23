/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integration.epcommerce.promotion.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository.FindCartOrder.BY_SHIPMENT_DETAILS_ID;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.ShippingServiceLevelRepository;

/**
 * A test.
 */
@RunWith(MockitoJUnitRunner.class)
public class AppliedShippingOptionPromotionsLookupStrategyImplTest {
	@InjectMocks
	private AppliedShippingOptionPromotionsLookupStrategyImpl appliedShippingOptionPromotionsLookupStrategy;

	@Mock
	private CartOrderRepository cartOrderRepository;

	@Mock
	private PromotionRepository promotionRepository;

	@Mock
	private ShippingServiceLevelRepository shippingServiceLevelRepository;

	@Mock
	private PricingSnapshotRepository pricingSnapshotRepository;

	private static final String EXPECTED_APPLIED_PROMO_CODE = "testPromoCode";

	@Test
	public void testGetAppliedPromotionsForShippingOption() throws Exception {
		String scope = "testScope";
		String decodedCompositeShipmentDetailsId = "testShipmentDetailsId";
		String shippingOptionId = "testShippingOptionId";
		String cartOrderGuid = "testCartOrderGuid";
		CartOrder cartOrder = mock(CartOrder.class);
		ShoppingCartPricingSnapshot pricingSnapshot = mock(ShoppingCartPricingSnapshot.class);

		when(cartOrder.getGuid()).thenReturn(cartOrderGuid);
		ShoppingCart shoppingCart = mock(ShoppingCart.class);
		when(
			cartOrderRepository.getEnrichedShoppingCart(scope, decodedCompositeShipmentDetailsId, BY_SHIPMENT_DETAILS_ID))
			.thenReturn(ExecutionResultFactory.createReadOK(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshot(shoppingCart))
				.thenReturn(ExecutionResultFactory.createReadOK(pricingSnapshot));

		ShippingServiceLevel shippingServiceLevel = mock(ShippingServiceLevel.class);
		List<ShippingServiceLevel> shippingServiceLevelList = new ArrayList<>();
		shippingServiceLevelList.add(shippingServiceLevel);
		when(shoppingCart.getShippingServiceLevelList()).thenReturn(shippingServiceLevelList);

		when(shippingServiceLevelRepository.getShippingServiceLevel(shippingServiceLevelList, shippingOptionId))
				.thenReturn(ExecutionResultFactory.createReadOK(shippingServiceLevel));
		Collection<String> expectedAppliedPromoCodes = new ArrayList<>();
		expectedAppliedPromoCodes.add(EXPECTED_APPLIED_PROMO_CODE);
		when(promotionRepository.getAppliedShippingPromotions(pricingSnapshot, shippingServiceLevel))
				.thenReturn(expectedAppliedPromoCodes);

		Collection<String> appliedPromotionsForShippingOption
				= appliedShippingOptionPromotionsLookupStrategy.getAppliedPromotionsForShippingOption(
					scope, decodedCompositeShipmentDetailsId, shippingOptionId).getData();
		assertTrue(appliedPromotionsForShippingOption.contains(EXPECTED_APPLIED_PROMO_CODE));
	}

}