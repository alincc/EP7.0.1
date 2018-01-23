/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.transform;

import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.definition.lookups.CodeEntity;
import com.elasticpath.rest.resource.lookups.constant.LookupConstants;
import com.elasticpath.rest.resource.lookups.impl.ItemLookupUriBuilderImpl;
import com.elasticpath.rest.resource.lookups.rels.LookupResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.ItemLookupUriBuilderFactory;

/**
 * Tests {@link CodeEntityTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CodeEntityTransformerTest {

	private static final String RESOURCE_NAME = "asdasdad";
	private static final String ITEM_URI = "/asd/asd/asd";
	private static final String CODE = "blah";

	@Mock
	private ItemLookupUriBuilderFactory itemLookupUriBuilderFactory;

	@InjectMocks
	private CodeEntityTransformer codeEntityTransformer;

	@Mock
	private Self itemSelf;
	@Mock
	private ResourceState<ItemEntity> item;

	@Test
	public void testSelfWhenTransformCodeEntity() {
		when(itemLookupUriBuilderFactory.get()).thenReturn(new ItemLookupUriBuilderImpl(RESOURCE_NAME));
		when(itemSelf.getUri()).thenReturn(ITEM_URI);
		when(item.getSelf()).thenReturn(itemSelf);
		CodeEntity codeEntity = CodeEntity.builder().withCode(CODE).build();
		Self  expectedSelf = SelfFactory.createSelf(new ItemLookupUriBuilderImpl(RESOURCE_NAME).setSourceUri(ITEM_URI).build());

		ResourceState<CodeEntity> result = codeEntityTransformer.transform(codeEntity, item);

		assertResourceState(result)
			.self(expectedSelf);
		assertEquals(LookupConstants.TEN_MINUTES, result.getResourceInfo());
	}

	@Test
	public void testItemLinkWhenTransformCodeEntity() {
		when(itemLookupUriBuilderFactory.get()).thenReturn(new ItemLookupUriBuilderImpl(RESOURCE_NAME));
		when(itemSelf.getUri()).thenReturn(ITEM_URI);
		when(item.getSelf()).thenReturn(itemSelf);
		CodeEntity codeEntity = CodeEntity.builder().withCode(CODE).build();
		ResourceLink expectedItemLink = ResourceLinkFactory.create(
				ITEM_URI,
				ItemsMediaTypes.ITEM.id(),
				LookupResourceRels.ITEM_REL,
				LookupResourceRels.CODE_REL);

		ResourceState<CodeEntity> result = codeEntityTransformer.transform(codeEntity, item);

		assertResourceState(result)
			.containsLinks(expectedItemLink);
	}
}
