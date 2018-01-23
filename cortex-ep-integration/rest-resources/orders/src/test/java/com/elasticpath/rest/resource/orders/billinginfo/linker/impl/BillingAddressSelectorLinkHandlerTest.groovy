package com.elasticpath.rest.resource.orders.billinginfo.linker.impl

import static com.elasticpath.rest.definition.orders.OrdersMediaTypes.ORDER
import static com.elasticpath.rest.resource.orders.billinginfo.BillingInfoConstants.BILLING_ADDRESS_SELECTOR_NAME
import static com.elasticpath.rest.schema.SelfFactory.createSelf

import org.junit.Test
import org.junit.runner.RunWith

import org.mockito.InjectMocks
import org.mockito.runners.MockitoJUnitRunner

import com.elasticpath.rest.definition.controls.SelectorEntity
import com.elasticpath.rest.schema.ResourceState

@RunWith(MockitoJUnitRunner)
class BillingAddressSelectorLinkHandlerTest {

	@InjectMocks
	BillingAddressSelectorLinkHandler handler = new BillingAddressSelectorLinkHandler(
			resourceServerName: 'orders'
	)

	SelectorEntity selectorEntity = SelectorEntity.builder()
			.withName(BILLING_ADDRESS_SELECTOR_NAME)
			.withSelectorId('orderId')
			.build()
	ResourceState<SelectorEntity> resourceState = ResourceState.Builder.create(selectorEntity)
			.withScope('scope')
			.withSelf(createSelf('orderUri', ORDER.id()))
			.build()

	@Test
	void 'Given valid values, when linking, should build correct link'() {
		def result = handler.getLinks(resourceState)[0]

		assert '/orders/scope/orderid/billingaddressinfo' == result.uri
	}
}
