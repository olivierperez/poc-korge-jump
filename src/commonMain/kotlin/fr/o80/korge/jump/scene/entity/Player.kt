package fr.o80.korge.jump.scene.entity

import com.soywiz.korev.*
import com.soywiz.korge.box2d.*
import com.soywiz.korge.input.*
import com.soywiz.korge.view.*
import fr.o80.korge.jump.scene.level.*
import org.jbox2d.common.*
import org.jbox2d.dynamics.*
import kotlin.math.*

// https://www.iforce2d.net/b2dtut/jumping
// https://www.iforce2d.net/b2dtut/constant-speed
// https://www.iforce2d.net/b2dtut/user-data
class Player(
    val view: Sprite,
    private val foot: Fixture,
    private val jumpForce: Float = 12f,
    private val maxSpeed: Float = 5f,
    private val acceleration: Float = .2f,
    private val deceleration: Float = .08f,
    private val maxJump: Int = 2,
    private val runSprites: Sprites,
    private val idleSprites: Sprites,
    private val jumpSprites: Sprites,
) : Entity {

    override val layer: Layer = Layer.PLAYER

    private val mainBody: Body = view.body ?: error("A main body is required")

    private var moveState: MoveState = MoveState.STOP

    private var remainingJumps: Int = maxJump

    private var jumping: Boolean = false

    init {
        mainBody.userData = UserData(this, "MAIN")
        foot.userData = UserData(this, "FOOT")
    }

    fun jump() {
        println("remainingJumps:$remainingJumps")
        if (remainingJumps > 0) {
            remainingJumps--
            val impulse = mainBody.getMass() * jumpForce
            mainBody.applyLinearImpulse(Vec2(0f, -impulse), mainBody.worldCenter, true)
        }
    }

    fun left() {
        moveState = MoveState.LEFT
    }

    fun right() {
        moveState = MoveState.RIGHT
    }

    fun update(keys: InputKeys) {
        if (!keys.pressing(Key.LEFT) && !keys.pressing(Key.RIGHT)) {
            moveState = MoveState.STOP
        }

        val currentVelocity = mainBody.linearVelocityX
        val desiredVelocity = when (moveState) {
            MoveState.STOP -> currentVelocity * (1 - deceleration)
            MoveState.LEFT -> max(-maxSpeed, currentVelocity - acceleration)
            MoveState.RIGHT -> min(maxSpeed, currentVelocity + acceleration)
        }

        val velocityChange = desiredVelocity - currentVelocity
        val impulse = mainBody.getMass() * velocityChange

        mainBody.applyLinearImpulse(Vec2(impulse, 0f), mainBody.worldCenter, true)

        val contact = foot.getBody()?.getContactList()
        if (contact != null) {
            val userDataA = contact.contact?.getFixtureA()?.userData as? UserData
            val userDataB = contact.contact?.getFixtureB()?.userData as? UserData
            if (contact.other?.userData is Block && (userDataA?.fixture == "FOOT" || userDataB?.fixture == "FOOT")) {
                if (jumping) {
                    jumping = false
                    remainingJumps = maxJump
                    println("reset jumps")
                }
            }
        } else {
            jumping = true
        }

        updateSprite()
    }

    private fun updateSprite() {
        when {
            jumping && mainBody.linearVelocityX < 0 ->
                view.playAnimationLooped(
                    spriteAnimation = jumpSprites.left,
                    spriteDisplayTime = jumpSprites.timeSpan
                )

            jumping && mainBody.linearVelocityX > 0 ->
                view.playAnimationLooped(
                    spriteAnimation = jumpSprites.right,
                    spriteDisplayTime = jumpSprites.timeSpan
                )

            mainBody.linearVelocityX < -.45 ->
                view.playAnimationLooped(
                    spriteAnimation = runSprites.left,
                    spriteDisplayTime = runSprites.timeSpan
                )

            mainBody.linearVelocityX > .45 ->
                view.playAnimationLooped(
                    spriteAnimation = runSprites.right,
                    spriteDisplayTime = runSprites.timeSpan
                )

            mainBody.linearVelocityX < 0 ->
                view.playAnimationLooped(
                    spriteAnimation = idleSprites.left,
                    spriteDisplayTime = idleSprites.timeSpan
                )

            mainBody.linearVelocityX > 0 ->
                view.playAnimationLooped(
                    spriteAnimation = idleSprites.right,
                    spriteDisplayTime = idleSprites.timeSpan
                )
        }
    }
}
