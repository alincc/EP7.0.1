/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.slots.transform.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import org.hamcrest.Matchers;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.definition.slots.SlotEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.slots.ItemIdLookup;
import com.elasticpath.rest.resource.slots.integration.dto.SlotDto;
import com.elasticpath.rest.resource.slots.integration.dto.SlotParameterDto;
import com.elasticpath.rest.resource.slots.rel.SlotsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.ItemsUriBuilder;
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Tests for {@link ProductCodeParameterHandlerImpl}.
 */
public final class ProductCodeParameterHandlerImplTest {

	private static final String SCOPE = "scope";
	private static final String TEST_PRODUCT_ID = "TEST";
	private static final String TEST_ITEM_ID = "Test_item_id";

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	@Mock
	private ItemsUriBuilderFactory mockItemsUriBuilderFactory;
	@Mock
	private ItemIdLookup mockItemIdLookup;
	@Mock
	private ItemsUriBuilder mockItemsUriBuilder;


	/**
	 * Tests the handle method.
	 */
	@Test
	public void testHandle() {
		ProductCodeParameterHandlerImpl handler = new ProductCodeParameterHandlerImpl(mockItemsUriBuilderFactory, mockItemIdLookup);
		SlotEntity.Builder entityBuilder = SlotEntity.builder();
		Collection<ResourceLink> linkAccumulator = new ArrayList<>();
		SlotDto dto = ResourceTypeFactory.createResourceEntity(SlotDto.class);
		SlotParameterDto value = ResourceTypeFactory.createResourceEntity(SlotParameterDto.class);
		value.setValue(TEST_PRODUCT_ID);
		dto.setParameters(Collections.singletonMap("productCode", value));
		final String expectedEncodedProductId = Base32Util.encode(TEST_PRODUCT_ID);
		final String itemUri = URIUtil.format("item", TEST_ITEM_ID, SCOPE);
		context.checking(new Expectations() {
			{
				allowing(mockItemIdLookup).getDefaultItemIdForProduct(SCOPE, expectedEncodedProductId);
				will(returnValue(ExecutionResultFactory.createReadOK(TEST_ITEM_ID)));

				allowing(mockItemsUriBuilderFactory).get();
				will(returnValue(mockItemsUriBuilder));

				allowing(mockItemsUriBuilder).setItemId(TEST_ITEM_ID);
				will(returnValue(mockItemsUriBuilder));

				allowing(mockItemsUriBuilder).setScope(SCOPE);
				will(returnValue(mockItemsUriBuilder));

				allowing(mockItemsUriBuilder).build();
				will(returnValue(itemUri));
			}
		});

		handler.handle(SCOPE, entityBuilder, linkAccumulator, dto);
		ResourceState<SlotEntity> representation = ResourceState.Builder.create(entityBuilder.build())
				.withLinks(linkAccumulator)
				.build();

		Collection<ResourceLink> links = representation.getLinks();
		Assert.assertThat(links, Matchers.hasSize(1));
		ResourceLink itemLink = CollectionUtil.first(links);
		ResourceLink expectedItemLink = ResourceLinkFactory.createNoRev(itemUri, ItemsMediaTypes.ITEM.id(),
				SlotsResourceRels.ON_EVENT_REL);
		assertEquals(expectedItemLink, itemLink);
	}
}
