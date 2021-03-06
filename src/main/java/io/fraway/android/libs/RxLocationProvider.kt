package io.fraway.android.libs


import android.annotation.SuppressLint
import android.app.Activity
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LifecycleObserver
import com.google.android.gms.location.LocationRequest
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.PlacesClient
import io.fraway.android.libs.models.RichLocation
import io.fraway.android.libs.observables.LastKnownLocationObserver
import io.fraway.android.libs.observables.LocationUpdatesObserver
import io.fraway.android.libs.observables.PlaceAutocompleteObserver
import io.reactivex.Observable

/**
 * @author Francesco Donzello <francesco.donzello@gmail.com>
 */

open class RxLocationProvider(private val activity: Activity) : LifecycleObserver {

    init {
        println("Ok init the lib")
    }


    @SuppressLint("SupportAnnotationUsage", "MissingPermission")
    @RequiresPermission(
            anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"]
    )
    open fun getLastKnownLocation(): Observable<RichLocation> =
            Observable.create(LastKnownLocationObserver(activity))

    @SuppressLint("SupportAnnotationUsage", "MissingPermission")
    @RequiresPermission(
            anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"]
    )
    open fun requestLocationUpdates(request: LocationRequest): Observable<List<RichLocation>> =
            Observable.create(LocationUpdatesObserver(activity, request))

    @SuppressLint("SupportAnnotationUsage", "MissingPermission")
    @RequiresPermission(
            anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"]
    )
    open fun autocompletePlace(client: PlacesClient, query: String, token: AutocompleteSessionToken): Observable<List<RichLocation>> =
            Observable.create(PlaceAutocompleteObserver(activity, client, query, token))
}
