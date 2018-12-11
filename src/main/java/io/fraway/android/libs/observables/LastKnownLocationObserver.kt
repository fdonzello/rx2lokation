package io.fraway.android.libs.observables

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.location.Geocoder
import android.os.Handler
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.tbruyelle.rxpermissions2.RxPermissions
import io.fraway.android.libs.exceptions.NullLocationException
import io.fraway.android.libs.models.RichLocation
import io.reactivex.ObservableEmitter

/**
 * @author Francesco Donzello <francesco.donzello@gmail.com>
 */
class LastKnownLocationObserver(private val activity: Activity) : BaseObservable<RichLocation>() {

    @SuppressLint("MissingPermission")
    override fun run(e: ObservableEmitter<RichLocation>) {
        Handler(Looper.getMainLooper()).post {
            // need permission checks
            RxPermissions(activity)
                    .request(Manifest.permission.ACCESS_COARSE_LOCATION)
                    .subscribe({ granted ->
                        if (granted) {
                            start(e)
                        } else {
                            if (!e.isDisposed) {
                                e.onError(SecurityException("Permission has not been granted."))
                            }
                        }
                    },
                            {
                                if (!e.isDisposed) {
                                    e.onError(SecurityException("Permission has not been granted."))
                                }
                            }
                    )
        }

    }

    @SuppressLint("MissingPermission")
    private fun start(e: ObservableEmitter<RichLocation>) {
        val geocoder = Geocoder(activity)
        FusedLocationProviderClient(activity).lastLocation
                .addOnSuccessListener { location ->

                    if (e.isDisposed) {
                        return@addOnSuccessListener
                    }

                    if (location != null) {
                        try {
                            val results = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            if (results.isNotEmpty()) {
                                e.onNext(RichLocation.fromAddress(results[0]))
                                return@addOnSuccessListener
                            }

                            e.onNext(RichLocation(
                                    location.latitude,
                                    location.longitude,
                                    "",
                                    "",
                                    null,
                                    null,
                                    null
                            ))
                            e.onComplete()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else {
                        e.onError(NullLocationException())
                    }
                }
                .addOnFailureListener { error ->
                    if (!e.isDisposed) {
                        e.onError(error)
                    }
                }
                .addOnCompleteListener {
                    e.onComplete()
                }
    }

}