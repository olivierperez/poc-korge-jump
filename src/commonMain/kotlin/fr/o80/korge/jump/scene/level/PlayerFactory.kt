package fr.o80.korge.jump.scene.level

import com.soywiz.klock.*
import com.soywiz.korge.box2d.*
import com.soywiz.korge.view.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.*
import fr.o80.korge.jump.scene.entity.*
import org.jbox2d.dynamics.*

class PlayerFactory {
    suspend fun createIn(container: Container, x: Int, y: Int): Player {
        val runSprites = loadRunSprites()
        val idleSprites = loadIdleSprites()
        val jumpSprites = loadJumpSprites()
        val playerView = container.sprite(idleSprites.right) {
            position(x, y)
        }
        playerView.playAnimationLooped(spriteDisplayTime = 100.milliseconds)

        val body = container.createBody {
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
            this.userData = "PLAYER"
        }

        val footRectangle = playerRect.copy(
            x = playerRect.width * 0.09,
            y = playerRect.y + playerRect.height,
            width = playerRect.width * 0.8,
            height = playerRect.height * .01
        )
        val foot = body.createFixture(
            FixtureDef().apply {
                this.shape = BoxShape(footRectangle)

                this.isSensor = true
                this.friction = 0f
                this.restitution = 0f
                this.density = 1f
                this.userData = "FOOT"
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
}
