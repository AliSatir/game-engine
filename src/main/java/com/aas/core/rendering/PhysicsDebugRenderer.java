package com.aas.core.rendering;

import com.aas.core.Camera;
import com.aas.core.ShaderManager;
import com.aas.core.components.BoxCollider;
import com.aas.core.components.SphereCollider; // Küre collider eklendi
import com.aas.core.components.Transform;
import com.aas.core.ecs.GameObject;
import com.aas.core.utils.Transformation;
import com.aas.core.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

public class PhysicsDebugRenderer {

    private ShaderManager shader;

    // Kutu (Box) için değişkenler
    private int boxVaoId;
    private int boxVboId;
    private int boxEboId;
    private int boxVertexCount;

    // Küre (Sphere) için değişkenler
    private int sphereVaoId;
    private int sphereVboId;
    private int sphereVertexCount;

    // Çizgilerin rengi (Yeşil)
    private final Vector3f COLOR = new Vector3f(0.0f, 1.0f, 0.0f);

    public void init() throws Exception {
        shader = new ShaderManager();
        shader.createVertexShader(Utils.loadResource("/shaders/debug_vertex.vs"));
        shader.createFragmentShader(Utils.loadResource("/shaders/debug_fragment.fs"));
        shader.link();

        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");
        shader.createUniform("transformationMatrix");
        shader.createUniform("colour");

        createUnitCube();
        createUnitSphere(); // Küre şeklini oluştur
    }

    // 1x1x1 boyutlarında, merkezi (0,0,0) olan bir birim küpün çizgilerini oluşturur
    private void createUnitCube() {
        float[] vertices = new float[]{
                -0.5f, -0.5f,  0.5f, // 0
                0.5f, -0.5f,  0.5f, // 1
                0.5f,  0.5f,  0.5f, // 2
                -0.5f,  0.5f,  0.5f, // 3
                -0.5f, -0.5f, -0.5f, // 4
                0.5f, -0.5f, -0.5f, // 5
                0.5f,  0.5f, -0.5f, // 6
                -0.5f,  0.5f, -0.5f  // 7
        };

        int[] indices = new int[]{
                0, 1, 1, 2, 2, 3, 3, 0, // Ön yüz
                4, 5, 5, 6, 6, 7, 7, 4, // Arka yüz
                0, 4, 1, 5, 2, 6, 3, 7  // Bağlantılar
        };
        boxVertexCount = indices.length;

        boxVaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(boxVaoId);

        boxVboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, boxVboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);

        boxEboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, boxEboId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    // 1 birim yarıçaplı (toplam 2 genişliğinde) 3 eksenli çember oluşturur
    private void createUnitSphere() {
        int segments = 36; // Çemberin detayı (ne kadar çoksa o kadar yuvarlak)
        List<Float> vertices = new ArrayList<>();
        float angleStep = (float) (2 * Math.PI / segments);

        // 1. XY Düzlemi (Z=0)
        for (int i = 0; i < segments; i++) {
            float a1 = i * angleStep;
            float a2 = (i + 1) * angleStep;
            vertices.add((float)Math.cos(a1)); vertices.add((float)Math.sin(a1)); vertices.add(0f);
            vertices.add((float)Math.cos(a2)); vertices.add((float)Math.sin(a2)); vertices.add(0f);
        }
        // 2. XZ Düzlemi (Y=0)
        for (int i = 0; i < segments; i++) {
            float a1 = i * angleStep;
            float a2 = (i + 1) * angleStep;
            vertices.add((float)Math.cos(a1)); vertices.add(0f); vertices.add((float)Math.sin(a1));
            vertices.add((float)Math.cos(a2)); vertices.add(0f); vertices.add((float)Math.sin(a2));
        }
        // 3. YZ Düzlemi (X=0)
        for (int i = 0; i < segments; i++) {
            float a1 = i * angleStep;
            float a2 = (i + 1) * angleStep;
            vertices.add(0f); vertices.add((float)Math.cos(a1)); vertices.add((float)Math.sin(a1));
            vertices.add(0f); vertices.add((float)Math.cos(a2)); vertices.add((float)Math.sin(a2));
        }

        float[] floatArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            floatArray[i] = vertices.get(i);
        }

        sphereVertexCount = floatArray.length / 3;

        sphereVaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(sphereVaoId);

        sphereVboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, sphereVboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatArray, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    public void render(Camera camera, Matrix4f projMatrix, List<GameObject> gameObjects) {
        shader.bind();
        shader.setUniform("projectionMatrix", projMatrix);
        shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
        shader.setUniform("colour", COLOR);

        // Çizgilerin her zaman üstte görünmesi için derinlik testini kapatıyoruz
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        for (GameObject obj : gameObjects) {
            Transform transform = obj.getComponent(Transform.class);
            if (transform == null) continue;

            // 1. BoxCollider Kontrolü ve Çizimi
            BoxCollider boxCollider = obj.getComponent(BoxCollider.class);
            if (boxCollider != null) {
                GL30.glBindVertexArray(boxVaoId);
                GL20.glEnableVertexAttribArray(0);

                Matrix4f modelMatrix = new Matrix4f();
                modelMatrix.translate(new Vector3f(transform.position).add(boxCollider.offset));
                modelMatrix.rotate(0, transform.rotation); // Eğer rotasyon kullanıyorsan x,y,z olarak ayarla

                // OTOMATİK BOYUTLANDIRMA: Modelden okunan boyut * scale
                modelMatrix.scale(boxCollider.size.x * transform.scale,
                        boxCollider.size.y * transform.scale,
                        boxCollider.size.z * transform.scale);

                shader.setUniform("transformationMatrix", modelMatrix);
                GL11.glDrawElements(GL11.GL_LINES, boxVertexCount, GL11.GL_UNSIGNED_INT, 0);

                GL20.glDisableVertexAttribArray(0);
                GL30.glBindVertexArray(0);
            }

            // 2. SphereCollider Kontrolü ve Çizimi
            SphereCollider sphereCollider = obj.getComponent(SphereCollider.class);
            if (sphereCollider != null) {
                GL30.glBindVertexArray(sphereVaoId);
                GL20.glEnableVertexAttribArray(0);

                Matrix4f modelMatrix = new Matrix4f();
                modelMatrix.translate(new Vector3f(transform.position).add(sphereCollider.offset));
                modelMatrix.rotate(0, transform.rotation);

                // KÜRE BOYUTLANDIRMA: Çapraz yarıçap (Radius) * scale
                float r = sphereCollider.radius * transform.scale;
                modelMatrix.scale(r, r, r);

                shader.setUniform("transformationMatrix", modelMatrix);
                // Küreyi drawArrays ile çiziyoruz (Index buffer kullanmadık)
                GL11.glDrawArrays(GL11.GL_LINES, 0, sphereVertexCount);

                GL20.glDisableVertexAttribArray(0);
                GL30.glBindVertexArray(0);
            }
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        shader.unbind();
    }

    public void cleanup() {
        if (shader != null) shader.cleanup();
        // Kutu bellek temizliği
        GL15.glDeleteBuffers(boxVboId);
        GL15.glDeleteBuffers(boxEboId);
        GL30.glDeleteVertexArrays(boxVaoId);
        // Küre bellek temizliği
        GL15.glDeleteBuffers(sphereVboId);
        GL30.glDeleteVertexArrays(sphereVaoId);
    }
}