package reservant_mobile.data.utils

import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.util.Locale

data class Country(
    val nameCode: String,
    val code: String,
    val fullName: String
)

/**
 * Indeksy regionalne obejmują zakres od 0x1F1E6 (A) do 0x1F1FF (Z).
 * Jest to wartość wskaźnika regionalnego A pomniejszona o 65 w systemie dziesiętnym,
 * więc możemy po prostu dodać ją do znaku A-Z.
 **/
fun getFlagEmojiFor(countryCode: String): String = countryCode
    .toCharArray()
    .joinToString(separator = "") {
        Character.toChars(0x1F1A5 + it.uppercaseChar().code)
            .joinToString(separator = "")
    }
fun getCountriesList(): List<Country> {
    val phoneNumberUtil = PhoneNumberUtil.getInstance()
    val countries = mutableListOf<Country>()

    for (regionCode in phoneNumberUtil.supportedRegions) {
        val countryCode = phoneNumberUtil.getCountryCodeForRegion(regionCode).toString()
        val countryName = Locale("", regionCode).getDisplayCountry(Locale.ENGLISH)
        countries.add(Country(regionCode.lowercase(Locale.getDefault()), countryCode, countryName))
    }

    return countries.sortedBy { it.fullName }
}

fun getCountryDetailsByCode(code: String): Country? {
    val countries = getCountriesList()
    if(code == "1")
        return Country("us", "1", "United States")
    return countries.find { it.code == code }
}