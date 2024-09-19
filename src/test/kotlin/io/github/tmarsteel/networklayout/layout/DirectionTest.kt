package io.github.tmarsteel.networklayout.layout

import io.kotest.core.spec.style.FreeSpec
import io.github.tmarsteel.networklayout.layout.Direction.*
import io.kotest.matchers.shouldBe

class DirectionTest : FreeSpec({
    "from north" {
        NORTH.angleTo(NORTH) shouldBe 0
        NORTH.angleTo(NORTH_EAST) shouldBe 45
        NORTH.angleTo(SOUTH_EAST) shouldBe 135
        NORTH.angleTo(SOUTH) shouldBe 180
        NORTH.angleTo(SOUTH_WEST) shouldBe -135
        NORTH.angleTo(NORTH_WEST) shouldBe -45
    }

    "from south-west" {
        SOUTH_WEST.angleTo(SOUTH_WEST) shouldBe 0
        SOUTH_WEST.angleTo(WEST) shouldBe 45
        SOUTH_WEST.angleTo(NORTH) shouldBe 135
        SOUTH_WEST.angleTo(NORTH_EAST) shouldBe 180
        SOUTH_WEST.angleTo(EAST) shouldBe -135
        SOUTH_WEST.angleTo(SOUTH) shouldBe -45
    }
})