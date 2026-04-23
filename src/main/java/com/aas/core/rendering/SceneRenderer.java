package com.aas.core.rendering;

import com.aas.core.Camera;
import com.aas.core.Scene;
import com.aas.core.ShaderManager;
import com.aas.core.WindowManager;
import com.aas.core.entity.Entity;
import com.aas.core.lighting.PointLight;
import com.aas.core.lighting.SpotLight;
import com.aas.core.utils.Transformation;
import com.aas.core.utils.Contents;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;

public class SceneRenderer {

    private ShaderManager shader;

    public SceneRenderer(ShaderManager shader) {
        this.shader = shader;
    }

    public void render(WindowManager window, Scene scene, Camera camera) {
        // Shader'ı aktif et
        shader.bind();

        // 1. Kamera ve Projeksiyon Uniformları (Global Veriler)
        shader.setUniform("projectionMatrix", window.updateProjectionMatrix());
        shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));

        // 2. Işıkları Sahneden Alıp Gönder
        renderLights(scene);

        // 3. Sahnedeki Tüm Objeleri Dön ve Çiz
        for (Entity entity : scene.getEntities()) {
            renderEntity(entity);
        }

        shader.unbind();
    }

    private void renderLights(Scene scene) {
        shader.setUniform("ambientLight", Contents.AMBIENT_LIGHT);
        shader.setUniform("specularPower", Contents.SPECULAR_POWER);
        shader.setUniform("directionalLight", scene.getDirectionalLight());

        // Point Light Listesi
        List<PointLight> pLights = scene.getPointLights();
        for (int i = 0; i < pLights.size(); i++) {
            shader.setUniform("pointLights", pLights.get(i), i);
        }

        // Spot Light Listesi
        List<SpotLight> sLights = scene.getSpotLights();
        for (int i = 0; i < sLights.size(); i++) {
            shader.setUniform("spotLights", sLights.get(i), i);
        }
    }

    private void renderEntity(Entity entity) {
        // Transformasyon ve Materyal
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(entity));
        shader.setUniform("material", entity.getModel().getMaterial());

        // Mesh Çizimi
        GL30.glBindVertexArray(entity.getModel().getId());
        GL20.glEnableVertexAttribArray(0); // Position
        GL20.glEnableVertexAttribArray(1); // Texture Coords
        GL20.glEnableVertexAttribArray(2); // Normals

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.getModel().getTexture().getId());

        GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }
}