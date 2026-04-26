package com.aas.core.components;

import com.aas.core.ecs.Component;
import com.aas.core.entity.Model;
import org.joml.Vector3f;

public class SphereCollider extends Component {
    public float radius;
    public Vector3f offset;

    public SphereCollider(float radius) {
        this.radius = radius;
        this.offset = new Vector3f(0, 0, 0);
    }

    // Yeni otomatik constructor
    public SphereCollider(Model model) {
        this.radius = model.getBoundingRadius();
        this.offset = model.getCenter();
    }

    public float getTransformedRadius() {
        Transform t = gameObject.getComponent(Transform.class);
        // Küre her yöne eşit büyüdüğü için scale.x'i baz alabiliriz
        return radius * t.scale;
    }

    public Vector3f getCenter() {
        Transform t = gameObject.getComponent(Transform.class);
        return new Vector3f(t.position).add(offset);
    }

    @Override public void start() {}
    @Override public void update(float deltaTime) {}
}