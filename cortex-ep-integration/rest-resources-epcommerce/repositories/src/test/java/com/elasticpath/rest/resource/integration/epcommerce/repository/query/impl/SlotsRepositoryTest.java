/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.query.impl;


import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;
import com.elasticpath.service.contentspace.DynamicContentResolutionException;
import com.elasticpath.service.contentspace.DynamicContentRuntimeService;
import com.elasticpath.service.query.CriteriaBuilder;
import com.elasticpath.service.query.QueryResult;
import com.elasticpath.service.query.QueryService;
import com.elasticpath.service.query.ResultType;
import com.elasticpath.service.query.relations.ContentSpaceRelation;
import com.elasticpath.tags.TagSet;

/**
 * Test class for {@link SlotsRepository}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SlotsRepositoryTest {

	private static final String TEST_CONTENT_SPACE_GUID = "test";
	private static final String TEST_TARGET_ID = "target_id";

	@Mock
	private CustomerSessionRepository customerSessionRepository;

	@Mock
	private QueryService<ContentSpace> contentSpaceQueryService;

	@Mock
	private DynamicContentRuntimeService dynamicContentRuntimeService;

	@InjectMocks
	private SlotsRepositoryImpl slotsRepository;

	@Test
	public void shouldFindAllSlotIds() {
		QueryResult queryResult = mock(QueryResult.class);
		when(queryResult.getResults()).thenReturn(Arrays.asList("slotid1", "slotid2"));
		when(contentSpaceQueryService.
						query(
								CriteriaBuilder.
										criteriaFor(ContentSpace.class).
										returning(ResultType.GUID)
						)
		).thenReturn(queryResult);

		ExecutionResult<Collection<String>> allSlotIds = slotsRepository.findAllSlotIds();

		assertExecutionResult(allSlotIds)
				.isSuccessful()
				.data(Arrays.asList("slotid1", "slotid2"));
	}

	@Test
	public void shouldFailFindAllSlotIdsOnRuntimeException() {
		doThrow(NullPointerException.class).
				when(contentSpaceQueryService).
				query(
						CriteriaBuilder.
								criteriaFor(ContentSpace.class).
								returning(ResultType.GUID)
				);


		ExecutionResult<Collection<String>> allSlotIds = slotsRepository.findAllSlotIds();

		assertExecutionResult(allSlotIds)
				.isFailure()
				.resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	@Test
	public void shouldGetContentSpaceByGuid() {
		QueryResult queryResult = mock(QueryResult.class);
		final ContentSpace contentSpace = mock(ContentSpace.class);
		when(queryResult.getSingleResult()).thenReturn(contentSpace);
		when(contentSpaceQueryService.
						query(

								CriteriaBuilder.criteriaFor(ContentSpace.class)
										.with(ContentSpaceRelation.having().guids(TEST_CONTENT_SPACE_GUID))
										.returning(ResultType.ENTITY)
						)
		).thenReturn(queryResult);

		ExecutionResult<ContentSpace> contentSpaceByGuid = slotsRepository.getContentSpaceByGuid(TEST_CONTENT_SPACE_GUID);

		assertExecutionResult(contentSpaceByGuid)
				.isSuccessful()
				.data(contentSpace);
	}

	@Test
	public void shouldFailGetContentSpaceByGuidOnNoResultFound() {
		QueryResult queryResult = mock(QueryResult.class);
		when(queryResult.getSingleResult()).thenReturn(null);
		when(contentSpaceQueryService.
						query(

								CriteriaBuilder.criteriaFor(ContentSpace.class)
										.with(ContentSpaceRelation.having().guids(TEST_CONTENT_SPACE_GUID))
										.returning(ResultType.ENTITY)
						)
		).thenReturn(queryResult);

		ExecutionResult<ContentSpace> contentSpaceByGuid = slotsRepository.getContentSpaceByGuid(TEST_CONTENT_SPACE_GUID);

		assertExecutionResult(contentSpaceByGuid)
				.isFailure();
	}

	@Test
	public void shouldFailGetContentSpaceByGuidOnRuntimeException() {
		doThrow(NullPointerException.class).
				when(contentSpaceQueryService).
				query(

						CriteriaBuilder.criteriaFor(ContentSpace.class)
								.with(ContentSpaceRelation.having().guids(TEST_CONTENT_SPACE_GUID))
								.returning(ResultType.ENTITY)
				);

		ExecutionResult<ContentSpace> contentSpaceByGuid = slotsRepository.getContentSpaceByGuid(TEST_CONTENT_SPACE_GUID);

		assertExecutionResult(contentSpaceByGuid)
				.isFailure()
				.resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	@Test
	public void shouldGetDynamicContentByGuid() throws Exception {
		QueryResult queryResult = mock(QueryResult.class);
		final ContentSpace contentSpace = mock(ContentSpace.class);
		when(contentSpace.getTargetId()).thenReturn(TEST_TARGET_ID);
		when(queryResult.getSingleResult()).thenReturn(contentSpace);
		when(contentSpaceQueryService.
						query(

								CriteriaBuilder.criteriaFor(ContentSpace.class)
										.with(ContentSpaceRelation.having().guids(TEST_CONTENT_SPACE_GUID))
										.returning(ResultType.ENTITY)
						)
		).thenReturn(queryResult);

		CustomerSession customerSession = mock(CustomerSession.class);
		TagSet tagSet = mock(TagSet.class);
		when(customerSession.getCustomerTagSet()).thenReturn(tagSet);
		when(customerSessionRepository.findOrCreateCustomerSession()).thenReturn(
				ExecutionResultFactory.createReadOK(
						customerSession
				)
		);

		DynamicContent mockDynamicContent = mock(DynamicContent.class);
		when(dynamicContentRuntimeService.resolve(tagSet, TEST_TARGET_ID)).thenReturn(
				mockDynamicContent
		);


		ExecutionResult<DynamicContent> dynamicContent = slotsRepository.getDynamicContent(TEST_CONTENT_SPACE_GUID);


		assertExecutionResult(dynamicContent)
				.isSuccessful()
				.data(mockDynamicContent);
	}

	@Test
	public void shouldFailGetDynamicContentByGuidOnConentSpaceQueryServiceThrowsException() {
		doThrow(RuntimeException.class).when(contentSpaceQueryService).
				query(

						CriteriaBuilder.criteriaFor(ContentSpace.class)
								.with(ContentSpaceRelation.having().guids(TEST_CONTENT_SPACE_GUID))
								.returning(ResultType.ENTITY)
				);

		ExecutionResult<DynamicContent> dynamicContent = slotsRepository.getDynamicContent(TEST_CONTENT_SPACE_GUID);


		assertExecutionResult(dynamicContent)
				.isFailure();
	}

	@Test
	public void shouldFailGetDynamicContentByGuidOnCustomerSessionRepositoryFailure() {
		QueryResult queryResult = mock(QueryResult.class);
		final ContentSpace contentSpace = mock(ContentSpace.class);
		when(contentSpace.getTargetId()).thenReturn(TEST_TARGET_ID);
		when(queryResult.getSingleResult()).thenReturn(contentSpace);
		when(contentSpaceQueryService.
						query(

								CriteriaBuilder.criteriaFor(ContentSpace.class)
										.with(ContentSpaceRelation.having().guids(TEST_CONTENT_SPACE_GUID))
										.returning(ResultType.ENTITY)
						)
		).thenReturn(queryResult);

		when(customerSessionRepository.findOrCreateCustomerSession()).thenReturn(
				ExecutionResultFactory.<CustomerSession>createServerError(
						null
				)
		);

		ExecutionResult<DynamicContent> dynamicContent = slotsRepository.getDynamicContent(TEST_CONTENT_SPACE_GUID);


		assertExecutionResult(dynamicContent)
				.isFailure();
	}

	@Test
	public void shouldNotGetDynamicContentByGuidOnDynamicContentResolutionException() throws Exception {
		QueryResult queryResult = mock(QueryResult.class);
		final ContentSpace contentSpace = mock(ContentSpace.class);
		when(contentSpace.getTargetId()).thenReturn(TEST_TARGET_ID);
		when(queryResult.getSingleResult()).thenReturn(contentSpace);
		when(contentSpaceQueryService.
						query(

								CriteriaBuilder.criteriaFor(ContentSpace.class)
										.with(ContentSpaceRelation.having().guids(TEST_CONTENT_SPACE_GUID))
										.returning(ResultType.ENTITY)
						)
		).thenReturn(queryResult);

		CustomerSession customerSession = mock(CustomerSession.class);
		TagSet tagSet = mock(TagSet.class);
		when(customerSession.getCustomerTagSet()).thenReturn(tagSet);
		when(customerSessionRepository.findOrCreateCustomerSession()).thenReturn(
				ExecutionResultFactory.createReadOK(
						customerSession
				)
		);

		doThrow(DynamicContentResolutionException.class).when(dynamicContentRuntimeService).resolve(tagSet, TEST_TARGET_ID);


		ExecutionResult<DynamicContent> dynamicContent = slotsRepository.getDynamicContent(TEST_CONTENT_SPACE_GUID);


		assertExecutionResult(dynamicContent)
				.isSuccessful()
				.data(null);
	}


}
