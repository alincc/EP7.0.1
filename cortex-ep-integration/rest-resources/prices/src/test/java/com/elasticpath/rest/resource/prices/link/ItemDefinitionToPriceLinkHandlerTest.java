/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.link;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Iterables;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionEntity;
import com.elasticpath.rest.definition.prices.PriceRangeEntity;
import com.elasticpath.rest.definition.prices.PricesMediaTypes;
import com.elasticpath.rest.resource.prices.ItemPriceLookup;
import com.elasticpath.rest.resource.prices.rel.PriceRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.PricesUriBuilder;
import com.elasticpath.rest.schema.uri.PricesUriBuilderFactory;

/**
 * Tests the {@link com.elasticpath.rest.resource.prices.link.ItemDefinitionToPriceLinkHandler}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemDefinitionToPriceLinkHandlerTest {
	private static final String ITEM_ID = "itemId";
	private static final String ITEM_DEFINITION_URI = "/itemDefinitionUri";
	private static final String SCOPE = "scope";
	private static final String ITEM_DEFINITION_PRICE_URI = "/itemDefinitionPriceUri";

	@Mock
	private ItemPriceLookup itemPriceLookup;
	@Mock
	private PricesUriBuilderFactory pricesUriBuilderFactory;
	@InjectMocks
	private ItemDefinitionToPriceLinkHandler linkHandler;

	@Mock
	private ResourceState<ItemDefinitionEntity> itemDefinition;
	@Mock
	private PriceRangeEntity priceRangeEntity;

	@Before
	public void setupCommonTestComponents() {
		PricesUriBuilder pricesUriBuilder = mock(PricesUriBuilder.class);
		given(pricesUriBuilderFactory.get()).willReturn(pricesUriBuilder);
		given(pricesUriBuilder.setSourceUri(ITEM_DEFINITION_URI)).willReturn(pricesUriBuilder);
		given(pricesUriBuilder.build()).willReturn(ITEM_DEFINITION_PRICE_URI);

		itemDefinition = ResourceState.Builder.create(ItemDefinitionEntity.builder()
																		.withItemId(ITEM_ID)
																		.build())
				.withSelf(SelfFactory.createSelf(ITEM_DEFINITION_URI))
				.withScope(SCOPE)
				.build();
	}

	@Test
	public void ensureItemDefinitionPriceRangeLinkIsReturnedForItemDefinition() {
		given(itemPriceLookup.getItemPriceRange(SCOPE, ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(priceRangeEntity));

		Iterable<ResourceLink> links = linkHandler.getLinks(itemDefinition);

		assertThat(links, hasItems(ResourceLinkFactory.create(ITEM_DEFINITION_PRICE_URI,
															PricesMediaTypes.PRICE_RANGE.id(),
															PriceRepresentationRels.FROM_PRICE_REL,
															PriceRepresentationRels.ITEM_DEFINITION_REV)));
	}

	@Test
	public void ensureNoLinksAreReturnedForItemDefinitionWithNoPriceRange() {
		given(itemPriceLookup.getItemPriceRange(SCOPE, ITEM_ID)).willReturn(ExecutionResultFactory.<PriceRangeEntity>createNotFound());

		assertTrue("No links should be returned for item definitions with no price range", Iterables.isEmpty(linkHandler.getLinks(itemDefinition)));
	}
}
