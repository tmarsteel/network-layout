package io.github.tmarsteel.networklayout.layout

sealed interface Constrainable {
    val concrecte: Double

    class Concrete(override val concrecte: Double) : Constrainable

    class Variable : Constrainable {
        override val concrecte: Double get() = error("no value defined")
    }

    class DerivePlus
}