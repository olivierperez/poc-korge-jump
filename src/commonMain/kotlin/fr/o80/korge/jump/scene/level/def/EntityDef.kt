package fr.o80.korge.jump.scene.level.def

interface EntityDef

data class BlockDef(val x:Int, val y: Int) : EntityDef
data class PlayerDef(val x: Int, val y: Int) : EntityDef
data class EndDef(val x: Int, val y: Int) : EntityDef
