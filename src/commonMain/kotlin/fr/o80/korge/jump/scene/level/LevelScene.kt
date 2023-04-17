package fr.o80.korge.jump.scene.level

import com.soywiz.klock.*
import com.soywiz.korev.*
import com.soywiz.korge.box2d.*
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korma.geom.*
import fr.o80.korge.jump.scene.entity.*
import org.jbox2d.dynamics.*

class LevelScene : Scene() {

    private var player: Player? = null

    override suspend fun SContainer.sceneMain() {
        text("Level") {
            centerXOnStage()
        }
        worldView {
            position(600, 600).scale(1.5)

            player = createPlayer()

            createLevelFrame()
            createFloors()
        }
    }

    private fun Container.createPlayer(): Player {
        val playerView = solidRect(10, 15, Colors.BLUE)
            .anchor(.5, .5)
            .position(20, -150)

        val body = createBody {
            this.type = BodyType.DYNAMIC
            this.angle = playerView.rotation
            this.angularVelocity = 0f
            this.position.set(playerView.x.toFloat(), playerView.y.toFloat())
            this.linearVelocity.set(0f, 0f)
            this.linearDamping = 0f
            this.angularDamping = 0f
            this.gravityScale = 2f
            this.allowSleep = true
            this.fixedRotation = true
            this.bullet = false
            this.awake = true
            this.active = true
        }
        val world = body.world

        val playerRect = playerView.getLocalBounds(Rectangle()) / world.customScale
        body.fixture {
            this.shape = BoxShape(playerRect)

            this.isSensor = false
            this.friction = 0f
            this.restitution = 0f
            this.density = 1f
        }
        val foot = body.createFixture(
            FixtureDef().apply {
                this.shape = BoxShape(
                    playerRect.copy(
                        y = playerRect.y + playerRect.height, height = playerRect.height * .02)
                )

                this.isSensor = true
                this.friction = 0f
                this.restitution = 0f
                this.density = 1f
            }
        ) ?: error("World is locked")
        body.view = playerView
        playerView.body = body

        return Player(
            main = playerView,
            foot = foot
        )
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

    private fun Container.createLevelFrame() {
        Floor.createIn(
            this,
            width = 1200,
            height = 10,
            x = 0,
            y = -10
        )

        solidRect(10, 600, Colors.DARKGRAY)
            .anchor(.5, .5)
            .position(-590, -300)
            .registerBodyWithFixture(
                type = BodyType.STATIC,
                friction = 0,
                density = 1,
                restitution = 0
            )

        solidRect(10, 600, Colors.DARKGRAY)
            .anchor(.5, .5)
            .position(590, -300)
            .registerBodyWithFixture(
                type = BodyType.STATIC,
                friction = 0,
                density = 1,
                restitution = 0
            )
    }

    private fun Container.createFloors() {
        Floor.createIn(
            this,
            width = 100,
            height = 10,
            x = -150,
            y = -40
        )

        Floor.createIn(
            this,
            width = 100,
            height = 10,
            x = -200,
            y = -70
        )
    }
}

enum class MoveState {
    STOP,
    LEFT,
    RIGHT
}
