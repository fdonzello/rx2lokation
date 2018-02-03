package io.fraway.android.libs.observables

import android.annotation.SuppressLint
import android.location.Location
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import io.reactivex.ObservableEmitter

/**
 * @author Francesco Donzello <francesco.donzello@gmail.com>
 */
class LocationUpdatesObserver(ctx: AppCompatActivity, private var request: LocationRequest) : BaseObservable<List<Location>>(ctx) {

    private var client: FusedLocationProviderClient = FusedLocationProviderClient(ctx)

    class MyCallback(private var e: ObservableEmitter<List<Location>>) : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            if (e.isDisposed) {
                return
            }

            if (result?.locations?.size == 0) {
                return
            }

            e.onNext(result!!.locations)
        }
    }

    private lateinit var callback: LocationCallback

    @SuppressLint("MissingPermission")
    override fun run(e: ObservableEmitter<List<Location>>) {
        this.callback = MyCallback(e)

        client.requestLocationUpdates(request, callback, null)
    }

}
