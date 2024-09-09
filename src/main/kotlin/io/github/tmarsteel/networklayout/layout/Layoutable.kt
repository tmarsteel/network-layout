package io.github.tmarsteel.networklayout.layout

import com.github.nwillc.ksvg.elements.SVG
import org.chocosolver.solver.Model
import org.chocosolver.solver.Solution
import org.chocosolver.solver.variables.IntVar

abstract class Layoutable(
    protected val model: Model,
) {
    val x: IntVar = model.intVar(0, MAX_X)
    val y: IntVar = model.intVar(0, MAX_Y)
    abstract val width: IntVar
    abstract val height: IntVar

    /** is always be `x.add(width)`; is stored as a separate property for caching */
    val xPlusWidth: IntVar by lazy {
        x.add(width).intVar()
    }

    /** is always be `x.add(height)`; is stored as a separate property for caching */
    val yPlusHeight: IntVar by lazy {
        y.add(height).intVar()
    }

    /**
     * Invoke to post constraints to the [Model] that can apply to any other layoutable, regardless of the
     * relation in the domain model. E.g. to define constraints against overlapping of all layoutables.
     * @param allOtherLayoutables must yield all layoutables in the diagram, except the one this method is called on.
     */
    open fun postGlobalConstraints(allOtherLayoutables: Sequence<Layoutable>) {
        allOtherLayoutables.forEach { other ->
            model.noOverlap(this, other).post()
        }
    }

    abstract val render: SVG.(layoutSolution: Solution) -> Unit

    companion object {
        val MAX_X = 10000
        val MAX_Y = 10000
    }
}