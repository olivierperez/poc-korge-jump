package fr.o80.korge.jump.scene

import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*

abstract class JumpScene : Scene() {

    protected lateinit var navigator: Navigator
        private set

    override suspend fun SContainer.sceneInit() {
        navigator = injector.getWith<Navigator>(sceneContainer)
    }
}
