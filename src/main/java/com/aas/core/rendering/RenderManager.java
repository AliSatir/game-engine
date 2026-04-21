package com.aas.core.rendering;

import com.aas.core.Camera;
import com.aas.core.ShaderManager;
import com.aas.core.WindowManager;
import com.aas.core.entity.Entity;
import com.aas.core.entity.SceneManager;
import com.aas.core.lighting.DirectionalLight;
import com.aas.core.lighting.PointLight;
import com.aas.core.lighting.SpotLight;
import com.aas.core.entity.terrain.Terrain;
import com.aas.core.utils.Contents;
import com.aas.test.Launcher;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class RenderManager {

    private final WindowManager window;
    private EntityRenderer entityRenderer;
    private TerrainRenderer terrainRenderer;

    public RenderManager(){
        window = Launcher.getWindow();
    }

    public void init()throws Exception{
        entityRenderer = new EntityRenderer();
        terrainRenderer = new TerrainRenderer();

        entityRenderer.init();
        terrainRenderer.init();
    }

    public static void renderLight(PointLight[] pointLights, SpotLight[] spotLights,
                                   DirectionalLight directionalLight, ShaderManager shader){
        shader.setUniform("ambientLight", Contents.AMBIENT_LIGHT);
        shader.setUniform("specularPower", Contents.SPECULAR_POWER);

        int numLights = spotLights != null ? spotLights.length : 0;
        for(int i = 0; i < numLights; i++){
            shader.setUniform("spotLights", spotLights[i], i);
        }

        numLights = pointLights != null ? pointLights.length : 0;
        for(int i = 0; i < numLights; i++){
            shader.setUniform("pointLights", pointLights[i], i);
        }
        shader.setUniform("directionalLight", directionalLight);
    }

    public void render(Camera camera, SceneManager scene){
        clear();

        if(window.isResize()){
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResize(false);
        }

        entityRenderer.render(camera,scene.getPointLights(), scene.getSpotLights(), scene.getDirectionalLight());
        terrainRenderer.render(camera,scene.getPointLights(), scene.getSpotLights(), scene.getDirectionalLight());

    }

    public void processEntities(Entity entity){
        List<Entity> entityList = entityRenderer.getEntities().get(entity.getModel());
        if(entityList != null)
            entityList.add(entity);
        else{
            List<Entity> newEntityList = new ArrayList<>();
            newEntityList.add(entity);
            entityRenderer.getEntities().put(entity.getModel(), newEntityList);
        }
    }

    public void processTerrain(Terrain terrain){
        terrainRenderer.getTerrains().add(terrain);
    }

    public void clear(){
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup(){
        entityRenderer.shader.cleanup();
        terrainRenderer.shader.cleanup();
    }



}
