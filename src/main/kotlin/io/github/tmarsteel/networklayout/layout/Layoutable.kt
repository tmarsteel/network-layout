package io.github.tmarsteel.networklayout.layout

import com.github.nwillc.ksvg.elements.SVG
import org.chocosolver.solver.Solution
import org.chocosolver.solver.variables.IntVar

interface Layoutable {
    val x: IntVar
    val y: IntVar
    val width: IntVar
    val height: IntVar

    fun postConstraints(allOtherLayoutables: Sequence<Layoutable>)

    val render: SVG.(layoutSolution: Solution) -> Unit

    companion object {
        val MAX_X = 10000
        val MAX_Y = 10000
    }
}