/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.slots.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.query.SlotsRepository;
import com.elasticpath.rest.resource.slots.integration.dto.SlotDto;
import com.elasticpath.rest.resource.slots.integration.epcommerce.transform.DynamicContentSpaceTransformer;
import com.elasticpath.rest.resource.slots.integration.epcommerce.wrapper.DynamicContentSpace;

/**
 * Tests for {@link SlotLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SlotLookupStrategyImplTest {
	private static final String OPERATION_SHOULD_BE_SUCCESSFUL = "Operation should be successful.";
	private static final String TARGET_ID = "target id";
	private static final String STORE_CODE = "storeCode";
	private static final String USERID = "userid";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ResourceOperationContext mockResourceOperationContext;
	@Mock
	private DynamicContentSpaceTransformer mockDynamicContentSpaceTransformer;
	@Mock
	private SlotsRepository slotsRepository;
	@Mock
	private ContentSpace mockContentSpace;

	@InjectMocks
	private SlotLookupStrategyImpl strategy;

	/**
	 * Sets up common elements of test.
	 */
	@Before
	public void setUp() {
		when(mockContentSpace.getTargetId()).thenReturn(TARGET_ID);
		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale(STORE_CODE, USERID, Locale.ENGLISH);
		when(mockResourceOperationContext.getSubject())
				.thenReturn(subject);
	}

	/**
	 * Tests calling findAllSlotTargetIds will return successfully.
	 */
	@Test
	public void testFindAllDecodedSlotIds() {
		Collection<String> contentSpaces = new ArrayList<>();

		when(slotsRepository.findAllSlotIds()).thenReturn(ExecutionResultFactory.<Collection<String>>createReadOK(contentSpaces));

		ExecutionResult<Collection<String>> result = strategy.findAllSlotIds(STORE_CODE);

		verify(slotsRepository).findAllSlotIds();
		assertTrue(OPERATION_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertEquals("Return slot guids do not match expected values.", contentSpaces, result.getData());

	}

	/**
	 * Tests getting a slotDto from targetId.
	 *
	 */
	@Test
	public void testGetSlot() {
		when(slotsRepository.getContentSpaceByGuid(TARGET_ID)).thenReturn(ExecutionResultFactory.<ContentSpace>createReadOK(mockContentSpace));
		DynamicContent mockDynamicContent = mock(DynamicContent.class);
		SlotDto slotDto = ResourceTypeFactory.createResourceEntity(SlotDto.class);
		when(slotsRepository.getDynamicContent(TARGET_ID)).thenReturn(ExecutionResultFactory.createReadOK(mockDynamicContent));
		when(mockDynamicContentSpaceTransformer.transformToEntity(any(DynamicContentSpace.class), any(Locale.class))).thenReturn(slotDto);

		ExecutionResult<SlotDto> slotResult = strategy.getSlot(STORE_CODE, TARGET_ID);

		assertTrue(OPERATION_SHOULD_BE_SUCCESSFUL, slotResult.isSuccessful());
		assertEquals("Result slot dto does not match expected value.", slotDto, slotResult.getData());
	}

	/**
	 * Tests getting a slotDto from targetId when SlotsRepositoryService return a null ExecutionResult.
	 *
	 */
	@Test
	public void testGetSlotWhenResolveDynamicContentThrowsException() {
		when(slotsRepository.getContentSpaceByGuid(TARGET_ID)).thenReturn(ExecutionResultFactory.<ContentSpace>createReadOK(mockContentSpace));
		when(slotsRepository.getDynamicContent(TARGET_ID)).thenReturn(ExecutionResultFactory.<DynamicContent>createReadOK(null));
		when(mockDynamicContentSpaceTransformer.transformToEntity(
						any(DynamicContentSpace.class),
						any(Locale.class))
		).thenReturn(
				ResourceTypeFactory.createResourceEntity(SlotDto.class)
		);

		ExecutionResult<SlotDto> slotResult = strategy.getSlot(STORE_CODE, TARGET_ID);

		assertTrue(OPERATION_SHOULD_BE_SUCCESSFUL, slotResult.isSuccessful());
	}


	/**
	 * Tests when contentSpaceService returns a null.
	 */
	@Test
	public void testGetSlotWithNullSlot() {
		when(slotsRepository.getContentSpaceByGuid(TARGET_ID)).thenReturn(ExecutionResultFactory.<ContentSpace>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.getSlot(STORE_CODE, TARGET_ID);
	}

}
