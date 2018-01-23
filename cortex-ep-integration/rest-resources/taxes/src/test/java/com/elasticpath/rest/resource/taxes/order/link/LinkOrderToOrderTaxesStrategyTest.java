/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.order.link;

import static com.elasticpath.rest.definition.taxes.TaxesMediaTypes.TAXES;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.taxes.order.rel.OrderTaxesResourceRels;
import com.elasticpath.rest.resource.taxes.rel.TaxesResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.TaxesUriBuilder;
import com.elasticpath.rest.schema.uri.TaxesUriBuilderFactory;
import com.elasticpath.rest.schema.uri.TestUriBuilderFactory;
import com.elasticpath.rest.test.AssertResourceLink;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests for {@link LinkOrderToOrderTaxesStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public class LinkOrderToOrderTaxesStrategyTest {

	private static final String SOURCE_URI = "/test/source/uri=";
	private static final String TAXES_URI = URIUtil.format("taxes", SOURCE_URI);

	@Mock private TaxesUriBuilderFactory taxesUriBuilderFactory;
	@Mock private OrderEntity orderEntity;

	@InjectMocks private LinkOrderToOrderTaxesStrategy linkStrategy;

	private final TaxesUriBuilder taxesUriBuilder = TestUriBuilderFactory.mockUriBuilder(TaxesUriBuilder.class, TAXES_URI);
	private ResourceState<OrderEntity> orderRepresentation;

	@Test
	public void testGetLinks() {
		when(taxesUriBuilderFactory.get()).thenReturn(taxesUriBuilder);
		Self self = SelfFactory.createSelf(SOURCE_URI);
		orderRepresentation = ResourceState.Builder.create(orderEntity).withSelf(self).build();

		List<ResourceLink> links = Lists.newArrayList(linkStrategy.getLinks(orderRepresentation));

		assertEquals("Exactly 1 link should be created.", 1, links.size());
		AssertResourceLink.assertResourceLink(links.get(0))
				.type(TAXES.id())
				.rel(TaxesResourceRels.TAX_REL)
				.rev(OrderTaxesResourceRels.ORDER_REV)
				.uri(TAXES_URI);
	}

}
