/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.impl;

import static com.elasticpath.rest.test.AssertResourceLink.assertResourceLink;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.resource.totals.TotalResourceLinkCreator;
import com.elasticpath.rest.resource.totals.rel.TotalResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.TotalsUriBuilderFactory;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Test class for {@link TotalResourceLinkCreatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class TotalResourceLinkCreatorImplTest {

	private static final String RESOURCE_SERVER_NAME = "totals";
	public static final String RESOURCE_REV = "resourcerev";
	public static final String RESOURCE_URI = "/resourceuri";

	@Mock
	private TotalsUriBuilderFactory totalsUriBuilderFactory;


	private TotalResourceLinkCreator totalResourceLinkCreator;

	@Before
	public void setUp() {
		when(totalsUriBuilderFactory.get()).thenAnswer(invocation -> new TotalsUriBuilderImpl(RESOURCE_SERVER_NAME));
		totalResourceLinkCreator = new TotalResourceLinkCreatorImpl(totalsUriBuilderFactory);
	}

	@Test
	public void testCreateLinkToOtherResource() {
		Collection<ResourceLink> result = totalResourceLinkCreator.createLinkToOtherResource(RESOURCE_URI, RESOURCE_REV);

		String expectedUri = new TotalsUriBuilderImpl(RESOURCE_SERVER_NAME).setSourceUri(RESOURCE_URI).build();

		assertEquals("There should only be one result", 1, result.size());

		assertResourceLink(CollectionUtil.first(result)).rel(TotalResourceRels.TOTAL_REL).rev(RESOURCE_REV).uri(expectedUri);
	}

	@Test
	public void testCreateLinkToOtherResourceWithSuccessfulResult() {
		final TotalEntity entity = TotalEntity.builder().build();
		ExecutionResult<ResourceState<TotalEntity>> successResult =
				ExecutionResultFactory.createReadOK(ResourceState.Builder.create(entity).build());

		Collection<ResourceLink> result = totalResourceLinkCreator.createLinkToOtherResource(RESOURCE_URI, successResult, RESOURCE_REV);

		assertEquals("There should only be one result", 1, result.size());
	}

	@Test
	public void testCreateLinkToOTherResourceWithFailureResult() {
		ExecutionResult<ResourceState<TotalEntity>> notFoundResult = ExecutionResultFactory.createNotFound("not found");

		Collection<ResourceLink> result = totalResourceLinkCreator.createLinkToOtherResource(RESOURCE_URI, notFoundResult, RESOURCE_REV);

		assertThat("Resource links should be empty.", result, Matchers.empty());
	}

}
