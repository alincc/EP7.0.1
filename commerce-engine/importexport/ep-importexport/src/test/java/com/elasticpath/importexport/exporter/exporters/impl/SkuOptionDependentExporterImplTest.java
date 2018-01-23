/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.exporters.DependentExporterFilter;
import com.elasticpath.service.catalog.SkuOptionService;

/**
 * Test for {@link SkuOptionDependentExporterImpl}.
 */
@RunWith(JMock.class)
@SuppressWarnings({ "PMD.NonStaticInitializer", "PMD.TooManyStaticImports" })
public class SkuOptionDependentExporterImplTest {
	private final SkuOptionDependentExporterImpl skuOptionExporter = new SkuOptionDependentExporterImpl();
	private SkuOptionService skuOptionService;
	private DependentExporterFilter dependentExporterFilter;
	private ExportContext exportContext;
	private final Mockery context = new JUnit4Mockery();
	private static final long CATALOG_UID = 14441;

	/**
	 * Test initialization.
	 * 
	 * @throws ConfigurationException in case of errors
	 */
	@Before
	public void setUp() throws ConfigurationException {
		skuOptionService = context.mock(SkuOptionService.class);
		skuOptionExporter.setSkuOptionService(skuOptionService);

		dependentExporterFilter = context.mock(DependentExporterFilter.class);
		exportContext = new ExportContext(new ExportConfiguration(), new SearchConfiguration());
		skuOptionExporter.initialize(exportContext, dependentExporterFilter);
	}

	/** Tests finding dependent objects when the dependent object should be filtered. */
	@Test
	public void testFindDependentObjectsFiltered() {
		SkuOption skuOption1 = context.mock(SkuOption.class, "skuOption-1");
		SkuOption skuOption2 = context.mock(SkuOption.class, "skuOption-2");
		final List<SkuOption> skuOptionList = Arrays.asList(skuOption1, skuOption2);
		context.checking(new Expectations() {
			{
				one(dependentExporterFilter).isFiltered(CATALOG_UID);
				will(returnValue(true));

				one(skuOptionService).findAllSkuOptionFromCatalog(CATALOG_UID);
				will(returnValue(skuOptionList));
			}
		});

		assertEquals(skuOptionList, skuOptionExporter.findDependentObjects(CATALOG_UID));
	}

	/** Tests finding dependent objects when the dependent object should be filtered. */
	@Test
	public void testFindDependentObjectsNotFiltered() {
		DependencyRegistry registry = new DependencyRegistry(Arrays.<Class<?>> asList(SkuOption.class));
		exportContext.setDependencyRegistry(registry);

		final long skuOption1Uid = 9;
		final long skuOption2Uid = 4541;
		final long skuOption3Uid = 4;
		registry.addUidDependency(SkuOption.class, skuOption1Uid);
		registry.addUidDependencies(SkuOption.class, new HashSet<>(Arrays.asList(skuOption2Uid, skuOption3Uid)));

		final SkuOption skuOption1 = context.mock(SkuOption.class, "skuOption-1");
		final SkuOption skuOption2 = context.mock(SkuOption.class, "skuOption-2");
		final SkuOption skuOption3 = context.mock(SkuOption.class, "skuOption-3");
		context.checking(new Expectations() {
			{
				one(dependentExporterFilter).isFiltered(CATALOG_UID);
				will(returnValue(false));

				Catalog dependentCatalog = context.mock(Catalog.class, "dependentCatalog");
				Catalog otherCatalog = context.mock(Catalog.class, "otherCatalog");
				allowing(dependentCatalog).getUidPk();
				will(returnValue(CATALOG_UID));
				allowing(otherCatalog).getUidPk();
				will(returnValue(0L));

				allowing(skuOption1).getCatalog();
				will(returnValue(dependentCatalog));
				allowing(skuOption2).getCatalog();
				will(returnValue(otherCatalog));
				allowing(skuOption3).getCatalog();
				will(returnValue(dependentCatalog));

				one(skuOptionService).get(skuOption1Uid);
				will(returnValue(skuOption1));
				one(skuOptionService).get(skuOption2Uid);
				will(returnValue(skuOption2));
				one(skuOptionService).get(skuOption3Uid);
				will(returnValue(skuOption3));
			}
		});

		List<SkuOption> result = skuOptionExporter.findDependentObjects(CATALOG_UID);
		assertThat("Missing skuOption1", result, hasItem(skuOption1));
		assertThat("Missing skuOption3", result, hasItem(skuOption3));
		assertThat("skuOption2 is not a part of this catalog", result, not(hasItem(skuOption2)));
		assertEquals("Other skuOptions returned?", 2, result.size());
	}
}
