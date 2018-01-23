/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.shipment.lineitem.integration.epcommerce.transform;

import java.util.Collection;
import java.util.Locale;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.rest.definition.taxes.TaxesEntity;

/**
 * Creates {@link TaxesEntity} for a single shipment line item.
 */
public interface ShipmentLineItemTaxesEntityTransformer {

	/**
	 * Returns a new {@link TaxesEntity} based on the given {@link TaxJournalRecord}s.
	 * 
	 * @param orderSku the line item's {@link OrderSku}
	 * @param taxJournalRecords a collection of {@link TaxJournalRecord}s detailing the per tax amount breakdown
	 * @param locale the {@link Locale}
	 * @return the {@link TaxesEntity}
	 */
	TaxesEntity transform(OrderSku orderSku, Collection<TaxJournalRecord> taxJournalRecords, Locale locale);
}
