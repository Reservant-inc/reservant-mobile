package com.example.reservant_mobile.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.example.reservant_mobile.R
import com.example.reservant_mobile.data.models.dtos.FileUploadDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantDTO
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

    var description by mutableStateOf("")
    var logoUri by mutableStateOf<String?>(null)
    var delivery by mutableStateOf(true)
    var selectedTags = mutableStateListOf<String>()

    suspend fun registerRestaurant(context: Context): Int {

        if (isRestaurantRegistrationInvalid()) {
            return R.string.error_register_invalid_request
        }

        val restaurant = RestaurantDTO(
            name = name,
            nip = nip,
            restaurantType = restaurantType,
            address = address,
            postalIndex = postalCode,
            city = city,
            rentalContract = sendFile(leaseUri, context, FileUploadService.PDF),
            alcoholLicense = sendFile(licenseUri, context, FileUploadService.PDF),
            businessPermission = sendFile(consentUri, context, FileUploadService.PDF),
            idCard = sendFile(idCardUri, context, FileUploadService.PDF),
            logo = sendPhoto(logoUri, context),
            provideDelivery = delivery,
            tags = selectedTags,
            description = description,
            groupId = 0,
            id = 0
        )

        val rService = RestaurantService()
//        FIXME: Add restaurant service implementation
//        return rService.registerRestaurant(restaurant)[0]
        return -1
    }
    suspend fun sendFile(uri: String?, context: Context, type: String): String {
        val file = uri?.let { GetFileFromURIUtil().getFileDataFromUri(context, it.toUri()) }
        val fDto: FileUploadDTO? = file?.let { FileUploadService().sendFile(type, it).value }
        if (fDto != null) {
            return fDto.fileName
        }
        return ""
    }

    suspend fun sendPhoto(uri: String?, context: Context): String {
        val file = uri?.let { GetFileFromURIUtil().getFileDataFromUri(context, it.toUri()) }
        var fDto: FileUploadDTO? = file?.let { FileUploadService().sendFile(FileUploadService.PNG, it).value }
        if (fDto != null) {
            return fDto.fileName
        }
        fDto = file?.let { FileUploadService().sendFile(FileUploadService.JPG, it).value }
        if (fDto != null) {
            return fDto.fileName
        }
        return ""
    }

    fun isRestaurantRegistrationInvalid(): Boolean {
        return isNameInvalid() ||
                isNipInvalid() ||
                isAddressInvalid() ||
                isPostalCodeInvalid() ||
                isCityInvalid() ||
                isInvalidUri(leaseUri) ||
                isInvalidUri(licenseUri) ||
                isDescriptionInvalid() ||
                areTagsInvalid() ||
                isInvalidUri(logoUri)
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

    private fun isInvalidUri(uri: String?): Boolean {
        return uri == null
    }

    private fun isDescriptionInvalid(): Boolean{
        return description.isBlank()
    }
    private fun areTagsInvalid(): Boolean{
        return selectedTags.isEmpty()
    }
}