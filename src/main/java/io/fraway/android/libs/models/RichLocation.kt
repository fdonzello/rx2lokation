package io.fraway.android.libs.models

import com.google.android.gms.maps.model.LatLng

/**
 * @author Francesco Donzello <francesco.donzello@gmail.com>
 */
class RichLocation(val latLng: LatLng, val text: String) {
    override fun toString(): String {
        return text
    }
}