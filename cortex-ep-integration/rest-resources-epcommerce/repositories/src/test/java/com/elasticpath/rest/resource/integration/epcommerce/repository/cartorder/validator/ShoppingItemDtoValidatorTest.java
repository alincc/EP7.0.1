/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.validator;

import static com.elasticpath.rest.ResourceStatus.BAD_REQUEST_BODY;
import static com.elasticpath.rest.ResourceStatus.UPDATE_OK;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.BDDMockito.given;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * Tests {@link ShoppingItemDtoValidator}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShoppingItemDtoValidatorTest {

	@Mock
	ShoppingItemDto shoppingItemDto;

	@InjectMocks
	private ShoppingItemDtoValidator shoppingItemDtoValidator;

	@Test
	public void ensureShoppingItemDtoWithValidQuantitySucceedsValidation() {
		given(shoppingItemDto.getQuantity())
				.willReturn(1);

		ExecutionResult<Void> result = shoppingItemDtoValidator.validate(shoppingItemDto);

		assertExecutionResult(result).resourceStatus(UPDATE_OK);
	}

	@Test
	public void ensureShoppingItemDtoWithInvalidQuantityReturnsStateFailure() {
		given(shoppingItemDto.getQuantity())
				.willReturn(-1);

		ExecutionResult<Void> result = shoppingItemDtoValidator.validate(shoppingItemDto);

		assertExecutionResult(result).resourceStatus(BAD_REQUEST_BODY);
	}
}
