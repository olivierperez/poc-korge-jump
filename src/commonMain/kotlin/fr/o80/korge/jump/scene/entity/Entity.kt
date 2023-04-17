package fr.o80.korge.jump.scene.entity

interface Entity {
    val layer: Layer
}

enum class Layer {
    PLAYER,
    WORLD,
}
