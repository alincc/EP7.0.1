/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.integration.transform;

import static com.elasticpath.rest.test.AssertResourceLink.assertResourceLink;
import static com.elasticpath.rest.test.AssertSelf.assertSelf;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.carts.CartsMediaTypes;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.resource.totals.impl.TotalsUriBuilderImpl;
import com.elasticpath.rest.resource.totals.rel.TotalResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.TotalsUriBuilderFactory;
import com.elasticpath.rest.util.collection.CollectionUtil;


/**
 * Tests {@link TotalTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class TotalTransformerTest {
	private static final String SCOPE = "scope";
	private static final String RESOURCE_SERVER_NAME = "totals";
	private static final String RESOURCE_REL = "resourcerel";
	private static final String RESOURCE_URI = "/resourceUri";

	private final CostEntity costEntity = ResourceTypeFactory.createResourceEntity(CostEntity.class);

	@Mock
	private TotalsUriBuilderFactory totalsUriBuilderFactory;
	private TotalTransformer totalTransformer;

	@Before
	public void setUp() {
		when(totalsUriBuilderFactory.get()).thenAnswer(invocation -> new TotalsUriBuilderImpl(RESOURCE_SERVER_NAME));
		totalTransformer =  new TotalTransformer(totalsUriBuilderFactory);
	}

	@Test
	public void testTotalRepresentationFromTransformResult() {
		ResourceState<CartEntity> representation = createTestRepresentation();
		TotalEntity totalDto = createTotalEntity();

		ResourceState<TotalEntity> totalRepresentation = totalTransformer.transform(totalDto, representation, RESOURCE_REL);

		assertEquals("totalRepresentation should have 1 cost entity", 1, totalRepresentation.getEntity().getCost().size());

		assertThat("totalRepresentation should have expected cost", totalRepresentation.getEntity().getCost(), hasItem(costEntity));
		assertEquals("totalRepresentation scope should be the same", SCOPE, totalRepresentation.getScope());
	}

	@Test
	public void testResourceLinkFromTransformResult() {
		ResourceState<CartEntity> representation =  createTestRepresentation();
		TotalEntity totalDto = createTotalEntity();

		ResourceState<TotalEntity> totalRepresentation = totalTransformer.transform(totalDto, representation, RESOURCE_REL);

		assertEquals("Should only have one link.", 1, totalRepresentation.getLinks().size());
		ResourceLink resourceLink = CollectionUtil.first(totalRepresentation.getLinks());

		assertResourceLink(resourceLink).rev(TotalResourceRels.TOTAL_REV).rel(RESOURCE_REL).uri(RESOURCE_URI);
	}

	@Test
	public void testSelfFromTransformResult() {
		ResourceState<CartEntity> representation = createTestRepresentation();
		TotalEntity totalDto = createTotalEntity();

		ResourceState<TotalEntity> totalRepresentation = totalTransformer.transform(totalDto, representation, RESOURCE_REL);

		String expectedSelfUri = new TotalsUriBuilderImpl(RESOURCE_SERVER_NAME)
				.setSourceUri(RESOURCE_URI)
				.build();

		assertSelf(totalRepresentation.getSelf())
				.uri(expectedSelfUri)
				.type(null);
	}

	private ResourceState<CartEntity> createTestRepresentation() {
		Self resourceSelf = SelfFactory.createSelf(RESOURCE_URI, CartsMediaTypes.CART.id());
		CartEntity cartEntity = CartEntity.builder().build();
		return ResourceState.Builder.create(cartEntity)
				.withScope(SCOPE)
				.withSelf(resourceSelf)
				.build();
	}

	private TotalEntity createTotalEntity() {
		return TotalEntity.builder()
				.withCost(Collections.singleton(costEntity))
				.build();
	}
}
