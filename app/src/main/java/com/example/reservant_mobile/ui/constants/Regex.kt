package com.example.reservant_mobile.ui.constants

const val NAME_REG = "^[A-Za-z]+$"
const val DATE_REG = "\\d{4}-\\d{2}-\\d{2}"
const val EMAIL_REG = "[a-zA-z0-9][\\w.-]*@[a-zA-z0-9]{2,}\\.[a-zA-Z]{2,}"
const val PHONE_REG = "^\\d{9,15}$" // 9-15 digits
// 8+ chars, min. 1 small and big letter, one digit and one special
const val PASSWORD_REG = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
