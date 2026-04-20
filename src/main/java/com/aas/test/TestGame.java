package com.aas.test;

import com.aas.core.*;
import com.aas.core.entity.Entity;
import com.aas.core.entity.Model;
import com.aas.core.entity.Texture;
import com.aas.core.lighting.DirectionalLight;
import com.aas.core.lighting.PointLight;
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

    private float lightAngle;
    private DirectionalLight directionalLight;
    private PointLight pointLight;

    public TestGame(){
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        camera = new Camera();
        cameraInk = new Vector3f(0,0,0);
        lightAngle = - 90;
    }


    @Override
    public void init() throws Exception {
        renderer.init();


        Model model = loader.loadOBJModel("/models/bunnyy.obj");
        model.setTexture(new Texture(loader.loadTexture("textures/blue.jpg")),1f);
        entity = new Entity(model, new Vector3f(1,0,-5), new Vector3f(0,0,0), 1);

        float lightIntensity = 1.0f;
        Vector3f lightPos = new Vector3f(0,0,-3.2f);
        Vector3f lightColor = new Vector3f(1,1,1);
        pointLight = new PointLight(lightColor, lightPos, lightIntensity, 0,0,1);

        lightPos = new Vector3f(-1,-10,0);
        lightColor = new Vector3f(1,1,1);
        directionalLight = new DirectionalLight( lightColor, lightPos, lightIntensity);
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

       if(window.isKeyPressed(GLFW.GLFW_KEY_O)){
           pointLight.getPosition().x += 0.1f;
       }
       if(window.isKeyPressed(GLFW.GLFW_KEY_P)){
           pointLight.getPosition().x -= 0.1f;
       }
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

        //entity.incRotation(0f, 0.25f, 0f);

        lightAngle += 0.05f;
        if(lightAngle > 90) {
            directionalLight.setIntensity(0);
            if(lightAngle >= 360)
                lightAngle = -90;
        }else if(lightAngle <= -80 || lightAngle >= 80){
            float factor = 1 - (Math.abs(lightAngle) -80) / 10f;
            directionalLight.setIntensity(factor);
            directionalLight.getColour().y = Math.max(factor, 0.9f);
            directionalLight.getColour().z = Math.max(factor, 0.5f);
        }else{
            directionalLight.setIntensity(1);
            directionalLight.getColour().x = 1;
            directionalLight.getColour().y = 1;
            directionalLight.getColour().z = 1;
        }
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);
    }

    @Override
    public void render() {
        renderer.render(entity, camera, directionalLight, pointLight);

    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
