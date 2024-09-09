package io.github.tmarsteel.networklayout.network

import io.github.tmarsteel.networklayout.layout.Layoutable
import io.github.tmarsteel.networklayout.layout.MajorStationMarkerLayoutable
import io.github.tmarsteel.networklayout.layout.Theme
import org.chocosolver.solver.Model

class Network {
    private val _stations = mutableMapOf<Long, Station>()
    private val _lines = mutableSetOf<Line>()

    fun registerStation(station: Station) {
        _stations[station.id] = station
    }

    fun addLine(line: Line) {
        _lines.add(line)
    }

    fun addStop(line: Line, atStationId: Long) {
        addLine(line)
        val station = _stations[atStationId] ?: error("Station#$atStationId not registered with the network")
        station.addLine(line)
    }

    fun createLayoutables(model: Model, theme: Theme, visitor: (Layoutable) -> Unit) {
        _stations.values.forEach { it.createLayoutables(model, theme, visitor) }
    }

    companion object {
        fun from(dto: NetworkDto): Network {
            val network = Network()
            dto.stations.asSequence()
                .map { Station(it.id, it.name) }
                .forEach(network::registerStation)

            dto.lines.asSequence()
                .forEach { lineDto ->
                    val lineEntity = Line(lineDto.type.name.uppercase() + lineDto.number.toString())
                    lineDto.stopsInPrimaryDirection.forEach { stop ->
                        network.addStop(lineEntity, stop.stationId)
                    }
                }

            return network
        }
    }
}

class Station(
    val id: Long,
    val name: String,
) {
    private val connectsToLines = mutableSetOf<Line>()

    fun addLine(line: Line) {
        connectsToLines.add(line)
    }

    fun createLayoutables(model: Model, theme: Theme, visitor: (Layoutable) -> Unit) {
        when (connectsToLines.size) {
            0, 1 -> {}
            else -> visitor(MajorStationMarkerLayoutable(model, theme, connectsToLines.size))
        }

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Station

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString() = "Station#$id[$name]"
}

class Line(
    val name: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Line

        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}