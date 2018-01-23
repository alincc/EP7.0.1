/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.navigations.integration.epcommerce.transform;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeMultiValueType;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.attribute.impl.AttributeGroupImpl;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.attribute.impl.CategoryAttributeValueImpl;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.base.DetailsEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.category.CategoryRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.AttributeValueTransformer;
import com.elasticpath.rest.resource.navigations.integration.dto.NavigationDto;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Test class for {@link CategoryTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CategoryTransformerTest {

	private static final String SHORT_TEXT_VALUE = "VALUE";
	private static final String KEY2 = "KEY2";
	private static final String NAME2 = "NAME2";
	private static final String KEY1 = "KEY1";
	private static final String NAME1 = "NAME1";
	private static final Locale LOCALE_EN = Locale.ENGLISH;
	private static final String CHILD_GUID = "CHILD_GUID";
	private static final String PARENT_GUID = "PARENT_GUID";
	private static final String CATEGORY_NAME = "CATEGORY_NAME";
	private static final String CATEGORY_DISPLAY_NAME = "CATEGORY_DISPLAY_NAME";
	private static final String STORE_CODE = "store";

	@Mock
	private AttributeValueTransformer mockAttributeValueTransformer;
	@Mock
	private CategoryType mockCategoryType;
	@Mock
	private AttributeValueGroup mockAttributeValueGroup;
	@Mock
	private CategoryRepository categoryRepository;

	private AttributeGroup attributeGroup;
	private List<Category> children;

	private CategoryTransformer categoryTransformer;


	/**
	 * Sets up common elements of test class.
	 */
	@Before
	public void setUp() {
		attributeGroup = new AttributeGroupImpl();

		TestCategoryImpl childCategory = createMockCategory(CHILD_GUID, null, null, null);
		children = Collections.<Category>singletonList(childCategory);

		when(mockCategoryType.getAttributeGroup())
				.thenReturn(attributeGroup);

		categoryTransformer = new CategoryTransformer(mockAttributeValueTransformer, categoryRepository);
	}

	/**
	 * Tests transform to entity.
	 */
	@Test
	public void testTransformToEntity() {
		AttributeValue attributeValue1 = createCategoryAttributeValue(NAME1, KEY1, false, AttributeType.BOOLEAN, true);
		AttributeValue attributeValue2 = createCategoryAttributeValue(NAME2, KEY2, true, AttributeType.SHORT_TEXT, SHORT_TEXT_VALUE);
		mockAttributeValueGroupWithAttributeValues(attributeValue1, attributeValue2);

		TestCategoryImpl category = createMockCategory(CATEGORY_NAME, PARENT_GUID, mockCategoryType, mockAttributeValueGroup);

		DetailsEntity booleanDetailsEntity = createDetailsEntity(KEY1, true, NAME1, "True");
		DetailsEntity textDetailsEntity = createDetailsEntity(KEY2, SHORT_TEXT_VALUE, NAME2, SHORT_TEXT_VALUE);

		shouldTransformToEntityWithResult(attributeValue1, booleanDetailsEntity);
		shouldTransformToEntityWithResult(attributeValue2, textDetailsEntity);

		Collection<DetailsEntity> detailsEntities = createDetailsEntities(booleanDetailsEntity, textDetailsEntity);

		NavigationDto expectedNavigationDto = createNavigationDto(PARENT_GUID, Collections.singleton(CHILD_GUID), detailsEntities);

		NavigationDto navigationDto = categoryTransformer.transformToEntity(category, children, LOCALE_EN, STORE_CODE);

		assertNavigationDtoEquals(expectedNavigationDto, navigationDto);
	}

	/**
	 * Test transform to entity with invalid attribute values.
	 */
	@Test
	public void testTransformToEntityWithInvalidAttributeValues() {
		AttributeValue attributeValue1 = createCategoryAttributeValue(NAME1, KEY1, false, AttributeType.BOOLEAN, null);
		AttributeValue attributeValue2 = createCategoryAttributeValue(NAME2, KEY2, true, AttributeType.SHORT_TEXT, null);
		mockAttributeValueGroupWithAttributeValues(attributeValue1, attributeValue2);

		TestCategoryImpl category = createMockCategory(CATEGORY_NAME, PARENT_GUID, mockCategoryType, mockAttributeValueGroup);

		shouldTransformToEntityWithResult(attributeValue1, null);
		shouldTransformToEntityWithResult(attributeValue2, null);

		NavigationDto expectedNavigationDto = createNavigationDto(PARENT_GUID,
				Collections.singleton(CHILD_GUID),
				Collections.<DetailsEntity>emptyList());

		NavigationDto navigationDto = categoryTransformer.transformToEntity(category, children, LOCALE_EN, STORE_CODE);

		assertNavigationDtoEquals(expectedNavigationDto, navigationDto);
	}

	/**
	 * Tests transform to entity with a null {@link AttributeValue} map on the category.
	 */
	@Test
	public void testTransformToEntityWithNoAttributeValues() {
		mockAttributeValueGroupWithAttributeValues();

		TestCategoryImpl category = createMockCategory(CATEGORY_NAME, PARENT_GUID, mockCategoryType, mockAttributeValueGroup);

		NavigationDto expectedNavigationDto = createNavigationDto(PARENT_GUID, Collections.singleton(CHILD_GUID), null);

		NavigationDto navigationDto = categoryTransformer.transformToEntity(category, children, LOCALE_EN, STORE_CODE);

		assertNavigationDtoEquals(expectedNavigationDto, navigationDto);
	}

	/**
	 * Tests transform to entity with no parent.
	 */
	@Test
	public void testTransformToEntityWithNoParent() {
		TestCategoryImpl category = createMockCategory(CATEGORY_NAME, null, mockCategoryType, mockAttributeValueGroup);

		NavigationDto expectedNavigationDto = createNavigationDto(null, Collections.singleton(CHILD_GUID), null);

		NavigationDto navigationDto = categoryTransformer.transformToEntity(category, children, LOCALE_EN, STORE_CODE);

		assertNavigationDtoEquals(expectedNavigationDto, navigationDto);
	}

	private void shouldTransformToEntityWithResult(final AttributeValue attributeValue, final DetailsEntity result) {
		when(mockAttributeValueTransformer.transformToEntity(attributeValue, LOCALE_EN))
				.thenReturn(result);
	}

	private void mockAttributeValueGroupWithAttributeValues(final AttributeValue... attributeValues) {

		List<AttributeValue> result = Arrays.asList(attributeValues);
		when(mockAttributeValueGroup.getAttributeValueMap())
				.thenReturn(null);
		when(mockAttributeValueGroup.getAttributeValues(attributeGroup, LOCALE_EN))
				.thenReturn(result);
	}

	private TestCategoryImpl createMockCategory(final String code,
			final String parentCategoryGuid,
			final CategoryType categoryType,
			final AttributeValueGroup attributeValueGroup) {

		TestCategoryImpl testCategory = new TestCategoryImpl();

		testCategory.setCode(code);
		testCategory.setParentGuid(parentCategoryGuid);
		testCategory.setCategoryType(categoryType);
		testCategory.setAttributeValueGroup(attributeValueGroup);
		when(categoryRepository.findParentCategoryCode(STORE_CODE, testCategory.getCode())).thenReturn(
				ExecutionResultFactory.createReadOK(parentCategoryGuid));

		return testCategory;
	}

	private NavigationDto createNavigationDto(final String parentGuid,
			final Collection<String> childIds,
			final Collection<DetailsEntity> detailsEntities) {
		NavigationDto dto = ResourceTypeFactory.createResourceEntity(NavigationDto.class);

		dto.setNavigationCorrelationId(CATEGORY_NAME)
				.setChildNavigationCorrelationIds(childIds)
				.setName(CATEGORY_NAME)
				.setDisplayName(CATEGORY_DISPLAY_NAME)
				.setAttributes(detailsEntities);

		if (parentGuid != null) {
			dto.setParentNavigationCorrelationId(parentGuid);
		}

		return dto;
	}

	private Collection<DetailsEntity> createDetailsEntities(final DetailsEntity... detailsEntities) {
		return Arrays.asList(detailsEntities);
	}


	private DetailsEntity createDetailsEntity(final String name, final Object value, final String displayName, final String displayValue) {

		return DetailsEntity.builder()
				.withName(name)
				.withValue(value)
				.withDisplayName(displayName)
				.withDisplayValue(displayValue)
				.build();
	}

	private AttributeValue createCategoryAttributeValue(final String attributeName,
			final String attributeKey,
			final boolean attributeMultiValueEnabled,
			final AttributeType attributeValueAttributeType,
			final Object attributeValueValue) {

		Attribute attribute = createAttribute(attributeName, attributeKey, attributeMultiValueEnabled);
		return createCategoryAttributeValue(attributeValueAttributeType, attributeValueValue, attribute);
	}

	private Attribute createAttribute(final String name, final String key, final boolean multiValueEnabled) {
		Attribute attribute = new AttributeImpl();

		attribute.setName(name);
		attribute.setKey(key);
		attribute.setMultiValueType(AttributeMultiValueType.createAttributeMultiValueType(String.valueOf(multiValueEnabled)));

		return attribute;
	}

	private AttributeValue createCategoryAttributeValue(final AttributeType attributeType, final Object expectedValue, final Attribute attribute) {
		final CategoryAttributeValueImpl attributeValue = new CategoryAttributeValueImpl();

		attributeValue.setAttribute(attribute);
		attributeValue.setAttributeType(attributeType);
		attributeValue.setValue(expectedValue);

		return attributeValue;
	}

	/**
	 * Test Category class that overrides complex methods for testing.
	 */
	class TestCategoryImpl extends CategoryImpl implements Category {

		private static final long serialVersionUID = 1L;

		private String categoryCode;

		@Override
		public void setCode(final String categoryCode) {
			this.categoryCode = categoryCode;
		}

		@Override
		public String getCode() {
			return categoryCode;
		}

		@Override
		public String getDisplayName(final Locale locale) {
			return CATEGORY_DISPLAY_NAME;
		}
	}

	private void assertNavigationDtoEquals(final NavigationDto expected, final NavigationDto actual) {
		assertEquals("The names should be the same.", expected.getName(), actual.getName());
		assertEquals("The display names should be the same.", expected.getDisplayName(), actual.getDisplayName());
		assertEquals("The parent IDs should be the same.", expected.getParentNavigationCorrelationId(), actual.getParentNavigationCorrelationId());
		assertEquals("There should be the same number of child IDs.",
				expected.getChildNavigationCorrelationIds().size(),
				actual.getChildNavigationCorrelationIds().size());
		assertEquals("The first child ID should be the same.",
				CollectionUtil.first(expected.getChildNavigationCorrelationIds()),
				CollectionUtil.first(actual.getChildNavigationCorrelationIds()));
		assertEquals("The navigation ID should be the same.", expected.getNavigationCorrelationId(), actual.getNavigationCorrelationId());

		if (expected.getAttributes() == null || actual.getAttributes() == null) {
			assertEquals("The attribute details entities should both be null.", expected.getAttributes(), actual.getAttributes());
		} else {
			assertEquals("There should be the same number of attribute details entities.",
					expected.getAttributes().size(), actual.getAttributes().size());
		}
	}
}
