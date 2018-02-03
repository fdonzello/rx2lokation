package io.fraway.android.libs


import android.annotation.SuppressLint
import android.arch.lifecycle.LifecycleObserver
import android.location.Location
import android.support.annotation.RequiresPermission
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.location.LocationRequest
import io.fraway.android.libs.models.RichLocation
import io.fraway.android.libs.observables.LastKnownLocationObserver
import io.fraway.android.libs.observables.LocationUpdatesObserver
import io.fraway.android.libs.observables.PlaceAutocompleteObserver
import io.reactivex.Observable

/**
 * @author Francesco Donzello <francesco.donzello></francesco.donzello>@gmail.com>
 */

open class RxLocationProvider(private val ctx: AppCompatActivity) : LifecycleObserver {

    private val observables: ArrayList<Observable<Any>> = ArrayList()

    init {
        println("Ok init the lib")
    }


    @SuppressLint("SupportAnnotationUsage", "MissingPermission")
    @RequiresPermission(
            anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"]
    )
    open fun getLastKnownLocation(): Observable<Location> =
            Observable.create(LastKnownLocationObserver(ctx))

    @SuppressLint("SupportAnnotationUsage", "MissingPermission")
    @RequiresPermission(
            anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"]
    )
    open fun requestLocationUpdates(request: LocationRequest): Observable<List<Location>> =
            Observable.create(LocationUpdatesObserver(ctx, request))

    @SuppressLint("SupportAnnotationUsage", "MissingPermission")
    @RequiresPermission(
            anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"]
    )
    open fun autocompletePlace(query: String): Observable<List<RichLocation>> =
            Observable.create(PlaceAutocompleteObserver(ctx, query))
}
