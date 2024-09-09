package io.github.tmarsteel.networklayout.cli

import com.github.ajalt.clikt.completion.CompletionCandidates
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.path
import com.github.nwillc.ksvg.elements.SVG
import io.github.tmarsteel.networklayout.layout.Layoutable
import io.github.tmarsteel.networklayout.layout.Theme
import io.github.tmarsteel.networklayout.network.Network
import io.github.tmarsteel.networklayout.network.NetworkDto
import io.github.tmarsteel.networklayout.viewBox
import kotlinx.serialization.json.decodeFromStream
import org.chocosolver.solver.Model
import kotlin.io.path.inputStream
import kotlin.io.path.writer

object MainCommand : CliktCommand() {
    val networkFile by option(
        "--network",
        completionCandidates = CompletionCandidates.Path,
    )
        .path(mustExist = true, canBeDir = false, mustBeReadable = true)
        .required()

    val outputFile by option(
        "--output",
    )
        .path(mustExist = false, canBeDir = false)
        .required()

    override fun run() {
        var networkDto = networkFile.inputStream().use { networkInStream ->
            NetworkDto.FORMAT.decodeFromStream<NetworkDto>(networkInStream)
        }
        networkDto = networkDto.copy(lines = networkDto.lines.takeLast(7))

        echo("Building semantic model of input data")
        val networkModel = Network.from(networkDto)

        echo("Defining layout constraints")
        val layoutModel = Model().apply {
            settings.enableViews()
        }
        val layoutables = ArrayList<Layoutable>()
        networkModel.createLayoutables(layoutModel, Theme.DEFAULT, layoutables::add)
        var layoutableIndex = 0
        while (layoutableIndex < layoutables.size) {
            val layoutable = layoutables[layoutableIndex]
            val allOthers = layoutables.filterIndexed { index, _ ->
                index != layoutableIndex
            }
            layoutable.postGlobalConstraints(allOthers.asSequence())
            layoutableIndex++
        }

        val modelStats = layoutModel.modelAnalyser.analyseModel()
        val nVars = modelStats.varsTypeStats.sumOf { it.nbVariables }
        val nPropagators = modelStats.cstrsTypeStats.sumOf { it.nbPropagators }
        echo("Computing a layout ($nVars vars, $nPropagators propagators)")
        // TODO: find an optimal solution
        if (!layoutModel.solver.solve()) {
            throw PrintMessage("there is no viable layout for this network, sorry.", statusCode = 2, true)
        }
        val layoutSolution = layoutModel.solver.findSolution()
        echo("Found a layout! Rendering to $outputFile")

        val offsetX = layoutables.asSequence()
            .map { layoutSolution.getIntVal(it.x) }
            .min()
        val offsetY = layoutables.asSequence()
            .map { layoutSolution.getIntVal(it.y) }
            .min()
        val sizeX = layoutables.asSequence()
            .map { layoutSolution.getIntVal(it.xPlusWidth) }
            .max()
        val sizeY = layoutables.asSequence()
            .map { layoutSolution.getIntVal(it.yPlusHeight) }
            .max()

        val svg = SVG.svg {
            viewBox = viewBox(offsetX.toDouble(), offsetY.toDouble(), sizeX.toDouble(), sizeY.toDouble())

            layoutables.forEach { it.render(this@svg, layoutSolution) }
        }

        outputFile.writer(Charsets.UTF_8).use { outputFileWriter ->
            svg.render(outputFileWriter, SVG.RenderMode.FILE)
        }
    }
}

fun main(args: Array<String>) {
    MainCommand.main(args)
}