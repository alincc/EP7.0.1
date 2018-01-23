/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.command.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertResourceInfo.assertResourceInfo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.purchases.PurchaseLookup;
import com.elasticpath.rest.resource.purchases.command.ReadPurchaseListCommand;
import com.elasticpath.rest.resource.purchases.constants.PurchaseResourceConstants;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;
import com.elasticpath.rest.util.collection.CollectionUtil;


/**
 * Tests for {@link ReadPurchaseListCommandImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ReadPurchaseListCommandImplTest {

	private static final String PURCHASES = "purchases";
	private static final String SCOPE = "scope";
	private static final String USER_ID = "user id";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ResourceOperationContext mockResourceOperationContext;
	@Mock
	private PurchaseLookup mockPurchaseLookup;


	/**
	 * Tests that the command returns a {@link ResourceState} with the correct element links upon lookup success.
	 */
	@Test
	public void testReadPurchaseListSuccess() {
		Collection<String> purchaseIds = Arrays.asList("purchaseId1", "purchaseId2");

		when(mockPurchaseLookup.findPurchaseIds(SCOPE, USER_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(purchaseIds));

		when(mockResourceOperationContext.getUserIdentifier())
				.thenReturn(USER_ID);

		ReadPurchaseListCommand command = createPurchaseListCommand();
		ExecutionResult<ResourceState<LinksEntity>> result = command.execute();

		assertTrue("Command should have been successful", result.isSuccessful());
		ResourceState<LinksEntity> purchaseListRepresentation = result.getData();

		String selfUri = URIUtil.format(PURCHASES, SCOPE);
		Self expectedSelf = SelfFactory.createSelf(selfUri);
		Collection<ResourceLink> expectedElementLinks =
				ElementListFactory.createElementsOfList(selfUri, purchaseIds, PurchasesMediaTypes.PURCHASE.id());

		assertEquals("The self is incorrect", expectedSelf, purchaseListRepresentation.getSelf());
		assertTrue("The links are wrong", CollectionUtil.areSame(expectedElementLinks, purchaseListRepresentation.getLinks()));
		assertResourceInfo(purchaseListRepresentation.getResourceInfo())
			.maxAge(PurchaseResourceConstants.MAX_AGE);
	}

	/**
	 * Tests that the command returns a {@link ResourceState} with no element links upon lookup success which returns an empty list.
	 */
	@Test
	public void testReadPurchaseListSuccessNoElements() {
		when(mockPurchaseLookup.findPurchaseIds(SCOPE, USER_ID))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createReadOK(Collections.<String>emptyList()));

		when(mockResourceOperationContext.getUserIdentifier())
				.thenReturn(USER_ID);

		ReadPurchaseListCommand command = createPurchaseListCommand();
		ExecutionResult<ResourceState<LinksEntity>> result = command.execute();

		assertTrue("Command failed", result.isSuccessful());
		ResourceState<LinksEntity> purchaseListRepresentation = result.getData();

		String selfUri = URIUtil.format(PURCHASES, SCOPE);
		Self expectedSelf = SelfFactory.createSelf(selfUri);

		assertEquals("The self is incorrect", expectedSelf, purchaseListRepresentation.getSelf());
		assertThat("There should be no links", purchaseListRepresentation.getLinks(), Matchers.empty());
		assertResourceInfo(purchaseListRepresentation.getResourceInfo())
			.maxAge(PurchaseResourceConstants.MAX_AGE);
	}

	/**
	 * Tests that the command fails upon lookup failure.
	 */
	@Test
	public void testReadPurchaseListFailure() {
		when(mockPurchaseLookup.findPurchaseIds(SCOPE, USER_ID))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createNotFound("Lookup failure"));

		when(mockResourceOperationContext.getUserIdentifier())
				.thenReturn(USER_ID);

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		ReadPurchaseListCommand command = createPurchaseListCommand();
		command.execute();
	}

	private ReadPurchaseListCommand createPurchaseListCommand() {
		ReadPurchaseListCommandImpl readPurchaseListCommand = new ReadPurchaseListCommandImpl(PURCHASES,
				mockResourceOperationContext, mockPurchaseLookup);
		ReadPurchaseListCommand.Builder builder = new ReadPurchaseListCommandImpl.BuilderImpl(readPurchaseListCommand);

		builder.setScope(SCOPE);

		return builder.build();
	}
}
