package de.nicetoapp.kapitalmeisterbackend.model.common

enum class Country(val locale: String) {
    German("de"),
    US("us");

    companion object {
        fun findByLocale(locale: String): Country? {
            return entries.firstOrNull { it.name.equals(locale, ignoreCase = true) }
        }
    }
}