package io.github.tmarsteel.networklayout.layout

import com.github.nwillc.ksvg.elements.SVG
import io.github.tmarsteel.networklayout.dslPath

class MajorStationLayoutable : Layoutable {
    override val width: Double = 5.0
    override val height: Double = 5.0

    override val render: SVG.(x: Double, y: Double, theme: Theme) -> Unit = { x, y, theme ->
        val halfStroke = theme.majorStationBorderWidth / 2.0
        dslPath {
            moveTo(
                x = x + theme.majorStationBorderRadius + halfStroke,
                y = y + halfStroke,
            )
            lineTo(
                x = x + this@MajorStationLayoutable.width - theme.majorStationBorderRadius - halfStroke,
                y = y + halfStroke,
            )
            arcTo(
                x = x + this@MajorStationLayoutable.width - halfStroke,
                y = y + theme.majorStationBorderRadius + halfStroke,
                radiusX = theme.majorStationBorderRadius,
                radiusY = theme.majorStationBorderRadius,
                sweep = true,
            )
            lineTo(
                x = x + this@MajorStationLayoutable.width - halfStroke,
                y = y + this@MajorStationLayoutable.height - theme.majorStationBorderRadius - halfStroke,
            )
            arcTo(
                x = x + this@MajorStationLayoutable.width - theme.majorStationBorderRadius - halfStroke,
                y = y + this@MajorStationLayoutable.height - halfStroke,
                radiusX = theme.majorStationBorderRadius,
                radiusY = theme.majorStationBorderRadius,
                sweep = true,
            )
            lineTo(
                x = x + theme.majorStationBorderRadius + halfStroke,
                y = y + this@MajorStationLayoutable.height - halfStroke
            )
            arcTo(
                x = x + halfStroke,
                y = y + this@MajorStationLayoutable.height - theme.majorStationBorderRadius - halfStroke,
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