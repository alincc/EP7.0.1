/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.email;

import java.io.IOException;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

/**
 * Utility method for examining {@link javax.mail.Message Email Message} contents.
 */
public final class EmailContentUtil {

	/**
	 * Private constructor.
	 */
	private EmailContentUtil() {
		// cannot instantiate utility class
	}

	/**
	 * <p>
	 * Finds and returns the {@link BodyPart} of the provided {@link MimeMultipart} that has a content type starting with {@code requiredContentType}
	 * , or {@code null} if no such {@code BodyPart} can be found.
	 * </p>
	 * <p>
	 * A {@link BodyPart} is considered a match if its content type begins with {@code requiredContentType} in order to match cases where the
	 * character set is included, for example {@code "text/html; charset=UTF-8"}.
	 * </p>
	 *
	 * @param mimeMultipart the mime multipart message
	 * @param requiredContentType the content type to find
	 * @return the matching {@link BodyPart}, or {@code null} if none exists
	 * @throws MessagingException in case of inconsistent {@code mimeMultipart} contents
	 * @throws IOException in case of errors while reading the input {@code mimeMultipart} contents
	 */
	public static BodyPart findBodyPartByContentType(final MimeMultipart mimeMultipart, final String requiredContentType) throws MessagingException,
			IOException {
		for (int i = 0; i < mimeMultipart.getCount(); i++) {
			final BodyPart bodyPart = mimeMultipart.getBodyPart(i);

			final String contentType = bodyPart.getContentType();
			final Object content = bodyPart.getContent();

			if (content.getClass().isAssignableFrom(MimeMultipart.class)) {
				return findBodyPartByContentType((MimeMultipart) content, requiredContentType);
			} else if (contentType.startsWith(requiredContentType)) {
				return bodyPart;
			}
		}

		return null;
	}

	/**
	 * <p>
	 * Finds and returns the contents of a {@link BodyPart} of the provided {@link MimeMultipart} that has a content type starting with {@code
	 * requiredContentType}, or {@code null} if no such {@code BodyPart} can be found.
	 * </p>
	 * <p>
	 * A {@link BodyPart} is considered a match if its content type begins with {@code requiredContentType} in order to match cases where the
	 * character set is included, for example {@code "text/html; charset=UTF-8"}.
	 * </p>
	 *
	 * @param mimeMultipart the mime multipart message
	 * @param requiredContentType the content type to find
	 * @return the String contents of the matching {@link BodyPart}, or {@code null} if none exists
	 * @throws MessagingException in case of inconsistent {@code mimeMultipart} contents
	 * @throws IOException in case of errors while reading the input {@code mimeMultipart} contents
	 */
	public static String findBodyPartContentsByContentType(final MimeMultipart mimeMultipart, final String requiredContentType) throws
			MessagingException, IOException {
		final BodyPart bodyPart = findBodyPartByContentType(mimeMultipart, requiredContentType);

		if (bodyPart == null) {
			return null;
		}

		return bodyPart.getContent().toString();
	}

}
