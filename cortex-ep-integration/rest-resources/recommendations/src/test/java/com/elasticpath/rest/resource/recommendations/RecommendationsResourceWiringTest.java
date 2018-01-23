/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations;

import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.test.context.ContextConfiguration;

import com.elasticpath.rest.resource.pagination.transform.PaginatedLinksTransformer;
import com.elasticpath.rest.resource.recommendations.integration.ItemRecommendationsLookupStrategy;
import com.elasticpath.rest.resource.recommendations.integration.NavigationRecommendationsLookupStrategy;
import com.elasticpath.rest.resource.recommendations.integration.StoreRecommendationsLookupStrategy;
import com.elasticpath.rest.resource.wiring.AbstractResourceWiringTest;


@ContextConfiguration
@SuppressWarnings({ "PMD.UnusedPrivateField", "PMD.TestClassWithoutTestCases" })
public class RecommendationsResourceWiringTest extends AbstractResourceWiringTest {

	@ReplaceWithMock(beanName = "paginatedLinksTransformer")
	private PaginatedLinksTransformer paginatedLinksTransformer;

	@ReplaceWithMock(beanName = "storeRecommendationsLookupStrategy")
	private StoreRecommendationsLookupStrategy storeRecommendationsLookupStrategy;

	@ReplaceWithMock(beanName = "navigationRecommendationsLookupStrategy")
	private NavigationRecommendationsLookupStrategy navigationRecommendationsLookupStrategy;

	@ReplaceWithMock(beanName = "itemRecommendationsLookupStrategy")
	private ItemRecommendationsLookupStrategy itemRecommendationsLookupStrategy;
}
