package com.aas.core.components;

import com.aas.core.ecs.Component;
import org.joml.Vector3f;

public class BoxCollider extends Component {
    public Vector3f size;   // Kutunun boyutları (Örn: 1, 1, 1)
    public Vector3f offset; // Merkeze göre kayma miktarı (Genelde 0,0,0)

    public BoxCollider(Vector3f size) {
        this.size = size;
        this.offset = new Vector3f(0, 0, 0);
    }

    // Kutunun dünyadaki en küçük ve en büyük noktalarını hesaplar
    public Vector3f getMin() {
        Transform t = gameObject.getComponent(Transform.class);
        return new Vector3f(t.position).add(offset).sub(new Vector3f(size).mul(0.5f));
    }

    public Vector3f getMax() {
        Transform t = gameObject.getComponent(Transform.class);
        return new Vector3f(t.position).add(offset).add(new Vector3f(size).mul(0.5f));
    }

    @Override public void start() {}
    @Override public void update(float deltaTime) {}
}