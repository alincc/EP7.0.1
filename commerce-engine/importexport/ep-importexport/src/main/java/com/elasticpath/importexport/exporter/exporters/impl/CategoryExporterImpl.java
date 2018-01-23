/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.category.CategoryDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.catalog.CategoryService;

/**
 * Exporter implementation for category object.
 */
@SuppressWarnings("PMD.GodClass")
public class CategoryExporterImpl extends AbstractExporterImpl<Category, CategoryDTO, Long> {

	private CategoryLookup categoryLookup;
	private CategoryService categoryService;

	private DomainAdapter<Category, CategoryDTO> categoryAdapter;

	private List<Long> categoryUidsFromSearchCriteria = Collections.emptyList();

	private ImportExportSearcher importExportSearcher;

	private List<Long> categoryUidsList = Collections.emptyList();

	private static final Logger LOG = Logger.getLogger(CategoryExporterImpl.class);

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		categoryUidsFromSearchCriteria = importExportSearcher.searchUids(context.getSearchConfiguration(), EPQueryType.CATEGORY);
		LOG.info("The UidPk list for " + categoryUidsFromSearchCriteria.size() + " categories is retrieved from database.");
	}

	@Override
	protected List<Category> findByIDs(final List<Long> subList) {
		List<Category> categoryList = new ArrayList<>();
		for (Long categoryUid : subList) {
			Category category = getCategoryLookup().findByUid(categoryUid);
			if (category == null) {
				LOG.error(new Message("IE-20700", categoryUid.toString()));
				continue;
			}

			if (category.isLinked()) {
				processLinkedCategory(category);
				continue;
			}

			categoryList.add(category);

		}
		return categoryList;
	}

	private void processLinkedCategory(final Category linkedCategory) {
		Category masterCategory = linkedCategory.getMasterCategory();
		if (!categoryUidsList.contains(masterCategory.getUidPk())) {
			Set<Long> linkedAncestorUids = getCategoryService().findAncestorCategoryUidsWithTreeOrder(
					Collections.singleton(masterCategory.getUidPk()));

			for (Long ancestorUid : linkedAncestorUids) {
				if (!categoryUidsList.contains(ancestorUid)) {
					categoryUidsList.add(ancestorUid);
				}
			}

			categoryUidsList.add(masterCategory.getUidPk());
		}
	}

	@Override
	protected DomainAdapter<Category, CategoryDTO> getDomainAdapter() {
		return categoryAdapter;
	}

	@Override
	protected Class<? extends CategoryDTO> getDtoClass() {
		return CategoryDTO.class;
	}

	@Override
	public JobType getJobType() {
		return JobType.CATEGORY;
	}

	@Override
	protected List<Long> getListExportableIDs() {
		Set<Long> categoryUidsSet = new HashSet<>(getContext().getDependencyRegistry().getDependentUids(Category.class));
		categoryUidsSet.addAll(categoryUidsFromSearchCriteria);

		// find all parent category uids in necessary tree order
		Set<Long> resultSet = getCategoryService().findAncestorCategoryUidsWithTreeOrder(categoryUidsSet);

		// add other category uids for export
		resultSet.addAll(categoryUidsSet);

		categoryUidsList = new ArrayList<>(Arrays.asList(resultSet.toArray(new Long[resultSet.size()])));
		return categoryUidsList;
	}

	@Override
	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { Category.class };
	}

	@Override
	protected void addDependencies(final List<Category> objects, final DependencyRegistry dependencyRegistry) {

		if (dependencyRegistry.supportsDependency(Catalog.class)) {
			addCatalogsIntoRegistry(objects, dependencyRegistry);
		}

		if (dependencyRegistry.supportsDependency(CategoryType.class)) {
			addCategoryTypesIntoRegistry(objects, dependencyRegistry);
		}

		if (dependencyRegistry.supportsDependency(Attribute.class)) {
			addAttributesIntoRegistry(objects, dependencyRegistry);
		}
	}

	private void addAttributesIntoRegistry(final List<Category> categories, final DependencyRegistry dependencyRegistry) {
		final Set<Long> dependents = new HashSet<>();
		for (Category category : categories) {
			for (Entry<String, AttributeValue> entry : category.getAttributeValueMap().entrySet()) {
				dependents.add(entry.getValue().getAttribute().getUidPk());
			}
		}
		dependencyRegistry.addUidDependencies(Attribute.class, dependents);
	}

	private void addCategoryTypesIntoRegistry(final List<Category> categories, final DependencyRegistry dependencyRegistry) {
		final Set<Long> dependents = new HashSet<>();
		for (Category category : categories) {
			if (category.getCategoryType() != null) {
				dependents.add(category.getCategoryType().getUidPk());
			}
		}
		dependencyRegistry.addUidDependencies(CategoryType.class, dependents);
	}

	/*
	 * Puts directly influencing catalogs, as well as indirect (catalog containing exported category is virtual).
	 */
	private void addCatalogsIntoRegistry(final List<Category> objects, final DependencyRegistry dependencyRegistry) {
		final VirtualCatalogDependencyHelper virtualDependencyHelper = new VirtualCatalogDependencyHelper();
		final Set<Long> catalogSetUid = new HashSet<>();
		for (Category category : objects) {
			catalogSetUid.add(category.getCatalog().getUidPk());
			virtualDependencyHelper.addInfluencingCatalogs(category, dependencyRegistry);
		}
		dependencyRegistry.addUidDependencies(Catalog.class, catalogSetUid);
	}

	protected CategoryLookup getCategoryLookup() {
		return categoryLookup;
	}

	public void setCategoryLookup(final CategoryLookup categoryLookup) {
		this.categoryLookup = categoryLookup;
	}

	protected CategoryService getCategoryService() {
		return categoryService;
	}

	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	/**
	 * Sets the categoryAdapter.
	 *
	 * @param categoryAdapter the categoryAdapter to set
	 */
	public void setCategoryAdapter(final DomainAdapter<Category, CategoryDTO> categoryAdapter) {
		this.categoryAdapter = categoryAdapter;
	}

	/**
	 * Gets importExportSearcher.
	 *
	 * @return importExportSearcher
	 */
	public ImportExportSearcher getImportExportSearcher() {
		return importExportSearcher;
	}

	/**
	 * Sets importExportSearcher.
	 * @param importExportSearcher the ImportExportSearcher
	 */
	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}
}
