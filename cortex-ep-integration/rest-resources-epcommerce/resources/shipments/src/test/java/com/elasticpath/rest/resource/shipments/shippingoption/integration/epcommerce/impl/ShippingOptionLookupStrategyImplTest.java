/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.shippingoption.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.command.ExecutionResultFactory.createReadOK;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.Locale;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.calc.ShippingCostCalculator;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.ShipmentShippingServiceLevelRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * Tests for {@link ShippingOptionLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippingOptionLookupStrategyImplTest {

	private static final String SCOPE = "testScope";
	private static final String PURCHASE_ID = "testPurchaseId";
	private static final String SHIPMENT_ID = "testShipmentId";
	private static final String SERVICE_GUID = "serviceGuid";
	private static final String SERVICE_LEVEL_CODE = "serviceCode";
	private static final String SERVICE_LEVEL_CODE_DISPLAY = "serviceDisplay";
	private static final String CARRIER = "carrier";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ShippingCostCalculator shippingCostCalculator;
	@Mock
	private MoneyTransformer moneyTransformer;
	@Mock
	@SuppressWarnings("PMD.UnusedPrivateField")
	private ResourceOperationContext resourceOperationContext;
	@Mock
	private ShipmentShippingServiceLevelRepository shipmentShippingServiceLevelRepository;
	@Mock
	private ShipmentRepository shipmentRepository;
	@InjectMocks
	private ShippingOptionLookupStrategyImpl lookupStrategyImpl;
	@Mock
	private PhysicalOrderShipment orderShipment;
	@Mock
	private ShippingServiceLevel shippingServiceLevel;


	private final CostEntity costEntity = CostEntity.builder().build();
	private final Money shippingCostMoney = Money.valueOf(BigDecimal.TEN, Currency.getInstance("CAD"));

	@Before
	public void setUp() {
		when(orderShipment.getShippingServiceLevelGuid()).thenReturn(SERVICE_GUID);
		when(moneyTransformer.transformToEntity(shippingCostMoney)).thenReturn(costEntity);
		when(shippingServiceLevel.getCode()).thenReturn(SERVICE_LEVEL_CODE);
		when(shippingServiceLevel.getCarrier()).thenReturn(CARRIER);
		when(shippingServiceLevel.getDisplayName(any(Locale.class), anyBoolean())).thenReturn(SERVICE_LEVEL_CODE_DISPLAY);
	}

	@Test
	public void testGetShippingOptionSuccess() {
		when(shippingCostCalculator.calculate(PURCHASE_ID, SHIPMENT_ID)).thenReturn(createReadOK(shippingCostMoney));
		when(shipmentRepository.find(PURCHASE_ID, SHIPMENT_ID)).thenReturn(createReadOK(orderShipment));
		when(shipmentShippingServiceLevelRepository.findByGuid(SERVICE_GUID)).thenReturn(createReadOK(shippingServiceLevel));

		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale("scope", "userId", Locale.ENGLISH);
		when(resourceOperationContext.getSubject()).thenReturn(subject);

		ExecutionResult<ShippingOptionEntity> lookupResult = lookupStrategyImpl.getShippingOption(SCOPE, PURCHASE_ID, SHIPMENT_ID);

		ShippingOptionEntity expectedData = buildExpectedData();
		assertExecutionResult(lookupResult)
			.isSuccessful()
			.data(expectedData);
	}

	@Test
	public void testGetShippingOptionWhenCostNotFound() {
		when(shippingCostCalculator.calculate(PURCHASE_ID, SHIPMENT_ID))
				.thenReturn(ExecutionResultFactory.<Money>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookupStrategyImpl.getShippingOption(SCOPE, PURCHASE_ID, SHIPMENT_ID);
	}


	private ShippingOptionEntity buildExpectedData() {
		return ShippingOptionEntity.builder()
				.withCost(Collections.singleton(costEntity))
				.withDisplayName(SERVICE_LEVEL_CODE_DISPLAY)
				.withName(SERVICE_LEVEL_CODE)
				.withCarrier(CARRIER)
				.build();
	}
}
