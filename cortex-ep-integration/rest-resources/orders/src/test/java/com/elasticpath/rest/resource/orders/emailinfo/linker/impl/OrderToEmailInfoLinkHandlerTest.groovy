package com.elasticpath.rest.resource.orders.emailinfo.linker.impl

import static com.elasticpath.rest.command.ExecutionResultFactory.createReadOK
import static com.elasticpath.rest.definition.emails.EmailsMediaTypes.EMAIL
import static com.elasticpath.rest.definition.orders.OrdersMediaTypes.ORDER
import static com.elasticpath.rest.resource.orders.emailinfo.EmailInfoConstants.EMAIL_INFO_NAME
import static com.elasticpath.rest.resource.orders.emailinfo.EmailInfoRepresentationRels.EMAIL_FORM_REL
import static com.elasticpath.rest.resource.orders.emailinfo.EmailInfoRepresentationRels.EMAIL_INFO_REV
import static com.elasticpath.rest.resource.orders.emailinfo.EmailInfoRepresentationRels.EMAIL_REL
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
import com.elasticpath.rest.resource.dispatch.operator.annotation.Form
import com.elasticpath.rest.resource.orders.emailinfo.EmailInfoLookup
import com.elasticpath.rest.schema.ResourceLink
import com.elasticpath.rest.schema.ResourceState
import com.elasticpath.rest.schema.uri.EmailFormUriBuilder
import com.elasticpath.rest.schema.uri.EmailFormUriBuilderFactory
import com.elasticpath.rest.schema.uri.EmailsUriBuilder
import com.elasticpath.rest.schema.uri.EmailsUriBuilderFactory
import com.elasticpath.rest.schema.uri.OrdersUriBuilder
import com.elasticpath.rest.schema.uri.OrdersUriBuilderFactory
import com.elasticpath.rest.uri.URIUtil

@RunWith(MockitoJUnitRunner)
class OrderToEmailInfoLinkHandlerTest {

	@Mock
	EmailInfoLookup emailInfoLookup

	@Mock
	EmailFormUriBuilderFactory emailFormUriBuilderFactory

	@Mock
	EmailsUriBuilderFactory emailsUriBuilderFactory

	@Mock
	OrdersUriBuilderFactory ordersUriBuilderFactory

	@InjectMocks
	OrderToEmailInfoLinkHandler handler

	def scope = 'scope'

	String orderUri = '/orders/orderId'
	ResourceLink expectedOrderLink = create(
			orderUri,
			ORDER.id(),
			ORDER_REL,
			EMAIL_INFO_REV
	)

	String emailFormUri = URIUtil.format('emails', scope, Form.URI_PART)
	ResourceLink expectedEmailFormLink = createNoRev(
			emailFormUri,
			EMAIL.id(),
			EMAIL_FORM_REL
	)

	String emailUri = URIUtil.format('emails', scope, 'emailId')
	ResourceLink expectedEmailLink = createNoRev(
			emailUri,
			EMAIL.id(),
			EMAIL_REL
	)

	InfoEntity infoEntity = InfoEntity.builder()
			.withName(EMAIL_INFO_NAME)
			.withInfoId('orderId')
			.build()
	ResourceState<InfoEntity> resourceState = ResourceState.Builder.create(infoEntity)
			.withScope(scope)
			.withSelf(createSelf('orderUri', ORDER.id()))
			.build()

	@Before
	void setUp() {
		'Given order uri'(orderUri)
		'Given email form uri'(emailFormUri)
		'Given email uri'(emailUri)

		given(emailInfoLookup.findEmailIdForOrder(anyString(), anyString()))
				.willReturn(createReadOK('emailId'))
	}

	@Test
	void 'Given orderUri, when linking, should construct correct orderLink'() {
		def result = handler.getLinks(resourceState)[0]

		assert expectedOrderLink == result
	}

	@Test
	void 'Given emailFormUri, when linking, should construct correct emailFormLink'() {
		def result = handler.getLinks(resourceState)[1]

		assert expectedEmailFormLink == result
	}

	@Test
	void 'Given email present, when linking, should construct correct emailLink'() {
		def result = handler.getLinks(resourceState)[2]

		assert expectedEmailLink == result
	}

	@Test
	void 'Given email not present, when linking, should not construct emailLink'() {
		given(emailInfoLookup.findEmailIdForOrder(anyString(), anyString()))
				.willReturn(createReadOK(null))

		def result = handler.getLinks(resourceState) as List

		assert !result.rel.contains(EMAIL_REL)
	}

	void 'Given email form uri'(String uri) {

		def builder = mock(EmailFormUriBuilder)

		given(emailFormUriBuilderFactory.get())
				.willReturn(builder)
		given(builder.setScope(anyString()))
				.willReturn(builder)
		given(builder.build())
				.willReturn(uri)
	}

	void 'Given email uri'(String uri) {

		def builder = mock(EmailsUriBuilder)

		given(emailsUriBuilderFactory.get())
				.willReturn(builder)
		given(builder.setEmailId(anyString()))
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
}
