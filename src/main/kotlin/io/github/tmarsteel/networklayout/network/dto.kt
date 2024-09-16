package io.github.tmarsteel.networklayout.network

import io.github.tmarsteel.networklayout.layout.Direction
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class StationDto(
    val id: Long,
    val name: String,
    val gravity: Direction? = null,
)

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