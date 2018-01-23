/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.transform;

import java.util.Locale;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;


import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.rest.definition.carts.LineItemConfigurationEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.domain.wrapper.LineItem;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transforms a {@link LineItem} into a {@link LineItemEntity}, and vice versa.
 */
@Singleton
@Named("lineItemTransformer")
public class LineItemTransformerImpl extends AbstractDomainTransformer<LineItem, LineItemEntity> implements LineItemTransformer {

	@Override
	public LineItem transformToDomain(final LineItemEntity lineItemEntity, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public LineItemEntity transformToEntity(final LineItem lineItem, final Locale locale) {

		final LineItemEntity.Builder lineItemEntityBuilder = LineItemEntity.builder()
			.withCartId(lineItem.getCartId())
			.withItemId(lineItem.getItemId())
			.withLineItemId(lineItem.getShoppingItem().getGuid())
			.withQuantity(lineItem.getShoppingItem().getQuantity());

		if (lineItem.getCartItemModifierValues() != null) {
			final LineItemConfigurationEntity.Builder configBuilder = LineItemConfigurationEntity.builder();

			for (Map.Entry<CartItemModifierField, String> fieldValue : lineItem.getCartItemModifierValues().entrySet()) {
				configBuilder.addingProperty(fieldValue.getKey().getCode(), fieldValue.getValue());
			}
			lineItemEntityBuilder.withConfiguration(configBuilder.build());
		}

		return lineItemEntityBuilder.build();
	}
}
