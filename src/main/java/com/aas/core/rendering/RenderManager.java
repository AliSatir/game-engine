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
import com.aas.core.utils.Utils;
import com.aas.test.Launcher;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class RenderManager {

    private final WindowManager window;
    private EntityRenderer entityRenderer;
    private TerrainRenderer terrainRenderer;
    private ShadowMap shadowMap;
    private ShaderManager shadowMapShader;
    private PhysicsDebugRenderer physicsDebugRenderer;

    public static boolean SHOW_COLLIDERS = true;

    public RenderManager(){
        window = Launcher.getWindow();
    }

    public void init()throws Exception{
        entityRenderer = new EntityRenderer();
        terrainRenderer = new TerrainRenderer();
        physicsDebugRenderer = new PhysicsDebugRenderer();
        shadowMap = new ShadowMap();


        entityRenderer.init();
        terrainRenderer.init();
        physicsDebugRenderer.init();
        shadowMap.init();

        // Shadow Map Shader Hazırlığı
        shadowMapShader = new ShaderManager();
        // Bu shader'lar çok basit olmalı (Sadece pozisyon ve matris)
        shadowMapShader.createVertexShader(Utils.loadResource("/shaders/shadow_vertex.vs"));
        shadowMapShader.createFragmentShader(Utils.loadResource("/shaders/shadow_fragment.fs"));
        shadowMapShader.link();

        // Sadece bu iki uniform yeterli, çünkü renk/ışık hesaplamıyoruz
        shadowMapShader.createUniform("projectionMatrix"); // Bu aslında lightSpaceMatrix olacak
        shadowMapShader.createUniform("transformationMatrix"); // Modelin dünyadaki yeri

        shadowMap.init();
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
        // 1. Önce matrisi hesapla
        Matrix4f lightSpaceMatrix = updateLightSpaceMatrix(scene.getDirectionalLight());

        // --- 1. AŞAMA: Shadow Pass ---
        shadowMap.bind();
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        entityRenderer.renderShadows(lightSpaceMatrix, shadowMapShader);
        shadowMap.unbind();

        // --- KRİTİK DÜZELTME BURADA ---
        // Gölge çizimi bittiği anda Viewport'u HER KAREDE pencere boyutuna geri çekmelisin.
        // Sadece resize olduğunda değil, her karede!
        GL11.glViewport(0, 0, window.getWidth(), window.getHeight());

        // window.isResize kontrolünü yine de tutabilirsin ama viewport yukarıdaki gibi dışarıda olmalı.
        if(window.isResize()){
            window.setResize(false);
        }

        // Ekranı temizle (Gölge pass'ten sonra temizlenmiş olması gerekir)
        clear();

        // --- 2. AŞAMA: Sahne Çizimi ---
        entityRenderer.render(camera, scene.getPointLights(), scene.getSpotLights(),
                scene.getDirectionalLight(), shadowMap.getDepthMapTexture(), lightSpaceMatrix);

        // TerrainRenderer'ı da (eğer shader'ını güncellediysen) benzer şekilde çağırmayı unutma
        terrainRenderer.render(camera, scene.getPointLights(), scene.getSpotLights(), scene.getDirectionalLight());

        if(SHOW_COLLIDERS)
            physicsDebugRenderer.render(camera,window.getProjectionMatrix(),scene.getObjects());

    }

    private Matrix4f updateLightSpaceMatrix(DirectionalLight light) {
        // 1. Projeksiyon: Işığın ne kadarlık bir alanı "gördüğünü" belirler.
        // Değerleri sahnenin büyüklüğüne göre ayarlayabilirsin.
        // (left, right, bottom, top, near, far)
        float nearPlane = 1.0f, farPlane = 20.0f;
        Matrix4f lightProjection = new Matrix4f().ortho(-15.0f, 15.0f, -15.0f, 15.0f, nearPlane, farPlane);

        // 2. View: Işığın pozisyonu ve baktığı yön.
        // Işığın yönünü tersine çevirerek bir "pozisyon" simüle ediyoruz.
        Vector3f lightDirection = new Vector3f(light.getDirection());
        Vector3f lightPos = new Vector3f(lightDirection).mul(-10.0f); // Işığı uzağa yerleştir

        Matrix4f lightView = new Matrix4f().lookAt(
                lightPos,                    // Işığın konumu
                new Vector3f(0, 0, 0),        // Baktığı nokta (Sahnenin merkezi)
                new Vector3f(0, 1, 0)         // Yukarı yönü
        );

        // 3. Light Space Matrix = Projection * View
        return lightProjection.mul(lightView);
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
        physicsDebugRenderer.cleanup();
    }

    public WindowManager getWindow() {
        return window;
    }

    public EntityRenderer getEntityRenderer() {
        return entityRenderer;
    }

    public TerrainRenderer getTerrainRenderer() {
        return terrainRenderer;
    }
}
