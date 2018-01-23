/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.geographies;

import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.test.context.ContextConfiguration;

import com.elasticpath.rest.resource.geographies.integration.GeographiesLookupStrategy;
import com.elasticpath.rest.resource.wiring.AbstractResourceWiringTest;


@ContextConfiguration
@SuppressWarnings({ "PMD.UnusedPrivateField", "PMD.TestClassWithoutTestCases" })
public class GeographiesResourceWiringTest extends AbstractResourceWiringTest {

	@ReplaceWithMock(beanName = "geographiesLookupStrategy")
	private GeographiesLookupStrategy geographiesLookupStrategy;
}
