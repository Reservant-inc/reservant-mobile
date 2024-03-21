package com.example.reservant_mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.time.YearMonth
import java.util.Locale
import java.util.regex.Pattern
import com.example.reservant_mobile.ui.constants.*

data class FormState(
    val isValid: Boolean = false,
    val errorMessage: String
)



class RegisterViewModel : ViewModel() {

    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var dateOfBirth by mutableStateOf(DateOfBirth())
    var email by mutableStateOf("")
    var phoneNumber by mutableStateOf(PhoneNumber())
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var birthday by mutableStateOf("") //nie jest uzywana
    var phoneNum by mutableStateOf("") //nie jest uzywana

    fun validateForm(): Boolean {
        return !(
                isInvalidWithRegex(NAME_REG, firstName) ||
                        isInvalidWithRegex(NAME_REG, lastName)  ||
                        isInvalidWithRegex(DATE_REG, birthday)  ||
                        isInvalidWithRegex(EMAIL_REG, email)    ||
                        isInvalidWithRegex(PHONE_REG, phoneNum) ||
                        isInvalidWithRegex(PASSWORD_REG, password) ||
                        confirmPassword != password
                )
    }

    private fun isInvalidWithRegex(regex: String, str: String): Boolean{
        return !Pattern.matches(regex, str)
    }
}

class DateOfBirth {
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
class PhoneNumber {
    var prefix: String by mutableStateOf("")
    var number: String by mutableStateOf("")

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