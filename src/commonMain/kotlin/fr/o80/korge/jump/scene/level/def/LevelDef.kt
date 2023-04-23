package fr.o80.korge.jump.scene.level.def

data class LevelDef(
    val width: Int,
    val height: Int,
    val layers: List<List<EntityDef>>
)
