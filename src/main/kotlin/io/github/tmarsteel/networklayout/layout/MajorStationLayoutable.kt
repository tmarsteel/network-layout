package io.github.tmarsteel.networklayout.layout

import com.github.nwillc.ksvg.elements.SVG
import io.github.tmarsteel.networklayout.dslPath

class MajorStationLayoutable : Layoutable {
    override val width: Double = 5.0
    override val height: Double = 5.0

    override val render: SVG.(x: Double, y: Double, theme: Theme) -> Unit = { x, y, theme ->
        rect {
            this@rect.x = x.toString()
            this@rect.y = y.toString()
            this@rect.width = this@MajorStationLayoutable.width.toString()
            this@rect.height = this@MajorStationLayoutable.height.toString()
            fill = "#F00"
        }
        rect {
            this@rect.x = (x + this@MajorStationLayoutable.height - theme.majorStationBorderWidth).toString()
            this@rect.y = (x + this@MajorStationLayoutable.width - theme.majorStationBorderWidth).toString()
            this@rect.width = theme.majorStationBorderWidth.toString()
            this@rect.height = theme.majorStationBorderWidth.toString()
            fill = "#0F0"
        }

        val halfStroke = theme.majorStationBorderWidth / 2.0
        dslPath {
            moveTo(
                x = x + theme.majorStationBorderWidth + theme.majorStationBorderRadius,
                y = y + halfStroke,
            )
            lineTo(
                x = x + this@MajorStationLayoutable.width - theme.majorStationBorderWidth - theme.majorStationBorderRadius,
                y = y + halfStroke,
            )
            arcTo(
                x = x + this@MajorStationLayoutable.width - halfStroke,
                y = y + theme.majorStationBorderWidth,
                radiusX = theme.majorStationBorderRadius,
                radiusY = theme.majorStationBorderRadius,
            )

            strokeWidth = theme.majorStationBorderWidth
            stroke = "#000"
            fill = "#F00"
        }
    }
}