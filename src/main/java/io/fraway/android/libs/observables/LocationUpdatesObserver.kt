package io.fraway.android.libs.observables

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import io.fraway.android.libs.models.RichLocation
import io.reactivex.ObservableEmitter

/**
 * @author Francesco Donzello <francesco.donzello@gmail.com>
 */
class LocationUpdatesObserver(ctx: Context, private var request: LocationRequest) : BaseObservable<List<RichLocation>>() {

    private var client: FusedLocationProviderClient = FusedLocationProviderClient(ctx)

    class MyCallback(private var e: ObservableEmitter<List<RichLocation>>) : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            if (e.isDisposed) {
                return
            }

            result?.let {
                if (it.locations.isEmpty()) {
                    return
                }

                val richLocations: ArrayList<RichLocation> = ArrayList()
                it.locations.mapTo(richLocations) {
                    RichLocation(it.latitude, it.longitude, "", "", null, null)
                }

                e.onNext(richLocations)
            }

        }
    }

    private lateinit var callback: LocationCallback

    @SuppressLint("MissingPermission")
    override fun run(e: ObservableEmitter<List<RichLocation>>) {
        this.callback = MyCallback(e)

        client.requestLocationUpdates(request, callback, null)
    }

}
