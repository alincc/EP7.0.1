/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.email;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.collect.Lists;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.elasticpath.base.exception.EpServiceException;

/**
 * DTO class representing the body and metadata of an email message.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.ShortVariable"})
public class EmailDto implements Serializable {

	private static final long serialVersionUID = -5690827309592857154L;

	private String contentType;
	private String from;
	private String subject;

	private List<String> to;

	private List<String> cc;
	private List<String> bcc;
	private List<String> replyTo;
	private String htmlBody;
	private String textBody;

	public void setContentType(final String contentType) {
		this.contentType = contentType;
	}

	public String getContentType() {
		return contentType;
	}

	public void setFrom(final String from) {
		this.from = from;
	}

	public String getFrom() {
		return from;
	}

	public void setSubject(final String subject) {
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}

	@JsonSetter
	public void setTo(final List<String> to) {
		this.to = to;
	}

	public void setTo(final String... to) {
		this.to = Lists.newArrayList(to);
	}

	public List<String> getTo() {
		return to;
	}

	@JsonSetter
	public void setCc(final List<String> cc) {
		this.cc = cc;
	}

	public void setCc(final String... cc) {
		this.cc = Lists.newArrayList(cc);
	}

	public List<String> getCc() {
		return cc;
	}

	@JsonSetter
	public void setBcc(final List<String> bcc) {
		this.bcc = bcc;
	}

	public void setBcc(final String... bcc) {
		this.bcc = Lists.newArrayList(bcc);
	}

	public List<String> getBcc() {
		return bcc;
	}

	@JsonSetter
	public void setReplyTo(final List<String> replyTo) {
		this.replyTo = replyTo;
	}

	public void setReplyTo(final String... replyTo) {
		this.replyTo = Lists.newArrayList(replyTo);
	}

	public List<String> getReplyTo() {
		return replyTo;
	}

	public void setHtmlBody(final String htmlBody) {
		this.htmlBody = htmlBody;
	}

	public String getHtmlBody() {
		return htmlBody;
	}

	public void setTextBody(final String textBody) {
		this.textBody = textBody;
	}

	public String getTextBody() {
		return textBody;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	/**
	 * Factory method to return a Builder capable of producing new EmailDto instances.
	 * 
	 * @return a Builder
	 */
	public static Builder<?> builder() {
		return new EmailDtoBuilderProvider();
	}

	/**
	 * Concrete implementation of {@link Builder} for EmailDto instances.
	 */
	private static class EmailDtoBuilderProvider extends Builder<EmailDtoBuilderProvider> {

		@Override
		protected EmailDtoBuilderProvider self() {
			return this;
		}

	}

	/**
	 * Builder class that constructs {@link EmailDto} instances.
	 * @param <T> Builder type
	 */
	@SuppressWarnings("PMD.AbstractNaming")
	public abstract static class Builder<T extends Builder<T>> {
		private final EmailDto dto = new EmailDto();
		private final List<String> tos = new ArrayList<>();
		private final List<String> ccs = new ArrayList<>();
		private final List<String> bccs = new ArrayList<>();
		private final List<String> replyTos = new ArrayList<>();

		/**
		 * Returns a reference to this Builder.
		 *
		 * @return this Builder
		 */
		protected abstract T self();

		/**
		 * Specifies an EmailDto instance to use a prototype.
		 *
		 * @param dto the prototype EmailDto
		 * @return the builder
		 */
		public T fromPrototype(final EmailDto dto) {
			try {
				BeanUtils.copyProperties(this.dto, dto);
			} catch (IllegalAccessException e) {
				throw new EpServiceException("Could not populate EmailDto", e);
			} catch (InvocationTargetException e) {
				throw new EpServiceException("Could not populate EmailDto", e);
			}

			withTo(dto.getTo());
			withCc(dto.getCc());
			withBcc(dto.getBcc());
			withReplyTo(dto.getReplyTo());

			return self();
		}

		/**
		 * Builds an {@link EmailDto} instance.
		 * 
		 * @return an {@link EmailDto} instance.
		 */
		public EmailDto build() {
			dto.setTo(tos);
			dto.setCc(ccs);
			dto.setBcc(bccs);
			dto.setReplyTo(replyTos);
			return dto;
		}

		/**
		 * Adds a BCC address.
		 * 
		 * @param bcc the address
		 * @return the builder
		 */
		public T addBcc(final String bcc) {
			this.bccs.add(bcc);
			return self();
		}

		/**
		 * Sets the BCC addresses.
		 * 
		 * @param bccs the addresses
		 * @return the builder
		 */
		public T withBcc(final String... bccs) {
			this.bccs.clear();
			this.bccs.addAll(Lists.newArrayList(bccs));
			return self();
		}

		/**
		 * Sets the BCC addresses.
		 * 
		 * @param bccs the addresses
		 * @return the builder
		 */
		public T withBcc(final List<String> bccs) {
			this.bccs.clear();
			this.bccs.addAll(bccs);
			return self();
		}

		/**
		 * Adds a CC address.
		 * 
		 * @param cc the address
		 * @return the builder
		 */
		public T addCc(final String cc) {
			this.ccs.add(cc);
			return self();
		}

		/**
		 * Sets the CC addresses.
		 *
		 * @param ccs the addresses
		 * @return the builder
		 */
		public T withCc(final String... ccs) {
			this.ccs.clear();
			this.ccs.addAll(Lists.newArrayList(ccs));
			return self();
		}

		/**
		 * Sets the addresses.
		 *
		 * @param ccs the addresses
		 * @return the builder
		 */
		public T withCc(final List<String> ccs) {
			this.ccs.clear();
			this.ccs.addAll(ccs);
			return self();
		}

		/**
		 * Adds a recipient address.
		 *
		 * @param to the address
		 * @return the builder
		 */
		public T addTo(final String to) {
			this.tos.add(to);
			return self();
		}

		/**
		 * Sets the recipient addresses.
		 * 
		 * @param tos the addresses
		 * @return the builder
		 */
		public T withTo(final String... tos) {
			this.tos.clear();
			this.tos.addAll(Lists.newArrayList(tos));
			return self();
		}

		/**
		 * Sets the recipient addresses.
		 * 
		 * @param tos the addresses
		 * @return the builder
		 */
		public T withTo(final List<String> tos) {
			this.tos.clear();
			this.tos.addAll(tos);
			return self();
		}

		/**
		 * Adds a Reply-To address.
		 * 
		 * @param replyTo the address
		 * @return the builder
		 */
		public T addReplyTo(final String replyTo) {
			this.replyTos.add(replyTo);
			return self();
		}

		/**
		 * Sets the Reply-To addresses.
		 * 
		 * @param replyTos the addresses
		 * @return the builder
		 */
		public T withReplyTo(final String... replyTos) {
			this.replyTos.clear();
			this.replyTos.addAll(Lists.newArrayList(replyTos));
			return self();
		}

		/**
		 * Sets the Reply-To addresses.
		 * 
		 * @param replyTos the addresses
		 * @return the builder
		 */
		public T withReplyTo(final List<String> replyTos) {
			this.replyTos.clear();
			this.replyTos.addAll(replyTos);
			return self();
		}

		/**
		 * Sets the Content-Type.
		 * 
		 * @param contentType the content type
		 * @return the builder
		 */
		public T withContentType(final String contentType) {
			dto.setContentType(contentType);
			return self();
		}

		/**
		 * Sets the from address.
		 * 
		 * @param from the from address
		 * @return the builder
		 */
		public T withFrom(final String from) {
			dto.setFrom(from);
			return self();
		}

		/**
		 * Sets the HTML body.
		 * 
		 * @param htmlBody the HTML body
		 * @return the builder
		 */
		public T withHtmlBody(final String htmlBody) {
			dto.setHtmlBody(htmlBody);
			return self();
		}

		/**
		 * Sets the subject.
		 * 
		 * @param subject the subject
		 * @return the builder
		 */
		public T withSubject(final String subject) {
			dto.setSubject(subject);
			return self();
		}

		/**
		 * Sets the text body.
		 * 
		 * @param textBody the text body
		 * @return the builder
		 */
		public T withTextBody(final String textBody) {
			dto.setTextBody(textBody);
			return self();
		}
	}

}
