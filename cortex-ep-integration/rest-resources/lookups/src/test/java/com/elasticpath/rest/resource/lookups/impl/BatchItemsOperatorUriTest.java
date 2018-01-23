/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.impl;

import static com.elasticpath.rest.TestResourceOperationFactory.createCreate;
import static com.elasticpath.rest.TestResourceOperationFactory.createRead;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.uri.URIUtil;

@RunWith(MockitoJUnitRunner.class)
public class BatchItemsOperatorUriTest extends AbstractUriTest {

	private static final String BATCH_ITEMS_URI = "/lookups/scope/batches/items";

	@Mock
	BatchItemsLookupResourceOperatorImpl classUnderTest;
	@Mock
	OperationResult operationResult;


	@Test
	public void testProcessReadBatchItemsForm() {
		when(classUnderTest.processReadBatchItemsForm(anyResourceOperation())).thenReturn(operationResult);
		String formUri = URIUtil.format(BATCH_ITEMS_URI, Form.URI_PART);
		ResourceOperation operation = createRead(formUri);

		dispatchMethod(operation, classUnderTest);

		verify(classUnderTest).processReadBatchItemsForm(anyResourceOperation());
	}

	@Test
	public void testProcessBatchItemsFormSubmission() {
		when(classUnderTest.processBatchItemsFormSubmission(anyResourceOperation())).thenReturn(operationResult);
		String formUri = URIUtil.format(BATCH_ITEMS_URI);
		ResourceOperation operation = createCreate(formUri, null);

		dispatchMethod(operation, classUnderTest);

		verify(classUnderTest).processBatchItemsFormSubmission(anyResourceOperation());
	}

	@Test
	public void testProcessReadBatchItems() {
		when(classUnderTest.processReadBatchItems(anyResourceOperation())).thenReturn(operationResult);
		String formUri = URIUtil.format(BATCH_ITEMS_URI, "anything=");
		ResourceOperation operation = createRead(formUri);

		dispatchMethod(operation, classUnderTest);

		verify(classUnderTest).processReadBatchItems(anyResourceOperation());
	}
}
