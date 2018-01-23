/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.slots.integration.epcommerce.transform;


import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.ValueTypeEnum;
import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.contentspace.Parameter;
import com.elasticpath.domain.contentspace.ParameterValue;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.resource.slots.integration.dto.SlotDto;
import com.elasticpath.rest.resource.slots.integration.dto.SlotParameterDto;
import com.elasticpath.rest.resource.slots.integration.epcommerce.wrapper.DynamicContentSpace;

/**
 * Tests {@link DynamicContentSpaceTransformer}.
 */
public class DynamicContentSpaceTransformerTest {

	private static final String IMAGE_NAME = "image.jpg";
	private static final String IMAGE_PARAMETER_ID = "Parameter id";
	private static final String CONTENT_WRAPPER_ID = "content wrapper id";
	private static final String TARGET_ID = "target id";
	private static final String CONTENT_SPACE_GUID = "contentSpaceGuid";

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final DynamicContentSpaceTransformer transformer = new DynamicContentSpaceTransformer();

	/**
	 * Test transform to domain.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testTransformToDomain() {
		transformer.transformToDomain(null);
	}

	/**
	 * Tests transform to entity.
	 */
	@Test
	public void testTransformToEntity() {

		final ContentSpace mockContentSpace = context.mock(ContentSpace.class);
		final DynamicContent mockDynamicContent = context.mock(DynamicContent.class);

		context.checking(new Expectations() {
			{
				allowing(mockContentSpace).getTargetId();
				will(returnValue(TARGET_ID));

				allowing(mockContentSpace).getGuid();
				will(returnValue(CONTENT_SPACE_GUID));

				allowing(mockDynamicContent).getContentWrapperId();
				will(returnValue(CONTENT_WRAPPER_ID));

				allowing(mockDynamicContent).getParameterValues();
				will(returnValue(Collections.emptyList()));
			}
		});

		DynamicContentSpace dynamicContentSpace = new DynamicContentSpace(mockContentSpace, mockDynamicContent);
		final SlotDto slotDtoResult = transformer.transformToEntity(dynamicContentSpace);
		assertEquals("Target ID does not match expected value.", TARGET_ID, slotDtoResult.getTargetId());
		assertEquals("Slot type does not match expected value.", CONTENT_WRAPPER_ID, slotDtoResult.getType());
		assertEquals("Number of parameters is not zero.", 0, slotDtoResult.getParameters().size());
	}


	/**
	 * Tests transform to entity.
	 */
	@Test
	public void testTransformToEntityWithParameterValues() {

		final ContentSpace mockContentSpace = context.mock(ContentSpace.class);
		final DynamicContent mockDynamicContent = context.mock(DynamicContent.class);
		final List<ParameterValue> listOfParameterValues = new ArrayList<>();

		listOfParameterValues.add(createParameterWithValue(ValueTypeEnum.Image, IMAGE_PARAMETER_ID, IMAGE_NAME));

		context.checking(new Expectations() {
			{
				allowing(mockContentSpace).getTargetId();
				will(returnValue(TARGET_ID));

				allowing(mockContentSpace).getGuid();
				will(returnValue(CONTENT_SPACE_GUID));

				allowing(mockDynamicContent).getContentWrapperId();
				will(returnValue(CONTENT_WRAPPER_ID));

				allowing(mockDynamicContent).getParameterValues();
				will(returnValue(listOfParameterValues));
			}
		});

		DynamicContentSpace dynamicContentSpace = new DynamicContentSpace(mockContentSpace, mockDynamicContent);
		final SlotDto slotDtoResult = transformer.transformToEntity(dynamicContentSpace, Locale.ENGLISH);
		assertEquals(TARGET_ID, slotDtoResult.getTargetId());

		assertEquals("There should be exactly 1 parameter.", 1, slotDtoResult.getParameters().size());
		SlotParameterDto slotParameterDto = slotDtoResult.getParameters().get(IMAGE_PARAMETER_ID);
		assertEquals("Slot type does not match expected value.", ValueTypeEnum.Image.name(), slotParameterDto.getType());
		assertEquals("Slot value does not match expected value.", IMAGE_NAME, slotParameterDto.getValue());
	}

	private ParameterValue createParameterWithValue(final ValueTypeEnum type, final String parameterId, final String value) {
		final ParameterValue parameterValue = context.mock(ParameterValue.class);
		final Parameter parameter = context.mock(Parameter.class);

		context.checking(new Expectations() {
			{
				allowing(parameterValue).getParameter();
				will(returnValue(parameter));

				allowing(parameter).getParameterId();
				will(returnValue(parameterId));

				allowing(parameter).getType();
				will(returnValue(type));

				allowing(parameterValue).getValue(Locale.ENGLISH.getLanguage());
				will(returnValue(value));
			}
		});
		return parameterValue;
	}
}
