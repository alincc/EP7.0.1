/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.transformer;

import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.resource.promotions.impl.PromotionsUriBuilderImpl;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.schema.uri.PromotionsUriBuilderFactory;

/**
 * Test class for {@link AppliedPromotionsTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractPromotionsRFOTransformerContractTest {

	static final String RESOURCE_SERVER_NAME = "promotions";
	static final String PROMOTION_ID = "12345";
	static final String SCOPE = "scope";
	static final String SOURCE_URI = "/source/mobee/12345=";
	final  ResourceState otherRepresentation = createCartRepresentation();

	@Mock
	PromotionsUriBuilderFactory promotionsUriBuilderFactory;
	TransformRfoToResourceState<LinksEntity, Collection<String>, ResourceEntity> transformerUnderTest;

	@Before
	public void setUp() {
		when(promotionsUriBuilderFactory.get()).thenAnswer(invocation -> new PromotionsUriBuilderImpl(RESOURCE_SERVER_NAME));
		PromotionsTransformer promotionsTransformer = new PromotionsTransformer(promotionsUriBuilderFactory);
		createTransformerUnderTest(promotionsTransformer);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSelfWhenTransformingIdCollectionToLinksRepresentationWithSourceUri() {
		String expectedSelfUri = createExpectedUri();
		Self expectedSelf = SelfFactory.createSelf(expectedSelfUri);

		ResourceState<LinksEntity> actualState =
				transformerUnderTest.transform(Collections.singleton(PROMOTION_ID), otherRepresentation);

		assertResourceState(actualState)
			.self(expectedSelf)
			.linkCount(1);
	}

	private ResourceState<?> createCartRepresentation() {
		Self self = SelfFactory.createSelf(SOURCE_URI);
		return ResourceState.builder()
				.withScope(SCOPE)
				.withSelf(self)
				.build();
	}

	abstract String createExpectedUri();

	abstract void createTransformerUnderTest(final PromotionsTransformer promotionsTransformer);
}
