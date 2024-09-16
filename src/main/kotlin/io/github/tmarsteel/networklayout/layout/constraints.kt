package io.github.tmarsteel.networklayout.layout

import org.chocosolver.solver.Model
import org.chocosolver.solver.constraints.Constraint
import org.chocosolver.solver.variables.IntVar

/*fun Model.noOverlap(a: Layoutable, b: Layoutable): Constraint {
    val noXOverlap = noRangeOverlap(a.x, a.xPlusWidth, b.x, b.xPlusWidth)
    val noYOverlap = noRangeOverlap(a.y, a.yPlusHeight, b.y, b.yPlusHeight)
    return or(noXOverlap, noYOverlap)
}*/

private fun Model.noRangeOverlap(rangeAStart: IntVar, rangeAEnd: IntVar, rangeBStart: IntVar, rangeBEnd: IntVar): Constraint {
    // thanks to https://stackoverflow.com/questions/3269434/whats-the-most-efficient-way-to-test-if-two-ranges-overlap
    return or(
        arithm(rangeAStart, ">", rangeBEnd),
        arithm(rangeBStart, ">", rangeAEnd)
    )
}