/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.slots.integration.epcommerce.impl;

import java.util.Collection;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.query.SlotsRepository;
import com.elasticpath.rest.resource.slots.integration.SlotLookupStrategy;
import com.elasticpath.rest.resource.slots.integration.dto.SlotDto;
import com.elasticpath.rest.resource.slots.integration.epcommerce.transform.DynamicContentSpaceTransformer;
import com.elasticpath.rest.resource.slots.integration.epcommerce.wrapper.DynamicContentSpace;

/**
 * Implementation of {@link SlotLookupStrategy} that uses EP core.
 */
@Singleton
@Named("slotLookupStrategy")
public class SlotLookupStrategyImpl implements SlotLookupStrategy {

	private final ResourceOperationContext resourceOperationContext;
	private final SlotsRepository slotsRepository;
	private final DynamicContentSpaceTransformer dynamicContentSpaceTransformer;

	/**
	 * The constructor.
	 *
	 * @param resourceOperationContext       the resource operation context.
	 * @param slotsRepository                the Query Respository
	 * @param dynamicContentSpaceTransformer The transformer.
	 */
	@Inject
	public SlotLookupStrategyImpl(
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("slotsRepository")
			final SlotsRepository slotsRepository,
			@Named("dynamicContentSpaceTransformer")
			final DynamicContentSpaceTransformer dynamicContentSpaceTransformer) {

		this.resourceOperationContext = resourceOperationContext;
		this.slotsRepository = slotsRepository;
		this.dynamicContentSpaceTransformer = dynamicContentSpaceTransformer;
	}

	@Override
	public ExecutionResult<Collection<String>> findAllSlotIds(final String storeCode) {
		return slotsRepository.findAllSlotIds();
	}

	@Override
	public ExecutionResult<SlotDto> getSlot(final String storeCode, final String contentSpaceGuid) {

		ContentSpace contentSpace = Assign.ifSuccessful(slotsRepository.getContentSpaceByGuid(contentSpaceGuid));
		DynamicContent dynamicContent = Assign.ifSuccessful(slotsRepository.getDynamicContent(contentSpaceGuid));
		DynamicContentSpace dynamicContentSpace = new DynamicContentSpace(contentSpace, dynamicContent);
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		SlotDto slotDto = dynamicContentSpaceTransformer.transformToEntity(dynamicContentSpace, locale);

		return ExecutionResultFactory.createReadOK(slotDto);
	}
}
