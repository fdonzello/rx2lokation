package io.fraway.android.libs.observables

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.location.Geocoder
import android.os.Handler
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.tbruyelle.rxpermissions2.RxPermissions
import io.fraway.android.libs.models.RichLocation
import io.reactivex.ObservableEmitter
import timber.log.Timber

/**
 * @author Francesco Donzello <francesco.donzello@gmail.com>
 */
class LocationUpdatesObserver(private val activity: Activity, private var request: LocationRequest) : BaseObservable<List<RichLocation>>() {

    private var client: FusedLocationProviderClient = FusedLocationProviderClient(activity)

    class MyCallback(private var e: ObservableEmitter<List<RichLocation>>, private val activity: Activity) : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            if (e.isDisposed) {
                return
            }

            result?.let {
                if (it.locations.isEmpty()) {
                    return
                }

                val richLocations: ArrayList<RichLocation> = ArrayList()
//                it.locations.mapTo(richLocations) {
//                    RichLocation(it.latitude, it.longitude, "", "", null, null)
//                }

                val geocoder = Geocoder(activity)

                try {
                    val location = it.locations.first()
                    val results = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (results.isNotEmpty()) {
                        e.onNext(arrayListOf(RichLocation.fromAddress(results[0])))
                        return
                    }

                    e.onNext(arrayListOf(RichLocation(location.latitude, location.longitude, "", "", null, null, null)))
                    e.onComplete()
                } catch (e: Exception) {
                    e.printStackTrace()
                }


                e.onNext(richLocations)
            }

        }
    }

    private lateinit var callback: LocationCallback

    @SuppressLint("MissingPermission")
    override fun run(e: ObservableEmitter<List<RichLocation>>) {
        this.callback = MyCallback(e, activity)

        Handler(Looper.getMainLooper()).post {
            // need permission checks
            RxPermissions(activity)
                    .request(Manifest.permission.ACCESS_FINE_LOCATION)
                    .subscribe({ granted ->
                        if (granted) {
                            Timber.v("requesting location updates")
                            client.requestLocationUpdates(request, callback, null)
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

}
