/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.transformer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.discounts.DiscountEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.DiscountsUriBuilder;
import com.elasticpath.rest.schema.uri.DiscountsUriBuilderFactory;

/**
 * Collaboration unit test for the transformer.
 */
@RunWith(MockitoJUnitRunner.class)
public class PurchaseDiscountsRfoRepresentationTransformerImplTest {
	private static final String DISCOUNT_URI = "/discountUri";
	private static final String PURCHASE_ID = "purchaseId";
	private static final String SOURCE_URI = "/purchases/scope/id";
	private static final String SCOPE = "scope";

	private PurchaseDiscountsRfoResourceStateTransformerImpl purchaseDiscountTransformer;

	@Mock
	private DiscountsUriBuilderFactory discountsUriBuilderFactory;
	@Mock
	private ResourceState<PurchaseEntity> purchaseRepresentation;

	@Before
	public void setUp() {
		purchaseDiscountTransformer = new PurchaseDiscountsRfoResourceStateTransformerImpl(discountsUriBuilderFactory);

		DiscountsUriBuilder discountsUriBuilder = mock(DiscountsUriBuilder.class);
		when(discountsUriBuilderFactory.get()).thenReturn(discountsUriBuilder);
		when(discountsUriBuilder.setSourceUri(SOURCE_URI)).thenReturn(discountsUriBuilder);
		when(discountsUriBuilder.build()).thenReturn(DISCOUNT_URI);

		purchaseRepresentation = ResourceState.Builder.create(PurchaseEntity.builder()
																				.withPurchaseId(PURCHASE_ID)
																				.build())
													.withSelf(SelfFactory.createSelf(SOURCE_URI, PurchasesMediaTypes.PURCHASE.id()))
													.withScope(SCOPE)
													.build();
	}

	@Test
	public void testTransform() {
		CostEntity costEntity = ResourceTypeFactory.createResourceEntity(CostEntity.class);


		DiscountEntity discountEntity =
				DiscountEntity.builder()
								.addingDiscount(costEntity)
								.build();
		ResourceState<DiscountEntity> expectedDiscount = ResourceState.Builder.create(DiscountEntity.builderFrom(discountEntity)
																							.withPurchaseId(PURCHASE_ID)
																							.build())
																.withScope(SCOPE)
																.withSelf(SelfFactory.createSelf(DISCOUNT_URI))
																.build();


		ResourceState<DiscountEntity> discountRepresentation = purchaseDiscountTransformer
				.transform(discountEntity, purchaseRepresentation);

		assertEquals("The transformed discount should be the same as expected", expectedDiscount, discountRepresentation);
	}

}
