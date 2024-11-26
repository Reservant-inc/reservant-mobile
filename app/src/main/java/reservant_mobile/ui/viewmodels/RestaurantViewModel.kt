package reservant_mobile.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.FileUploadDTO
import reservant_mobile.data.models.dtos.LocationDTO
import reservant_mobile.data.models.dtos.RestaurantDTO
import reservant_mobile.data.models.dtos.RestaurantGroupDTO
import reservant_mobile.data.models.dtos.fields.FormField
import reservant_mobile.data.models.dtos.fields.Result
import reservant_mobile.data.services.DataType
import reservant_mobile.data.services.IRestaurantService
import reservant_mobile.data.services.RestaurantService
import reservant_mobile.data.utils.getFileFromUri
import reservant_mobile.data.utils.getFileName
import reservant_mobile.data.utils.isFileNameInvalid
import reservant_mobile.data.utils.isFileSizeInvalid
import java.time.LocalTime
import kotlin.math.max

class RestaurantViewModel(
    private val restaurantService: IRestaurantService = RestaurantService()
): ReservantViewModel() {

    // Wynik rejestracji
    var resultFirstStep by mutableStateOf(Result(isError = false, value = false))
    var resultFileUploads by mutableStateOf(Result(isError = false, value = false))
    var resultGroup by mutableStateOf(Result(isError = false, value = false))
    var resultRegistration by mutableStateOf(Result<RestaurantDTO?>(isError = false, value = null))

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

    //Godziny otwarcia
    var openingHours: MutableList<Pair<String?, String?>> = MutableList(7){
        "09:00" to "18:00"
    }

    // Tagowanie i inne
    var tags = listOf(String())
    var selectedTags: List<String> by mutableStateOf(listOf())
    var delivery by mutableStateOf(false)

    // Grupa
    var groups: List<RestaurantGroupDTO>? by mutableStateOf(listOf())
    var selectedGroup by mutableStateOf<RestaurantGroupDTO?>(null)
    var newGroup: FormField = FormField(RestaurantDTO::groupName.name)
    var maxReservationMinutes = FormField(RestaurantDTO::maxReservationDurationMinutes.name)

    var restaurantId by mutableStateOf<Int?>(null)

    init {
        viewModelScope.launch {
            getGroups()
            getTags()
        }
    }

    suspend fun assignData(id: Int, group: RestaurantGroupDTO) {
        restaurantId = id
        val restaurant = restaurantService.getUserRestaurant(restaurantId!!)
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
            restaurant.value.maxReservationDurationMinutes?.let {
                maxReservationMinutes.value = it.toString()
            }
            openingHours = restaurant.value.openingHours?.map {
                it.from to it.until
            }?.toMutableList() ?: MutableList(7){
                "09:00" to "18:00"
            }

        }
    }


    suspend fun registerRestaurant(context: Context): Boolean {
        if (isRestaurantRegistrationInvalid(context)) {
            return false
        }

        val restaurant = getRestaurantData()

        resultRegistration = restaurantService.registerRestaurant(restaurant)

        if (resultRegistration.isError){
            return false
        }

        restaurantId = resultRegistration.value?.restaurantId

        if(newGroup.value.isNotBlank()){
            resultGroup = restaurantService.addGroup(RestaurantGroupDTO(
                name = newGroup.value,
                restaurantIds = listOf(restaurantId!!)
            ))
            return !resultGroup.isError
        }
        return true
    }

    suspend fun editRestaurant(context: Context): Boolean {
        if (isRestaurantRegistrationInvalid(context)) {
            return false
        }

        val restaurant = getRestaurantData()

        resultRegistration = restaurantService.editRestaurant(restaurant.restaurantId, restaurant)

        if (resultRegistration.isError){
            return false
        }

        if(newGroup.value.isNotBlank()){
                resultGroup = restaurantService.addGroup(RestaurantGroupDTO(
                    name = newGroup.value,
                    restaurantIds = listOf(restaurantId!!)
                ))
                return !resultFirstStep.isError
        }

        return true
    }

    suspend fun validateFirstStep(): Boolean {
        if (isRestaurantRegistrationFirstStepInvalid()) {
            return false
        }

        val restaurant = getFirstStepData()

        resultFirstStep = restaurantService.validateFirstStep(restaurant)
        return resultFirstStep.value
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

    private fun getFirstStepData(): RestaurantDTO {
        return RestaurantDTO(
            restaurantId = restaurantId ?: -1,
            name = name.value,
            restaurantType = restaurantType.value,
            nip = nip.value,
            address = address.value,
            postalIndex = postalCode.value,
            city = city.value,
        )
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
            location = LocationDTO(latitude = 52.39625635, longitude = 20.91364863552046),
            openingHours = openingHours.map {
                RestaurantDTO.AvailableHours(
                    from = it.first?.ifEmpty { null },
                    until = it.second?.ifEmpty { null }
                )
            },
            maxReservationDurationMinutes = maxReservationMinutes.value.toInt()
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
                isMaxReservationDurationInvalid() ||
                areOpeningHoursInvalid() ||
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

    fun isMaxReservationDurationInvalid(): Boolean {

        val maxResMinutesInt = maxReservationMinutes.value.toIntOrNull()

        return maxResMinutesInt == null ||
                maxResMinutesInt < 30 ||
                getFieldError(resultRegistration, maxReservationMinutes.name) != -1
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

    fun isOpeningHoursTimeInvalid(times: Pair<String?, String?>): Boolean {
        if (times.first == null && times.second == null){
            return false
        }

        val startTime = times.first?.let {
            LocalTime.parse(it)
        }
        val endTime = times.second?.let {
            LocalTime.parse(it)
        }

        return startTime == null || endTime == null || startTime >= endTime
    }

    fun areOpeningHoursInvalid(): Boolean {
        return openingHours.any {
            isOpeningHoursTimeInvalid(it)
        }
    }

    fun areTagsInvalid(): Boolean {
        return selectedTags.isEmpty()
    }

    fun getNameError(): Int {
        return getFieldError(resultFirstStep, name.name)
    }

    fun getRestaurantTypeError(): Int {
        return getFieldError(resultFirstStep, restaurantType.name)
    }

    fun getNipError(): Int {
        return getFieldError(resultFirstStep, nip.name)
    }

    fun getAdressError(): Int {
        return getFieldError(resultFirstStep, address.name)
    }

    fun getPostalError(): Int {
        return getFieldError(resultFirstStep, postalCode.name)
    }

    fun getCityError(): Int {
        return getFieldError(resultFirstStep, city.name)
    }

    fun getRentalContractError(): Int {
        return getFieldError(resultFileUploads, rentalContract.name)
    }

    fun getAlcoholLicenseError(): Int {
        return getFieldError(resultFileUploads, alcoholLicense.name)
    }

    fun getBusinessPermissionError(): Int {
        return getFieldError(resultFileUploads, businessPermission.name)
    }

    fun getIdCardError(): Int {
        return getFieldError(resultFileUploads, idCard.name)
    }

    fun getLogoError(): Int {
        return getFieldError(resultFileUploads, logo.name)
    }

    fun getGroupError(): Int {
        return getFieldError(resultRegistration, newGroup.name)
    }

    fun getDescriptionError(): Int {
        return getFieldError(resultRegistration, description.name)
    }

    fun getReservationDurationError(): Int {
        return getFieldError(resultRegistration, maxReservationMinutes.name)
    }

    fun getToastError1(): Int {
        return getToastError(resultFirstStep)
    }
    fun getToastError2(): Int {
        return getToastError(resultFileUploads)
    }
    fun getToastError3(): Int {
        return getToastError(resultRegistration)
    }

}
