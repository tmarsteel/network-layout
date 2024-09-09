package io.github.tmarsteel.networklayout.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class StationDto(
    val id: Long,
    val name: String,
    val gravity: Gravity? = null,
) {
    enum class Gravity {
        NORTH,
        NORTH_EAST,
        EAST,
        SOUTH_EAST,
        SOUTH,
        SOUTH_WEST,
        WEST,
        NORTH_WEST,
        ;
    }
}

@Serializable
data class NetworkDto(
    val stations: List<StationDto>,
    val lines: List<LineDto>,
) {
    companion object {
        val FORMAT = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        }
    }
}

@Serializable
data class LineDto(
    val number: Long,
    val type: Type,
    @SerialName("stations")
    val stopsInPrimaryDirection: List<LineStopDto>,
) {
    enum class Type {
        U,
        S,
        ;
    }
}

@Serializable
data class LineStopDto(
    @SerialName("id")
    val stationId: Long,
    val durationToNext: Long? = null,
)