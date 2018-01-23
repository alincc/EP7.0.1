/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.navigations.integration.epcommerce.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.base.DetailsEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.category.CategoryRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.AttributeValueTransformer;
import com.elasticpath.rest.resource.navigations.integration.dto.NavigationDto;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Transforms a {@link Category} into a {@link NavigationDto}, and vice versa.
 */
@Singleton
@Named("categoryTransformer")
public class CategoryTransformer {

	private final AttributeValueTransformer attributeValueTransformer;
	private final CategoryRepository categoryRepository;

	/**
	 * Default constructor.
	 *
	 * @param attributeValueTransformer the attribute value transformer
	 * @param categoryRepository the category lookup
	 */
	@Inject
	public CategoryTransformer(
			@Named("attributeValueTransformer")
			final AttributeValueTransformer attributeValueTransformer,
			@Named("categoryRepository")
			final CategoryRepository categoryRepository) {

		this.attributeValueTransformer = attributeValueTransformer;
		this.categoryRepository = categoryRepository;
	}

	/**
	 * Transforms the given Category to a NavigationDto.
	 * Retrieves localized fields with the given Locale.
	 *
	 * @param category the category.
	 * @param children the child categories.
	 * @param locale the locale.
	 * @param storeCode the store code.
	 * @return the navigation dto.
	 */
	public NavigationDto transformToEntity(final Category category, final Collection<Category> children,
			final Locale locale, final String storeCode) {

		final NavigationDto navigationDto = ResourceTypeFactory.createResourceEntity(NavigationDto.class);

		ExecutionResult<String> parentCategoryLookupResult = categoryRepository.findParentCategoryCode(storeCode, category.getCode());
		if (parentCategoryLookupResult.isSuccessful()) {
			navigationDto.setParentNavigationCorrelationId(parentCategoryLookupResult.getData());
		}
		navigationDto.setNavigationCorrelationId(category.getCode());

		Collection<String> childIds = new ArrayList<>(children.size());

		for (Category child : children) {
			childIds.add(child.getCode());
		}
		AttributeGroup categoryAttributeGroup = category.getCategoryType().getAttributeGroup();
		AttributeValueGroup categoryAttributeValueGroup = category.getAttributeValueGroup();
		List<AttributeValue> attributeValues = categoryAttributeValueGroup.getAttributeValues(categoryAttributeGroup, locale);

		Collection<DetailsEntity> attributes = createDetailsEntities(attributeValues, locale);

		navigationDto.setChildNavigationCorrelationIds(childIds)
				.setName(category.getCode())
				.setDisplayName(category.getDisplayName(locale))
				.setAttributes(attributes);

		return navigationDto;
	}

	private Collection<DetailsEntity> createDetailsEntities(final Collection<AttributeValue> attributeValues, final Locale locale) {
		if (CollectionUtil.isEmpty(attributeValues)) {
			return null;
		}

		Collection<DetailsEntity> attributes = new ArrayList<>(attributeValues.size());

		for (AttributeValue attributeValue : attributeValues) {
			DetailsEntity detailsEntity = attributeValueTransformer.transformToEntity(attributeValue, locale);
			if (detailsEntity != null) {
				attributes.add(detailsEntity);
			}
		}

		return attributes;
	}
}
