/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.transform;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import rx.Completable;
import rx.Observable;
import rx.Single;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Converts calls to nullable services and legacy repositories to RX java's reactive equivalents.
 */
public interface ReactiveAdapter {

	/**
	 * Creates an Observable with a deferred execution of a nullable CE service call.
	 * In the case the CE service call will return null a "not found" ResourceOperationFailure
	 * is created and registered on the Observable's error channel. In the case the CE service call
	 * will throw an unchecked exception this exception is captured and registered on
	 * the Observable's error channel.
	 *
	 * @param serviceCall service call
	 * @param <T>         service call return type
	 * @return Observable with deferred service call execution
	 */
	<T> Observable<T> fromService(Callable<T> serviceCall);

	/**
	 * Creates an Observable with a deferred execution of a nullable CE service call.
	 * In the case the CE service call will return null a "not found" ResourceOperationFailure
	 * is created and registered on the Observable's error channel. In the case the CE service call
	 * will throw an unchecked exception this exception is captured and registered on
	 * the Observable's error channel.
	 *
	 * @param serviceCall              service call
	 * @param notFoundExceptionMessage the exception message
	 * @param <T>                      service call return type
	 * @return Observable with deferred service call execution
	 */
	<T> Observable<T> fromService(Callable<T> serviceCall, String notFoundExceptionMessage);

	/**
	 * Creates an Single with a deferred execution of a nullable CE service call.
	 * In the case the CE service call will return null a "not found" ResourceOperationFailure
	 * is created and registered on the Single's error channel. In the case the CE service call
	 * will throw an unchecked exception this exception is captured and registered on
	 * the Single's error channel.
	 *
	 * @param serviceCall service call
	 * @param <T>         service call return type
	 * @return Single with deferred service call execution
	 */
	<T> Single<T> fromServiceAsSingle(Callable<T> serviceCall);

	/**
	 * Creates an Single with a deferred execution of a nullable CE service call.
	 * In the case the CE service call will return null a "not found" ResourceOperationFailure
	 * is created and registered on the Single's error channel. In the case the CE service call
	 * will throw an unchecked exception this exception is captured and registered on
	 * the Single's error channel.
	 *
	 * @param serviceCall              service call
	 * @param notFoundExceptionMessage the exception message
	 * @param <T>                      service call return type
	 * @return Single with deferred service call execution
	 */
	<T> Single<T> fromServiceAsSingle(Callable<T> serviceCall, String notFoundExceptionMessage);

	/**
	 * Creates an Completable with a deferred execution of a nullable CE service call.
	 * In the case the CE service call will throw an unchecked exception this exception
	 * is captured and registered on the Completable's error channel.
	 *
	 * @param serviceCall service call
	 * @param <T>         service call return type
	 * @return Completable with deferred service call execution
	 */
	<T> Completable fromServiceAsCompletable(Callable<T> serviceCall);

	/**
	 * Creates an Observable with a deferred execution of a given legacy repository method which emits
	 * an ExecutionResult which is compatible with Observable semantics {@see ExecutionResult#toObservable}.
	 *
	 * @param repositoryCall non reactive repository call
	 * @param <T>            observable return type
	 * @return Observable with deferred repository execution
	 */
	<T> Observable<T> fromRepository(Supplier<ExecutionResult<? extends Iterable<T>>> repositoryCall);

	/**
	 * Creates an Observable with a deferred execution of a given legacy repository method which emits
	 * an ExecutionResult which is compatible with Completable semantics {@see ExecutionResult#toCompletable}.
	 *
	 * @param repositoryCall non reactive repository call
	 * @return Observable with deferred repository execution
	 */
	Completable fromRepositoryAsCompletable(Supplier<ExecutionResult<?>> repositoryCall);

	/**
	 * Creates an Observable with a deferred execution of a given legacy repository method which emits
	 * an ExecutionResult which is compatible with Single semantics {@see ExecutionResult#toSingle}.
	 *
	 * @param repositoryCall non reactive repository call
	 * @param <T>            repository call return type
	 * @return Observable with deferred repository execution
	 */
	<T> Single<T> fromRepositoryAsSingle(Supplier<ExecutionResult<T>> repositoryCall);
}
