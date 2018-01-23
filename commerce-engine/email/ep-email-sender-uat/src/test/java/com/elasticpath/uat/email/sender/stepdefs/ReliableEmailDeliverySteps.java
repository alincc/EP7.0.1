/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.uat.email.sender.stepdefs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.processor.RedeliveryPolicy;
import org.apache.camel.spi.BrowsableEndpoint;
import org.jvnet.mock_javamail.Mailbox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.elasticpath.cucumber.testexecutionlisteners.CucumberDatabaseTestExecutionListener;
import com.elasticpath.cucumber.testexecutionlisteners.CucumberJmsRegistrationTestExecutionListener;
import com.elasticpath.email.EmailContentUtil;
import com.elasticpath.email.EmailDto;
import com.elasticpath.email.test.support.EmailSendingMockInterceptor;
import com.elasticpath.messaging.camel.test.support.CamelContextMessagePurger;

/**
 * Steps for the reliable email delivery feature.
 */
@ContextConfiguration("/integration-context.xml")
@TestExecutionListeners({
		CucumberJmsRegistrationTestExecutionListener.class,
		CucumberDatabaseTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class
})
public class ReliableEmailDeliverySteps {

	private static final int SECONDS_TO_WAIT_FOR_EMAIL_SENDING = 5;

	@Autowired
	@Qualifier("ep-email-sender")
	private ModelCamelContext camelContext;

	@Autowired
	@Qualifier("epEmailSenderEmailSendingEndpoint")
	private Endpoint emailSendingEndpoint;

	@Autowired
	@Qualifier("emailSendingRedeliveryPolicy")
	private RedeliveryPolicy redeliveryPolicy;

	@Autowired
	private CamelContextMessagePurger camelContextMessagePurger;

	@Autowired
	private EmailSendingMockInterceptor emailSendingMockInterceptor;

	private MockEndpoint mockSmtpEndpoint;

	private Mailbox mailbox;
	private EmailDto emailMessage;
	private NotifyBuilder blockingUntilEmailSentNotifyBuilder;
	private String emailMessageJson;
	private int maxRetryAttempts;
	private long retryIntervalSeconds;

	@Before
	public void setUp() throws Exception {
		mockSmtpEndpoint = emailSendingMockInterceptor.wireTapEmailSending();
	}

	@After
	@Before
	public void drainCamelEndpoints() throws Exception {
		camelContextMessagePurger.purgeMessages(camelContext);
	}

	@After
	public void tearDown() {
		Mailbox.clearAll();
	}

	@Given("^the SMTP server is available$")
	public void verifySmtpServerAccessible() throws Throwable {
		// email sending is enabled by default
	}

	@When("^a message representing an email with recipient (.+) is published to the (.+) queue$")
	public void publishEmailMessage(final String recipientEmailAddress, final String queueName) throws Throwable {
		emailMessage = EmailDto.builder()
				.withTo(recipientEmailAddress)
				.withFrom("Sender Sendingman <sender@elasticpath.com>")
				.withSubject("Re: subject")
				.withContentType("text/plain")
				.withTextBody("Message contents")
				.build();

		blockingUntilEmailSentNotifyBuilder = new NotifyBuilder(camelContext)
				.from(emailSendingEndpoint.getEndpointUri())
				.whenCompleted(1)
				.create();

		emailMessageJson = createJsonFrom(emailMessage);

		camelContext.createProducerTemplate().sendBody(jms(queueName), emailMessageJson);
	}

	private String createJsonFrom(final EmailDto emailMessage) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(emailMessage);
	}

	@Then("^the corresponding email is sent to (.+)$")
	public void verifyEmailReceivedAtAddress(final String recipientEmailAddress) throws Throwable {
		assertTrue("Email not be sent within " + SECONDS_TO_WAIT_FOR_EMAIL_SENDING + " seconds",
				   blockingUntilEmailSentNotifyBuilder.matches(SECONDS_TO_WAIT_FOR_EMAIL_SENDING, TimeUnit.SECONDS));

		final Mailbox messages = Mailbox.get(recipientEmailAddress);
		assertEquals("Mailbox contains an unexpected number of email messages", 1, messages.size());

		final Message actualEmailMessage = messages.get(0);
		verifySentEmailMatchesExpected(emailMessage, actualEmailMessage);
	}

	private void verifySentEmailMatchesExpected(final EmailDto expectedEmailMessage, final Message actualEmailMessage) throws MessagingException,
			IOException {
		assertEquals("Unexpected email subject", expectedEmailMessage.getSubject(), actualEmailMessage.getSubject());

		final Address[] actualFroms = actualEmailMessage.getFrom();
		assertEquals("Unexpected number of 'from' addresses", 1, actualFroms.length);
		assertEquals("Unexpected email from address", expectedEmailMessage.getFrom(), actualFroms[0].toString());

		verifyAddressListMatchesExpected(expectedEmailMessage.getTo(), Message.RecipientType.TO, actualEmailMessage);
		verifyAddressListMatchesExpected(expectedEmailMessage.getCc(), Message.RecipientType.CC, actualEmailMessage);
		verifyAddressListMatchesExpected(expectedEmailMessage.getBcc(), Message.RecipientType.BCC, actualEmailMessage);

		if (expectedEmailMessage.getHtmlBody() == null) {
			assertEquals("Unexpected email content type", expectedEmailMessage.getContentType(), actualEmailMessage.getContentType());
			assertEquals("Unexpected email contents", expectedEmailMessage.getTextBody(), actualEmailMessage.getContent());
		} else {
			// Getting an HTML section from a Mail message is pretty hard.
			final Object content = actualEmailMessage.getContent();
			assertTrue("Expected email to contain a MimeMultipart message", content instanceof MimeMultipart);

			final BodyPart bodyPart = EmailContentUtil.findBodyPartByContentType((MimeMultipart) content, "text/html");

			assertEquals("Unexpected email content type", expectedEmailMessage.getContentType(), bodyPart.getContentType());
			assertEquals("Unexpected email contents", expectedEmailMessage.getHtmlBody(), bodyPart.getContent().toString());
		}
	}

	private void verifyAddressListMatchesExpected(final Collection<String> expectedAddresses, final Message.RecipientType recipientType,
												  final Message actualMessage) throws MessagingException {
		final Address[] actualAddresses = actualMessage.getRecipients(recipientType);
		if (actualAddresses == null) {
			assertTrue("No '" + recipientType + "' addresses set, but expected " + expectedAddresses.size(), expectedAddresses.isEmpty());
			return;
		}

		assertEquals("Unexpected number of '" + recipientType + "' addresses", expectedAddresses.size(), actualAddresses.length);

		for (final Address actualAddress : actualAddresses) {
			assertTrue("Unexpected '" + recipientType + "' address [" + actualAddress + "]",
					   expectedAddresses.contains(actualAddress.toString()));
		}
	}

	@Given("^the email sending service is configured to retry delivery every (\\d+) seconds up to a maximum of (\\d+) attempts$")
	public void configureEmailSendingRetryValues(final long retryIntervalSeconds, final int maxRetryAttempts) throws Throwable {
		this.maxRetryAttempts = maxRetryAttempts;
		this.retryIntervalSeconds = retryIntervalSeconds;

		redeliveryPolicy.setMaximumRedeliveryDelay(retryIntervalSeconds * 1000); // 1000 milliseconds per second
		redeliveryPolicy.setMaximumRedeliveries(maxRetryAttempts);

		camelContext.stopRoute("ep-email-sender");
		camelContext.startRoute("ep-email-sender");
	}

	@Given("^the SMTP server is unable to deliver messages to (.+)")
	public void makeSmtpServerUnableToSendTo(final String recipientEmailAddress) throws Throwable {
		mailbox = Mailbox.get(recipientEmailAddress);
		mailbox.setError(true);
	}

	@Then("^when the SMTP server becomes available again within the retry window$")
	public void reenableSmtpServer() throws Throwable {
		mailbox.setError(false);
	}

	@And("^all retries are exhausted$")
	public void waitForRetriesToExpire() throws Throwable {
		final MockEndpoint assertionMock = camelContext.getEndpoint("mock:assert", MockEndpoint.class);

		 // header populated by Camel and incremented on each retry
		assertionMock.expectedHeaderReceived("org.apache.camel.redeliveryCount", maxRetryAttempts);
		assertionMock.expectedMinimumMessageCount(1);

		final NotifyBuilder failureNotify = new NotifyBuilder(camelContext)
				.from(emailSendingEndpoint.getEndpointUri())
				.whenDoneSatisfied(assertionMock)
				.create();

		// The very first attempt (prior to error) does not count within the max retry count.
		//
		// Thus, a configuration of 2 retries looks like
		// 		original attempt
		//		+        retry 1
		// 		+        retry 2
		//		________________
		// 	  = 3 attempts total
		//
		// So why do we add the additional 2 seconds at the end?  Well, because
		// without it, the conditions are not met.  :(  Camel's NotifyBuilder is notoriously
		// flaky and inconsistent.  In this case the extra seconds make it happy, and if it's
		// not happy, we're not happy.
		final long maxRetryDuration = (maxRetryAttempts + 1) * retryIntervalSeconds + 2;

		assertTrue("Routing conditions not met within " + maxRetryDuration + " seconds",
				   failureNotify.matches(maxRetryDuration, TimeUnit.SECONDS));
	}

	@Then("^no email is delivered$")
	public void verifyNoEmailDelivered() throws Throwable {
		mockSmtpEndpoint.expectedMessageCount(0);
		mockSmtpEndpoint.assertIsSatisfied();
	}

	@Then("^the message is delivered to the (.+) queue$")
	public void verifyMessageDeliveredToQueue(final String queueName) throws Throwable {
		final BrowsableEndpoint endpoint = camelContext.getEndpoint(jms(queueName), BrowsableEndpoint.class);

		final Exchange exchange = endpoint.createPollingConsumer().receive(2000);
		assertNotNull("Message not sent to the " + queueName + " queue.", exchange);
		assertEquals("Unexpected message contents on the " + queueName + " queue", emailMessageJson, exchange.getIn().getBody());
	}

	@When("^a message that does not represent a valid email message is published to the (.+) queue$")
	public void publishMalformedEmailMessage(final String queueName) throws Throwable {
		emailMessageJson = "{\"foo\":\"bar\"}";

		camelContext.createProducerTemplate().sendBody(jms(queueName), emailMessageJson);
	}

	private String jms(final String queueName) {
		return "jms:" + queueName;
	}

}