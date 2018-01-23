/**
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;

import com.elasticpath.commons.exception.EpValidationException;
import com.elasticpath.commons.exception.InvalidBusinessStateException;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ExceptionTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;

/**
 * Reactive Adapter implementation.
 */
@Singleton
@Named("reactiveAdapter")
public final class ReactiveAdapterImpl implements ReactiveAdapter {

	private final ExceptionTransformer exceptionTransformer;

	/**
	 * Constructor.
	 *
	 * @param exceptionTransformer the exception transformer
	 */
	@Inject
	ReactiveAdapterImpl(
			@Named("exceptionTransformer")
			final ExceptionTransformer exceptionTransformer) {
		this.exceptionTransformer = exceptionTransformer;
	}

	@Override
	public <T> Observable<T> fromService(final Callable<T> serviceCall) {
		return fromService(serviceCall, "");
	}

	@Override
	public <T> Observable<T> fromService(final Callable<T> serviceCall, final String notFoundExceptionMessage) {
		return Observable.fromCallable(serviceCall)
				.onErrorResumeNext(getThrowableObservableFunc())
				.filter(Objects::nonNull)
				.switchIfEmpty(Observable.error(ResourceOperationFailure.notFound(notFoundExceptionMessage)));
	}

	@Override
	public <T> Single<T> fromServiceAsSingle(final Callable<T> serviceCall) {
		return fromService(serviceCall)
				.toSingle();
	}

	@Override
	public <T> Single<T> fromServiceAsSingle(final Callable<T> serviceCall, final String notFoundExceptionMessage) {
		return fromService(serviceCall, notFoundExceptionMessage)
				.toSingle();
	}

	@Override
	public <T> Completable fromServiceAsCompletable(final Callable<T> serviceCall) {
		return Completable.fromCallable(serviceCall)
				.onErrorResumeNext(getThrowableCompletableFunc());
	}

	@Override
	public <T> Observable<T> fromRepository(final Supplier<ExecutionResult<? extends Iterable<T>>> repositoryCall) {
		return Observable.defer(() -> repositoryCall.get().toObservable());
	}

	@Override
	public Completable fromRepositoryAsCompletable(final Supplier<ExecutionResult<?>> repositoryCall) {
		return Completable.defer(() -> repositoryCall.get().toCompletable());
	}

	@Override
	public <T> Single<T> fromRepositoryAsSingle(final Supplier<ExecutionResult<T>> repositoryCall) {
		return Single.defer(() -> repositoryCall.get().toSingle());
	}

	private <T extends Throwable> Throwable transformToResourceOperationFailureIfPossible(final T throwable) {
		if (throwable instanceof InvalidBusinessStateException) {
			return exceptionTransformer.getResourceOperationFailure((InvalidBusinessStateException) throwable);
		} else if (throwable instanceof EpValidationException) {
			return exceptionTransformer.getResourceOperationFailure((EpValidationException) throwable);
		}
		return throwable;
	}

	private <T> Func1<Throwable, Observable<? extends T>> getThrowableObservableFunc() {
		return throwable -> Observable.error(transformToResourceOperationFailureIfPossible(throwable));
	}

	private Func1<Throwable, Completable> getThrowableCompletableFunc() {
		return throwable -> Completable.error(transformToResourceOperationFailureIfPossible(throwable));
	}
}
