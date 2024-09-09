package io.github.tmarsteel.networklayout

import com.github.nwillc.ksvg.elements.Container

fun viewBox(offsetX: Double, offsetY: Double, width: Double, height: Double) = "$offsetX $offsetY $width $height"

fun Container.dslPath(block: DslPath.() -> Unit) {
    val dslPath = DslPath()
    dslPath.block()
    path {
        d = dslPath.definition
        dslPath.strokeWidth?.let { strokeWidth = it.toString() }
        dslPath.stroke?.let { stroke = it }
        dslPath.fill?.let { fill = it }
    }
}

@DslMarker
annotation class PathDslMarker

@PathDslMarker
class DslPath {
    private val _definition = StringBuilder()
    val definition: String get() = _definition.toString().trim()
    var strokeWidth: Double? = null
    var stroke: String? = null
    var fill: String? = null

    fun moveTo(x: Double, y: Double) {
        _definition.append(" M")
        _definition.append(x.toString())
        _definition.append(',')
        _definition.append(y.toString())
    }

    fun lineTo(x: Double, y: Double) {
        _definition.append(" L")
        _definition.append(x.toString())
        _definition.append(',')
        _definition.append(y.toString())
    }

    fun arcTo(x: Double, y: Double, radiusX: Double, radiusY: Double, rotateX: Double = 0.0, largeArc: Boolean = false, sweep: Boolean = false) {
        _definition.append(" A")
        _definition.append(radiusX.toString())
        _definition.append(',')
        _definition.append(radiusY.toString())
        _definition.append(',')
        _definition.append(rotateX.toString())
        _definition.append(',')
        _definition.appendFlag(largeArc)
        _definition.append(',')
        _definition.appendFlag(sweep)
        _definition.append(',')
        _definition.append(x.toString())
        _definition.append(',')
        _definition.append(y.toString())
    }

    private fun StringBuilder.appendFlag(flag: Boolean) {
        if (flag) {
            append('1')
        } else {
            append('0')
        }
    }
}