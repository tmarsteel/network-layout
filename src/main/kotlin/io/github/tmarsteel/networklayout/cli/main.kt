package io.github.tmarsteel.networklayout.cli

import com.github.ajalt.clikt.completion.CompletionCandidates
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.path
import io.github.tmarsteel.networklayout.layout.determineRelativeLayout
import io.github.tmarsteel.networklayout.network.Network
import io.github.tmarsteel.networklayout.network.NetworkDto
import kotlinx.serialization.json.decodeFromStream
import kotlin.io.path.inputStream

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

        val network = Network.from(networkDto)
        determineRelativeLayout(network.cornerstoneStations.toSet())

        /*
        val svg = SVG.svg {
            viewBox = viewBox(offsetX.toDouble(), offsetY.toDouble(), sizeX.toDouble(), sizeY.toDouble())

            layoutables.forEach { it.render(this@svg, layoutSolution) }
        }

        outputFile.writer(Charsets.UTF_8).use { outputFileWriter ->
            svg.render(outputFileWriter, SVG.RenderMode.FILE)
        }*/
    }
}

fun main(args: Array<String>) {
    MainCommand.main(args)
}