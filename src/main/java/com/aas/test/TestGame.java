package com.aas.test;

import com.aas.core.*;
import com.aas.core.entity.Entity;
import com.aas.core.entity.Model;
import com.aas.core.entity.Texture;
import com.aas.core.utils.Contents;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class TestGame implements ILogic {

    private static final float CAMERA_MOVE_SPEED = 0.05f;

    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;

    private Entity entity;
    private Camera camera;

    Vector3f cameraInk;

    public TestGame(){
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        camera = new Camera();
        cameraInk = new Vector3f(0,0,0);
    }


    @Override
    public void init() throws Exception {
        renderer.init();

        float[] vertices = new float[] {
                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                -0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
        };
        float[] textCoords = new float[]{
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,
                0.0f, 0.0f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.0f,
                0.5f, 0.5f,
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,
        };
        int[] indices = new int[]{
                0, 1, 3, 3, 1, 2,
                8, 10, 11, 9, 8, 11,
                12, 13, 7, 5, 12, 7,
                14, 15, 6, 4, 14, 6,
                16, 18, 19, 17, 16, 19,
                4, 6, 7, 5, 4, 7,
        };


        Model model = loader.loadModel(vertices,textCoords,indices);
        model.setTexture(new Texture(loader.loadTexture("textures/grassblock.png")));
        entity = new Entity(model, new Vector3f(1,0,-5), new Vector3f(0,0,0), 1);
    }

    @Override
    public void input() {
       cameraInk.set(0,0,0);
       if(window.isKeyPressed(GLFW.GLFW_KEY_W))
           cameraInk.z = -1;
       if(window.isKeyPressed(GLFW.GLFW_KEY_S))
           cameraInk.z = 1;
       if(window.isKeyPressed(GLFW.GLFW_KEY_A))
           cameraInk.x = -1;
       if(window.isKeyPressed(GLFW.GLFW_KEY_D))
           cameraInk.x = 1;
       if(window.isKeyPressed(GLFW.GLFW_KEY_Z))
           cameraInk.y = -1;
       if(window.isKeyPressed(GLFW.GLFW_KEY_X))
           cameraInk.y = 1;
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        camera.movePosition(cameraInk.x * CAMERA_MOVE_SPEED
                , cameraInk.y * CAMERA_MOVE_SPEED, cameraInk.z * CAMERA_MOVE_SPEED);

        if(mouseInput.isRightButtonPress()){
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * Contents.MOUSE_SENSITIVITY
                    , rotVec.y * Contents.MOUSE_SENSITIVITY,0);
        }

        entity.incRotation(0f, 0.5f, 0f);
    }

    @Override
    public void render() {
        if(window.isResize()){
            GL11.glViewport(0,0, window.getWidth(), window.getHeight());
            window.setResize(false);
        }

        window.setClearColour(0f,0f,0f,0f);
        renderer.render(entity, camera);

    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
