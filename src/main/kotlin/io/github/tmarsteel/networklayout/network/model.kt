package io.github.tmarsteel.networklayout.network

import io.github.tmarsteel.networklayout.lateinitOnce
import io.github.tmarsteel.networklayout.layout.Direction

class Network {
    private val _stations = mutableMapOf<Long, Station>()
    private val _lines = mutableSetOf<Line>()

    fun registerStation(station: Station) {
        _stations[station.id] = station
    }

    fun addLine(line: Line) {
        _lines.add(line)
    }

    fun getStation(id: Long): Station {
        return _stations[id] ?: error("Station#$id not registered with the network")
    }

    val cornerstoneStations: Iterable<Station> get() {
        val cornerstones = mutableSetOf<Station>()
        for (line in _lines) {
            cornerstones.add(line.stopsInPrimaryDirection.first())
            cornerstones.add(line.stopsInPrimaryDirection.last())
        }

        for (station in _stations.values) {
            if (station in cornerstones) {
                continue
            }

            if (station.connections.size > 2) {
                // simple stations have only two connections
                continue
            }

            val allConnectionsSameLines = station.connections
                .asSequence()
                .map { it.byLines }
                .distinct()
                .isSingleton()

            if (allConnectionsSameLines) {
                continue
            }

            cornerstones.add(station)
        }

        return cornerstones
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
                    lineEntity.stopsInPrimaryDirection = lineDto.stopsInPrimaryDirection
                        .map { network.getStation(it.stationId) }
                        .toTypedArray()
                    network.addLine(lineEntity)
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

    val connections: Set<Connection> by lazy {
        val linesByConnectedStation = mutableMapOf<Station, MutableSet<Line>>()
        for (line in connectsToLines) {
            val stops = line.stopsInPrimaryDirection
            val indexOfSelf = stops.indexOf(this)
            check(indexOfSelf >= 0) { "$this is supposed to be connected to $line, but didn't find $this in the stops of $line" }
            val connectsToStations = when (indexOfSelf) {
                0 -> if (stops.size > 1) setOf(stops[1]) else emptySet()
                stops.lastIndex -> if (stops.size > 1) setOf(stops[stops.lastIndex - 1]) else emptySet()
                else -> setOf(stops[indexOfSelf - 1], stops[indexOfSelf + 1])
            }
            connectsToStations.forEach { connectedStation ->
                linesByConnectedStation.computeIfAbsent(connectedStation, { mutableSetOf() }).add(line)
            }
        }

        return@lazy linesByConnectedStation
            .map { (connectedStation, byLines) ->
                Connection(connectedStation, byLines)
            }
            .toSet()
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

    /**
     * resembles a connection from one station (not named) to another station [station]. The station is reachable
     * by [byLines]
     */
    data class Connection(
        val station: Station,
        val byLines: Set<Line>,
    ) {
        /**
         * the [Direction] in which this connection _leaves_ this station on the map. Is set during stage 2.
         */
        var outgoingDirection by lateinitOnce<Direction>()
    }
}

class Line(
    val name: String
) {
    private var _primaryStops by lateinitOnce<Array<Station>>()
    var stopsInPrimaryDirection: Array<Station>
        get() = _primaryStops
        set(value) {
            value.forEach { it.addLine(this) }
            _primaryStops = value
        }

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

private fun <T> Sequence<T>.isSingleton(): Boolean {
    return take(2).count() == 1
}