package reservant_mobile.data.constants

data object Regex{
    const val LOGIN = "[\\w.\\-$!]{2,}"
    const val NAME_REG = "^[\\p{L} -]+$" //litery specjalne z różnych języków, spacja i -
    const val DATE_REG = "^[0,1,2]\\d{3}-[0,1]\\d-[0,1,2]\\d\$"
    const val EMAIL_REG = "[a-zA-z0-9][\\w.-]*@[a-zA-z0-9]{2,}\\.[a-zA-Z]{2,}"
    const val PHONE_REG = "^(\\+\\d{1,3})?\\d{9,15}$" // optional country code and 9-15 digits
    // 8+ chars, min. 1 small and big letter, one digit and one special
    const val PASSWORD_REG = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
}
