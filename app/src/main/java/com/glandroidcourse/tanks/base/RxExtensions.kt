package com.glandroidcourse.tanks.base

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by anatoliy on 25.02.18.
 */

fun <T> Observer<T>.createObservable(block: (ObservableEmitter<T>) -> Unit): Observable<T> {
    return Observable.create<T> { block(it) }
}

fun <T> Observable<T>.standardSubscribeIO(observer: Observer<T>): Observable<T> {
    this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    return this
}

fun <T> Observable<T>.standardSubscribeCOMP(observer: Observer<T>): Observable<T> {
    this.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    return this
}

fun <T> ObservableEmitter<T>.onComplete(value: T) {
    this.onNext(value)
    this.onComplete()
}

fun <T> SubRX<T>.onComplete(value: T) {
    this.onNext(value)
    this.onComplete()
}

fun <T> Observer<T>.ioListen(block: (ObservableEmitter<T>) -> Unit) {
    this.createObservable { block(it) }.standardSubscribeIO(this)
}

fun <T> Observer<T>.compListen(block: (ObservableEmitter<T>) -> Unit) {
    this.createObservable { block(it) }.standardSubscribeCOMP(this)
}