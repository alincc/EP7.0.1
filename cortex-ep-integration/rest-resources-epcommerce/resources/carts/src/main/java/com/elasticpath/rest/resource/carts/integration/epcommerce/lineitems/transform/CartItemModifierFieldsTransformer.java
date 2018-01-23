/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.transform;

import java.util.Locale;

import javax.inject.Named;


import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.rest.definition.carts.LineItemConfigurationEntity;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transforms a {@link CartItemModifierField} into a {@link LineItemConfigurationEntity}.
 */
@Named("cartItemModifierFieldsTransformer")
public class CartItemModifierFieldsTransformer extends AbstractDomainTransformer<Iterable<CartItemModifierField>, LineItemConfigurationEntity> {

	@Override
	public Iterable<CartItemModifierField> transformToDomain(final LineItemConfigurationEntity resourceEntity, final Locale locale) {
		throw new UnsupportedOperationException("Transform to domain not implemented");
	}

	@Override
	public LineItemConfigurationEntity transformToEntity(final Iterable<CartItemModifierField> fields, final Locale locale) {
		final LineItemConfigurationEntity.Builder configBuilder = LineItemConfigurationEntity.builder();
		fields.forEach(field -> configBuilder.addingProperty(field.getCode(), ""));
		return configBuilder.build();
	}
}
