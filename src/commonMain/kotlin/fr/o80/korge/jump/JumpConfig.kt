package fr.o80.korge.jump

import com.soywiz.korge.scene.*
import com.soywiz.korgw.*
import com.soywiz.korim.color.*
import com.soywiz.korinject.*
import com.soywiz.korma.geom.*
import fr.o80.korge.jump.scene.*
import fr.o80.korge.jump.scene.level.*
import fr.o80.korge.jump.scene.main.*
import kotlin.reflect.*

object JumpConfig : Module() {

    override val bgcolor: RGBA = Colors["#2b2b2b"]
    override val size: SizeInt = SizeInt(1200, 600)
    override val windowSize: SizeInt = SizeInt(1200, 600)
    override val mainScene: KClass<out Scene> = MainScene::class
    override val quality: GameWindow.Quality = GameWindow.Quality.QUALITY

    override suspend fun AsyncInjector.configure() {
        mapPrototype { Navigator(get()) }

        mapPrototype { MainScene() }
        mapPrototype { LevelScene() }
    }
}
