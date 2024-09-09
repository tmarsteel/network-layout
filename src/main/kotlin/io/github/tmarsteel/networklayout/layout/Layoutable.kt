package io.github.tmarsteel.networklayout.layout

interface Layoutable : SvgRenderable {
    val width: Double
    val height: Double

    val x: Constrainable
    val y: Constrainable
}