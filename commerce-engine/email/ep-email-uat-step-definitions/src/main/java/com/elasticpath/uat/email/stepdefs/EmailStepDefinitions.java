/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.uat.email.stepdefs;

import static org.apache.camel.builder.Builder.header;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.velocity.tools.generic.DateTool;
import org.jvnet.mock_javamail.Mailbox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.elasticpath.email.EmailContentUtil;
import com.elasticpath.email.test.support.EmailEnabler;
import com.elasticpath.email.test.support.EmailSendingMockInterceptor;
import com.elasticpath.uat.ScenarioContextValueHolder;

/**
 * Step Definitions for email functionality.
 */
@ContextConfiguration("classpath:cucumber.xml")
public class EmailStepDefinitions {

	private static final String DEFAULT_DATE_FORMAT_STRING = "MMMM d, yyyy";

	private static final long MAX_SECONDS_TO_WAIT_FOR_EMAIL = 20;

	@Autowired
	private EmailEnabler emailEnabler;

	@Autowired
	private ScenarioContextValueHolder<Map<String, Message>> emailMessagesHolder;

	@Autowired
	private EmailSendingMockInterceptor emailSendingMockInterceptor;

	@Autowired
	private ScenarioContextValueHolder<Runnable> emailSendingCommandHolder;

	@After
	public void tearDown() {
		Mailbox.clearAll();
	}

	@Given("^email sending is enabled")
	public void enableEmail() {
		emailEnabler.setEmailEnabledSettingDefaultValue(true);
	}
	@Given("^email sending is disabled")
	public void disableEmailSettings() {
		emailEnabler.setEmailEnabledSettingDefaultValue(false);
	}
	@Given("^email sending is updated to be enabled")
	public void disableEmailValue() throws Throwable {
		emailEnabler.setEmailEnabledSettingsValue(true);
	}


	@Then("^(?:.+) should receive (\\d+) email(?:\\(s\\))? in (?:my|their) (.+) inbox$")
	public void verifyEmailsReceived(final int expectedNumberOfEmails, final String recipientEmailAddress) throws Exception {
		final NotifyBuilder notifyBuilder = emailSendingMockInterceptor.createNotifyBuilderForEmailSendingMockInterceptor()
				.filter(header("to").contains(recipientEmailAddress))
				.whenDone(expectedNumberOfEmails)
				.create();

		// Execute the code that will result in an email being sent. To avoid a race condition we can't
		// have that code run before the notify builder is created.
		emailSendingCommandHolder.get().run();

		assertTrue("Timed out waiting for email to be sent",
				   notifyBuilder.matches(MAX_SECONDS_TO_WAIT_FOR_EMAIL, TimeUnit.SECONDS));

		final Mailbox messages = Mailbox.get(recipientEmailAddress);
		assertEquals("Mailbox contains an unexpected number of email messages", expectedNumberOfEmails, messages.size());

		final Map<String, Message> emailMessageMap = new HashMap<>(messages.size());
		for (final Message message : messages) {
			emailMessageMap.put(message.getSubject(), message);

			final Object content = message.getContent();

			assertThat("Email content should be a MimeMultipart (for HTML & text) or String (for text only) instance",
					   content, anyOf(
							   instanceOf(MimeMultipart.class),
							   instanceOf(String.class)));
		}

		emailMessagesHolder.set(emailMessageMap);
	}

	@Then("^the subject of(?: one of)? the emails? should be \"([^\"]*)\"$")
	public void verifyEmailSubject(final String expectedSubject) throws Exception {
		assertThat("No such email found", emailMessagesHolder.get(), hasKey(expectedSubject));
	}

	@Then("^the subject of(?: one of)? the emails? should contain \"([^\"]*)\"$")
	public void verifyEmailSubjectSubstring(final String expectedSubjectSubstring) throws Exception {
		for (final Message message : emailMessagesHolder.get().values()) {
			if (message.getSubject().contains(expectedSubjectSubstring)) {

				// Since we're identifying this email by a substring of its subject, we're never going to examine its
				// full subject name in another step definition - we're going to refer to it by this substring.
				// We must clear out the map entry for the whole subject and replace it with the identifying part that we care about.
				emailMessagesHolder.get().put(message.getSubject(), null);
				emailMessagesHolder.get().put(expectedSubjectSubstring, message);

				return;
			}
		}

		fail("No such email found");
	}

	@Then("^the(?: \"(.+)\")? email should contain today's date$")
	public void verifyEmailContainsCurrentDate(final String emailSubject) throws Throwable {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, emailMessagesHolder.get()));
		assertThat("The email contents should include today's date",
				   emailContents, containsString(new DateTool().get(DEFAULT_DATE_FORMAT_STRING)));
	}

	/**
	 * <p>Convenience method to assist in retrieving a particular message by subject.</p>
	 * <p>Step Definitions for examining one of many emails are expected to be identified by email subject, in the following format:</p>
	 * <ul>
	 * <li>[Given] the <em>property</em> of the "Foo" email is ___</li>
	 * <li>[Given] the <em>property</em> of the "Bar" email is ___</li>
	 * <li>etc.</li>
	 * </ul>
	 * <p>It is also permitted to omit the "Subject" element when expecting only one email, i.e.</p>
	 * <ul>
	 * <li>"[Given] the <em>property</em> of the email is ___"</li>
	 * </ul>
	 * <p>This method will return the corresponding Message identified by subject, or the first email found if subject is omitted.</p>
	 *
	 * @param emailSubject the subject of the email to retrieve
	 * @param emailMessageMap the map of emails to inspect
	 * @return the corresponding Message
	 */
	public static Message getEmailMessageBySubject(final String emailSubject, final Map<String, Message> emailMessageMap) {
		if (emailSubject == null) {
			return Iterables.find(emailMessageMap.values(), new MessagePredicate());
		}

		return emailMessageMap.get(emailSubject);
	}

	/**
	 * Convenience method to return the contents of the given {@link javax.mail.Message} as a String.  This accommodates both simple text/plain
	 * emails as well as complex {@link MimeMultipart} messages used in text/html emails.
	 *
	 * @param message the Message
	 * @return a String containing the Message's contents
	 * @throws IOException in case of errors while reading the input MimeMultipart contents
	 * @throws MessagingException in case of inconsistent MimeMultipart contents
	 */
	public static String getContents(final Message message) throws IOException, MessagingException {
		final Object content = message.getContent();

		if (content instanceof String) {
			return (String) content;
		}

		assertTrue("Non-String Email content should be a MimeMultipart instance", content instanceof MimeMultipart);

		final String bodyPartContent = EmailContentUtil.findBodyPartContentsByContentType((MimeMultipart) content, "text/html");

		assertNotNull("No text/html body part found", bodyPartContent);

		return bodyPartContent;
	}



	/**
	 * Predicate used for finding a message that has content.
	 */
	private static class MessagePredicate implements Predicate<Message> {
		@Override
		public boolean apply(final Message message) {
			boolean goodMessage;
			try {
				goodMessage = message != null && message.getContent() != null;
			} catch (IOException e) {
				goodMessage = false;
			} catch (MessagingException e) {
				goodMessage = false;
			}

			return goodMessage;
		}
	}
}
