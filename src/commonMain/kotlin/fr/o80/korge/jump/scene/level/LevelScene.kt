package fr.o80.korge.jump.scene.level

import com.soywiz.klock.*
import com.soywiz.korev.*
import com.soywiz.korge.box2d.*
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korio.file.std.*
import fr.o80.korge.jump.scene.entity.*
import fr.o80.korge.jump.scene.level.def.*

class LevelScene : Scene() {

    private var player: Player? = null
    private val playerFactory = PlayerFactory()

    override suspend fun SContainer.sceneMain() {
        val level = resourcesVfs["levels/001.lvl"].readLevel()
        val blockSize = 20

        text("Level") {
            centerXOnStage()
        }
        worldView {
            position(600, 600).scale(1.5)

            level.layers.forEach { layer ->
                layer.forEach { entityDef ->
                    when (entityDef) {
                        is PlayerDef -> {
                            if (player != null) {
                                error("Player should exists only once in the level")
                            }
                            player = playerFactory.createIn(
                                this,
                                x = entityDef.x * blockSize,
                                y = -entityDef.y * blockSize
                            )
                        }

                        is BlockDef -> {
                            Block.createIn(
                                this,
                                width = blockSize,
                                height = blockSize,
                                x = entityDef.x * blockSize,
                                y = -entityDef.y * blockSize
                            )
                        }
                    }
                }
            }

            if (player == null) {
                error("Player doesn't exists in the level")
            }
        }
    }

    override suspend fun SContainer.sceneInit() {
        keys {
            justDown(Key.SPACE) {
                println("Jump")
                player?.jump()
            }
            justDown(Key.LEFT) {
                player?.left()
            }
            justDown(Key.RIGHT) {
                player?.right()
            }
        }

        addFixedUpdater(60.timesPerSecond) {
            player?.update(views.keys)
        }
    }
}

enum class MoveState {
    STOP,
    LEFT,
    RIGHT
}
