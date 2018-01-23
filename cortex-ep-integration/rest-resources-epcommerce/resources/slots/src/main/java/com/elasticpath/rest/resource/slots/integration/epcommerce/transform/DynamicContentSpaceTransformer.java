/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.slots.integration.epcommerce.transform;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.contentspace.ParameterValue;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.resource.slots.integration.dto.SlotDto;
import com.elasticpath.rest.resource.slots.integration.dto.SlotParameterDto;
import com.elasticpath.rest.resource.slots.integration.epcommerce.wrapper.DynamicContentSpace;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transforms {@link DynamicContentSpace} into a {@link SlotDto}, and vice versa.
 */
@Singleton
@Named("dynamicContentSpaceTransformer")
public class DynamicContentSpaceTransformer extends AbstractDomainTransformer<DynamicContentSpace, SlotDto> {

	@Override
	public DynamicContentSpace transformToDomain(final SlotDto slotDto, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public SlotDto transformToEntity(final DynamicContentSpace dynamicContentSpace, final Locale locale) {
		ContentSpace contentSpace = dynamicContentSpace.getContentSpace();
		DynamicContent dynamicContent = dynamicContentSpace.getDynamicContent();

		SlotDto slotDto = ResourceTypeFactory.createResourceEntity(SlotDto.class);
		slotDto.setTargetId(contentSpace.getTargetId());
		slotDto.setCorrelationId(contentSpace.getGuid());

		if (dynamicContent != null) {
			slotDto.setType(dynamicContent.getContentWrapperId());

			List<ParameterValue> parameterValues = dynamicContent.getParameterValues();
			Map<String, SlotParameterDto> slotParameterMap = new HashMap<>(parameterValues.size());

			for (ParameterValue parameterValue : parameterValues) {
				SlotParameterDto slotParameterDto = ResourceTypeFactory.createResourceEntity(SlotParameterDto.class);
				slotParameterDto.setType(parameterValue.getParameter().getType().getName());
				slotParameterDto.setValue(parameterValue.getValue(locale.getLanguage()));

				slotParameterMap.put(parameterValue.getParameter().getParameterId(), slotParameterDto);
			}
			slotDto.setParameters(slotParameterMap);
		}

		return slotDto;
	}
}
