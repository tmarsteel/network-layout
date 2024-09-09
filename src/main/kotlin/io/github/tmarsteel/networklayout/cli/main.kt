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
import kotlin.io.path.readText
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
        val networkDto = networkFile.inputStream().use { networkInStream ->
            NetworkDto.FORMAT.decodeFromStream<NetworkDto>(networkInStream)
        }

        val networkModel = Network.from(networkDto)
        val layoutModel = Model()
        val layoutables = mutableSetOf<Layoutable>()
        networkModel.createLayoutables(layoutModel, Theme.DEFAULT, layoutables::add)
        layoutables.forEach { postingLayoutable ->
            val allOthers = layoutables.asSequence().filter { it !== postingLayoutable }
            postingLayoutable.postConstraints(allOthers)
        }

        // TODO: find an optimal solution
        if (!layoutModel.solver.solve()) {
            throw PrintMessage("there is no viable layout for this network, sorry.", statusCode = 2, true)
        }
        val layoutSolution = layoutModel.solver.findSolution()

        val svg = SVG.svg {
            viewBox = viewBox(offsetX = 0.0, offsetY = 0.0, width = 10.0, height = 10.0)

            layoutables.take(1).forEach { it.render(this@svg, layoutSolution) }
        }

        outputFile.writer(Charsets.UTF_8).use { outputFileWriter ->
            svg.render(outputFileWriter, SVG.RenderMode.FILE)
        }
    }
}

fun main(args: Array<String>) {
    MainCommand.main(args)
}