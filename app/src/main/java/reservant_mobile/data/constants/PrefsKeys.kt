package reservant_mobile.data.constants

enum class PrefsKeys(val keyName: String) {
    BEARER_TOKEN("prefs_bearer_token"),
    EMPLOYEE_CURRENT_RESTAURANT("prefs_employee_current_restaurant"),
    FCM_TOKEN("prefs_cm_tok"),
    APP_THEME("prefs_app_theme")
}

enum class ThemePrefsKeys(val themeValue: String){
    DARK("theme_dark"),
    LIGHT("theme_light")
}