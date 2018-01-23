package com.elasticpath.rest.resource.shipments.lineitems.link.impl

import com.elasticpath.rest.definition.base.CostEntity
import com.elasticpath.rest.resource.shipments.lineitems.rel.ShipmentLineItemResourceRels

import static com.elasticpath.rest.definition.shipments.ShipmentsMediaTypes.SHIPMENT_LINE_ITEM
import static com.elasticpath.rest.schema.SelfFactory.createSelf

import org.junit.Test
import org.junit.runner.RunWith

import org.mockito.InjectMocks
import org.mockito.runners.MockitoJUnitRunner

import com.elasticpath.rest.definition.taxes.TaxesEntity
import com.elasticpath.rest.schema.ResourceLinkFactory
import com.elasticpath.rest.schema.ResourceState

@RunWith(MockitoJUnitRunner)
class AddShipmentLineItemLinkToShipmentLineItemTaxesTest {

	@InjectMocks
	AddShipmentLineItemLinkToShipmentLineItemTaxes handler

	def shipmentLineItemUri = "/shipments/{purchases-uri}/{shipment-id}/lineitems/{lineitem-id}"
	def shipmentLineItemTaxesUri = "/taxes/shipments/{purchases-uri}/{shipment-id}/lineitems/{lineitem-id}"

	def taxesEntity = TaxesEntity.builder()
			.withTotal(
				CostEntity.builder().withAmount(BigDecimal.ONE).build()
			)
			.build()
	def shipmentLineItemTaxesRepresentation = ResourceState.Builder.create(taxesEntity)
			.withSelf(createSelf(shipmentLineItemTaxesUri))
			.build()

	def shipmentLineItemLink = ResourceLinkFactory.create(
			shipmentLineItemUri,
			SHIPMENT_LINE_ITEM.id(),
			ShipmentLineItemResourceRels.LINE_ITEM_REL,
			ShipmentLineItemResourceRels.LINE_ITEM_TAXES_REV
	)

	@Test
	void 'Should create taxes link to line items'() {
		def result = handler.getLinks(shipmentLineItemTaxesRepresentation)[0]
		assert shipmentLineItemLink == result
	}

	def shipmentTaxesUri = "/taxes/shipments/{purchases-uri}/{shipment-id}"
	def shipmentTaxesRepresentation = ResourceState.Builder.create(taxesEntity)
			.withSelf(createSelf(shipmentTaxesUri))
			.build()

	@Test
	void 'Should not create taxes link to shipment'() {
		assert !handler.getLinks(shipmentTaxesRepresentation).iterator().hasNext()
	}
}