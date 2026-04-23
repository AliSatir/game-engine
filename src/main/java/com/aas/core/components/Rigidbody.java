package com.aas.core.components;

import com.aas.core.ecs.Component;
import org.joml.Vector3f;

public class Rigidbody extends Component {
    public Vector3f velocity; // Nesnenin hızı
    public float mass;        // Kütlesi
    public float gravityScale = 2.0f;
    public boolean useGravity; // Yer çekiminden etkilensin mi?
    public float drag;        // Hava sürtünmesi (isteğe bağlı)

    public Rigidbody() {
        this.velocity = new Vector3f(0, 0, 0);
        this.mass = 1.0f;
        this.useGravity = true;
        this.drag = 0.02f;
    }

    @Override
    public void start() {}

    @Override
    public void update(float deltaTime) {}
}