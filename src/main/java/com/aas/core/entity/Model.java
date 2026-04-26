package com.aas.core.entity;

import org.joml.Vector3f;

public class Model {
    private final int id;
    private final int vertexCount;
    private Material material;

    private Vector3f minBound = new Vector3f(-0.5f);
    private Vector3f maxBound = new Vector3f(0.5f);
    private Vector3f center = new Vector3f(0f);
    private float boundingRadius = 0.5f;

    public Model(int id, int vertexCount){
        this.id = id;
        this.vertexCount = vertexCount;
        this.material = new Material();
    }

    public Model(int id, int vertexCount, Texture texture ){
        this.id = id;
        this.vertexCount = vertexCount;
        this.material = new Material(texture);
    }

    public Model(Model model, Texture texture){
        this.id = model.getId();
        this.vertexCount = model.getVertexCount();
        this.material = model.getMaterial();
        this.setTexture(texture);

    }

    public void setBounds(Vector3f min, Vector3f max) {
        this.minBound = new Vector3f(min);
        this.maxBound = new Vector3f(max);

        // Merkezi hesapla: (Max + Min) / 2
        this.center = new Vector3f(max).add(min).mul(0.5f);

        float width = max.x - min.x;
        float height = max.y - min.y;
        float depth = max.z - min.z;

        // Üç eksenden en büyük olanın yarısını yarıçap yap
        this.boundingRadius = Math.max(width, Math.max(height, depth)) / 2.0f;
    }

    public Vector3f getMinBound() { return minBound; }
    public Vector3f getMaxBound() { return maxBound; }
    public Vector3f getCenter() { return center; }
    public float getBoundingRadius() { return boundingRadius; }

    public int getId(){
        return id;
    }

    public int getVertexCount(){
        return vertexCount;
    }
    public Material getMaterial(){
        return material;
    }

    public Texture getTexture() {
        return material.getTexture();
    }

    public void setTexture(Texture texture) {
        material.setTexture(texture);
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setTexture(Texture texture, float reflectance ) {
        this.material.setTexture(texture);
        this.material.setReflectance(reflectance);
    }

}
