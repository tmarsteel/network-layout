package io.github.tmarsteel.networklayout.layout

data class Theme(
    val majorStationBorderRadius: Double = 1.0,
    val majorStationBorderWidth: Double = 0.5,
) {
    companion object {
        val DEFAULT = Theme()
    }
}