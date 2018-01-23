/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.transform;

import java.util.Locale;

import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.domain.wrapper.LineItem;

 /**
 * Transforms {@link LineItem} to {@link LineItemEntity} and vice versa.
 */
public interface LineItemTransformer {
		/**
		 * Transform to domain.
		 *
		 * @param lineItemEntity the line item entity
		 * @return the money
		 */
		LineItem transformToDomain(LineItemEntity lineItemEntity);

		/**
		 * Transform to domain.
		 *
		 * @param lineItemEntity the line item entity
		 * @param locale the locale
		 * @return the money
		 */
		LineItem transformToDomain(LineItemEntity lineItemEntity, Locale locale);

		/**
		 * Transform to entity.
		 *
		 * @param lineItem the line time
		 * @return the cost entity
		 */
		LineItemEntity transformToEntity(LineItem lineItem);

		/**
		 * Transform to entity.
		 *
		 * @param lineItem the line time
		 * @param locale the locale
		 * @return the cost entity
		 */
		LineItemEntity transformToEntity(LineItem lineItem, Locale locale);
}
