/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.impl;

import static com.elasticpath.rest.TestResourceOperationFactory.createRead;
import static com.elasticpath.rest.definition.carts.CartsMediaTypes.CART;
import static com.elasticpath.rest.resource.discounts.rel.DiscountsResourceRels.DISCOUNT_REL;
import static com.elasticpath.rest.uri.URIUtil.format;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Tests URI-related annotations on {@link DiscountsResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class DiscountsResourceOperatorImplUriTest extends AbstractUriTest {

	private final String cartUri = format(
			DISCOUNT_REL,
			format("carts", "scope", "mqytamjrmy2gen3egrstmmdeg4zginzzmyzdiyrtgrtdgojygizdqmtegi3dkztf")
					.substring(1)
	);

	@Mock
	private DiscountsResourceOperatorImpl resourceOperator;

	@Test
	public void testReadForOther() {

		mediaType(CART);
		readOther(createRead(cartUri));
		when(resourceOperator.processReadForCart(anyCartEntity(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(createRead(cartUri), resourceOperator);

		verify(resourceOperator).processReadForCart(anyCartEntity(), anyResourceOperation());
	}

	private static ResourceState<CartEntity> anyCartEntity() {

		return Mockito.any();
	}
}
