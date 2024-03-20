package com.example.reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.time.YearMonth
import java.util.Locale
import java.util.regex.Pattern

data class FormState(
    val isValid: Boolean = false,
    val errorMessage: String
)



class RegisterViewModel : ViewModel() {

    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var dayOfBirth by mutableStateOf("")
    var monthOfBirth by mutableStateOf("")
    var yearOfBirth by mutableStateOf("")
    var email by mutableStateOf("")
    var prefix by mutableStateOf("")
    var number by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var birthday by mutableStateOf("") //nie jest uzywana
    var phoneNum by mutableStateOf("") //nie jest uzywana

    val dateRegex = "\\d{4}-\\d{2}-\\d{2}"
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    val phoneRegex = "^\\d{9}$"

    fun validateForm(): Boolean {
        return !(
                firstName.isBlank() ||
                lastName.isBlank()  ||
                isInvalidWithRegex(dateRegex, birthday)  ||
                isInvalidWithRegex(emailRegex, email)    ||
                isInvalidWithRegex(phoneRegex, phoneNum) ||
                password.isBlank() ||
                confirmPassword.isBlank()
        )
    }

    private fun isInvalidWithRegex(regex: String, str: String): Boolean{
        return !Pattern.matches(regex, str)
    }

    fun getCountryCodesWithPrefixes(): List<String> {
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        val countryCodesWithPrefixes = mutableListOf<String>()

        for (regionCode in phoneNumberUtil.supportedRegions) {
            val countryPrefix = phoneNumberUtil.getCountryCodeForRegion(regionCode).toString()
            val countryName = Locale("", regionCode).getDisplayCountry(Locale.ENGLISH)
            val formattedString = "$countryName - $countryPrefix"
            countryCodesWithPrefixes.add(formattedString)
        }

        return countryCodesWithPrefixes.sorted()
    }
}

class Calendar {
    var yearOfBirth: String by mutableStateOf("")
    var monthOfBirth: String by mutableStateOf("")
    var dayOfBirth: String by mutableStateOf("")

    fun getDaysList(year: String, month: String): List<String> {
        return if (year.isNotEmpty() && month.isNotEmpty()) {
            (1..YearMonth.of(year.toInt(), month.toInt()).lengthOfMonth()).map { it.toString() }
        } else {
            listOf()
        }
    }
}