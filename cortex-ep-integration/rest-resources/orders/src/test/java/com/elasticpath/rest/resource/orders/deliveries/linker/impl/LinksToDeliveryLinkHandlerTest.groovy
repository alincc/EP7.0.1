package com.elasticpath.rest.resource.orders.deliveries.linker.impl

import static com.elasticpath.rest.definition.collections.CollectionsMediaTypes.LINKS
import static com.elasticpath.rest.schema.ResourceLinkFactory.createNoRev
import static com.elasticpath.rest.rel.ListElementRels.LIST

import org.junit.Test
import org.junit.runner.RunWith

import org.mockito.InjectMocks
import org.mockito.runners.MockitoJUnitRunner

import com.elasticpath.rest.definition.orders.DeliveryEntity
import com.elasticpath.rest.resource.orders.deliveries.Deliveries
import com.elasticpath.rest.schema.ResourceLink
import com.elasticpath.rest.schema.ResourceState
import com.elasticpath.rest.uri.URIUtil

@RunWith(MockitoJUnitRunner)
class LinksToDeliveryLinkHandlerTest {

	def resourceServerName = 'orders'

	@InjectMocks
	LinksToDeliveryLinkHandler handler = new LinksToDeliveryLinkHandler(
			resourceServerName: resourceServerName
	)

	DeliveryEntity deliveryEntity = DeliveryEntity.builder()
			.withOrderId('orderId')
			.build()
	ResourceState<DeliveryEntity> resourceState = ResourceState.Builder.create(deliveryEntity)
			.withScope('scope')
			.build()

	String deliveriesUri = URIUtil.format(resourceServerName, resourceState.scope, deliveryEntity.orderId, Deliveries.URI_PART)
	ResourceLink expectedDeliveryLink = createNoRev(
			deliveriesUri,
			LINKS.id(),
			LIST
	)

	@Test
	void 'Given deliveries, when linking, should build link to correct links link'() {
		def result = handler.getLinks(resourceState)

		assert expectedDeliveryLink == result[0]
	}
}
