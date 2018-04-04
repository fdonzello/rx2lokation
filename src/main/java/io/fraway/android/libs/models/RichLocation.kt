package io.fraway.android.libs.models

/**
 * @author Francesco Donzello <francesco.donzello@gmail.com>
 */
data class RichLocation(val lat: Double, val lng: Double, val address: String, val city: String) {
    override fun toString(): String {
        return address
    }
}