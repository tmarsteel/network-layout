package io.github.tmarsteel.networklayout.svg

import com.github.nwillc.ksvg.elements.SVG
import io.github.tmarsteel.networklayout.layout.Theme

class MultiLineStationDrawable(
    val theme: Theme,
) : Drawable() {
    override val draw: SVG.() -> Unit = {
        val halfStroke = theme.majorStationBorderWidth / 2.0
        val x = 0.0 // TODO
        val y = 0.0 // TODO
        val width = 5.0 // TODO
        val height = 5.0 // TODO

        dslPath {
            moveTo(
                x = x + theme.majorStationBorderRadius + halfStroke,
                y = y + halfStroke,
            )
            lineTo(
                x = x + width - theme.majorStationBorderRadius - halfStroke,
                y = y + halfStroke,
            )
            arcTo(
                x = x + width - halfStroke,
                y = y + theme.majorStationBorderRadius + halfStroke,
                radiusX = theme.majorStationBorderRadius,
                radiusY = theme.majorStationBorderRadius,
                sweep = true,
            )
            lineTo(
                x = x + width - halfStroke,
                y = y + height - theme.majorStationBorderRadius - halfStroke,
            )
            arcTo(
                x = x + width - theme.majorStationBorderRadius - halfStroke,
                y = y + height - halfStroke,
                radiusX = theme.majorStationBorderRadius,
                radiusY = theme.majorStationBorderRadius,
                sweep = true,
            )
            lineTo(
                x = x + theme.majorStationBorderRadius + halfStroke,
                y = y + height - halfStroke
            )
            arcTo(
                x = x + halfStroke,
                y = y + height - theme.majorStationBorderRadius - halfStroke,
                radiusX = theme.majorStationBorderRadius,
                radiusY = theme.majorStationBorderRadius,
                sweep = true,
            )
            lineTo(
                x = x + halfStroke,
                y = y + theme.majorStationBorderRadius + halfStroke
            )
            arcTo(
                x = x + theme.majorStationBorderRadius + halfStroke,
                y = y + halfStroke,
                radiusX = theme.majorStationBorderRadius,
                radiusY = theme.majorStationBorderRadius,
                sweep = true,
            )
            closePath()

            strokeWidth = theme.majorStationBorderWidth
            stroke = "#000"
            fill = "#FFF"
        }
    }
}