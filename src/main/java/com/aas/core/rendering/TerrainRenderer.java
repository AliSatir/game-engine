package com.aas.core.rendering;

import com.aas.core.Camera;
import com.aas.core.ShaderManager;
import com.aas.core.entity.Model;
import com.aas.core.lighting.DirectionalLight;
import com.aas.core.lighting.PointLight;
import com.aas.core.lighting.SpotLight;
import com.aas.core.entity.terrain.Terrain;
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
import java.util.List;

public class TerrainRenderer implements IRenderer{

    ShaderManager shader;
    private List<Terrain> terrains;

    public TerrainRenderer() throws Exception {
        terrains = new ArrayList<Terrain>();
        shader = new ShaderManager();
    }

    @Override
    public void init() throws Exception {
        shader.createVertexShader(Utils.loadResource("/shaders/terrain_vertex.vs"));
        shader.createFragmentShader(Utils.loadResource("/shaders/terrain_fragment.fs"));
        shader.link();
        shader.createUniform("textureSampler");
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
    public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight, int shadowTexture, Matrix4f lightSpaceMatrix) throws Exception {

    }

    @Override
    public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight) {
        shader.bind();
        shader.setUniform("projectionMatrix", Launcher.getWindow().updateProjectionMatrix());
        RenderManager.renderLight(pointLights, spotLights, directionalLight, shader);
        for(Terrain terrain : terrains){
            bind(terrain.getModel());
            prepare(terrain, camera);
            GL11.glDrawElements(GL11.GL_TRIANGLES,terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            unbind();
        }
        terrains.clear();
        shader.unbind();
    }

    @Override
    public void bind(Model model) {
        GL30.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        shader.setUniform("material",model.getMaterial());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getId());
    }

    @Override
    public void unbind() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void prepare(Object terrain, Camera camera) {
        shader.setUniform("textureSampler", 0);
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix((Terrain) terrain));
        shader.setUniform("viewMatrix",Transformation.getViewMatrix(camera));
    }

    @Override
    public void cleanup() {
        shader.cleanup();
    }

    public List<Terrain> getTerrains() {
        return terrains;
    }
}
