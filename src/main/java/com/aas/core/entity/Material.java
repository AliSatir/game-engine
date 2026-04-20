package com.aas.core.entity;

import com.aas.core.utils.Contents;
import org.joml.Vector4f;

public class Material {
    private Vector4f ambientColour, diffuseColour, specularColour;
    private float reflectence;
    private Texture texture;

    public Material(){
        this.ambientColour = Contents.DEFAULT_COLOR;
        this.diffuseColour = Contents.DEFAULT_COLOR;
        this.specularColour = Contents.DEFAULT_COLOR;
        this.texture = null;
        this.reflectence = 0;
    }
    public Material(Vector4f colour, float reflectence){
        this(colour,colour,colour, reflectence, null);
    }

    public Material(Vector4f colour, float reflectence,  Texture texture){
        this(colour,colour,colour,reflectence,texture);
    }

    public Material(Texture texture){
        this.ambientColour = Contents.DEFAULT_COLOR;
        this.diffuseColour = Contents.DEFAULT_COLOR;
        this.specularColour = Contents.DEFAULT_COLOR;
        this.texture = texture;
        this.reflectence = 0;
    }

    public Material(Vector4f ambientColour, Vector4f diffuseColour, Vector4f specularColour, float reflectence, Texture texture) {
        this.ambientColour = ambientColour;
        this.diffuseColour = diffuseColour;
        this.specularColour = specularColour;
        this.reflectence = reflectence;
        this.texture = texture;
    }

    public Vector4f getAmbientColour() {
        return ambientColour;
    }

    public void setAmbientColour(Vector4f ambientColour) {
        this.ambientColour = ambientColour;
    }

    public Vector4f getDiffuseColour() {
        return diffuseColour;
    }

    public void setDiffuseColour(Vector4f diffuseColour) {
        this.diffuseColour = diffuseColour;
    }

    public Vector4f getSpecularColour() {
        return specularColour;
    }

    public void setSpecularColour(Vector4f specularColour) {
        this.specularColour = specularColour;
    }

    public float getReflectence() {
        return reflectence;
    }

    public void setReflectence(float reflectence) {
        this.reflectence = reflectence;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public boolean hasTexture() {
        return this.texture != null;
    }
}
