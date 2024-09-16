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
}