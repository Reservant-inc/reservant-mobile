package com.example.reservant_mobile.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.example.reservant_mobile.data.models.dtos.RestaurantDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantGroupDTO
import com.example.reservant_mobile.data.models.dtos.fields.FormField
import com.example.reservant_mobile.data.models.dtos.fields.Result
import com.example.reservant_mobile.data.services.DataType
import com.example.reservant_mobile.data.services.FileUploadService
import com.example.reservant_mobile.data.services.IRestaurantService
import com.example.reservant_mobile.data.services.RestaurantService
import com.example.reservant_mobile.data.utils.getFileFromUri
import com.example.reservant_mobile.data.utils.getFileName

class RegisterRestaurantViewModel(private val restaurantService: IRestaurantService = RestaurantService()) :
    ViewModel() {

    // Wynik rejestracji
    var result by mutableStateOf(Result(isError = false, value = false))
    var result2 by mutableStateOf(Result(isError = false, value = false))
    var result3 by mutableStateOf(Result(isError = false, value = false))

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
    var tags = listOf(String())
    var selectedTags = mutableStateListOf<String>()
    var delivery by mutableStateOf(false)

    // Grupa
    var groups: List<RestaurantGroupDTO>? by mutableStateOf(listOf())
    var selectedGroup by mutableStateOf<RestaurantGroupDTO?>(null)

    suspend fun registerRestaurant(context: Context): Boolean {
        if (isRestaurantRegistrationInvalid(context)) {
            return false
        }

        val restaurant = getRestaurantData(context)

        result = restaurantService.registerRestaurant(restaurant)

        return result.value
    }

    suspend fun validateFirstStep(context: Context): Boolean {
        if (isRestaurantRegistrationFirstStepInvalid()) {
            return false
        }

        val restaurant = getRestaurantData(context)

        result = restaurantService.validateFirstStep(restaurant)
        return result.value
    }

    suspend fun validateSecondStep(context: Context): Boolean {

         val permission = sendFile(businessPermission.value, context, DataType.PDF)
         val card = sendFile(idCard.value, context, DataType.PDF)
         val rental = if (rentalContract.value.isBlank()) "" else sendFile(rentalContract.value, context, DataType.PDF)
         val license = if (alcoholLicense.value.isBlank()) "" else sendFile(alcoholLicense.value, context, DataType.PDF)

        if (isRestaurantRegistrationSecondStepInvalid(context) || permission.isEmpty() || card.isEmpty()) {
            businessPermission.value = permission.toUri().toString()
            idCard.value = card.toUri().toString()
            return false
        }

        businessPermission.value = permission
        idCard.value = card
        rentalContract.value = rental
        alcoholLicense.value = license

        return true
    }

    suspend fun getRestaurantData(context: Context): RestaurantDTO {

        return RestaurantDTO(
            name = name.value,
            restaurantType = restaurantType.value,
            nip = nip.value,
            address = address.value,
            postalIndex = postalCode.value,
            city = city.value,
            rentalContract = rentalContract.value,
            alcoholLicense = alcoholLicense.value,
            businessPermission = businessPermission.value,
            idCard = idCard.value,
            logo = sendPhoto(logo.value, context),
            description = description.value,
            provideDelivery = delivery,
            tags = selectedTags.toList(),
            groupId = selectedGroup?.restaurantGroupId,
            photos = emptyList(),
            tables = emptyList()
        )
    }

    suspend fun getGroups() {
        groups = restaurantService.getGroups().value
    }

    suspend fun getTags() {
        //Przypisujemy pustą listę listOf(), jeśli getRestaurantTags() jest null
        tags = restaurantService.getRestaurantTags().value ?: listOf()
    }

    suspend fun sendFile(uri: String?, context: Context, type: DataType): String {
        val file = uri?.let { getFileFromUri(context, it.toUri()) }
        val fDto = file?.let { FileUploadService().sendFile(type, it).value }
        println(fDto?.fileName)
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

    fun isRestaurantRegistrationInvalid(context: Context): Boolean {
        return isNameInvalid() ||
                isNipInvalid() ||
                isAddressInvalid() ||
                isPostalCodeInvalid() ||
                isCityInvalid() ||
                isDescriptionInvalid() ||
                isBusinessPermissionInvalid(context) ||
                isIdCardInvalid(context) ||
                isAlcoholLicenseInvalid(context) ||
                isRentalContractInvalid(context) ||
                isLogoInvalid(context) ||
                isRestaurantTypeInvalid() ||
                areTagsInvalid()
    }

    fun isRestaurantRegistrationFirstStepInvalid(): Boolean {
        return isNameInvalid() ||
                isNipInvalid() ||
                isAddressInvalid() ||
                isPostalCodeInvalid() ||
                isCityInvalid() ||
                isRestaurantTypeInvalid()
    }

    fun isRestaurantRegistrationSecondStepInvalid(context: Context): Boolean {
        return isBusinessPermissionInvalid(context) ||
                isIdCardInvalid(context) ||
                isAlcoholLicenseInvalid(context) ||
                isRentalContractInvalid(context) ||
                isGroupInvalid()

    }

    fun isNameInvalid(): Boolean {
        return name.value.isBlank()
    }

    fun isRestaurantTypeInvalid(): Boolean {
        return restaurantType.value.isBlank()
    }

    fun isNipInvalid(): Boolean {
        if (nip.value.length != 10 || !nip.value.all { it.isDigit() }) {
            return true
        }

        val weights = listOf(6, 5, 7, 2, 3, 4, 5, 6, 7)
        val sum = (0 until 9).sumOf { Character.getNumericValue(nip.value[it]) * weights[it] }
        val controlDigit = if (sum % 11 == 10) 0 else sum % 11
        val lastDigit = Character.getNumericValue(nip.value[9])

        return controlDigit != lastDigit
    }

    fun isAddressInvalid(): Boolean {
        return address.value.isBlank()
    }

    fun isPostalCodeInvalid(): Boolean {
        val postalCode = postalCode.value
        return postalCode.length != 6 ||
                !postalCode.take(2).all { it.isDigit() } ||
                postalCode[2] != '-' ||
                !postalCode.substring(3).all { it.isDigit() }
    }

    fun isCityInvalid(): Boolean {
        return city.value.isBlank()
    }

    fun isDescriptionInvalid(): Boolean {
        return description.value.isBlank()
    }

    fun isGroupInvalid(): Boolean {
        return selectedGroup == null
    }

    fun isBusinessPermissionInvalid(context: Context): Boolean {
        val value = businessPermission.value
        return value.isBlank() || !getFileName(context, value.toUri()).endsWith(
            ".pdf",
            ignoreCase = true
        )
    }

    fun isIdCardInvalid(context: Context): Boolean {
        val value = idCard.value
        return value.isBlank() || !getFileName(context, value.toUri()).endsWith(
            ".pdf",
            ignoreCase = true
        )
    }

    fun isAlcoholLicenseInvalid(context: Context): Boolean {
        val value = alcoholLicense.value
        return if (value.isBlank())
            false
        else
            !getFileName(context, value.toUri()).endsWith(".pdf", ignoreCase = true)
    }

    fun isRentalContractInvalid(context: Context): Boolean {
        val value = rentalContract.value
        return if (value.isBlank())
            false
        else
            !getFileName(context, value.toUri()).endsWith(".pdf", ignoreCase = true)
    }


    fun isLogoInvalid(context: Context): Boolean {
        val value = logo.value
        return if (value.isBlank()) {
            false
        } else {
            val fileName = getFileName(context, value.toUri())
            !(fileName.endsWith(".png", ignoreCase = true) || fileName.endsWith(
                ".jpg",
                ignoreCase = true
            ))
        }
    }


    fun areTagsInvalid(): Boolean {
        return selectedTags.isEmpty()
    }


    private fun <T> getFieldError(result: Result<T>, name: String): Int {
        if (!result.isError) {
            return -1
        }

        return result.errors?.getOrDefault(name, -1) ?: -1
    }


    fun getNameError(): Int {
        return getFieldError(result, name.name)
    }

    fun getRestaurantTypeError(): Int {
        return getFieldError(result, restaurantType.name)
    }

    fun getNipError(): Int {
        return getFieldError(result, nip.name)
    }

    fun getAdressError(): Int {
        return getFieldError(result, address.name)
    }

    fun getPostalError(): Int {
        return getFieldError(result, postalCode.name)
    }

    fun getCityError(): Int {
        return getFieldError(result, city.name)
    }

    fun getRentalContractError(): Int {
        return getFieldError(result2, rentalContract.name)
    }

    fun getAlcoholLicenseError(): Int {
        return getFieldError(result2, alcoholLicense.name)
    }

    fun getBusinessPermissionError(): Int {
        return getFieldError(result2, businessPermission.name)
    }

    fun getIdCardError(): Int {
        return getFieldError(result2, idCard.name)
    }

    fun getLogoError(): Int {
        return getFieldError(result3, logo.name)
    }

    fun getDescriptionError(): Int {
        return getFieldError(result3, description.name)
    }

    fun <T> getToastError(result: Result<T>): Int {
        return getFieldError(result, "TOAST")
    }
}
