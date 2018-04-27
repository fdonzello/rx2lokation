package io.fraway.android.libs.models

import android.location.Address

/**
 * @author Francesco Donzello <francesco.donzello@gmail.com>
 */
data class RichLocation(val lat: Double, val lng: Double, val address: String?, val city: String?, val postalCode: String?, val countryCode: String?) {

    companion object {
        fun fromAddress(address: Address): RichLocation {

            var addressName = ""
            if (address.maxAddressLineIndex != -1) {
                for (i in 0..address.maxAddressLineIndex) {
                    addressName += address.getAddressLine(i) + " "
                }
            }

            var city = ""
            if (address.locality.isNullOrEmpty().not()) {
                city = address.locality
            }

            var postalCode = ""
            if (address.postalCode.isNullOrEmpty().not()) {
                postalCode = address.postalCode
            }

            var countryCode = ""
            if (address.countryCode.isNullOrEmpty().not()) {
                countryCode = address.countryCode
            }

            return RichLocation(address.latitude, address.longitude, addressName, city, postalCode, countryCode).apply {
                hasAddress = address.thoroughfare.isNullOrEmpty().not()
            }
        }
    }

    var hasAddress = false

    override fun toString(): String {
        return address ?: ""
    }
}