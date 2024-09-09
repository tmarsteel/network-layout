package io.github.tmarsteel.networklayout.layout

import com.github.nwillc.ksvg.elements.SVG

interface Layoutable {
    val width: Double
    val height: Double

    val render: SVG.(x: Double, y: Double, theme: Theme) -> Unit
}