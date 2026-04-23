package com.aas.core.entity;

import com.aas.core.ecs.GameObject;
import com.aas.core.entity.terrain.Terrain;
import com.aas.core.lighting.DirectionalLight;
import com.aas.core.lighting.PointLight;
import com.aas.core.lighting.SpotLight;
import com.aas.core.utils.Contents;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class SceneManager {
    private List<Entity> entities;
    private List<Terrain> terrains;
    private List<GameObject> objects;

    private Vector3f ambientLight;
    private SpotLight[] spotLights;
    private PointLight[] pointLights;
    private DirectionalLight directionalLight;
    private float lightAnle, spotAnle = 0, spotInc = 1;

    public SceneManager(float lightAnle) {
        entities = new ArrayList<>();
        terrains = new ArrayList<>();
        objects = new ArrayList<>();
        ambientLight = Contents.AMBIENT_LIGHT;
        this.lightAnle = lightAnle;
    }


    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public List<Terrain> getTerrains() {
        return terrains;
    }

    public void setTerrains(List<Terrain> terrains) {
        this.terrains = terrains;
    }

    public void addTerrain(Terrain terrain) {
        this.terrains.add(terrain);
    }

    public Vector3f getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(Vector3f ambientLight) {
        this.ambientLight = ambientLight;
    }

    public void setAmbientLight(float x, float y, float z) {
        ambientLight = new Vector3f(x, y, z);
    }

    public SpotLight[] getSpotLights() {
        return spotLights;
    }

    public void setSpotLights(SpotLight[] spotLights) {
        this.spotLights = spotLights;
    }

    public PointLight[] getPointLights() {
        return pointLights;
    }

    public void setPointLights(PointLight[] pointLights) {
        this.pointLights = pointLights;
    }

    public float getLightAnle() {
        return lightAnle;
    }

    public void setLightAnle(float lightAnle) {
        this.lightAnle = lightAnle;
    }

    public void incLightAngle(float increment) {
        this.lightAnle += increment;
    }

    public float getSpotAnle() {
        return spotAnle;
    }

    public void incSpotAngle(float increment) {
        this.spotAnle += increment;
    }

    public void setSpotAnle(float spotAnle) {
        this.spotAnle = spotAnle;
    }

    public float getSpotInc() {
        return spotInc;
    }

    public void setSpotInc(float spotInc) {
        this.spotInc = spotInc;
    }

    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    public void setDirectionalLight(DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
    }

    public List<GameObject> getObjects() {
        return objects;
    }
    public void addObjects(GameObject objects) {
        this.objects.add(objects);
    }
}//class
