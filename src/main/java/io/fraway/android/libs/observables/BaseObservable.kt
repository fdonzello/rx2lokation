package io.fraway.android.libs.observables

import android.annotation.SuppressLint
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe

/**
 * @author Francesco Donzello <francesco.donzello@gmail.com>
 */
abstract class BaseObservable<T> : ObservableOnSubscribe<T> {

    abstract fun run(e: ObservableEmitter<T>)

    @SuppressLint("MissingPermission")
    override fun subscribe(e: ObservableEmitter<T>) {
        run(e)
    }
}