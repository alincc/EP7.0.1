package com.elasticpath.rest.resource.orders.billinginfo.linker.impl

import static com.elasticpath.rest.command.ExecutionResultFactory.createReadOK
import static com.elasticpath.rest.definition.addresses.AddressesMediaTypes.ADDRESS
import static com.elasticpath.rest.definition.controls.ControlsMediaTypes.SELECTOR
import static com.elasticpath.rest.definition.orders.OrdersMediaTypes.ORDER
import static com.elasticpath.rest.resource.orders.billinginfo.BillingInfoConstants.BILLING_ADDRESS_LIST_NAME
import static com.elasticpath.rest.schema.ResourceLinkFactory.createNoRev
import static com.elasticpath.rest.schema.ResourceLinkFactory.createUriRel
import static com.elasticpath.rest.schema.SelfFactory.createSelf
import static com.elasticpath.rest.common.selector.SelectorRepresentationRels.DESCRIPTION
import static com.elasticpath.rest.common.selector.SelectorRepresentationRels.SELECT_ACTION
import static org.mockito.BDDMockito.given
import static org.mockito.Matchers.anyString
import static org.mockito.Mockito.mock

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import com.elasticpath.rest.definition.collections.LinksEntity
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector
import com.elasticpath.rest.resource.orders.billinginfo.BillingAddressInfo
import com.elasticpath.rest.resource.orders.billinginfo.BillingInfoLookup
import com.elasticpath.rest.schema.ResourceLink
import com.elasticpath.rest.schema.ResourceState
import com.elasticpath.rest.common.selector.SelectorRepresentationRels
import com.elasticpath.rest.schema.uri.BillingAddressListUriBuilder
import com.elasticpath.rest.schema.uri.BillingAddressListUriBuilderFactory
import com.elasticpath.rest.uri.URIUtil

@RunWith(MockitoJUnitRunner)
class BillingAddressChoiceLinkHandlerTest {

	String resourceServerName = 'orders'
	String SCOPE = 'scope'

	@Mock
	BillingInfoLookup billingInfoLookup

	@Mock
	BillingAddressListUriBuilderFactory billingAddressListUriBuilderFactory

	@InjectMocks
	BillingAddressChoiceLinkHandler handler = new BillingAddressChoiceLinkHandler(
			resourceServerName: resourceServerName
	)

	String billingAddressListUri = URIUtil.format('addresses', SCOPE,  'billing')
	String selectorUri = URIUtil.format(resourceServerName, SCOPE, 'orderId', BillingAddressInfo.URI_PART, Selector.URI_PART, billingAddressListUri)
	ResourceLink selectorLink = createNoRev(
			selectorUri,
			SELECTOR.id(),
			SelectorRepresentationRels.SELECTOR
	)

	String billingAddressUri = URIUtil.format('addresses', SCOPE, 'BILLING_ADDRESS_ID')
	String descriptorUri = billingAddressUri
	ResourceLink descriptorLink = createNoRev(
			descriptorUri,
			ADDRESS.id(),
			DESCRIPTION
	)

	String selectActionUri = URIUtil.format('orderUri', Selector.URI_PART, billingAddressUri)
	ResourceLink selectActionLink = createUriRel(
			selectActionUri,
			SELECT_ACTION
	)

	LinksEntity linksEntity = LinksEntity.builder()
			.withName(BILLING_ADDRESS_LIST_NAME)
			.withElementListId('orderId')
			.build()
	ResourceState<LinksEntity> resourceState = ResourceState.Builder.create(linksEntity)
			.withScope('scope')
			.withSelf(createSelf(URIUtil.format('/orderUri', Selector.URI_PART, billingAddressUri), ORDER.id()))
			.build()

	@Before
	void setUp() {
		'Given profile uri'(billingAddressListUri)

		given(billingInfoLookup.findAddressForOrder(anyString(), anyString()))
				.willReturn(createReadOK('BILLING_ADDRESS_ID'))
	}

	@Test
	void 'Given valid selectorLink, when linking, should build correct link'() {
		def result = handler.getLinks(resourceState)

		assert selectorLink == result[0]
	}

	@Test
	void 'Given valid descriptorLink, when linking, should build correct link'() {
		def result = handler.getLinks(resourceState)

		assert descriptorLink == result[1]
	}

	@Test
	void 'Given valid selectActionLink, when linking, should build correct link'() {
		def result = handler.getLinks(resourceState)

		assert selectActionLink == result[2]
	}

	void 'Given profile uri'(String profileId) {

		def builder = mock(BillingAddressListUriBuilder)

		given(billingAddressListUriBuilderFactory.get())
				.willReturn(builder)
		given(builder.setScope(anyString()))
				.willReturn(builder)
		given(builder.build())
				.willReturn(profileId)
	}
}
