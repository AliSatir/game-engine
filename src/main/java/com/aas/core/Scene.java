package com.aas.core;

import com.aas.core.entity.Entity;
import com.aas.core.lighting.DirectionalLight;
import com.aas.core.lighting.PointLight;
import com.aas.core.lighting.SpotLight;
import java.util.ArrayList;
import java.util.List;

public class Scene {
    private List<Entity> entities;
    private DirectionalLight directionalLight;
    private List<PointLight> pointLights;
    private List<SpotLight> spotLights;

    public Scene() {
        entities = new ArrayList<>();
        pointLights = new ArrayList<>();
        spotLights = new ArrayList<>();
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }


    public List<Entity> getEntities() { return entities; }
    public void setDirectionalLight(DirectionalLight light) { this.directionalLight = light; }
    public DirectionalLight getDirectionalLight() { return directionalLight; }
    public List<PointLight> getPointLights() { return pointLights; }
    public List<SpotLight> getSpotLights() { return spotLights; }
}