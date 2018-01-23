/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.wiring;

import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.test.context.ContextConfiguration;

import com.elasticpath.rest.resource.wiring.AbstractResourceWiringTest;
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;

/**
 * Tests lookups resource wiring.
 */
@ContextConfiguration
@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.TestClassWithoutTestCases"})
public class LookupsResourceWiringTest extends AbstractResourceWiringTest {

	@ReplaceWithMock(beanName = "itemsUriBuilderFactory")
	private ItemsUriBuilderFactory itemsUriBuilderFactory;

}

