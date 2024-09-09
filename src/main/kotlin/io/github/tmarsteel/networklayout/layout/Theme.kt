package io.github.tmarsteel.networklayout.layout

data class Theme(
    val majorStationBorderRadius: Double = 1.0,
    val majorStationBorderWidth: Double = 0.5,
    /** thickness of lines (the visual lines representing the tracks/paths) */
    val lineThickness: Double = 1.0,
    /** spacing between adjacent lines/tracks/paths running in the same direction */
    val lineSpacing: Double = 0.5,
    /** minimum distance between two stations that are only served by one line */
    val simpleStationSeparation: Double = 3.0,
) {
    companion object {
        val DEFAULT = Theme()
    }
}