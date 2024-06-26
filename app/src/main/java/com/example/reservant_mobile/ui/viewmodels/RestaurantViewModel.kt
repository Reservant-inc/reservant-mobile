package com.example.reservant_mobile.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.example.reservant_mobile.data.endpoints.File
import com.example.reservant_mobile.data.models.dtos.FileUploadDTO
import com.example.reservant_mobile.data.models.dtos.LocationDTO
import com.example.reservant_mobile.data.models.dtos.PointDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantDTO
import com.example.reservant_mobile.data.models.dtos.RestaurantGroupDTO
import com.example.reservant_mobile.data.models.dtos.fields.FormField
import com.example.reservant_mobile.data.models.dtos.fields.Result
import com.example.reservant_mobile.data.services.DataType
import com.example.reservant_mobile.data.services.FileService
import com.example.reservant_mobile.data.services.IRestaurantService
import com.example.reservant_mobile.data.services.RestaurantService
import com.example.reservant_mobile.data.utils.getFileFromUri
import com.example.reservant_mobile.data.utils.getFileName
import com.example.reservant_mobile.data.utils.isFileNameInvalid
import com.example.reservant_mobile.data.utils.isFileSizeInvalid
import java.util.UUID

class RestaurantViewModel(
    private val restaurantService: IRestaurantService = RestaurantService(),
    private val fileService: FileService = FileService()
) :
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
    var selectedTags: List<String> by mutableStateOf(listOf())
    var delivery by mutableStateOf(false)

    // Grupa
    var groups: List<RestaurantGroupDTO>? by mutableStateOf(listOf())
    var selectedGroup by mutableStateOf<RestaurantGroupDTO?>(null)
    var newGroup: FormField = FormField(RestaurantDTO::groupName.name)

    var restaurantId by mutableStateOf<Int?>(null)

    suspend fun assignData(id: Int, group: RestaurantGroupDTO) {
        restaurantId = id
        val restaurant = restaurantService.getRestaurant(restaurantId!!)
        if (restaurant.value != null) {
            name.value = restaurant.value.name
            restaurantType.value = restaurant.value.restaurantType
            nip.value = restaurant.value.nip
            address.value = restaurant.value.address
            postalCode.value = restaurant.value.postalIndex
            city.value = restaurant.value.city
            rentalContract.value = restaurant.value.rentalContract.orEmpty().drop(9)
            alcoholLicense.value = restaurant.value.alcoholLicense.orEmpty().drop(9)
            businessPermission.value = restaurant.value.businessPermission.orEmpty().drop(9)
            idCard.value = restaurant.value.idCard.orEmpty().drop(9)
            logo.value = restaurant.value.logo.orEmpty().drop(9)
            description.value = restaurant.value.description
            delivery = restaurant.value.provideDelivery
            selectedTags = restaurant.value.tags
            selectedGroup = group
        }
    }


    suspend fun registerRestaurant(context: Context): Boolean {
        if (isRestaurantRegistrationInvalid(context)) {
            return false
        }

        val restaurant = getRestaurantData()

        val response = restaurantService.registerRestaurant(restaurant)
        result.isError = response.isError

        restaurantId = response.value?.restaurantId

        if (response.value == null){
            return false
        }

        if(newGroup.value.isNotBlank()){
                val new = RestaurantGroupDTO(
                    name = newGroup.value,
                    restaurantIds = listOf(restaurantId!!)
                )
                result = restaurantService.addGroup(new)
        }

        return true
    }

    suspend fun editRestaurant(context: Context): Boolean {
        if (isRestaurantRegistrationInvalid(context)) {
            return false
        }

        val restaurant = getRestaurantData()

        val response = restaurantService.editRestaurant(restaurant.restaurantId, restaurant)

        if (response.value == null){
            return false
        }

        if(newGroup.value.isNotBlank()){
                val new = RestaurantGroupDTO(
                    name = newGroup.value,
                    restaurantIds = listOf(restaurantId!!)
                )
                result = restaurantService.addGroup(new)
        }

        return result.value
    }

    suspend fun validateFirstStep(): Boolean {
        if (isRestaurantRegistrationFirstStepInvalid()) {
            return false
        }

        val restaurant = getRestaurantData()

        result = restaurantService.validateFirstStep(restaurant)
        return result.value
    }

    suspend fun validateSecondStep(context: Context): Boolean {

        val restaurantLogo = if (
            !logo.value.endsWith(".png", ignoreCase = true) &&
            !logo.value.endsWith(".jpg", ignoreCase = true)
        ) {
            sendPhoto(logo.value, context)
        } else {
            null
        }

        val permission = if (!businessPermission.value.endsWith(
                ".pdf",
                ignoreCase = true
            )
        ) sendFile(
            businessPermission.value,
            context,
            DataType.PDF
        ) else null

        val card =
            if (!idCard.value.endsWith(
                    ".pdf",
                    ignoreCase = true
                )
            ) sendFile(idCard.value, context, DataType.PDF) else null

        val rental =
            if (rentalContract.value.isBlank() || rentalContract.value == "null") null else sendFile(
                rentalContract.value,
                context,
                DataType.PDF
            )
        val license =
            if (alcoholLicense.value.isBlank() || alcoholLicense.value == "null") null else sendFile(
                alcoholLicense.value,
                context,
                DataType.PDF
            )

        if (restaurantLogo != null) {
            if (!restaurantLogo.isError)
                logo.value = restaurantLogo.value?.fileName ?: ""
            else
                logo.value = "Bledny plik"
        }

        if (permission != null) {
            if (!permission.isError)
                businessPermission.value = permission.value?.fileName ?: ""
            else
                businessPermission.value = "Bledny plik"
        }
        if (card != null) {
            if (!card.isError)
                idCard.value = card.value?.fileName ?: ""
            else
                idCard.value = "Bledny plik"
        }

        if (rental != null) {
            if (!rental.isError) {
                rentalContract.value = rental.value?.fileName ?: ""
            } else {
                rentalContract.value = "Bledny plik"
            }
        }

        if (license != null) {
            if (!license.isError)
                alcoholLicense.value = license.value?.fileName ?: ""
            else
                alcoholLicense.value = "Bledny plik"
        }

        if (isRestaurantRegistrationSecondStepInvalid(context)) {
            return false
        }

        if (!rentalContract.value.endsWith(
                ".pdf",
                ignoreCase = true
            )
        ) {
            rentalContract.value = ""
        }

        if (!alcoholLicense.value.endsWith(
                ".pdf",
                ignoreCase = true
            )
        ) {
            alcoholLicense.value = ""
        }

        return true
    }

    private fun getRestaurantData(): RestaurantDTO {

        return RestaurantDTO(
            restaurantId = restaurantId ?: -1,
            name = name.value,
            restaurantType = restaurantType.value,
            nip = nip.value,
            address = address.value,
            postalIndex = postalCode.value,
            city = city.value,
            rentalContract = rentalContract.value.ifBlank { null },
            alcoholLicense = alcoholLicense.value.ifBlank { null },
            businessPermission = businessPermission.value,
            idCard = idCard.value,
            logo = logo.value,
            description = description.value,
            provideDelivery = delivery,
            tags = selectedTags,
            groupId = selectedGroup?.restaurantGroupId,
            photos = emptyList(),
            tables = emptyList(),
            location = LocationDTO(latitude = 52.39625635, longitude = 20.91364863552046)
        )
    }

    suspend fun getGroups() {
        groups = restaurantService.getGroups().value
    }

    suspend fun getTags() {
        //Przypisujemy pustą listę listOf(), jeśli getRestaurantTags() jest null
        tags = restaurantService.getRestaurantTags().value ?: listOf()
    }

    suspend fun sendFile(uri: String?, context: Context, type: DataType): Result<FileUploadDTO?>? {
        if (isFileNameInvalid(uri?.let { getFileName(context, it) })) {
            return null
        }
        val file = uri?.let { getFileFromUri(context, it.toUri()) }
        return file?.let { fileService.sendFile(type, it) }
    }


    suspend fun sendPhoto(uri: String?, context: Context): Result<FileUploadDTO?>? {
        if (isFileNameInvalid(uri?.let { getFileName(context, it) })) {
            return null
        }

        val file = uri?.let { getFileFromUri(context, it.toUri()) }
        var fDto = file?.let { fileService.sendFile(DataType.PNG, it) }
        if (fDto != null) {
            if (fDto.value == null) {
                fDto = file?.let { fileService.sendFile(DataType.JPG, it) }
            }
        }
        return fDto
    }

    private fun isRestaurantRegistrationInvalid(context: Context): Boolean {
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
                isLogoInvalid(context)

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
        return selectedGroup == null && newGroup.value.isBlank()
    }

    fun isBusinessPermissionInvalid(context: Context): Boolean {
        val value = businessPermission.value
        return value.isBlank() || !getFileName(context, value).endsWith(
            ".pdf",
            ignoreCase = true
        ) || isFileSizeInvalid(context, value)
    }

    fun isIdCardInvalid(context: Context): Boolean {
        val value = idCard.value
        return value.isBlank() || !getFileName(context, value).endsWith(
            ".pdf",
            ignoreCase = true
        ) || isFileSizeInvalid(context, value)
    }

    fun isAlcoholLicenseInvalid(context: Context): Boolean {
        val value = alcoholLicense.value
        return if (value.isBlank() || value == "null")
            false
        else
            !getFileName(context, value).endsWith(".pdf", ignoreCase = true) || isFileSizeInvalid(
                context,
                value
            )
    }

    fun isRentalContractInvalid(context: Context): Boolean {
        val value = rentalContract.value
        return if (value.isBlank() || value == "null")
            false
        else
            !getFileName(context, value).endsWith(".pdf", ignoreCase = true) || isFileSizeInvalid(
                context,
                value
            )
    }


    fun isLogoInvalid(context: Context): Boolean {
        val value = logo.value
        return value.isBlank() ||
                !(getFileName(context, value)
                    .endsWith(
                        ".png",
                        ignoreCase = true
                    )
                        || getFileName(
                    context,
                    value
                ).endsWith(
                    ".jpg",
                    ignoreCase = true
                )) || isFileSizeInvalid(context, value)
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
        return getFieldError(result2, logo.name)
    }

    fun getGroupError(): Int {
        return getFieldError(result3, newGroup.name)
    }

    fun getDescriptionError(): Int {
        return getFieldError(result3, description.name)
    }

    fun <T> getToastError(result: Result<T>): Int {
        return getFieldError(result, "TOAST")
    }
}
