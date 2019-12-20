@file:Suppress("MagicNumber")

package com.qyuan.toolkit.rx

import io.reactivex.Emitter
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

/**
 * Helper class for providing polling transformer functions to enable polling when requests
 * are implemented using RxAndroid. <br></br>
 * See how it is used in  ApiAttractionBookingsProvider for an example.
 *
 * @author cchaleva
 * @since 2/5/16
 */

class RxPollingHelper @JvmOverloads constructor(
    private val scheduler: Scheduler = Schedulers.computation()
) {

    /**
     * Transformer method for polling a request that returns an Observable of type T.<br></br>
     * Use it with the compose() operator.<br></br>
     * The polling duration (in milliseconds) will mainly be defined by `maxTries` and
     * `exponentialBackOffBase`.
     *
     * @param maxErrors Number of max retries allowed in case of api error.
     * @param maxTries Number of max retries allowed while status is still in progress.
     * @param exponentialBackoffBase Base to use for the exponential backoff.
     * @param mapper<T>              A mapper to get a [PollingState] from a response object
     * @return An Observable of the same type
     * @see RxPollingHelper.poll
    </T> */
    fun <T> poll(
        maxErrors: Int,
        maxTries: Int,
        exponentialBackoffBase: Float,
        mapper: (T) -> PollingState
    ): ObservableTransformer<T, T> {
        val pollIntervals = Observable
            .generate(
                Callable { 1 },
                BiFunction<Int, Emitter<Long>, Int> { count, emitter ->
                    emitter.onNext(
                        (Math.pow(
                            exponentialBackoffBase.toDouble(),
                            count.toDouble()
                        ) * 1000).toLong()
                    )
                    return@BiFunction count + 1
                }
            )
            .take(maxTries - 1L)
        return ObservableTransformer { observable ->
            observable.compose(
                RxRepeatAndRetry(
                    pollIntervals,
                    TimeUnit.MILLISECONDS,
                    maxErrors,
                    RxRepeatAndRetry.CHECK_FATAL,
                    scheduler
                )
            ).skipWhile { response -> mapper.invoke(response) == PollingState.IN_PROGRESS }
                .concatWith(Observable.error { RuntimeException("Max polling tries reached") })
                // Either take the first response with pollingStatus != IN_PROGRESS,
                // or if they were all skipped, RuntimeException.
                .firstElement()
                .toObservable()
        }
    }

    /**
     * Poll for approximately 40 sec.
     *
     *
     * `maxErrors` defaults to 3<br></br>
     * `maxTries` defaults to 10<br></br>
     * `exponentialBackoffBase` defaults to 1.30<br></br>
     *
     * @see RxPollingHelper.poll
     */
    fun <T> poll(mapper: (T) -> PollingState): ObservableTransformer<T, T> {
        return poll(3, 10, 1.30F, mapper)
    }
}
