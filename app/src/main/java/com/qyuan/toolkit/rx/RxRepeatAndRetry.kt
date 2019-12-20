package com.qyuan.toolkit.rx

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import io.reactivex.exceptions.Exceptions
import io.reactivex.functions.Predicate
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/** Repeats and retries an observable, with delays between attempts. */
class RxRepeatAndRetry<T> internal constructor(
    private val pollingIntervals: Observable<Long>,
    private val pollingUnit: TimeUnit,
    private val maxErrorLimit: Int,
    private val fatalErrors: Predicate<in Throwable>,
    private val scheduler: Scheduler
) : ObservableTransformer<T, T> {
    override fun apply(upstream: Observable<T>): ObservableSource<T> = upstream.materialize()
        .repeatWhen { completions ->
            Observables
                .zip(completions, pollingIntervals) { _, pollingInterval -> pollingInterval }
                .flatMap { pollingInterval ->
                    Observable.timer(
                        pollingInterval,
                        pollingUnit,
                        scheduler
                    )
                }
        }
        .doOnNext { item ->
            if (item.isOnError) {
                val error = item.error ?: return@doOnNext
                if (fatalErrors.test(error)) {
                    // Throw so that the stream of notification stops immediately.
                    // Throw as Exception if possible, wrap otherwise.
                    throw error as? Exception ?: error as? Error ?: RuntimeException(error)
                }
            }
        }
        // Keep a running count of the number of errors encountered so far.
        .map { item -> (if (item.isOnError) 1 else 0) to item }
        .scan { (errorCount, _), (isOnError, item) -> errorCount + isOnError to item }
        .filter { (errorCount, item) ->
            // If we have reached our error limit, pass all notifications through - onError and onComplete will
            // terminate the stream when dematerialized.
            // Otherwise, only pass through onNext; the stream continues past onError and onComplete.
            errorCount >= maxErrorLimit || item.isOnNext
        }
        .map { (_, item) -> item }
        .dematerialize()

    companion object {
        @JvmField
        internal val CHECK_FATAL: Predicate<Throwable> = Predicate { error ->
            Exceptions.throwIfFatal(error)
            false
        }

        /**
         * @param pollingIntervals intervals to wait between repeats/retries. polling will stop when exhausted
         * @param pollingUnit unit of `pollingIntervals`
         * @param maxErrorLimit polling will stop after this many errors
         * @param fatalErrors polling will immediately stop (regardless of `maxErrorLimits`) if true or throws
         */
        @[JvmName("poll") JvmOverloads JvmStatic]
        operator fun <T> invoke(
            pollingIntervals: Iterable<Long>,
            pollingUnit: TimeUnit,
            maxErrorLimit: Int,
            fatalErrors: Predicate<in Throwable> = CHECK_FATAL
        ): RxRepeatAndRetry<T> =
            invoke(
                Observable.fromIterable(pollingIntervals),
                pollingUnit,
                maxErrorLimit,
                fatalErrors
            )

        /**
         * @param pollingIntervals intervals to wait between repeats/retries. polling will stop when exhausted
         * @param pollingUnit unit of `pollingIntervals`
         * @param maxErrorLimit polling will stop after this many errors
         * @param fatalErrors polling will immediately stop (regardless of `maxErrorLimits`) if true or throws
         */
        @[JvmName("poll") JvmOverloads JvmStatic]
        operator fun <T> invoke(
            pollingIntervals: Observable<Long>,
            pollingUnit: TimeUnit,
            maxErrorLimit: Int,
            fatalErrors: Predicate<in Throwable> = CHECK_FATAL
        ): RxRepeatAndRetry<T> =
            RxRepeatAndRetry(
                pollingIntervals,
                pollingUnit,
                maxErrorLimit,
                fatalErrors,
                Schedulers.computation()
            )
    }
}
