package io.fraway.android.libs.observables

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.location.Geocoder
import android.os.Handler
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.tbruyelle.rxpermissions2.RxPermissions
import io.fraway.android.libs.exceptions.NullLocationException
import io.fraway.android.libs.models.RichLocation
import io.reactivex.ObservableEmitter

/**
 * @author Francesco Donzello <francesco.donzello@gmail.com>
 */
class LastKnownLocationObserver(private var activity: Activity) : BaseObservable<RichLocation>() {

    @SuppressLint("MissingPermission")
    override fun run(e: ObservableEmitter<RichLocation>) {
        Handler(Looper.getMainLooper()).post({
            // need permission checks
            RxPermissions(activity)
                    .request(Manifest.permission.ACCESS_COARSE_LOCATION)
                    .filter({ a -> a })
                    .subscribe({ granted ->
                        if (granted) {
                            start(e)
                        } else {
                            e.onError(SecurityException("Permission has not been granted."))
                        }
                    })
        })

    }

    @SuppressLint("MissingPermission")
    private fun start(e: ObservableEmitter<RichLocation>) {
        val geocoder = Geocoder(activity)
        FusedLocationProviderClient(activity).lastLocation
                .addOnSuccessListener({ location ->

                    if (e.isDisposed) {
                        return@addOnSuccessListener
                    }

                    if (location != null) {

                        val results = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        var name = ""
                        if (results.isNotEmpty()) {
                            name = results[0].locality
                        }

                        e.onNext(RichLocation(
                                LatLng(location.latitude, location.longitude),
                                name
                        ))
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