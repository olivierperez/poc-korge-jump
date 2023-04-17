package fr.o80.korge.jump.scene

import com.soywiz.korge.scene.*
import fr.o80.korge.jump.scene.level.*

class Navigator(
    private val sceneContainer: SceneContainer
) {
    suspend fun pushLevel() {
        sceneContainer.pushTo<LevelScene>()
    }

    suspend fun back() {
        sceneContainer.back()
    }
}
