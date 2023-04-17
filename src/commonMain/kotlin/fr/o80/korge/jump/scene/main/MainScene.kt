package fr.o80.korge.jump.scene.main

import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import fr.o80.korge.jump.scene.*

class MainScene : JumpScene() {
    override suspend fun SContainer.sceneMain() {
        uiButton("Let's go!") {
            centerOnStage()
            onClick { navigator.pushLevel() }
        }
    }
}
