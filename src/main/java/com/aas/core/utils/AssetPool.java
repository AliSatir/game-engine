package com.aas.core.utils;

import com.aas.core.entity.Model;
import com.aas.core.entity.Texture;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    private static Map<String, Model> models = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();

    public static Model getModel(String resourcePath) {
        if (!models.containsKey(resourcePath)) {
            // Eğer model havuzda yoksa yükle (Burada senin ModelLoader'ını kullanıyoruz)
            // Not: ModelLoader metodunun statik olduğunu varsayıyorum
            // models.put(resourcePath, ModelLoader.loadGLB(resourcePath, loader));
        }
        return models.get(resourcePath);
    }

    public static Texture getTexture(String resourcePath) {
        if (!textures.containsKey(resourcePath)) {
            // textures.put(resourcePath, new Texture(loader.loadTexture(resourcePath)));
        }
        return textures.get(resourcePath);
    }
}