package io.fraway.android.libs.observables

import android.annotation.SuppressLint
import android.location.Geocoder
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import io.fraway.android.libs.models.RichLocation
import io.reactivex.ObservableEmitter

/**
 * @author Francesco Donzello <francesco.donzello@gmail.com>
 */
class PlaceAutocompleteObserver(private val ctx: AppCompatActivity, private var query: String) : BaseObservable<List<RichLocation>>(ctx) {

    @SuppressLint("MissingPermission")
    override fun run(e: ObservableEmitter<List<RichLocation>>) {
        val query = String.format("%s, %s", query)
        val addresses = Geocoder(ctx).getFromLocationName(query, 3)
        val locations = ArrayList<RichLocation>()
        for (address in addresses) {
            var addressName = ""
            if (address.maxAddressLineIndex != -1) {
                for (i in 0..address.maxAddressLineIndex) {
                    addressName += address.getAddressLine(i) + " "
                }
            }
            locations.add(RichLocation(LatLng(address.latitude, address.longitude), addressName))
        }

        e.onNext(locations)
    }

}
