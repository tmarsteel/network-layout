package io.github.tmarsteel.networklayout.layout

import io.github.tmarsteel.networklayout.lateinitOnce
import io.github.tmarsteel.networklayout.network.Network
import io.github.tmarsteel.networklayout.network.Station
import org.chocosolver.solver.Model
import org.chocosolver.solver.Solution
import org.chocosolver.solver.variables.IntVar
import java.util.*
import kotlin.math.absoluteValue

private data class StationNode(
    val station: Station,
) {
    private val edges = mutableSetOf<StationNode>()
    fun addEdge(other: StationNode) {
        edges.add(other)
        other.edges.add(this)
    }

    fun hasEdgeWith(other: StationNode): Boolean = other in edges

    /**
     * a dimensionless value of how close to the center of the map this value should be located
     */
    val insidedness: Int get() {
        if (edges.size == 1) {
            // is an end-station, by definition outside
            return 0
        }

        return edges.sumOf { connectedNode ->
            this.station.gravity!!.angleTo(connectedNode.station.gravity!!).absoluteValue
        }
    }

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

// there can be multiple independent tracks running in the same direction
// e.g. in berlin:
// U7 and S3+S9 run independently to NORTH_WEST, converging in Spandau
// S7 and S5 run completely independently to NORTH_EAST
// these independent tracks will be called strands in this code
private class Strand(initialStation: StationNode) {
    private var completed = false
    private val stations = mutableSetOf<StationNode>(initialStation)

    /**
     * Attempts to add the given [candidate] to this strand. Succeeds iff
     * * [candidate] has a connection to another station in the strand
     * * the strand remains linear (any station has at most 2 connections to other stations in the strand)
     * @return whether the station was added or [candidate] was already part of the strand before
     */
    fun tryAdd(candidate: StationNode): Boolean {
        check(!completed) { "Strand already completed!" }

        if (candidate in stations) {
            return true
        }

        val connectingStation = stations.find { it.hasEdgeWith(candidate) }
            ?: return false // doesn't connect to this strand (yet)

        val edgeCountBeforeAdd = stations.count { it.hasEdgeWith(connectingStation) }
        if (edgeCountBeforeAdd >= 2) {
            // would break linearity
            return false
        }

        stations.add(candidate)
        return true
    }

    /**
     * The stations of this strand, sorted by distance to the map center (inwards, most distant station first).
     */
    private val stationsInOrder = ArrayList<StationNode>(1)

    /**
     * completes the strand, preventing further mutation through [tryAdd]
     */
    fun complete() {
        if (completed) {
            return
        }
        completed = true

        stationsInOrder.ensureCapacity(stations.size)

        val stationsToSort = stations.toMutableList()
        while (stationsToSort.isNotEmpty()) {
            val stationIterator = stationsToSort.listIterator()
            var anyStationSorted = false
            stations@while (stationIterator.hasNext()) {
                val stationToSort = stationIterator.next()
                if (stationsInOrder.isEmpty()) {
                    stationsInOrder.add(stationToSort)
                    stationIterator.remove()
                    anyStationSorted = true
                    continue@stations
                }

                val connectsToIndices = stationsInOrder
                    .asSequence()
                    .mapIndexed { index, sortedStation -> Pair(index, sortedStation) }
                    .filter { (_, sortedStation) -> sortedStation.hasEdgeWith(stationToSort) }
                    .map { (index, _) -> index }
                    .sorted()
                    .toList()

                when (connectsToIndices.size) {
                    0 -> {}
                    1 -> {
                        when (val connectsToIndex = connectsToIndices.single()) {
                            0 -> stationsInOrder.addFirst(stationToSort)
                            else -> stationsInOrder.add(connectsToIndex + 1, stationToSort)
                        }
                        stationIterator.remove()
                        anyStationSorted = true
                    }
                    2 -> {
                        val (indexBefore, indexAfter) = connectsToIndices
                        check(indexBefore + 1 == indexAfter) {
                            "Non-linear strand! $stationToSort doesn't fit linearly into this partial strand: $stationsInOrder"
                        }

                        stationsInOrder.add(indexAfter, stationToSort)
                        stationIterator.remove()
                        anyStationSorted = true
                    }
                }
            }

            check(anyStationSorted) {
                "Could not topologically sort this strand: $stations; stations not sorted: $stationsToSort"
            }
        }

        // make sure the sort direction is correct
        if (stationsInOrder.first().insidedness > stationsInOrder.last().insidedness) {
            stationsInOrder.reverse()
        }
    }

    override fun toString(): String {
        return if (completed) {
            "Strand[completed; $stationsInOrder]"
        } else {
            "Strand[partial; $stations]"
        }
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


    val strandsByDirection = mutableMapOf<Direction, MutableSet<Strand>>()
    cornerstoneNodes
        .groupBy { it.station.gravity!! }
        .forEach { (direction, stationNodes) ->
            val strands = strandsByDirection.computeIfAbsent(direction,  { _ -> HashSet() })
            val stationNodesToAllocate = stationNodes.toMutableList()
            allocate@while (stationNodesToAllocate.isNotEmpty()) {
                val stationsIterator = stationNodesToAllocate.listIterator()
                while (stationsIterator.hasNext()) {
                    val stationNode = stationsIterator.next()
                    for (strand in strands) {
                        if (strand.tryAdd(stationNode)) {
                            stationsIterator.remove()
                            continue@allocate
                        }
                    }
                }
                // could not allocate any station, start a new strand
                strands.add(Strand(stationNodesToAllocate.removeFirst()))
            }
        }
    strandsByDirection.values.flatten().forEach { it.complete() }

    strandsByDirection.forEach { direction, strands ->
        println(direction)
        strands.forEach(::println)
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
                    val stationANode = cornerstoneStationNodesByStation.getValue(stationA)
                    val stationBNode = cornerstoneStationNodesByStation.getValue(connection.station)
                    edges.add(ConnectionEdge(stationANode, stationBNode))
                    stationANode.addEdge(stationBNode)
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