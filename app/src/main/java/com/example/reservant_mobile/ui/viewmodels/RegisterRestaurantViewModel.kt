package com.example.reservant_mobile.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.RegisterRestaurantDTO
import com.example.reservant_mobile.data.services.FileUploadService
import com.example.reservant_mobile.data.services.RestaurantService
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

//        !!! Example of FileUploadServiceUsage !!!
//        val file = leaseUri?.let { GetFileFromURIUtil().getFileDataFromUri(context, it.toUri()) }
//        val fDto: FileUploadDTO? = file?.let { FileUploadService().sendFile(FileUploadService.PDF, it).value }

        val restaurant = RegisterRestaurantDTO(
            name = name,
            nip = nip,
            restaurantType = restaurantType,
            address = address,
            postalCode = postalCode,
            city = city,
            lease = leaseUri?: "",//?.let { GetFileFromURIUtil().getFileDataFromUri(context, it.toUri()) },
            license = licenseUri?: "",//?.let { GetFileFromURIUtil().getFileDataFromUri( context, it.toUri() },
            consent = consentUri?: "",//?.let { GetFileFromURIUtil().getFileDataFromUri(context,it.toUri() },
            idCard = idCardUri?: ""//?.let { GetFileFromURIUtil().getFileDataFromUri(context, it.toUri()) }
        )

        val rService = RestaurantService()
//        FIXME: Add restaurant service implementation
//        return rService.registerRestaurant(restaurant)[0]
        return -1
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
        // Sprawdzenie czy NIP składa się z 10 cyfr
        if (nip.length != 10 || !nip.all { it.isDigit() }) {
            return true
        }

        // Wagi dla poszczególnych cyfr NIP
        val weights = listOf(6, 5, 7, 2, 3, 4, 5, 6, 7)

        // Obliczanie sumy iloczynów
        var sum = 0
        for (i in 0 until 9) {
            sum += Character.getNumericValue(nip[i]) * weights[i]
        }

        // Sprawdzenie poprawności ostatniej cyfry kontrolnej
        val controlDigit = if (sum % 11 == 10) 0 else sum % 11
        val lastDigit = Character.getNumericValue(nip[9])
        return controlDigit != lastDigit
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