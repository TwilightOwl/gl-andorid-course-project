package com.glandroidcourse.tanks.base

import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by User on 04.01.2018.
 *
 * Обертка для наблюдателя реактивщины
 */

class SubRX<T>: Observer<T>, Disposable {

    private var iComplete: () -> Unit = {}
    private var iError: (Throwable) -> Unit = {}
    private var iNext: (T) -> Unit = {}
    private var iFinally: (T?, Throwable?) -> Unit = { _: T?, _: Throwable? -> }
    private var iSubscribe: (Disposable) -> Unit = {}

    private var onNextResult: T? = null
    private var onErrorReason: Throwable? = null

    private val composite: CompositeDisposable = CompositeDisposable()

    constructor() { }

    constructor(iFinally: (T?, Throwable?) -> Unit) {
        this.iFinally = iFinally
    }

    fun setComplete(complete: () -> Unit): SubRX<T> {
        iComplete = complete
        return this
    }

    fun setError(error: (Throwable) -> Unit): SubRX<T> {
        iError = error
        return this
    }

    fun setNext(next: (T) -> Unit): SubRX<T> {
        iNext = next
        return this
    }

    fun setSubscribe(subscribe: (Disposable) -> Unit): SubRX<T> {
        iSubscribe = subscribe
        return this
    }

    /**
     * Выполняется после onError или onComplete
     * onError -> onFinally
     * onNext -> onComplete -> onFinally
     *
     * @param iFinally
     * @return
     */
    fun setFinal(finally: (T?, Throwable?) -> Unit): SubRX<T> {
        iFinally = finally
        return this
    }

    fun onComplete(result: T) {
        onNext(result)
        onComplete()
    }

    override fun onComplete() {
        iComplete()
        iFinally(onNextResult, onErrorReason)
    }

    override fun onError(e: Throwable) {
        iError(e)
        onErrorReason = e
        iFinally(onNextResult, onErrorReason)
    }

    override fun onNext(t: T) {
        iNext(t)
        onNextResult = t
    }

    override fun onSubscribe(d: Disposable) {
        plus(d)
        iSubscribe(d)
    }

    override fun isDisposed(): Boolean = composite.isDisposed
    override fun dispose() = composite.dispose()
    private fun plus(d: Disposable): Boolean = composite.add(d)

    fun <T> plus(observer: SubRX<T>): SubRX<T> {

        val iSubscribe = observer.iSubscribe
        observer.setSubscribe { disposable ->
            plus(disposable)
            iSubscribe(disposable)
        }

        return observer
    }

}