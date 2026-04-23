package com.aas.core;

import com.aas.core.entity.Model;
import com.aas.core.entity.Texture;
import org.lwjgl.assimp.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ModelLoader {

    public static Model loadGLB(String fileName, ObjectLoader loader) throws Exception {
        // 1. Dosyanın sistem yolunu al
        String resourcePath = ModelLoader.class.getResource(fileName).getPath();

        // 2. Assimp ile import et
        AIScene aiScene = Assimp.aiImportFile(resourcePath,
                Assimp.aiProcess_Triangulate |
                        Assimp.aiProcess_FlipUVs |
                        Assimp.aiProcess_JoinIdenticalVertices |
                        Assimp.aiProcess_GenSmoothNormals);

        if (aiScene == null || aiScene.mRootNode() == null) {
            throw new Exception("Model Yüklenemedi: " + Assimp.aiGetErrorString());
        }

        List<Float> verticesList = new ArrayList<>();
        List<Float> texturesList = new ArrayList<>();
        List<Float> normalsList = new ArrayList<>();
        List<Integer> indicesList = new ArrayList<>();
        int vertexOffset = 0;

        // 3. Tüm Mesh'leri Tek Bir Modelde Birleştir
        int numMeshes = aiScene.mNumMeshes();
        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiScene.mMeshes().get(i));

            // Vertexleri çek
            AIVector3D.Buffer aiVertices = aiMesh.mVertices();
            while (aiVertices.hasRemaining()) {
                AIVector3D v = aiVertices.get();
                verticesList.add(v.x()); verticesList.add(v.y()); verticesList.add(v.z());
            }

            // Texture Koordinatlarını çek
            AIVector3D.Buffer aiTextures = aiMesh.mTextureCoords(0);
            for (int j = 0; j < aiMesh.mNumVertices(); j++) {
                if (aiTextures != null) {
                    AIVector3D textCoord = aiTextures.get(j);
                    texturesList.add(textCoord.x());
                    // NOT: Eğer limon hala sapsarıysa, burayı '1.0f - textCoord.y()' yaparak test edebilirsin.
                    texturesList.add(textCoord.y());
                } else {
                    texturesList.add(0.0f); texturesList.add(0.0f);
                }
            }

            // Normalleri çek
            AIVector3D.Buffer aiNormals = aiMesh.mNormals();
            while (aiNormals != null && aiNormals.hasRemaining()) {
                AIVector3D n = aiNormals.get();
                normalsList.add(n.x()); normalsList.add(n.y()); normalsList.add(n.z());
            }

            // İndisleri çek
            AIFace.Buffer aiFaces = aiMesh.mFaces();
            while (aiFaces.hasRemaining()) {
                AIFace aiFace = aiFaces.get();
                IntBuffer buffer = aiFace.mIndices();
                while (buffer.hasRemaining()) {
                    indicesList.add(buffer.get() + vertexOffset);
                }
            }

            vertexOffset += aiMesh.mNumVertices();
        }

        // 4. Listeleri array'e çevir ve modeli yükle
        float[] vertices = listToArray(verticesList);
        float[] textures = listToArray(texturesList);
        float[] normals = listToArray(normalsList);
        int[] indices = indicesList.stream().mapToInt(Integer::intValue).toArray();

        Model model = loader.loadModel(vertices, textures, normals, indices);

        // 5. Gömülü Dokuyu Yükle ve Materyal Filtresini Kaldır
        if (aiScene.mNumMaterials() > 0) {
            AIMaterial aiMaterial = AIMaterial.create(aiScene.mMaterials().get(0));
            int textureId = loadEmbeddedTexture(aiScene, aiMaterial, loader);

            // Konsol kontrolü
            System.out.println("Model: " + fileName + " | Yüklenen Doku ID: " + textureId);

            if (textureId != -1) {
                // Dokuyu ata
                model.setTexture(new Texture(textureId));

                // MATERYAL NÖTRLEME:
                // Varsayılan sarı rengi (DEFAULT_COLOR) etkisiz hale getirmek için Beyaz (1,1,1,1) yapıyoruz.
                // Bu sayede dokunun kendi renkleri (siyah kollar, benekler) doğrudan görünür.
                org.joml.Vector4f neutralWhite = new org.joml.Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
                model.getMaterial().setAmbientColour(neutralWhite);
                model.getMaterial().setDiffuseColour(neutralWhite);
                model.getMaterial().setSpecularColour(neutralWhite);

                model.getMaterial().setHasTexture(1);
                // Işık yansımasını makul bir seviyeye çek
                model.getMaterial().setReflectance(0.1f);
            }
        }

        Assimp.aiReleaseImport(aiScene);
        return model;
    }

    // Yardımcı metod: List<Float> -> float[]
    private static float[] listToArray(List<Float> list) {
        float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); i++) array[i] = list.get(i);
        return array;
    }
    private static int loadEmbeddedTexture(AIScene aiScene, AIMaterial aiMaterial, ObjectLoader loader) throws Exception {
        AIString path = AIString.calloc();
        // Diffuse (Renk) dokusunun yolunu/indisini alıyoruz
        Assimp.aiGetMaterialTexture(aiMaterial, Assimp.aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
        String texturePath = path.dataString();

        int textureId = -1;
        if (texturePath != null && texturePath.length() > 0) {
            // Eğer doku dosyanın içine gömülüyse, yol "*" ile başlar (örn: *0, *1)
            if (texturePath.startsWith("*")) {
                int textureIndex = Integer.parseInt(texturePath.substring(1));
                AITexture aiTexture = AITexture.create(aiScene.mTextures().get(textureIndex));

                // Gömülü veriyi (png/jpg) ByteBuffer olarak al
                ByteBuffer textureBuffer = aiTexture.pcDataCompressed();
                // Mevcut ObjectLoader'ındaki loadTexture'ı ByteBuffer alacak şekilde overload etmeliyiz
                textureId = loader.loadTextureFromMemory(textureBuffer);
            }
        }
        System.out.println(textureId);
        path.free();
        return textureId; // Doku bulunamadı
    }
}
