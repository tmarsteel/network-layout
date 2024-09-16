package io.github.tmarsteel.networklayout.svg

import com.github.nwillc.ksvg.elements.SVG
import org.chocosolver.solver.Solution

abstract class Drawable(

) {
    abstract val draw: SVG.() -> Unit
}