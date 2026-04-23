package com.aas.core.scripts_test;

import com.aas.core.components.Rigidbody;
import com.aas.core.components.Transform;
import com.aas.core.ecs.Behaviour;
import com.aas.core.WindowManager;
import org.lwjgl.glfw.GLFW;

public class PlayerController extends Behaviour {
    private float speed = 55.0f;
    private float jumpForce = 25.0f;
    private WindowManager window;

    public PlayerController(WindowManager window) {
        this.window = window;
    }

    @Override
    public void start() {}

    @Override
    public void update(float deltaTime) {
        Transform transform = gameObject.getComponent(Transform.class);
        Rigidbody rb = gameObject.getComponent(Rigidbody.class);

        if (transform == null) return;

        // Sağa - Sola Hareket (Basitçe)
        if (window.isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
            transform.position.x += speed * deltaTime;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT)) {
            transform.position.x -= speed * deltaTime;
        }

        // Zıplama (Sadece yerdeyse zıplasın)
        if (window.isKeyPressed(GLFW.GLFW_KEY_SPACE) && rb != null && rb.velocity.y == 0) {
            rb.velocity.y = jumpForce;
        }
    }
}