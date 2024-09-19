package io.github.tmarsteel.networklayout.layout

enum class Direction {
    NORTH,
    NORTH_EAST,
    EAST,
    SOUTH_EAST,
    SOUTH,
    SOUTH_WEST,
    WEST,
    NORTH_WEST,
    ;

    val opposite: Direction by lazy {
        val values = enumValues<Direction>()
        values[(ordinal + (values.size / 2)) % values.size]
    }

    /**
     * @return the angle between `this` and [other]. A positive value indicates that [other] is rotated clockwise
     * up to and including 180°, a negative value indicates rotation counter-clockwise of up to **but excluding** 180°.
     */
    fun angleTo(other: Direction): Int {
        if (ordinal > other.ordinal) {
            return when (val inverseAngle = other.angleTo(this)) {
                180 -> inverseAngle
                else -> -inverseAngle
            }
        }

        val nSteps = other.ordinal - this.ordinal
        val angle = nSteps * (360 / enumValues<Direction>().size)
        return if (angle > 180) {
            -(360 - angle)
        } else {
            angle
        }
    }
}