package com.aas.core;

import com.aas.core.entity.Model;
import com.aas.core.utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ObjectLoader {

    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();

    public Model loadOBJModel(String filename) {
        List<String> lines = Utils.readAllLines(filename);

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3i> faces = new ArrayList<>();

        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "v":
                    //vertices
                    Vector3f verticesVec = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    vertices.add(verticesVec);
                    break;
                case "vt":
                    //vertices texture
                    Vector2f textureVec = new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2])
                    );
                    textures.add(textureVec);
                    break;
                case "vn":
                    //vertexNormal
                    Vector3f normalsVec = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    normals.add(normalsVec);
                    break;
                case "f":
                    processFace(tokens[1], faces);
                    processFace(tokens[2], faces);
                    processFace(tokens[3], faces);
                    break;
                default:
                    break;
            }
        }

        List<Float> verticesList = new ArrayList<>();
        List<Float> texturesList = new ArrayList<>();
        List<Float> normalsList = new ArrayList<>();
        List<Integer> indicesList = new ArrayList<>();

        // Benzersiz köşeleri takip etmek için Map kullanıyoruz
        // Key formatı: "vIndex/vtIndex/vnIndex"
        java.util.Map<String, Integer> uniqueNodes = new java.util.HashMap<>();

        for (Vector3i face : faces) {
            String key = face.x + "/" + face.y + "/" + face.z;

            if (!uniqueNodes.containsKey(key)) {
                // Bu köşe kombinasyonu ilk kez görülüyor
                int newIndex = uniqueNodes.size();
                uniqueNodes.put(key, newIndex);
                indicesList.add(newIndex);

                // Verileri listeye ekle
                Vector3f v = vertices.get(face.x);
                verticesList.add(v.x); verticesList.add(v.y); verticesList.add(v.z);

                if (face.y >= 0) {
                    Vector2f vt = textures.get(face.y);
                    texturesList.add(vt.x); texturesList.add(1 - vt.y);
                } else {
                    texturesList.add(0f); texturesList.add(0f);
                }

                if (face.z >= 0) {
                    Vector3f vn = normals.get(face.z);
                    normalsList.add(vn.x); normalsList.add(vn.y); normalsList.add(vn.z);
                } else {
                    normalsList.add(0f); normalsList.add(0f); normalsList.add(0f);
                }
            } else {
                // Bu köşe daha önce işlendi, sadece indexini ekle
                indicesList.add(uniqueNodes.get(key));
            }
        }

        // Listeleri ilkel dizilere (float[]) çeviriyoruz
        float[] vArr = new float[verticesList.size()];
        for(int i=0; i<verticesList.size(); i++) vArr[i] = verticesList.get(i);

        float[] tArr = new float[texturesList.size()];
        for(int i=0; i<texturesList.size(); i++) tArr[i] = texturesList.get(i);

        float[] nArr = new float[normalsList.size()];
        for(int i=0; i<normalsList.size(); i++) nArr[i] = normalsList.get(i);

        int[] iArr = indicesList.stream().mapToInt(Integer::intValue).toArray();

        return loadModel(vArr, tArr, nArr, iArr);
    }

    private static void processVertex(int pos, int texCoord, int normal, List<Vector2f> texCoordList,
                                      List<Vector3f> normalList, List<Integer> indicesList,
                                      float[] texCoordArr, float[] normalArr) {

        indicesList.add(pos);

        if (texCoord >= 0) {
            Vector2f texCoordVec = texCoordList.get(texCoord);
            texCoordArr[pos * 2] = texCoordVec.x;
            texCoordArr[(pos * 2) + 1] = 1 - texCoordVec.y;
        }

        if (normal >= 0) {
            Vector3f normalVec = normalList.get(normal);
            normalArr[pos * 3] = normalVec.x;
            normalArr[(pos * 3) + 1] = normalVec.y;
            normalArr[(pos * 3) + 2] = normalVec.z;
        }

    }

    // Sınıfın içine yeni bir metod ekleyelim veya mevcut olanı bu şekilde güncelleyelim
    private static void processVertex(Vector3i selection, List<Vector2f> texCoordList,
                                      List<Vector3f> normalList, List<Integer> indicesList,
                                      float[] texCoordArr, float[] normalArr) {

        int pos = selection.x;
        indicesList.add(pos);

        // Doku koordinatlarını yerleştir
        if (selection.y >= 0) {
            Vector2f texCoordVec = texCoordList.get(selection.y);
            texCoordArr[pos * 2] = texCoordVec.x;
            texCoordArr[(pos * 2) + 1] = 1 - texCoordVec.y;
        }

        // Normalleri yerleştir
        if (selection.z >= 0) {
            Vector3f normalVec = normalList.get(selection.z);
            normalArr[pos * 3] = normalVec.x;
            normalArr[(pos * 3) + 1] = normalVec.y;
            normalArr[(pos * 3) + 2] = normalVec.z;
        }
    }

    private static void processFace(String token, List<Vector3i> faces) {
        String[] lineToken = token.split("/");
        int length = lineToken.length;
        int pos = -1, coords = -1, normal = -1;
        pos = Integer.parseInt(lineToken[0]) - 1;
        if (length > 1) {
            String textCoords = lineToken[1];
            coords = textCoords.length() > 0 ? Integer.parseInt(textCoords) - 1 : -1;
            if (length > 2)
                normal = Integer.parseInt(lineToken[2]) - 1;

        }
        Vector3i facesVed = new Vector3i(pos, coords, normal);
        faces.add(facesVed);
    }

    public Model loadModel(float[] vertices, float[] textureCoords, float[] normals, int[] indices) {
        int id = createVAO();
        storeIndicesBuffer(indices);
        storeDataInAttribList(0, 3, vertices);
        storeDataInAttribList(1, 2, textureCoords);
        storeDataInAttribList(2, 3, normals);

        unbind();
        return new Model(id, indices.length);
    }

    public int loadTexture(String fileName) throws Exception {
        int width, height;
        ByteBuffer buffer;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            buffer = STBImage.stbi_load(fileName, w, h, c, 4);
            if (buffer == null)
                throw new Exception("Image file: " + fileName + "not loaded " + STBImage.stbi_failure_reason());

            width = w.get();
            height = h.get();
        }

        int id = GL11.glGenTextures();
        textures.add(id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

        // ObjectLoader.loadTexture içinde glBindTexture'dan sonra ekle:
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        //

        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, // GL_RGB yerine GL_RGBA
                width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        STBImage.stbi_image_free(buffer);
        return id;

    }
    public int loadTextureFromMemory(ByteBuffer buffer) throws Exception {
        int width, height, channels;
        ByteBuffer imageBuffer;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            // STBImage bellekten (memory) okuma yapar
            imageBuffer = STBImage.stbi_load_from_memory(buffer, w, h, c, 4);
            if (imageBuffer == null)
                throw new Exception("Gömülü doku yüklenemedi: " + STBImage.stbi_failure_reason());

            width = w.get();
            height = h.get();
        }

        int id = GL11.glGenTextures();
        textures.add(id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageBuffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        STBImage.stbi_image_free(imageBuffer);
        return id;
    }

    private int createVAO() {
        int id = GL30.glGenVertexArrays();
        vaos.add(id);
        GL30.glBindVertexArray(id);
        return id;
    }

    private void storeIndicesBuffer(int[] indices) {
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private void storeDataInAttribList(int attribNo, int vertexCount, float[] data) {
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = Utils.storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attribNo, vertexCount, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

    }

    private void unbind() {
        GL30.glBindVertexArray(0);
    }

    public void cleanup() {
        for (int vao : vaos)
            GL30.glDeleteVertexArrays(vao);
        for (int vbo : vbos)
            GL30.glDeleteBuffers(vbo);
        for (int texture : textures)
            GL11.glDeleteTextures(texture);

    }

}
