/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.query.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.collect.ImmutableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.query.SlotsRepository;
import com.elasticpath.service.contentspace.DynamicContentResolutionException;
import com.elasticpath.service.contentspace.DynamicContentRuntimeService;
import com.elasticpath.service.query.CriteriaBuilder;
import com.elasticpath.service.query.QueryResult;
import com.elasticpath.service.query.QueryService;
import com.elasticpath.service.query.ResultType;
import com.elasticpath.service.query.relations.ContentSpaceRelation;
import com.elasticpath.tags.TagSet;

/**
 * Implementation of a repository for accessing slots related services.
 */
@Singleton
@Named("slotsRepository")
public class SlotsRepositoryImpl implements SlotsRepository {

	private static final Logger LOG = LoggerFactory.getLogger(SlotsRepositoryImpl.class);
	private final CustomerSessionRepository customerSessionRepository;
	private final QueryService<ContentSpace> contentSpaceQueryService;
	private final DynamicContentRuntimeService dynamicContentRuntimeService;

	/**
	 * Constructor of class.
	 *
	 * @param customerSessionRepository the customer session repo
	 * @param contentSpaceQueryService ContentSpace query service
	 * @param dynamicContentRuntimeService DynamicContent runtime service
	 */
	@Inject
	public SlotsRepositoryImpl(
			@Named("customerSessionRepository") final CustomerSessionRepository customerSessionRepository,
			@Named("contentSpaceQueryService") final QueryService<ContentSpace> contentSpaceQueryService,
			@Named("dynamicContentRuntimeService") final DynamicContentRuntimeService dynamicContentRuntimeService) {
		this.customerSessionRepository = customerSessionRepository;
		this.contentSpaceQueryService = contentSpaceQueryService;
		this.dynamicContentRuntimeService = dynamicContentRuntimeService;
	}

	@Override
	@CacheResult
	public ExecutionResult<Collection<String>> findAllSlotIds() {
		QueryResult<String> contentSpaceResult;
		try {
			contentSpaceResult = contentSpaceQueryService.query(
					CriteriaBuilder.criteriaFor(ContentSpace.class)
							.returning(ResultType.GUID));
		} catch (RuntimeException runtimeException) {
			LOG.error("Could not find all slot ids", runtimeException);
			return ExecutionResultFactory.createServerError("Server error when finding all slot ids");
		}
		Collection<String> contentSpaceGuids = ImmutableList.copyOf(contentSpaceResult.getResults());
		return ExecutionResultFactory.createReadOK(contentSpaceGuids);
	}

	@Override
	@CacheResult
	public ExecutionResult<ContentSpace> getContentSpaceByGuid(final String contentSpaceGuid) {
		return new ExecutionResultChain() {
			@Override
			public ExecutionResult<?> build() {
				QueryResult<ContentSpace> contentSpaceResult;

				try {
					contentSpaceResult = contentSpaceQueryService.query(
							CriteriaBuilder.criteriaFor(ContentSpace.class)
									.with(ContentSpaceRelation.having().guids(contentSpaceGuid))
									.returning(ResultType.ENTITY));
				} catch (RuntimeException runtimeException) {
					final String errorMessage = String.format("Get contente space by GUID %s failed", contentSpaceGuid);
					LOG.error(errorMessage, runtimeException);
					return ExecutionResultFactory.createServerError(String.format("Server error: %s", errorMessage));
				}
				ContentSpace contentSpace = Assign.ifNotNull(contentSpaceResult.getSingleResult(),
						OnFailure.returnNotFound("Slot not found."));
				return ExecutionResultFactory.createReadOK(contentSpace);
			}
		}.execute();
	}

	@Override
	@CacheResult
	public ExecutionResult<DynamicContent> getDynamicContent(final String contentSpaceGuid) {
		return new ExecutionResultChain() {
			@Override
			public ExecutionResult<?> build() {
				ContentSpace contentSpace = Assign.ifSuccessful(getContentSpaceByGuid(contentSpaceGuid));
				//only need CustomerSession for TagSet.
				CustomerSession customerSession = Assign.ifSuccessful(customerSessionRepository.findOrCreateCustomerSession());
				DynamicContent dynamicContent = Assign.ifSuccessful(
						resolveDynamicContent(contentSpace.getTargetId(), customerSession.getCustomerTagSet()));
				return ExecutionResultFactory.createReadOK(dynamicContent);
			}
		}.execute();
	}

	private ExecutionResult<DynamicContent> resolveDynamicContent(final String targetId, final TagSet tagSet) {
		ExecutionResult<DynamicContent> result;
		try {
			DynamicContent dynamicContent = dynamicContentRuntimeService.resolve(tagSet, targetId);
			result = ExecutionResultFactory.createReadOK(dynamicContent);
		} catch (DynamicContentResolutionException exception) {
			LOG.debug(String.format("Could not resolve DynamicContent for targetID: %s", targetId));
			result = ExecutionResultFactory.createReadOK(null);
		}
		return result;
	}



}
