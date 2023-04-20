package fr.o80.korge.jump.scene.level

import com.soywiz.klock.*
import com.soywiz.korev.*
import com.soywiz.korge.box2d.*
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
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

    private suspend fun Container.createPlayer(): Player {
        val runSprites = loadRunSprites()
        val idleSprites = loadIdleSprites()
        val jumpSprites = loadJumpSprites()
        val playerView = sprite(idleSprites.right) {
            position(20, -150)
        }
        playerView.playAnimationLooped(spriteDisplayTime = 100.milliseconds)

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
                        y = playerRect.y + playerRect.height, height = playerRect.height * .02
                    )
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
            foot = foot,
            runSprites = runSprites,
            idleSprites = idleSprites,
            jumpSprites = jumpSprites,
        )
    }

    private suspend fun loadRunSprites(): Sprites {
        val spriteMap = resourcesVfs["Pink_Monster_Run.png"].readBitmap(PNG)
        val runLeft = SpriteAnimation(
            spriteMap,
            spriteWidth = 20,
            spriteHeight = 29,
            rows = 1,
            columns = 6,
            marginTop = 34,
            marginLeft = 6,
            offsetBetweenColumns = 12,
        )
        val runRight = SpriteAnimation(
            spriteMap,
            spriteWidth = 20,
            spriteHeight = 29,
            rows = 1,
            columns = 6,
            marginTop = 2,
            marginLeft = 6,
            offsetBetweenColumns = 12,
        )
        return Sprites(runLeft, runRight, 100.milliseconds)
    }

    private suspend fun loadIdleSprites(): Sprites {
        val spriteMap = resourcesVfs["Pink_Monster_Idle.png"].readBitmap(PNG)
        val idleLeft = SpriteAnimation(
            spriteMap,
            spriteWidth = 20,
            spriteHeight = 29,
            rows = 1,
            columns = 4,
            marginTop = 35,
            marginLeft = 5,
            offsetBetweenColumns = 12,
        )
        val idleRight = SpriteAnimation(
            spriteMap,
            spriteWidth = 20,
            spriteHeight = 29,
            rows = 1,
            columns = 4,
            marginTop = 3,
            marginLeft = 5,
            offsetBetweenColumns = 12,
        )
        return Sprites(idleLeft, idleRight, 200.milliseconds)
    }

    private suspend fun loadJumpSprites(): Sprites {
        val spriteMap = resourcesVfs["Pink_Monster_Jump.png"].readBitmap(PNG)
        val jumpLeft = SpriteAnimation(
            spriteMap,
            spriteWidth = 20,
            spriteHeight = 29,
            rows = 1,
            columns = 2,
            marginTop = 35,
            marginLeft = 5,
            offsetBetweenColumns = 12,
        )
        val jumpRight = SpriteAnimation(
            spriteMap,
            spriteWidth = 20,
            spriteHeight = 29,
            rows = 1,
            columns = 2,
            marginTop = 3,
            marginLeft = 5,
            offsetBetweenColumns = 12,
        )
        return Sprites(jumpLeft, jumpRight, 150.milliseconds)
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
            y = -70
        )

        Floor.createIn(
            this,
            width = 100,
            height = 10,
            x = -200,
            y = -130
        )
    }
}

enum class MoveState {
    STOP,
    LEFT,
    RIGHT
}
