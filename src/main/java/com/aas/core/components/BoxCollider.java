package com.aas.core.components;

import com.aas.core.ecs.Component;
import com.aas.core.entity.Model;
import org.joml.Vector3f;

public class BoxCollider extends Component {
    public Vector3f size;   // Kutunun boyutları (Örn: 1, 1, 1)
    public Vector3f offset; // Merkeze göre kayma miktarı (Genelde 0,0,0)

    public BoxCollider(Vector3f size) {
        this.size = size;
        this.offset = new Vector3f(0, 0, 0);
    }

    // Yeni otomatik constructor
    public BoxCollider(Model model) {
        Vector3f min = model.getMinBound();
        Vector3f max = model.getMaxBound();

        // Boyut = Max - Min (Örneğin max=1, min=-1 ise boyut 2 olur)
        this.size = new Vector3f(max).sub(min);

        // Eğer modelin merkezi (0,0,0) değilse (örneğin ayaklarından pivotlanmışsa)
        // Offset değerini modelin merkezine kaydırıyoruz.
        this.offset = model.getCenter();
    }

    // Kutunun dünyadaki en küçük ve en büyük noktalarını hesaplar
    public Vector3f getMin() {
        Transform t = gameObject.getComponent(Transform.class);
        // Boyutu scale ile çarpıp merkeze göre yarı çapını buluyoruz
        float halfX = (size.x * t.scale)*0.5f;
        float halfY = (size.y * t.scale)*0.5f;
        float halfZ = (size.z * t.scale)*0.5f;

        return new Vector3f(t.position).add(offset).sub(halfX, halfY, halfZ);
    }

    public Vector3f getMax() {
        Transform t = gameObject.getComponent(Transform.class);
        float halfX = (size.x * t.scale)*0.5f;
        float halfY = (size.y * t.scale)*0.5f;
        float halfZ = (size.z * t.scale)*0.5f;

        return new Vector3f(t.position).add(offset).add(halfX, halfY, halfZ);
    }

    @Override public void start() {}
    @Override public void update(float deltaTime) {}
}