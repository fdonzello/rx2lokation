package io.fraway.android.libs.observables

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Geocoder
import io.fraway.android.libs.models.RichLocation
import io.reactivex.ObservableEmitter

/**
 * @author Francesco Donzello <francesco.donzello@gmail.com>
 */
class PlaceAutocompleteObserver(private val activity: Activity, private var query: String) : BaseObservable<List<RichLocation>>() {

    @SuppressLint("MissingPermission")
    override fun run(e: ObservableEmitter<List<RichLocation>>) {

        if (query.isEmpty()) {
            return
        }

        val query = String.format("%s", query)
        val addresses = Geocoder(activity).getFromLocationName(query, 3)
        val locations = ArrayList<RichLocation>()
        for (address in addresses) {
            var addressName = ""
            if (address.maxAddressLineIndex != -1) {
                for (i in 0..address.maxAddressLineIndex) {
                    addressName += address.getAddressLine(i) + " "
                }
            }
            locations.add(RichLocation(address.latitude, address.longitude, addressName, "")) // TODO
        }

        e.onNext(locations)
    }

}
