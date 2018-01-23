/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.link;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.itemdefinitions.ItemdefinitionsMediaTypes;
import com.elasticpath.rest.definition.prices.PriceRangeEntity;
import com.elasticpath.rest.resource.prices.rel.PriceRepresentationRels;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.ItemDefinitionsUriBuilder;
import com.elasticpath.rest.schema.uri.ItemDefinitionsUriBuilderFactory;

/**
 * Tests the {@link com.elasticpath.rest.resource.prices.link.PriceToItemDefinitionLinkHandler}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PriceToItemDefinitionLinkHandlerTest {
	private static final String ITEM_ID = "itemId";
	private static final String SCOPE = "scope";
	private static final String ITEM_DEFINITION_URI = "/itemDefinitionUri";

	@Mock
	private ItemDefinitionsUriBuilderFactory itemDefinitionsUriBuilderFactory;
	@InjectMocks
	private PriceToItemDefinitionLinkHandler linkHandler;

	private ResourceState<PriceRangeEntity> priceRange;

	@Before
	public void setUpCommonTestComponents() {
		ItemDefinitionsUriBuilder itemDefinitionsUriBuilder = mock(ItemDefinitionsUriBuilder.class);
		given(itemDefinitionsUriBuilderFactory.get()).willReturn(itemDefinitionsUriBuilder);
		given(itemDefinitionsUriBuilder.setItemId(ITEM_ID)).willReturn(itemDefinitionsUriBuilder);
		given(itemDefinitionsUriBuilder.setScope(SCOPE)).willReturn(itemDefinitionsUriBuilder);
		given(itemDefinitionsUriBuilder.build()).willReturn(ITEM_DEFINITION_URI);

		priceRange = ResourceState.Builder.create(PriceRangeEntity.builder()
																		.withItemId(ITEM_ID)
																		.build())
											.withScope(SCOPE)
											.build();
	}

	@Test
	public void ensureItemLinkIsReturned() {
		assertThat(linkHandler.getLinks(priceRange), hasItems(ResourceLinkFactory.create(ITEM_DEFINITION_URI,
																						ItemdefinitionsMediaTypes.ITEM_DEFINITION.id(),
																						PriceRepresentationRels.ITEM_DEFINITION_REL,
																						PriceRepresentationRels.FROM_PRICE_REV)));
	}
}
