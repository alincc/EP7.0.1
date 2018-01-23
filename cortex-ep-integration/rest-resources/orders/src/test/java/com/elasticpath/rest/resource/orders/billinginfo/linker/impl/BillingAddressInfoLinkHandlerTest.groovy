package com.elasticpath.rest.resource.orders.billinginfo.linker.impl

import static com.elasticpath.rest.command.ExecutionResultFactory.createReadOK
import static com.elasticpath.rest.definition.addresses.AddressesMediaTypes.ADDRESS
import static com.elasticpath.rest.definition.controls.ControlsMediaTypes.SELECTOR
import static com.elasticpath.rest.definition.orders.OrdersMediaTypes.ORDER
import static com.elasticpath.rest.resource.orders.billinginfo.BillingInfoConstants.BILLING_ADDRESS_INFO_NAME
import static com.elasticpath.rest.resource.orders.billinginfo.rel.BillingInfoRepresentationRels.ADDRESS_FORM_REL
import static com.elasticpath.rest.resource.orders.billinginfo.rel.BillingInfoRepresentationRels.BILLING_ADDRESS_INFO_REV
import static com.elasticpath.rest.resource.orders.billinginfo.rel.BillingInfoRepresentationRels.BILLING_ADDRESS_REL
import static com.elasticpath.rest.resource.orders.rel.OrdersRepresentationRels.ORDER_REL
import static com.elasticpath.rest.schema.ResourceLinkFactory.create
import static com.elasticpath.rest.schema.ResourceLinkFactory.createNoRev
import static com.elasticpath.rest.schema.SelfFactory.createSelf
import static org.mockito.BDDMockito.given
import static org.mockito.Matchers.anyString
import static org.mockito.Mockito.mock

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import com.elasticpath.rest.definition.controls.InfoEntity
import com.elasticpath.rest.resource.ResourceOperationContext
import com.elasticpath.rest.resource.orders.billinginfo.BillingInfoLookup
import com.elasticpath.rest.schema.ResourceState
import com.elasticpath.rest.common.selector.SelectorRepresentationRels
import com.elasticpath.rest.schema.uri.OrdersUriBuilder
import com.elasticpath.rest.schema.uri.OrdersUriBuilderFactory
import com.elasticpath.rest.schema.uri.AddressFormUriBuilder
import com.elasticpath.rest.schema.uri.AddressFormUriBuilderFactory
import com.elasticpath.rest.schema.uri.AddressUriBuilder
import com.elasticpath.rest.schema.uri.AddressUriBuilderFactory
import com.elasticpath.rest.schema.uri.BillingAddressListUriBuilder
import com.elasticpath.rest.schema.uri.BillingAddressListUriBuilderFactory

@RunWith(MockitoJUnitRunner)
class BillingAddressInfoLinkHandlerTest {

	@Mock
	BillingInfoLookup billingInfoLookup

	@Mock
	OrdersUriBuilderFactory ordersUriBuilderFactory

	@Mock
	AddressUriBuilderFactory addressUriBuilderFactory

	@Mock
	BillingAddressListUriBuilderFactory billingAddressListUriBuilderFactory

	@Mock
	AddressFormUriBuilderFactory addressFormUriBuilderFactory

	@Mock
	ResourceOperationContext resourceOperationContext

	@InjectMocks
	BillingAddressInfoLinkHandler linker

	String orderUri = '/orders/orderId'
	def expectedOrderUri = create(
			orderUri,
			ORDER.id(),
			ORDER_REL,
			BILLING_ADDRESS_INFO_REV
	)

	String profileUri = '/profiles/profileUri'
	def expectedProfileUri = create(
			'/orderuri/selector/profiles/profileuri',
			SELECTOR.id(),
			SelectorRepresentationRels.SELECTOR,
			BILLING_ADDRESS_INFO_REV
	)

	String addressUri = '/addressUri'
	def expectedBillingInfoLink = createNoRev(
			addressUri,
			ADDRESS.id(),
			BILLING_ADDRESS_REL
	)

	String addressFormUri = '/addressForm'
	def expectedAddressFormLink = createNoRev(addressFormUri,
			ADDRESS.id(),
			ADDRESS_FORM_REL);

	InfoEntity infoEntity = InfoEntity.builder()
			.withName(BILLING_ADDRESS_INFO_NAME)
			.withInfoId('orderId')
			.build()
	ResourceState<InfoEntity> resourceState = ResourceState.Builder.create(infoEntity)
			.withScope('scope')
			.withSelf(createSelf('orderUri', ORDER.id()))
			.build()

	String profileId = 'profileId'
	String addressId = 'addressId'

	@Before
	void setUp() {

		'Given address uri'(addressUri)
		'Given address form uri'(addressFormUri)
		'Given order uri'(orderUri)
		'Given profile uri'(profileUri)

		given(resourceOperationContext.getUserIdentifier())
				.willReturn(profileId)
		given(billingInfoLookup.findAddressForOrder(anyString(), anyString()))
				.willReturn(createReadOK(addressId))
	}

	@Test
	void 'Given orderUri, when linking, should construct correct orderUri'() {
		def result = linker.getLinks(resourceState)

		assert expectedOrderUri == result[0]
	}

	@Test
	void 'Given profileUri, when linking, should construct correct orderUri'() {
		def result = linker.getLinks(resourceState)

		assert expectedProfileUri == result[1]
	}

	@Test
	void 'Given billing address present, when linking, should construct correct billingAddressInfoUri'() {
		def result = linker.getLinks(resourceState)

		assert expectedBillingInfoLink == result[2]
	}

	@Test
	void 'Given billing address not present, when linking, should construct correct billingAddressInfoUri'() {
		given(billingInfoLookup.findAddressForOrder(anyString(), anyString()))
				.willReturn(createReadOK(null))

		def result = linker.getLinks(resourceState) as List

		assert !result.rel.contains(BILLING_ADDRESS_REL)
	}

	@Test
	void 'Given address form present, when linking, should construct correct billingAddressInfoUri'() {
		def result = linker.getLinks(resourceState)

		assert expectedAddressFormLink == result[3]
	}

	void 'Given address form uri'(String uri) {

		def builder = mock(AddressFormUriBuilder)

		given(addressFormUriBuilderFactory.get())
				.willReturn(builder)
		given(builder.setScope(anyString()))
				.willReturn(builder)
		given(builder.build())
				.willReturn(uri)
	}

	void 'Given address uri'(String uri) {

		def builder = mock(AddressUriBuilder)

		given(addressUriBuilderFactory.get())
				.willReturn(builder)
		given(builder.setAddressId(anyString()))
				.willReturn(builder)
		given(builder.setScope(anyString()))
				.willReturn(builder)
		given(builder.build())
				.willReturn(uri)
	}

	void 'Given order uri'(String uri) {

		def builder = mock(OrdersUriBuilder)

		given(ordersUriBuilderFactory.get())
				.willReturn(builder)
		given(builder.setOrderId(anyString()))
				.willReturn(builder)
		given(builder.setScope(anyString()))
				.willReturn(builder)
		given(builder.build())
				.willReturn(uri)
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
