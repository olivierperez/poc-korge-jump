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

private fun parseLayer(layer: List<String>, maxHeight: Int): List<EntityDef>? {
    return layer.flatMapIndexed { lineIndex, line ->
        line.groupedUntilChanged().mapNotNull { defGroup ->
            when (defGroup.char) {
                'P' -> PlayerDef(defGroup.index, maxHeight - lineIndex)
                '#' -> BlockDef(defGroup.index, maxHeight - lineIndex, defGroup.count)
                'E' -> EndDef(defGroup.index, maxHeight - lineIndex)
                else -> null
            }
        }
    }.takeIf { it.isNotEmpty() }
}

private fun CharSequence.groupedUntilChanged(): Collection<DefGroup> {
    return this.foldIndexed(listOf()) { index, acc, element ->
        val lastGroup = acc.lastOrNull()
        if (lastGroup?.char != element) {
            acc + DefGroup(element, index, 1)
        } else {
            acc.subList(0, acc.size - 1) + DefGroup(element, lastGroup.index, lastGroup.count + 1)
        }
    }
}

class DefGroup(
    val char: Char,
    val index: Int,
    val count: Int
)
