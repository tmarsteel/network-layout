package io.github.tmarsteel.networklayout.layout

import com.github.nwillc.ksvg.elements.SVG
import io.github.tmarsteel.networklayout.dslPath
import org.chocosolver.solver.Model
import org.chocosolver.solver.Solution
import org.chocosolver.solver.variables.IntVar
import kotlin.math.nextUp

class MajorStationMarkerLayoutable(
    model: Model,
    val theme: Theme,
    val nLines: Int,
) : Layoutable(model) {
    override val width: IntVar = model.intVar(
        (nLines.toDouble() * (theme.lineThickness + theme.lineSpacing)).nextUp().toInt()
            .coerceAtLeast((theme.majorStationBorderRadius.coerceAtLeast(theme.majorStationBorderWidth) * 2.0).toInt())
    )
    override val height = width

    override val render: SVG.(Solution) -> Unit = { layoutSolution ->
        val halfStroke = theme.majorStationBorderWidth / 2.0
        val x = layoutSolution.getIntVal(this@MajorStationMarkerLayoutable.x).toDouble()
        val y = layoutSolution.getIntVal(this@MajorStationMarkerLayoutable.y).toDouble()
        val width = layoutSolution.getIntVal(this@MajorStationMarkerLayoutable.width).toDouble()
        val height = layoutSolution.getIntVal(this@MajorStationMarkerLayoutable.height).toDouble()

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