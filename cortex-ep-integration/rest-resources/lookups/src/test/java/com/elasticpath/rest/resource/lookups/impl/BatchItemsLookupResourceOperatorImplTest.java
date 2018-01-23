/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.definition.lookups.BatchItemsActionIdentifier;
import com.elasticpath.rest.definition.lookups.BatchItemsFormIdentifier;
import com.elasticpath.rest.definition.lookups.BatchItemsIdentifier;
import com.elasticpath.rest.definition.lookups.CodesEntity;
import com.elasticpath.rest.definition.lookups.LookupsIdentifier;
import com.elasticpath.rest.definitions.validator.Validator;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.transform.IdentifierTransformerProvider;
import com.elasticpath.rest.id.transform.ResourceIdentifierTransformer;
import com.elasticpath.rest.resource.lookups.constant.LookupConstants;
import com.elasticpath.rest.resource.lookups.integration.ItemLookupLookupStrategy;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.test.AssertResourceLink;
import com.elasticpath.rest.test.AssertSelf;

@RunWith(MockitoJUnitRunner.class)
public class BatchItemsLookupResourceOperatorImplTest {

	private static final String LOOKUPS_FORM_URI = "/lookups/form";
	private static final String ACTION_URI = "/lookups/form";
	private static final String BATCH_URI = "/lookups/batches/id";

	@Mock
	ItemLookupLookupStrategy itemLookupLookupStrategy;
	@Mock
	Validator<CodesEntity> itemCodesFormValidator;
	@Mock
	IdentifierTransformerProvider idTransformerProvider;

	@InjectMocks
	BatchItemsLookupResourceOperatorImpl classUnderTest;

	@Mock
	ResourceIdentifierTransformer<BatchItemsFormIdentifier> batchItemsFormTransformer;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	BatchItemsFormIdentifier batchItemsFormIdentifier;
	@Mock
	LookupsIdentifier lookupsIdentifier;
	@Mock
	ResourceIdentifierTransformer<BatchItemsActionIdentifier> batchItemsActionTransformer;
	@Mock
	BatchItemsActionIdentifier batchItemsActionIdentifier;

	@Mock
	IdentifierPart batchId;
	@Mock
	ResourceIdentifierTransformer<BatchItemsIdentifier> batchItemsTransformer;
	@Mock
	BatchItemsIdentifier batchItemsIdentifier;

	@Mock
	ItemIdentifier itemidentifier1;
	@Mock
	ItemIdentifier itemidentifier2;
	@Mock
	ItemIdentifier itemidentifier3;

	@Mock
	ResourceIdentifierTransformer<ItemIdentifier> itemTransformer;


	@Before
	public void setUp() {
		when(idTransformerProvider.forClass(BatchItemsFormIdentifier.class))
			.thenReturn(batchItemsFormTransformer);
		when(batchItemsFormTransformer.uriToIdentifier(LOOKUPS_FORM_URI))
			.thenReturn(batchItemsFormIdentifier);
		when(batchItemsFormIdentifier.getBatchItemsAction().getLookups())
			.thenReturn(lookupsIdentifier);

		when(idTransformerProvider.forClass(BatchItemsActionIdentifier.class))
			.thenReturn(batchItemsActionTransformer);
		when(batchItemsActionTransformer.identifierToUri(any(BatchItemsActionIdentifier.class)))
			.thenReturn(ACTION_URI);
		when(batchItemsActionTransformer.uriToIdentifier(ACTION_URI))
			.thenReturn(batchItemsActionIdentifier);
		when(batchItemsActionIdentifier.getLookups())
			.thenReturn(lookupsIdentifier);

		when(idTransformerProvider.forClass(BatchItemsIdentifier.class))
			.thenReturn(batchItemsTransformer);
		when(batchItemsTransformer.identifierToUri(any(BatchItemsIdentifier.class)))
			.thenReturn(BATCH_URI);
		when(batchItemsTransformer.uriToIdentifier(BATCH_URI))
			.thenReturn(batchItemsIdentifier);

		when(idTransformerProvider.forClass(ItemIdentifier.class))
			.thenReturn(itemTransformer);
		when(itemTransformer.identifierToUri(any(ItemIdentifier.class)))
			.thenReturn("/items/1", "/items/2", "/items/3");
	}

	@Test
	public void testProcessReadBatchItemsForm() throws Exception {
		ResourceOperation operation = TestResourceOperationFactory.createRead(LOOKUPS_FORM_URI);

		OperationResult actual = classUnderTest.processReadBatchItemsForm(operation);

		assertTrue(actual.isSuccessful());
		ResourceState<CodesEntity> actualState = (ResourceState<CodesEntity>) actual.getResourceState();
		AssertSelf.assertSelf(actualState.getSelf())
			.uri(LOOKUPS_FORM_URI);
		assertEquals(LookupConstants.TEN_MINUTES, actualState.getResourceInfo());
		CodesEntity actualEntity = actualState.getEntity();
		assertThat(actualEntity.getCodes(), Matchers.contains(""));
	}

	@Test
	public void testProcessBatchItemsFormSubmission() throws Exception {
		when(itemCodesFormValidator.validate(any(CodesEntity.class)))
			.thenReturn(ExecutionResultFactory.<Void>createUpdateOK());
		when(itemLookupLookupStrategy.getBatchIdForCodes(any(Iterable.class)))
			.thenReturn(batchId);

		ResourceState<CodesEntity> formData = ResourceState.Builder
				.create(CodesEntity.builder()
						.addingCodes("1", "2", "3")
						.build())
				.build();
		ResourceOperation operation = TestResourceOperationFactory.createCreate(ACTION_URI, formData);

		OperationResult actual = classUnderTest.processBatchItemsFormSubmission(operation);

		assertTrue(actual.isSuccessful());
		ResourceState<?> actualState =  actual.getResourceState();
		AssertSelf.assertSelf(actualState.getSelf())
			.uri(BATCH_URI);
	}

	@Test
	@SuppressWarnings("checkstyle:magicnumber")
	public void testProcessReadBatchItems() throws Exception {
		Collection<ItemIdentifier> identifierIterable = Arrays.asList(itemidentifier1, itemidentifier2, itemidentifier3);
		when(itemLookupLookupStrategy.getItemIdsForBatchId(any(BatchItemsIdentifier.class)))
			.thenReturn(identifierIterable);

		ResourceOperation operation = TestResourceOperationFactory.createRead(BATCH_URI);

		OperationResult actual = classUnderTest.processReadBatchItems(operation);

		assertTrue(actual.isSuccessful());
		ResourceState<?> actualState = actual.getResourceState();
		AssertSelf.assertSelf(actualState.getSelf())
			.uri(BATCH_URI);
		List<ResourceLink> actualLinks = actualState.getLinks();
		assertThat(actualLinks, Matchers.hasSize(3));
		int pos = 1;
		for (ResourceLink actualLink : actualLinks) {
			AssertResourceLink.assertResourceLink(actualLink)
				.rel("element")
				.type(ItemsMediaTypes.ITEM.id())
				.uri("/items/" + pos++);
		}
	}
}