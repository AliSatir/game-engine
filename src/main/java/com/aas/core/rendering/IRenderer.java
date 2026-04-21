package com.aas.core.rendering;

import com.aas.core.Camera;
import com.aas.core.entity.Model;
import com.aas.core.lighting.DirectionalLight;
import com.aas.core.lighting.PointLight;
import com.aas.core.lighting.SpotLight;

public interface IRenderer<T> {

    public void init() throws Exception;
    public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights,
                       DirectionalLight directionalLight) throws Exception;

    abstract void bind(Model model);

    public void unbind();
    public void prepare(T t, Camera camera);
    public void cleanup();

}
