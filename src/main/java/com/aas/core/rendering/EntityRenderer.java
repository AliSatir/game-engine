package com.aas.core.rendering;

import com.aas.core.Camera;
import com.aas.core.ShaderManager;
import com.aas.core.components.MeshRenderer;
import com.aas.core.components.Transform;
import com.aas.core.ecs.GameObject;
import com.aas.core.entity.Entity;
import com.aas.core.entity.Model;
import com.aas.core.lighting.DirectionalLight;
import com.aas.core.lighting.PointLight;
import com.aas.core.lighting.SpotLight;
import com.aas.core.utils.Contents;
import com.aas.core.utils.Transformation;
import com.aas.core.utils.Utils;
import com.aas.test.Launcher;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityRenderer implements IRenderer{

    ShaderManager shader;
    private Map<Model, List<Entity>> entities;
    private Map<Model,List<GameObject>> entitiesG;

    public EntityRenderer() throws Exception {
        entities = new HashMap<>();
        entitiesG = new HashMap<>();
        shader = new ShaderManager();
    }

    @Override
    public void init() throws Exception {
        shader.createVertexShader(Utils.loadResource("/shaders/entity_vertex.vs"));
        shader.createFragmentShader(Utils.loadResource("/shaders/entity_fragment.fs"));
        shader.link();
        shader.createUniform("textureSampler");
        shader.createUniform("shadowMap");
        shader.createUniform("lightSpaceMatrix");
        shader.createUniform("transformationMatrix");
        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");
        shader.createUniform("ambientLight");
        shader.createMaterialUniform("material");
        shader.createUniform("specularPower");
        shader.createDirectionalLightUniform("directionalLight");
        shader.createPointLightListUniform("pointLights", Contents.MAX_POINT_LIGHTS);
        shader.createSpotLightListUniform("spotLights", Contents.MAX_SPOT_LIGHTS);
    }

    @Override
    public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights
            , DirectionalLight directionalLight, int shadowTexture, Matrix4f lightSpaceMatrix) {
        shader.bind();
        shader.setUniform("projectionMatrix", Launcher.getWindow().updateProjectionMatrix());
        shader.setUniform("lightSpaceMatrix", lightSpaceMatrix);
        RenderManager.renderLight(pointLights, spotLights, directionalLight, shader);

        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, shadowTexture);
        shader.setUniform("shadowMap", 1);

        for (Model model : entitiesG.keySet()) {
            bind(model); // Modeli, dokuyu ve materyali ekran kartına bağla

            List<GameObject> list = entitiesG.get(model);
            for (GameObject gameObject : list) {
                // Her bir GameObject'i çizime hazırla
                prepare(gameObject, camera);
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            unbind();
        }
        entitiesG.clear(); // Her kare çizildikten sonra listeyi temizle
        shader.unbind();
    }

    @Override
    public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight) {

    }

    // YENİ METOD: Oyun döngüsünden nesneleri bu metoda göndereceğiz
    public void processGameObject(GameObject gameObject) {
        // Objenin görünümü ve konumu var mı kontrol et
        MeshRenderer renderer = gameObject.getComponent(MeshRenderer.class);
        Transform transform = gameObject.getComponent(Transform.class);

        if (renderer != null && transform != null) {
            Model model = renderer.getModel();
            List<GameObject> batch = entitiesG.get(model);
            if (batch != null) {
                batch.add(gameObject);
            } else {
                List<GameObject> newBatch = new ArrayList<>();
                newBatch.add(gameObject);
                entitiesG.put(model, newBatch);
            }
        }
    }

    public void renderShadows(Matrix4f lightSpaceMatrix, ShaderManager depthShader) {
        depthShader.bind();

        // Işığın bakış açısını temsil eden matrisi gönder
        depthShader.setUniform("lightSpaceMatrix", lightSpaceMatrix);

        for (Model model : entitiesG.keySet()) {
            // Modeli bağla (Shadow pass'te sadece vertex pozisyonu yani 0. indis yeterlidir)
            GL30.glBindVertexArray(model.getId());
            GL20.glEnableVertexAttribArray(0);

            List<GameObject> list = entitiesG.get(model);
            for (GameObject gameObject : list) {
                Transform transform = gameObject.getComponent(Transform.class);

                // Modelin dünyadaki yerini hesapla ve gönder
                Matrix4f modelMatrix = Transformation.createTransformationMatrix(transform);
                depthShader.setUniform("transformationMatrix", modelMatrix);

                // Sadece derinlik bilgisini çiz (Color buffer kapalı olduğu için sadece derinlik yazılır)
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }

            GL20.glDisableVertexAttribArray(0);
            GL30.glBindVertexArray(0);
        }

        depthShader.unbind();
    }
    @Override
    public void bind(Model model) {
        GL30.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        shader.setUniform("material",model.getMaterial());

        GL13.glActiveTexture(GL13.GL_TEXTURE0);

        if(model.getTexture() != null){
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getId());
        }

        shader.setUniform("textureSampler",0);

    }


    @Override
    public void unbind() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void prepare(Object obj, Camera camera) {
        GameObject gameObject = (GameObject) obj;

        // Transform bileşenini alıp matrise gönderiyoruz
        Transform transform = gameObject.getComponent(Transform.class);

        shader.setUniform("textureSampler", 0);
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(transform));
        shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
    }

    @Override
    public void cleanup() {
        shader.cleanup();
    }

     public Map<Model, List<Entity>> getEntities() {
       return entities;
    }

    public Map<Model, List<GameObject>> getEntitiesG() {return this.entitiesG;}
}
