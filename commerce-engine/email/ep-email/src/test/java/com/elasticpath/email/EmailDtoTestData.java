/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.email;

/**
 * Generates Email Test data for unit testing purposes.
 */
public final class EmailDtoTestData {

	private static final String CHARSET = "UTF-8";
	private static final String HTML_CONTENT_TYPE = "text/html; charset=" + CHARSET;
	private static final String PLAIN_TEXT_CONTENT_TYPE = "text/plain; charset=" + CHARSET;
	private static final String FROM_NAME = "Sender Sendingman";
	private static final String FROM_ADDRESS = "sender@elasticpath.com";
	private static final String FROM_COMBINED = FROM_NAME + " <" + FROM_ADDRESS + ">";
	private static final String SUBJECT = "Re: Subject";
	private static final String TO_1 = "recipient-one@elasticpath.com";
	private static final String TO_2 = "recipient-two@elasticpath.com";
	private static final String CC_1 = "cc-recipient-one@elasticpath.com";
	private static final String CC_2 = "cc-recipient-two@elasticpath.com";
	private static final String BCC_1 = "bcc-recipient-one@elasticpath.com";
	private static final String BCC_2 = "bcc-recipient-two@elasticpath.com";
	private static final String REPLY_TO_1 = "senders-PA@elasticpath.com";
	private static final String REPLY_TO_2 = "senders-PAs-PA@elasticpath.com";
	private static final String MESSAGE_PLAIN_TEXT = "This is an email message.";
	private static final String MESSAGE_HTML = "<html><body>This is an email message.</body></html>";

	/**
	 * Private constructor.
	 */
	private EmailDtoTestData() {
		// cannot be instantiated
	}

	/**
	 * Returns an EmailDto Builder configured with a standard set of values for a text/plain email.
	 * @return an EmailDto Builder configured with a standard set of values
	 */
	public static EmailDto.Builder<?> plainTextEmailDtoBuilder() {
		return EmailDto.builder()
				.withContentType(PLAIN_TEXT_CONTENT_TYPE)
				.withFrom(FROM_COMBINED)
				.withSubject(SUBJECT)
				.withTo(TO_1, TO_2)
				.withCc(CC_1, CC_2)
				.withBcc(BCC_1, BCC_2)
				.withReplyTo(REPLY_TO_1, REPLY_TO_2)
				.withTextBody(MESSAGE_PLAIN_TEXT);
	}

	/**
	 * Returns an EmailDto Builder configured with a standard set of values for a text/html email.
	 * @return an EmailDto Builder configured with a standard set of values
	 */
	public static EmailDto.Builder<?> htmlEmailDtoBuilder() {
		return plainTextEmailDtoBuilder()
				.withContentType(HTML_CONTENT_TYPE)
				.withHtmlBody(MESSAGE_HTML);
	}

}