package io.fraway.android.libs.observables

import android.annotation.SuppressLint
import android.location.Location
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import io.fraway.android.libs.exceptions.NullLocationException
import io.reactivex.ObservableEmitter

/**
 * @author Francesco Donzello <francesco.donzello@gmail.com>
 */
class LastKnownLocationObserver(private var ctx: AppCompatActivity) : BaseObservable<Location>(ctx) {

    @SuppressLint("MissingPermission")
    override fun run(e: ObservableEmitter<Location>) {
        FusedLocationProviderClient(ctx).lastLocation
                .addOnSuccessListener({ location ->

                    if (e.isDisposed) {
                        return@addOnSuccessListener
                    }

                    if (location != null) {
                        e.onNext(location)
                    } else {
                        e.onError(NullLocationException())
                    }
                })
                .addOnFailureListener({ error ->
                    e.onError(error)
                })
                .addOnCompleteListener {
                    e.onComplete()
                }
    }

}