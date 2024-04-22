package com.example.reservant_mobile.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.reservant_mobile.data.models.dtos.RestaurantDTO
import com.example.reservant_mobile.data.models.dtos.fields.FormField
import com.example.reservant_mobile.data.models.dtos.fields.Result
import com.example.reservant_mobile.data.services.DataType
import com.example.reservant_mobile.data.services.FileUploadService
import com.example.reservant_mobile.data.services.RestaurantService
import com.example.reservant_mobile.data.utils.getFileFromUri
import androidx.core.net.toUri
import com.example.reservant_mobile.data.services.IRestaurantService

class RegisterRestaurantViewModel(private val restaurantService: IRestaurantService = RestaurantService()) : ViewModel() {

    // Wynik rejestracji
    var result by mutableStateOf(Result(isError = false, value = false))

    // Pola do walidacji
    val name: FormField = FormField(RestaurantDTO::name.name)
    val restaurantType: FormField = FormField(RestaurantDTO::restaurantType.name)
    val nip: FormField = FormField(RestaurantDTO::nip.name)
    val address: FormField = FormField(RestaurantDTO::address.name)
    val postalCode: FormField = FormField(RestaurantDTO::postalIndex.name)
    val city: FormField = FormField(RestaurantDTO::city.name)
    val description: FormField = FormField(RestaurantDTO::description.name)

    // Pliki do załączenia
    val rentalContract: FormField = FormField(RestaurantDTO::rentalContract.name)
    val alcoholLicense: FormField = FormField(RestaurantDTO::alcoholLicense.name)
    val businessPermission: FormField = FormField(RestaurantDTO::businessPermission.name)
    val idCard: FormField = FormField(RestaurantDTO::idCard.name)
    val logo: FormField = FormField(RestaurantDTO::logo.name)

    // Tagowanie i inne
    var selectedTags = mutableStateListOf<String>()
    var delivery by mutableStateOf(true)

    suspend fun registerRestaurant(context: Context): Boolean {
        if (isRestaurantRegistrationInvalid()) {
            return false
        }

        val restaurant = RestaurantDTO(
            name = name.value,
            restaurantType = restaurantType.value,
            nip = nip.value,
            address = address.value,
            postalIndex = postalCode.value,
            city = city.value,
            rentalContract = sendFile(rentalContract.value, context, DataType.PDF),
            alcoholLicense = sendFile(alcoholLicense.value, context, DataType.PDF),
            businessPermission = sendFile(businessPermission.value, context, DataType.PDF),
            idCard = sendFile(idCard.value, context, DataType.PDF),
            logo = sendPhoto(logo.value, context),
            description = description.value,
            provideDelivery = delivery,
            tags = selectedTags.toList(),
            groupId = null,
            photos = emptyList(),
            tables = emptyList()
        )

        // Rejestracja restauracji
        result = restaurantService.registerRestaurant(restaurant)

        return result.value
    }

    suspend fun sendFile(uri: String?, context: Context, type: DataType): String {
        val file = uri?.let { getFileFromUri(context, it.toUri()) }
        val fDto = file?.let { FileUploadService().sendFile(type, it).value }
        return fDto?.fileName ?: ""
    }

    suspend fun sendPhoto(uri: String?, context: Context): String {
        val file = uri?.let { getFileFromUri(context, it.toUri()) }
        var fDto = file?.let { FileUploadService().sendFile(DataType.PNG, it).value }
        if (fDto == null) {
            fDto = file?.let { FileUploadService().sendFile(DataType.JPG, it).value }
        }
        return fDto?.fileName ?: ""
    }

    fun isRestaurantRegistrationInvalid(): Boolean {
        return isNameInvalid() ||
                isNipInvalid() ||
                isAddressInvalid() ||
                isPostalCodeInvalid() ||
                isCityInvalid() ||
                isDescriptionInvalid() ||
                isRentalContractInvalid() ||
                isAlcoholLicenseInvalid() ||
                isBusinessPermissionInvalid() ||
                isIdCardInvalid() ||
                isLogoInvalid()// ||
//                areTagsInvalid()
    }

    private fun isNameInvalid(): Boolean {
        return name.value.isBlank()
    }

    private fun isNipInvalid(): Boolean {
        if (nip.value.length != 10 || !nip.value.all { it.isDigit() }) {
            return true
        }

        val weights = listOf(6, 5, 7, 2, 3, 4, 5, 6, 7)
        val sum = (0 until 9).sumOf { Character.getNumericValue(nip.value[it]) * weights[it] }
        val controlDigit = if (sum % 11 == 10) 0 else sum % 11
        val lastDigit = Character.getNumericValue(nip.value[9])

        return controlDigit != lastDigit
    }

    private fun isAddressInvalid(): Boolean {
        return address.value.isBlank()
    }

    private fun isPostalCodeInvalid(): Boolean {
        val postalCode = postalCode.value
        return postalCode.length != 6 ||
                !postalCode.take(2).all { it.isDigit() } ||
                postalCode[2] != '-' ||
                !postalCode.substring(3).all { it.isDigit()}
    }

    private fun isCityInvalid(): Boolean {
        return city.value.isBlank()
    }

    private fun isDescriptionInvalid(): Boolean {
        return description.value.isBlank()
    }

    private fun isRentalContractInvalid(): Boolean {
        return rentalContract.value.isBlank()
    }

    private fun isAlcoholLicenseInvalid(): Boolean {
        return alcoholLicense.value.isBlank()
    }

    private fun isBusinessPermissionInvalid(): Boolean {
        return businessPermission.value.isBlank()
    }

    private fun isIdCardInvalid(): Boolean {
        return idCard.value.isBlank()
    }

    private fun isLogoInvalid(): Boolean {
        return logo.value.isBlank()
    }

    private fun areTagsInvalid(): Boolean {
        return selectedTags.isEmpty()
    }
}
