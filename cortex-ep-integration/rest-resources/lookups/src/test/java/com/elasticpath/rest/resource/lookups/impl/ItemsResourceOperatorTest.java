/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertOperationResult.assertOperationResult;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.lookups.CodeEntity;
import com.elasticpath.rest.definitions.validator.Validator;
import com.elasticpath.rest.resource.lookups.integration.ItemLookupLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.schema.uri.ItemLookupUriBuilder;
import com.elasticpath.rest.schema.uri.ItemLookupUriBuilderFactory;
import com.elasticpath.rest.schema.uri.ItemsUriBuilder;
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;

/**
 * Tests uri patterns in {@link ItemLookupResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemsResourceOperatorTest {

	private static final String RESOURCE_SERVER = "/lookups";
	private static final String SCOPE = "scope";
	private static final String SKU_CODE = "sku";
	private static final String ITEM_ID = "itemid";
	private static final String URI = "/blah/blah/blah";

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private Validator<CodeEntity> itemCodeFormValidator;
	@Mock
	private ItemsUriBuilderFactory itemsUriBuilderFactory;
	@Mock
	private ItemLookupUriBuilder itemLookupUriBuilder;
	@Mock
	private ItemLookupUriBuilderFactory itemLookupUriBuilderFactory;
	@Mock
	private ItemLookupLookupStrategy itemLookupLookupStrategy;
	@Mock
	private TransformRfoToResourceState<CodeEntity, CodeEntity, ItemEntity> codeEntityTransformer;
	@InjectMocks
	private ItemLookupResourceOperatorImpl resourceOperator;

	private static final ResourceOperation READ_OP = TestResourceOperationFactory.createRead(RESOURCE_SERVER);


	@Test
	public void testRootLookupsRead() {
		when(itemLookupUriBuilderFactory.get()).thenReturn(itemLookupUriBuilder);
		when(itemLookupUriBuilder.setScope(SCOPE)).thenReturn(itemLookupUriBuilder);
		when(itemLookupUriBuilder.setFormPart()).thenReturn(itemLookupUriBuilder);
		when(itemLookupUriBuilder.build()).thenReturn(URI);

		OperationResult result = resourceOperator.processReadLookups(SCOPE, READ_OP);

		assertOperationResult(result)
				.resourceStatus(ResourceStatus.READ_OK);
	}

	@Test
	public void testFormUriRead() {
		when(itemLookupUriBuilderFactory.get()).thenReturn(itemLookupUriBuilder);
		when(itemLookupUriBuilder.setScope(SCOPE)).thenReturn(itemLookupUriBuilder);
		when(itemLookupUriBuilder.setFormPart()).thenReturn(itemLookupUriBuilder);
		when(itemLookupUriBuilder.setItemsPart()).thenReturn(itemLookupUriBuilder);
		when(itemLookupUriBuilder.build()).thenReturn(URI);

		OperationResult result = resourceOperator.processItemLookupFormRead(SCOPE, READ_OP);

		assertOperationResult(result)
				.resourceStatus(ResourceStatus.READ_OK);
	}

	@Test
	public void testSearchUriCreateWhenSuccessful() {
		ResourceState representation = ResourceState.Builder.create(CodeEntity.builder().withCode(SKU_CODE).build()).build();
		ResourceOperation createOp = TestResourceOperationFactory.createCreate(RESOURCE_SERVER, representation);
		when(itemLookupLookupStrategy.getItemIdByCode(SKU_CODE)).thenReturn(ExecutionResultFactory.createReadOK(ITEM_ID));
		when(itemCodeFormValidator.validate(anyCodeEntity())).thenReturn(ExecutionResultFactory.<Void>createUpdateOK());
		arrangeItemUriBuilder();

		OperationResult result = resourceOperator.processItemCodeSearch(SCOPE, createOp);

		assertOperationResult(result)
				.resourceStatus(ResourceStatus.CREATE_OK);
	}

	@Test
	public void testSearchUriCreateWhenValidatorFails() {
		ResourceState representation = ResourceState.Builder.create(CodeEntity.builder().withCode(SKU_CODE).build()).build();
		ResourceOperation createOp = TestResourceOperationFactory.createCreate(RESOURCE_SERVER, representation);
		when(itemLookupLookupStrategy.getItemIdByCode(SKU_CODE)).thenReturn(ExecutionResultFactory.createReadOK(ITEM_ID));
		when(itemCodeFormValidator.validate(anyCodeEntity())).thenReturn(ExecutionResultFactory.<Void>createBadRequestBody("Bad"));

		thrown.expect(containsResourceStatus(ResourceStatus.BAD_REQUEST_BODY));
		arrangeItemUriBuilder();

		resourceOperator.processItemCodeSearch(SCOPE, createOp);
	}

	@Test
	public void testRfoUriRead() {
		ResourceState<ItemEntity> mockItem = arrangeItemEntity();
		CodeEntity mockCodeEntity = mock(CodeEntity.class);
		ResourceState<CodeEntity> expectedCode = ResourceState.Builder.create(mockCodeEntity).build();
		when(itemLookupLookupStrategy.getItemLookupByItem(ITEM_ID)).thenReturn(ExecutionResultFactory.createReadOK(mockCodeEntity));
		when(codeEntityTransformer.transform(mockCodeEntity, mockItem)).thenReturn(expectedCode);

		OperationResult result = resourceOperator.processReadCodeForItem(mockItem, READ_OP);

		assertOperationResult(result)
				.resourceState(expectedCode)
				.resourceStatus(ResourceStatus.READ_OK);
	}


	private ResourceState<ItemEntity> arrangeItemEntity() {
		Self mockSelf = mock(Self.class);
		when(mockSelf.getUri()).thenReturn(URI);
		ItemEntity mockEntity = mock(ItemEntity.class);
		when(mockEntity.getItemId()).thenReturn(ITEM_ID);
		return  ResourceState.Builder.create(mockEntity).withSelf(mockSelf).build();
	}


	private void arrangeItemUriBuilder() {
		ItemsUriBuilder mockUriBuilder = mock(ItemsUriBuilder.class);
		when(itemsUriBuilderFactory.get()).thenReturn(mockUriBuilder);
		when(mockUriBuilder.setScope(SCOPE)).thenReturn(mockUriBuilder);
		when(mockUriBuilder.setItemId(ITEM_ID)).thenReturn(mockUriBuilder);
		when(mockUriBuilder.build()).thenReturn(URI);
	}

	private CodeEntity anyCodeEntity() {
		return any();
	}
}
