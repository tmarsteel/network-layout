package io.github.tmarsteel.networklayout.layout

import com.github.nwillc.ksvg.elements.SVG
import org.chocosolver.solver.Model
import org.chocosolver.solver.Solution

class SimpleStationMarkerLayoutable(
    model: Model,
    private val theme: Theme,
) : Layoutable(model) {
    override val width = model.intVar(1)
    override val height = model.intVar(1)

    override val render: SVG.(layoutSolution: Solution) -> Unit = { layoutSolution ->
        rect {
            x = layoutSolution.getIntVal(this@SimpleStationMarkerLayoutable.x).toString()
            y = layoutSolution.getIntVal(this@SimpleStationMarkerLayoutable.y).toString()
            width = layoutSolution.getIntVal(this@SimpleStationMarkerLayoutable.width).toString()
            height = layoutSolution.getIntVal(this@SimpleStationMarkerLayoutable.height).toString()
            fill = "#F00" // todo: obtain color from line
        }
    }
}