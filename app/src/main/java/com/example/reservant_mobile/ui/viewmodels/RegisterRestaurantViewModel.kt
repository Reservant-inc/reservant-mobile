package com.example.reservant_mobile.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.RegisterRestaurantDTO
import com.example.reservant_mobile.data.utils.GetFileFromURIUtil

class RegisterRestaurantViewModel : ViewModel() {

    var name by mutableStateOf("")
    var nip by mutableStateOf("")
    var restaurantType by mutableStateOf("Restaurant")
    var address by mutableStateOf("")
    var postalCode by mutableStateOf("")
    var city by mutableStateOf("")

    var leaseUri by mutableStateOf<String?>(null)
    var licenseUri by mutableStateOf<String?>(null)
    var consentUri by mutableStateOf<String?>(null)
    var idCardUri by mutableStateOf<String?>(null)

    suspend fun registerRestaurant(context: Context): Int {

        if (isRestaurantRegistrationInvalid()) {
            return R.string.error_register_invalid_request
        }

        val restaurant = RegisterRestaurantDTO(
            name = name,
            nip = nip,
            restaurantType = restaurantType,
            address = address,
            postalCode = postalCode,
            city = city,
            lease = leaseUri?.let { GetFileFromURIUtil().getFileDataFromUri(context, it.toUri()) },
            license = licenseUri?.let { GetFileFromURIUtil().getFileDataFromUri(context, it.toUri()) },
            consent = consentUri?.let { GetFileFromURIUtil().getFileDataFromUri(context, it.toUri()) },
            idCard = idCardUri?.let { GetFileFromURIUtil().getFileDataFromUri(context, it.toUri()) }
        )

        //return restaurantService.registerRestaurant(restaurant)[0]
        return 0;
    }

    fun isRestaurantRegistrationInvalid(): Boolean {
        return isNameInvalid() ||
                isNipInvalid() ||
                isAddressInvalid() ||
                isPostalCodeInvalid() ||
                isCityInvalid() ||
                isValidUri(leaseUri) ||
                isValidUri(licenseUri) ||
                isValidUri(consentUri) ||
                isValidUri(idCardUri)
    }

    private fun isNameInvalid(): Boolean {
        return name.isBlank()
    }

    private fun isNipInvalid(): Boolean {
        // NIP
        return false
    }

    private fun isAddressInvalid(): Boolean {
        return address.isBlank()
    }

    private fun isPostalCodeInvalid(): Boolean {
        return postalCode.length != 6 || !postalCode.take(2)
            .all { it.isDigit() } || postalCode[2] != '-' || !postalCode.substring(3)
            .all { it.isDigit() }
    }

    private fun isCityInvalid(): Boolean {
        return city.isBlank()
    }

    private fun isValidUri(uri: String?): Boolean {
        return uri != null
    }
}