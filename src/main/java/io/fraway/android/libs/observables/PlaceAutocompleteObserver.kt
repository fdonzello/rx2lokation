package io.fraway.android.libs.observables

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Geocoder
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.AutocompleteSessionToken.*
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import io.fraway.android.libs.models.RichLocation
import io.reactivex.ObservableEmitter
import timber.log.Timber

/**
 * @author Francesco Donzello <francesco.donzello@gmail.com>
 */
class PlaceAutocompleteObserver(
        private val activity: Activity,
        private val client: PlacesClient,
        private var query: String,
        private var autocompleteToken: AutocompleteSessionToken
) : BaseObservable<List<RichLocation>>() {

    @SuppressLint("MissingPermission")
    override fun run(e: ObservableEmitter<List<RichLocation>>) {

        if (query.isEmpty()) {
            return
        }

//        val query = String.format("%s", query)
//        val addresses = Geocoder(activity).getFromLocationName(query, 3)
//        val locations = ArrayList<RichLocation>()
//        addresses.forEach {
//            locations.add(RichLocation.fromAddress(it))
//        }
//
//        e.onNext(locations)
//        e.onComplete()

        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
//        val token = newInstance()

        // Use the builder to create a FindAutocompletePredictionsRequest.
        val request =
                FindAutocompletePredictionsRequest.builder()
//                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(autocompleteToken)
                        .setQuery(query)
                        .build()

        client.findAutocompletePredictions(request)
                .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                    for (prediction in response.autocompletePredictions) {
                        Timber.i(prediction.placeId)
                        Timber.i(prediction.getPrimaryText(null).toString())
                    }

                    val locations = arrayListOf<RichLocation>()
                    response.autocompletePredictions.forEach { p ->

                        locations.add(
                                RichLocation(0.0, 0.0, p.getFullText(null).toString(), "", "", p.placeId)
                        )
                    }

                    e.onNext(locations)
                    e.onComplete()

                }.addOnFailureListener { exception: Exception? ->
                    if (exception is ApiException) {
                        Timber.e("Place not found: " + exception.statusCode)
                    }
                }
    }

}
