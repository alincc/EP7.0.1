/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.promotions.integration.AppliedShippingOptionPromotionsLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Test class for {@link ShippingOptionPromotionsLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ShippingOptionPromotionsLookupImplTest {

	private static final String SCOPE = "scope";
	private static final String PROMOTION_ID = "12345";
	private static final String SHIPPING_DETAILS_ID = "test-shipping-details-id";
	private static final String SHIPPING_OPTION_ID = "testShippingOptionsId";
	private static final String ENCODED_SHIPPING_OPTION_ID = Base32Util.encode(SHIPPING_OPTION_ID);
	private static final String SOURCE_URI = URIUtil.format(
		"shipmentdetails", SCOPE, SHIPPING_DETAILS_ID, "shippingoptions", ENCODED_SHIPPING_OPTION_ID);

	private final Collection<String> promotionIds = Collections.singleton(PROMOTION_ID);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ResourceState<LinksEntity> expectedLinksRepresentation;
	private final ResourceState<ShippingOptionEntity> shippingOptionRepresentation = createShippingOptionRepresentation();

	@Mock
	private AppliedShippingOptionPromotionsLookupStrategy mockLookupStrategy;

	@Mock
	private TransformRfoToResourceState<LinksEntity, Collection<String>, ShippingOptionEntity> mockPromotionsTransformer;

	@InjectMocks
	private ShippingOptionPromotionsLookupImpl shippingOptionPromotionsLookup;

	@Test
	public void testGetPromotionsForShippingOptionWhenSuccessfulResultFromStrategy() {
		when(mockLookupStrategy.getAppliedPromotionsForShippingOption(SCOPE, SHIPPING_DETAILS_ID, SHIPPING_OPTION_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(promotionIds));
		when(mockPromotionsTransformer.transform(promotionIds, shippingOptionRepresentation))
				.thenReturn(expectedLinksRepresentation);

		ExecutionResult<ResourceState<LinksEntity>> result =
				shippingOptionPromotionsLookup.getAppliedPromotionsForShippingOption(shippingOptionRepresentation);

		assertExecutionResult(result)
				.isSuccessful()
				.resourceStatus(ResourceStatus.READ_OK)
				.data(expectedLinksRepresentation);
	}

	@Test
	public void testGetPromotionsForShippingOptionWhenFailureResultFromStrategy() {
		when(mockLookupStrategy.getAppliedPromotionsForShippingOption(SCOPE, SHIPPING_DETAILS_ID, SHIPPING_OPTION_ID))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shippingOptionPromotionsLookup.getAppliedPromotionsForShippingOption(shippingOptionRepresentation);
	}

	private ResourceState<ShippingOptionEntity> createShippingOptionRepresentation() {
		ShippingOptionEntity shippingOptionEntity = ShippingOptionEntity.builder()
				.withShippingOptionId(ENCODED_SHIPPING_OPTION_ID)
				.build();
		Self self = SelfFactory.createSelf(SOURCE_URI);

		return ResourceState.<ShippingOptionEntity>builder()
				.withEntity(shippingOptionEntity)
				.withSelf(self)
				.withScope(SCOPE)
				.build();
	}
}
