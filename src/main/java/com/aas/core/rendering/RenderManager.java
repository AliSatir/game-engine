package com.aas.core.rendering;

import com.aas.core.Camera;
import com.aas.core.ShaderManager;
import com.aas.core.WindowManager;
import com.aas.core.entity.Entity;
import com.aas.core.entity.Model;
import com.aas.core.lighting.DirectionalLight;
import com.aas.core.lighting.PointLight;
import com.aas.core.lighting.SpotLight;
import com.aas.core.utils.Contents;
import com.aas.core.utils.Transformation;
import com.aas.core.utils.Utils;
import com.aas.test.Launcher;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenderManager {

    private final WindowManager window;
    private EntityRenderer entityRenderer;

    public RenderManager(){
        window = Launcher.getWindow();
    }

    public void init()throws Exception{
        entityRenderer = new EntityRenderer();

        entityRenderer.init();
    }

    public static void renderLight(PointLight[] pointLights, SpotLight[] spotLights,
                                   DirectionalLight directionalLight, ShaderManager shader){
        shader.setUniform("ambientLight", Contents.AMBIENT_LIGHTS);
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

    public void render(Camera camera, DirectionalLight directionalLight, PointLight[] pointLights, SpotLight[] spotLights){
        clear();

        if(window.isResize()){
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResize(false);
        }

        entityRenderer.render(camera,pointLights,spotLights,directionalLight);

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

    public void clear(){
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup(){
        entityRenderer.shader.cleanup();
    }



}
