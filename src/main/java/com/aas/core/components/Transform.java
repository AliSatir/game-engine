package com.aas.core.components;

import com.aas.core.ecs.Component;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Transform extends Component {
    public Vector3f position;
    public Vector3f rotation;
    public float scale;

    private Transform parent;
    private List<Transform> children  = new ArrayList<>();

    // Varsayılan kurucu (Merkezde, dönmemiş ve normal boyutta)
    public Transform() {
        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Vector3f(0, 0, 0);
        this.scale = 1.0f;
    }

    // Özel konumlu kurucu
    public Transform(Vector3f position, Vector3f rotation, float scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    @Override
    public void start() {
        // Transform ilk eklendiğinde çalışacak kodlar (Şimdilik boş)
    }

    @Override
    public void update(float deltaTime) {
        // Her karede çalışacak kodlar (Şimdilik boş)
    }

    public void setParent(Transform parent) {
        this.parent = parent;
        parent.children.add(this);
    }

    // Dünyadaki gerçek matrisi hesaplar (Rekürsif olarak babaları toplar)
    public Matrix4f getWorldMatrix() {
        Matrix4f matrix = new Matrix4f().identity()
                .translate(position)
                .rotateX((float) Math.toRadians(rotation.x))
                .rotateY((float) Math.toRadians(rotation.y))
                .rotateZ((float) Math.toRadians(rotation.z))
                .scale(scale);

        if (parent != null) {
            System.out.println("babababa");
            // Babasının matrisi ile kendi matrisini çarp (Parent * Child)
            return new Matrix4f(parent.getWorldMatrix()).mul(matrix);
        }

        return matrix;
    }

}