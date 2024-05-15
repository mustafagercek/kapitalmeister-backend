package de.nicetoapp.kapitalmeisterbackend.model.common

enum class SeedDataSource(val refreshInterval: Int, val version: Int) {
    GERMAN_MONTHLY_CPI(1, 0),
    GERMANY_YEARLY_CPI(4, 0),
    GERMAN_CATEGORY(-1, 0),
    GERMAN_CAT_MONTHLY_CPI(1, 0);
}