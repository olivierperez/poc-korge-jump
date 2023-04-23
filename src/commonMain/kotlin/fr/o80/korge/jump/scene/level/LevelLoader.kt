package fr.o80.korge.jump.scene.level

import com.soywiz.korio.file.*
import fr.o80.korge.jump.scene.level.def.*

suspend fun VfsFile.readLevel(): LevelDef {
    val lines = this.readLines()

    val (width, height) = lines.first().split('x').map { it.toInt() }

    val layers = lines.drop(1).chunked(height)
        .mapNotNull { layer -> parseLayer(layer, height) }
        .toList()

    println("layers: ${layers.joinToString("\n\n")}")

    return LevelDef(width, height, layers)
}

fun parseLayer(layer: List<String>, maxHeight: Int): List<EntityDef>? {
    return layer.flatMapIndexed { lineIndex, line ->
        line.mapIndexedNotNull { columnIndex, char ->
            when (char) {
                'P' -> PlayerDef(columnIndex, maxHeight - lineIndex)
                '#' -> BlockDef(columnIndex, maxHeight - lineIndex)
                'E' -> EndDef(columnIndex, maxHeight - lineIndex)
                else -> null
            }
        }
    }.takeIf { it.isNotEmpty() }
}
