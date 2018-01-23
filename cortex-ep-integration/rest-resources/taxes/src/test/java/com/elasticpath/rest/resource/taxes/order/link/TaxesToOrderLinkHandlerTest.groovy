package com.elasticpath.rest.resource.taxes.order.link

import static com.elasticpath.rest.definition.orders.OrdersMediaTypes.ORDER
import static com.elasticpath.rest.resource.taxes.order.rel.OrderTaxesResourceRels.ORDER_REL
import static com.elasticpath.rest.resource.taxes.rel.TaxesResourceRels.TAX_REV
import static com.elasticpath.rest.schema.SelfFactory.createSelf

import org.junit.Test
import org.junit.runner.RunWith

import org.mockito.InjectMocks
import org.mockito.runners.MockitoJUnitRunner

import com.elasticpath.rest.definition.taxes.TaxesEntity
import com.elasticpath.rest.schema.ResourceLinkFactory
import com.elasticpath.rest.schema.ResourceState
import com.elasticpath.rest.uri.URIUtil

@RunWith(MockitoJUnitRunner)
class TaxesToOrderLinkHandlerTest {

	@InjectMocks
	TaxesToOrderLinkHandler handler

	def orderUri = '/orders/uri'
	def taxUri = URIUtil.format("taxes", orderUri)

	def taxesEntity = TaxesEntity.builder()
			.build()
	def taxesState = ResourceState.Builder.create(taxesEntity)
			.withSelf(createSelf(taxUri))
			.build()

	def link = ResourceLinkFactory.create(
			orderUri,
			ORDER.id(),
			ORDER_REL,
			TAX_REV
	)

	@Test
	void 'Should create taxes link'() {
		def result = handler.getLinks(taxesState)[0]

		assert link == result
	}

}
