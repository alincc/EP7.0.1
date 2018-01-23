/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.validator;

import javax.inject.Named;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;

/**
 * Validates ep {@link com.elasticpath.common.dto.ShoppingItemDto}s.
 * The responsibility of this class should be pushed down into CE.
 */
@Named("shoppingItemDtoValidator")
public class ShoppingItemDtoValidator {

	/**
	 * Validates {@link com.elasticpath.common.dto.ShoppingItemDto}.
	 *
	 * @param shoppingItemDto the {@link com.elasticpath.common.dto.ShoppingItemDto}
	 * @return SUCCESS if shopping item meets validation criteria STATE_FAILURE otherwise
	 */
	public ExecutionResult<Void> validate(final ShoppingItemDto shoppingItemDto) {
		if (shoppingItemDto.getQuantity() < 1) {
			return ExecutionResultFactory.createBadRequestBody("Quantity must be positive");
		} else {
			return ExecutionResultFactory.createUpdateOK();
		}
	}
}
