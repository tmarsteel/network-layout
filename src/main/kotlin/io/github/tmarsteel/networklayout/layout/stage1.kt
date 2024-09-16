package io.github.tmarsteel.networklayout.layout

import io.github.tmarsteel.networklayout.lateinitOnce
import io.github.tmarsteel.networklayout.network.Network
import io.github.tmarsteel.networklayout.network.Station
import org.chocosolver.solver.Model
import org.chocosolver.solver.Solution
import org.chocosolver.solver.variables.IntVar
import java.util.*
import kotlin.math.nextUp
import kotlin.math.sqrt

private data class StationNode(
    val station: Station,
) {


    fun postToModel(model: Model) {

    }
}

private class ConnectionEdge(
    val stationAnode: StationNode,
    val stationBnode: StationNode,
) {
    var stationAConnectionDirection: IntVar by lateinitOnce()

    fun postToModel(model: Model) {
        stationAConnectionDirection = model.intVar(0, Direction.entries.lastIndex)
    }

    fun storeSolutionBackToModel(solution: Solution) {
        val aDirection = solution.getIntVal(stationAConnectionDirection).let(Direction.entries::get)
        val bDirection = aDirection.opposite
        
        stationAnode.station.connections
            .single { it.station == stationBnode.station }
            .outgoingDirection = aDirection

        stationBnode.station.connections
            .single { it.station == stationAnode.station }
            .outgoingDirection = bDirection
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ConnectionEdge) return false

        if (other.stationAnode == this.stationAnode && other.stationBnode == this.stationBnode) return true
        if (other.stationBnode == this.stationAnode && other.stationAnode == this.stationBnode) return true
        return false
    }

    override fun hashCode(): Int {
        return stationAnode.hashCode() + stationBnode.hashCode()
    }
}

/**
 * implements stage 1
 */
fun Network.placeCornerstoneStationsOnGrid() {
    val (cornerstoneNodes, edges) = findCornerstoneStationsAndEdges()
    cornerstoneNodes
        .filter { it.station.gravity == null }
        .takeUnless { it.isEmpty() }
        ?.let { cornerstonesWithoutGravity ->
            error("missing gravity on cornerstone stations " + cornerstonesWithoutGravity.sortedBy { it.station.id }.joinToString(transform = { it.station.toString() }))
        }
}

private fun Network.findCornerstoneStationsAndEdges(): Pair<Set<StationNode>, Set<ConnectionEdge>> {
    val cornerstoneStationsSet = cornerstoneStations.toSet()
    val cornerstoneStationNodesByStation = cornerstoneStationsSet.associateWith { StationNode(it) }
    val stationsVisited = Collections.newSetFromMap<Station>(IdentityHashMap())
    val edges = mutableSetOf<ConnectionEdge>()
    fun visit(stationA: Station) {
        if (stationA in stationsVisited) {
            return
        }

        stationsVisited.add(stationA)
        val followedStations = mutableSetOf<Station>()

        fun follow(connection: Station.Connection) {
            if (connection.station in followedStations) {
                return
            }
            followedStations.add(connection.station)
            if (connection.station in cornerstoneStationsSet) {
                if (connection.station != stationA) {
                    edges.add(
                        ConnectionEdge(
                            cornerstoneStationNodesByStation.getValue(stationA),
                            cornerstoneStationNodesByStation.getValue(connection.station)
                        )
                    )
                }
                return
            }

            connection.station.connections.forEach(::follow)
        }

        stationA.connections.forEach(::follow)
    }
    cornerstoneStations.forEach(::visit)

    return Pair(cornerstoneStationNodesByStation.values.toSet(), edges)
}

fun placeOnGrid() {
    /*val gridSizeX = sqrt(stations.size.toDouble()).nextUp().toInt()
    val gridLength = gridSizeX * gridSizeX

    val stationNodesByStation = stations
        .map { StationNode(it, gridLength, gridSizeX) }
        .associateBy { it.station }

    val edges = stationNodesByStation.values.asSequence()
        .flatMap { stationAnode ->
            stationAnode.station.connections.asSequence()
                .mapNotNull { connection -> stationNodesByStation[connection.station] }
                .map { stationBnode -> ConnectionEdge(stationAnode, stationBnode) }
        }
        .toSet()

    val model = Model()
    stationNodesByStation.values.forEach { it.postToModel(model) }
    
    edges.forEach { it.postToModel(model) }
    edges.asSequence().distinctPairs().forEach { (edge1, edge2) ->

    }
    val solution = model.solver.findSolution()
        ?: error("Couldn't find a viable relative layout for the cornerstone stations")

    edges.forEach { it.storeSolutionBackToModel(solution) }*/
}

/**
 * @return all distinct 2-element combinations from the iterable.
 */
private fun <T> Sequence<T>.distinctPairs(): Sequence<Pair<T, T>> {
    return sequence {
        this@distinctPairs.forEachIndexed { aIndex, a ->
            this@distinctPairs.asSequence().drop(aIndex + 1).forEach { b ->
                yield(Pair(a, b))
            }
        }
    }
}