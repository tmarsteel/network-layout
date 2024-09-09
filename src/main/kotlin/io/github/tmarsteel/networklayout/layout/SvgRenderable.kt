package io.github.tmarsteel.networklayout.layout

import com.github.nwillc.ksvg.elements.SVG

interface SvgRenderable {
    val render: SVG.(theme: Theme) -> Unit
}