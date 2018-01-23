/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.shipment.lineitem.integration.epcommerce.impl;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.taxdocument.TaxDocumentRepository;
import com.elasticpath.rest.resource.taxes.shipment.lineitem.integration.ShipmentLineItemTaxesLookupStrategy;
import com.elasticpath.rest.resource.taxes.shipment.lineitem.integration.epcommerce.transform.ShipmentLineItemTaxesEntityTransformer;

/**
 * Implementation of {@link ShipmentLineItemTaxesLookupStrategy} for shipment line item taxes.
 */
@Singleton
@Named("shipmentLineItemTaxesLookupStrategy")
public class ShipmentLineItemTaxesLookupStrategyImpl implements ShipmentLineItemTaxesLookupStrategy {

	/**
	 * Error message when line item not found.
	 */
	public static final String LINE_ITEM_NOT_FOUND = "Line item not found";

	private final ResourceOperationContext resourceOperationContext;

	private final ShipmentRepository shipmentRepository;

	private final TaxDocumentRepository taxDocumentRepository;

	private final ShipmentLineItemTaxesEntityTransformer taxesTransformer;

	/**
	 * Constructor.
	 *
	 * @param resourceOperationContext the resource operation context
	 * @param shipmentRepository       the shipment repository
	 * @param taxDocumentRepository    the {@link TaxDocumentRepository}
	 * @param taxesTransformer         the {@link ShipmentLineItemTaxesEntityTransformer}
	 */
	@Inject
	public ShipmentLineItemTaxesLookupStrategyImpl(
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("shipmentRepository")
			final ShipmentRepository shipmentRepository,
			@Named("taxDocumentRepository")
			final TaxDocumentRepository taxDocumentRepository,
			@Named("shipmentLineItemTaxesEntityTransformer")
			final ShipmentLineItemTaxesEntityTransformer taxesTransformer) {
		this.resourceOperationContext = resourceOperationContext;
		this.shipmentRepository = shipmentRepository;
		this.taxDocumentRepository = taxDocumentRepository;
		this.taxesTransformer = taxesTransformer;
	}

	@Override
	public ExecutionResult<TaxesEntity> getTaxes(final String scope, final String purchaseId, final String shipmentId, final String lineItemId) {

		PhysicalOrderShipment shipment = Assign.ifSuccessful(shipmentRepository.find(purchaseId, shipmentId));

		OrderSku orderSku = Assign.ifNotNull(getShoppingItemByGuid(shipment.getShipmentOrderSkus(), lineItemId),
				OnFailure.returnNotFound(LINE_ITEM_NOT_FOUND));

		Collection<TaxJournalRecord> taxRecords = Assign.ifSuccessful(taxDocumentRepository.getTaxDocument(shipment.getTaxDocumentId(),
				orderSku.getSkuCode()));

		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		TaxesEntity taxesEntity = taxesTransformer.transform(orderSku, taxRecords, locale);

		return ExecutionResultFactory.createReadOK(taxesEntity);
	}

	private OrderSku getShoppingItemByGuid(final Set<OrderSku> orderSkus, final String skuGuid) {

		for (OrderSku orderSku : orderSkus) {
			if (orderSku.getGuid().equals(skuGuid)) {
				return orderSku;
			}
		}
		return null;
	}

}
