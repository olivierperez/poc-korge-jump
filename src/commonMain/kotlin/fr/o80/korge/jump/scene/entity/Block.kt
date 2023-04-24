package fr.o80.korge.jump.scene.entity

import com.soywiz.korge.box2d.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import org.jbox2d.dynamics.*

class Block private constructor(
    view: View
) : Entity {

    override val layer: Layer = Layer.WORLD

    init {
        view.body!!.userData = this
    }

    companion object {
        fun createIn(container: Container, width: Int, height: Int, x: Int, y: Int): Block {
            return Block(
                container.solidRect(width, height, Colors.DARKGRAY)
                    .position(x, y)
                    .registerBodyWithFixture(
                        type = BodyType.STATIC,
                        friction = 0,
                        density = 1,
                        restitution = 0
                    )
            )
        }
    }
}
