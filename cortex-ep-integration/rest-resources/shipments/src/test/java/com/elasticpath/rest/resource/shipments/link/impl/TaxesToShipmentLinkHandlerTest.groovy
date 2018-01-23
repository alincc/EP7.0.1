package com.elasticpath.rest.resource.shipments.link.impl
import static com.elasticpath.rest.definition.shipments.ShipmentsMediaTypes.SHIPMENT
import static com.elasticpath.rest.schema.SelfFactory.createSelf

import org.junit.Test
import org.junit.runner.RunWith

import org.mockito.InjectMocks
import org.mockito.runners.MockitoJUnitRunner

import com.elasticpath.rest.definition.taxes.TaxesEntity
import com.elasticpath.rest.resource.shipments.rel.ShipmentsResourceRels
import com.elasticpath.rest.schema.ResourceLinkFactory
import com.elasticpath.rest.schema.ResourceState

@RunWith(MockitoJUnitRunner)
class TaxesToShipmentLinkHandlerTest {

	@InjectMocks
	AddShipmentLinkToShipmentTaxes handler

	def shipmentTaxesUri = '/taxes/shipments/purchases/scope/purchase-id/shipment-id'

	def taxesEntity = TaxesEntity.builder().build()

	def shipmentTaxesRepresentation = ResourceState.Builder.create(taxesEntity)
			.withSelf(createSelf(shipmentTaxesUri))
			.build()

	def shipmentUri = '/shipments/purchases/scope/purchase-id/shipment-id'
	def shipmentTaxesLink = ResourceLinkFactory.create(
			shipmentUri,
			SHIPMENT.id(),
			ShipmentsResourceRels.SHIPMENT_REL,
			ShipmentsResourceRels.TAX_REV)

	def shipmentLineItemTaxesUri = '/taxes/shipments/purchases/scope/purchase-id/shipment-id/lineitems/line-id'
	def shipmentLineItemTaxesRepresentation = ResourceState.Builder.create(taxesEntity)
			.withSelf(createSelf(shipmentLineItemTaxesUri))
			.build()

	@Test
	void 'Should create shipment link on shipment taxes'() {
		def result = handler.getLinks(shipmentTaxesRepresentation)[0]
		assert shipmentTaxesLink == result
	}

	@Test
	void 'Should not create shipment link to shipment lineitem taxes'() {
		assert !handler.getLinks(shipmentLineItemTaxesRepresentation).iterator().hasNext()
	}
}