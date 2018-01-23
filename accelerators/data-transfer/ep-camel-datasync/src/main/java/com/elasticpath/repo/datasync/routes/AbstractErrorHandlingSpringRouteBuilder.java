package com.elasticpath.repo.datasync.routes;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jms.JMSException;
import javax.persistence.PersistenceException;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.TransactedDefinition;
import org.apache.camel.spring.SpringRouteBuilder;

import com.elasticpath.repo.datasync.tools.exception.RecoverableMessageException;

/**
 * <p>A wrapper for {@code SpringRouteBuilder} to handle routing exceptions.</p>
 *
 * <p>Routes configured through this class are wrapped in a transaction.</p>
 *
 * <p>If the exception is of type {@link RecoverableMessageException}, a number of
 *    redelivery attempts may be performed depending on the configured redliveryPolicy.
 *    When all redelivery attempts are exhausted, the transaction is rolled back, and
 *    the exception is propagated back to activemq to be handled.</p>
 *
 * <p>For all other exceptions, the original message is directly moved to a dead letter
 *    queue with no redelivery attempted. In this case, the transaction is not rolled
 *    back and the exception is marked as handled.</p>
 */
public abstract class AbstractErrorHandlingSpringRouteBuilder extends SpringRouteBuilder {

	/**
	 * Dead letter queue message definition.
	 */
	protected static final String DLQ_ERROR_MESSAGE = " in.headers: ${in.headers}, in.body: ${in.body},"
			+ " exception: ${exception}, exception.stacktrace: ${exception.stacktrace}";

	/**
	 * Grep the logs for this string to find the non-recoverable errors.
	 */
	protected static final String DLQ_ERROR_TEXT = "Routing to DeadLetterQueue.";

	private Endpoint sourceEndpoint;

	private Endpoint deadLetterQueueEndpoint;

	private String transactionPolicyBeanId;

	private String redeliveryPolicyBeanId;

	/**
	 * Configure route.
	 *
	 * @param processorDefinition the route definition chain
	 * @return the route definition chain
	 */
	public abstract ProcessorDefinition<?> configureRoute(final ProcessorDefinition<?> processorDefinition);

	@Override
	public void configure() {
		errorHandler(transactionErrorHandler());

		onException(getRecoverableExceptionsArray())
			.redeliveryPolicyRef(redeliveryPolicyBeanId)
			.process(new Processor() {
				@Override
				public void process(final Exchange exchange) throws Exception {
					// Wrap exception in a RecoverableMessageException
					final Exception originalException = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
					if (!(originalException instanceof RecoverableMessageException)) {
						exchange.setException(new RecoverableMessageException(originalException));
					}
				}
			});

		onException(Exception.class)
			.onWhen(property(Exchange.EXCEPTION_CAUGHT).not().isInstanceOf(RecoverableMessageException.class))
				.handled(true)
				.useOriginalMessage()
				.log(LoggingLevel.ERROR,
						DLQ_ERROR_TEXT + DLQ_ERROR_MESSAGE)
				.to(deadLetterQueueEndpoint);

		RouteDefinition routeDef = from(sourceEndpoint);
		ProcessorDefinition<?> routeSpecificExceptionHandlingDef = getRouteSpecificOnExceptionHandling(routeDef);
		TransactedDefinition processorDefinition = routeSpecificExceptionHandlingDef.transacted(transactionPolicyBeanId);

		configureRoute(processorDefinition);
	}

	/**
	 * Add route specific handling to child classes in this method. By default no route specific error handling will be added.
	 *
	 * @param routeDefinition route definition
	 * @return route with specific error handling
	 */
	protected ProcessorDefinition<?> getRouteSpecificOnExceptionHandling(final ProcessorDefinition<?> routeDefinition) {
		return routeDefinition;
	}

	/**
	 * Returns the list of recoverable exceptions.
	 *
	 * @return the list of recoverable exceptions
	 */
	@SuppressWarnings("unchecked")
	protected List<Class<? extends Throwable>> getRecoverableExceptions() {
		List<Class<? extends Throwable>> exceptions = new ArrayList<Class<? extends Throwable>>();
		exceptions.addAll(Arrays.asList(
				RecoverableMessageException.class,
				PersistenceException.class,
				JMSException.class
			));
		return exceptions;
	}

	@SuppressWarnings("unchecked")
	private Class<? extends Throwable>[] getRecoverableExceptionsArray() {
		final List<Class<? extends Throwable>> recoverableExceptions = getRecoverableExceptions();
		Class<? extends Throwable>[] classArray =
				(Class<? extends Throwable>[]) Array.newInstance(Class.class, recoverableExceptions.size());
		return recoverableExceptions.toArray(classArray);
	}

	public void setSourceEndpoint(final Endpoint sourceEndpoint) {
		this.sourceEndpoint = sourceEndpoint;
	}

	protected Endpoint getDeadLetterQueueEndpoint() {
		return deadLetterQueueEndpoint;
	}
	
	public void setDeadLetterQueueEndpoint(final Endpoint deadLetterQueueEndpoint) {
		this.deadLetterQueueEndpoint = deadLetterQueueEndpoint;
	}

	public void setTransactionPolicyBeanId(final String transactionPolicyBeanId) {
		this.transactionPolicyBeanId = transactionPolicyBeanId;
	}

	public void setRedeliveryPolicyBeanId(final String redeliveryPolicyBeanId) {
		this.redeliveryPolicyBeanId = redeliveryPolicyBeanId;
	}

}
