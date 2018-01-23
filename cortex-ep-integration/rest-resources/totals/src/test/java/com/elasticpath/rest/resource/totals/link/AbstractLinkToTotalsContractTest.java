/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.link;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Iterables;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.totals.TotalLookup;
import com.elasticpath.rest.resource.totals.TotalResourceLinkCreator;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * Abstract contract test for link strategies.
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractLinkToTotalsContractTest<R extends ResourceEntity> {

	ResourceState<R> testRepresentation;

	@Mock
	TotalResourceLinkCreator totalResourceLinkCreator;

	@Mock
	ResourceState<TotalEntity> totalEntityResourceState;

	@Mock
	TotalLookup<R> totalLookup;

	ResourceLink resourceLink;

	ExecutionResult<ResourceState<TotalEntity>> executionResult;

	ResourceStateLinkHandler<R> linkCommandStrategy;

	protected static final String RESOURCE_URI = "/resourceuri";

	protected final Self self = SelfFactory.createSelf(RESOURCE_URI);

	@Before
	public void setUp() {
		testRepresentation = createRepresentationUnderTest();
		linkCommandStrategy = createLinkCommandStrategyUnderTest();
		resourceLink = ResourceLink.builder().build();
		executionResult = ExecutionResultFactory.createReadOK(totalEntityResourceState);
	}

	@Test
	public void testCreateLinkWhenLookupReturnsSuccess() {
		arrangeTotalResourceLinkHelperToReturnResourceLink();
		arrangeTotalLookupToReturnTotals();

		Iterable<ResourceLink> links = linkCommandStrategy.getLinks(testRepresentation);

		assertEquals("Should only have one resource link returned.", 1, Iterables.size(links));
		assertEquals("Should only have one resource link returned.", resourceLink, Iterables.getFirst(links, null));
	}

	abstract ResourceState<R> createRepresentationUnderTest();

	abstract ResourceStateLinkHandler<R> createLinkCommandStrategyUnderTest();

	abstract void arrangeTotalLookupToReturnTotals();

	abstract void arrangeTotalResourceLinkHelperToReturnResourceLink();

}
