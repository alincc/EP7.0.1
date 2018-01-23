/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.email;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collections;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.junit.Test;

/**
 * Test class for {@link com.elasticpath.email.EmailContentUtil}.
 */
public class EmailContentUtilTest {

	private static final String TEXT_HTML = "text/html";
	private static final String TEXT_PLAIN = "text/plain";

	private static final String HTML_MESSAGE = "<html><body>This is an HTML message</body></html>";
	private static final String TEXT_MESSAGE = "This is a plaintext message.";

	private static final String UNEXPECTED_EMAIL_CONTENTS_FOR_CONTENT_TYPE_TEXT_HTML = "Unexpected email contents for content type \"text/html\"";
	private static final String UNEXPECTED_EMAIL_CONTENTS_FOR_CONTENT_TYPE_TEXT_PLAIN = "Unexpected email contents for content type \"text/plain\"";

	@Test
	public void verifyNullReturnedWhenNoSuchContentTypeForBodyPart() throws Exception {
		final MimeMessage mimeMessage = createMimeMessage(TEXT_MESSAGE, HTML_MESSAGE);

		final MimeMultipart mimeMultipart = getMimeMultipart(mimeMessage);

		final BodyPart bodyPart = EmailContentUtil.findBodyPartByContentType(mimeMultipart, "no/such");
		assertNull("Expected null returned for unknown content type", bodyPart);
	}

	@Test
	public void verifyNullReturnedWhenNoSuchContentTypeForContent() throws Exception {
		final MimeMessage mimeMessage = createMimeMessage(TEXT_MESSAGE, HTML_MESSAGE);

		final MimeMultipart mimeMultipart = getMimeMultipart(mimeMessage);

		final String contents = EmailContentUtil.findBodyPartContentsByContentType(mimeMultipart, "no/such");
		assertNull("Expected null returned for unknown content type", contents);
	}

	@Test
	public void verifyUtilFindsPlainTextBodyInMultipartMessage() throws Exception {
		final MimeMessage mimeMessage = createMimeMessage(TEXT_MESSAGE, HTML_MESSAGE);

		final MimeMultipart mimeMultipart = getMimeMultipart(mimeMessage);

		final BodyPart bodyPart = EmailContentUtil.findBodyPartByContentType(mimeMultipart, TEXT_PLAIN);
		assertEquals(UNEXPECTED_EMAIL_CONTENTS_FOR_CONTENT_TYPE_TEXT_PLAIN, TEXT_MESSAGE, bodyPart.getContent());
	}

	@Test
	public void verifyUtilFindsPlainTextContentInMultipartMessage() throws Exception {
		final MimeMessage mimeMessage = createMimeMessage(TEXT_MESSAGE, HTML_MESSAGE);

		final MimeMultipart mimeMultipart = getMimeMultipart(mimeMessage);

		final String contents = EmailContentUtil.findBodyPartContentsByContentType(mimeMultipart, TEXT_PLAIN);
		assertEquals(UNEXPECTED_EMAIL_CONTENTS_FOR_CONTENT_TYPE_TEXT_PLAIN, TEXT_MESSAGE, contents);
	}

	@Test
	public void verifyUtilFindsHtmlTextBodyInMultipartMessage() throws Exception {
		final MimeMessage mimeMessage = createMimeMessage(TEXT_MESSAGE, HTML_MESSAGE);

		final MimeMultipart mimeMultipart = getMimeMultipart(mimeMessage);

		final BodyPart bodyPart = EmailContentUtil.findBodyPartByContentType(mimeMultipart, TEXT_HTML);
		assertEquals(UNEXPECTED_EMAIL_CONTENTS_FOR_CONTENT_TYPE_TEXT_HTML, HTML_MESSAGE, bodyPart.getContent());
	}

	@Test
	public void verifyUtilFindsHtmlTextContentInMultipartMessage() throws Exception {
		final MimeMessage mimeMessage = createMimeMessage(TEXT_MESSAGE, HTML_MESSAGE);

		final MimeMultipart mimeMultipart = getMimeMultipart(mimeMessage);

		final String contents = EmailContentUtil.findBodyPartContentsByContentType(mimeMultipart, TEXT_HTML);
		assertEquals(UNEXPECTED_EMAIL_CONTENTS_FOR_CONTENT_TYPE_TEXT_HTML, HTML_MESSAGE, contents);
	}

	@Test
	public void verifyUtilFindsHtmlTextBodyInSingleMessageTypeMessage() throws Exception {
		final MimeMessage mimeMessage = createMimeMessage(null, HTML_MESSAGE);

		final MimeMultipart mimeMultipart = getMimeMultipart(mimeMessage);

		final BodyPart bodyPart = EmailContentUtil.findBodyPartByContentType(mimeMultipart, TEXT_HTML);
		assertEquals(UNEXPECTED_EMAIL_CONTENTS_FOR_CONTENT_TYPE_TEXT_HTML, HTML_MESSAGE, bodyPart.getContent());
	}

	@Test
	public void verifyUtilFindsHtmlTextContentInSingleMessageTypeMessage() throws Exception {
		final MimeMessage mimeMessage = createMimeMessage(null, HTML_MESSAGE);

		final MimeMultipart mimeMultipart = getMimeMultipart(mimeMessage);

		final String contents = EmailContentUtil.findBodyPartContentsByContentType(mimeMultipart, TEXT_HTML);
		assertEquals(UNEXPECTED_EMAIL_CONTENTS_FOR_CONTENT_TYPE_TEXT_HTML, HTML_MESSAGE, contents);
	}

	private MimeMessage createMimeMessage(final String textMessage, final String htmlMessage) throws EmailException, MessagingException {
		final HtmlEmail email = new HtmlEmail();
		if (textMessage != null) {
			email.setTextMsg(textMessage);
		}

		if (htmlMessage != null) {
			email.setHtmlMsg(htmlMessage);
		}

		email.setFrom("from@from.com");
		email.setTo(Collections.singletonList(new InternetAddress("to@to.foo")));
		email.setHostName("localhost");
		email.buildMimeMessage();
		final MimeMessage mimeMessage = email.getMimeMessage();
		mimeMessage.saveChanges();
		return mimeMessage;
	}

	private MimeMultipart getMimeMultipart(final MimeMessage mimeMessage) throws IOException, MessagingException {
		final Object emailContent = mimeMessage.getContent();
		assertTrue("Email content should be of type MimeMultipart for an email with both HTML and plain text content",
				emailContent instanceof MimeMultipart);

		return (MimeMultipart) emailContent;
	}

}