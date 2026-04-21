package com.aas.test;

import com.aas.core.*;
import com.aas.core.entity.Entity;
import com.aas.core.entity.Model;
import com.aas.core.entity.Texture;
import com.aas.core.lighting.DirectionalLight;
import com.aas.core.lighting.PointLight;
import com.aas.core.lighting.SpotLight;
import com.aas.core.rendering.RenderManager;
import com.aas.core.utils.Contents;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestGame implements ILogic {

    private static final float CAMERA_MOVE_SPEED = 0.05f;

    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;

    private List<Entity> entities;
    private Camera camera;

    Vector3f cameraInk;

    private float lightAngle, spotAngle = 0, spotInc = 1;
    private DirectionalLight directionalLight;
    private PointLight[] pointLights;
    private SpotLight[] spotLights;

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

        Model model = loader.loadOBJModel("/models/cube.obj");
        model.setTexture(new Texture(loader.loadTexture("textures/blue.jpg")),1f);

        entities = new ArrayList<>();
        Random rnd = new Random();
        for(int i = 0; i < 200; i++){
            float x = rnd.nextFloat() * 100 - 50;
            float y = rnd.nextFloat() * 100 - 50;
            float z = rnd.nextFloat() * -300;
            entities.add(new Entity(model, new Vector3f(x,y,z),
                    new Vector3f(rnd.nextFloat() * 180,rnd.nextFloat() * 180, 0),1));
        }
        entities.add(new Entity(model, new Vector3f(0,0, -2f),new Vector3f(0,0,0),1));

        float lightIntensity = 1.0f;

        //pointLight
        Vector3f lightPos = new Vector3f(-0.5f,-0.5f,-3.2f);
        Vector3f lightColor = new Vector3f(1,1,1);
        PointLight pointLight = new PointLight(lightColor, lightPos, lightIntensity, 0f,0f,1f);

        //spot light
        Vector3f coneDir = new Vector3f(0,0,1);
        float cutoff = (float) Math.cos(Math.toRadians(140));
        SpotLight spotLight = new SpotLight(new PointLight(lightColor, new Vector3f(0,0, -3.6f),
                lightIntensity, 0,0,0.2f),coneDir, cutoff);

        SpotLight spotLight1 = new SpotLight(new PointLight(lightColor,lightPos,lightIntensity,0f,0f,1f)
                , coneDir, cutoff);
        spotLight1.getPointLight().setPosition(new Vector3f(0.5f,0.5f,-3.6f));

        //directional light
        lightPos = new Vector3f(-1,-1,0);
        lightColor = new Vector3f(1,1,1);
        directionalLight = new DirectionalLight( lightColor, lightPos, lightIntensity);

        pointLights = new PointLight[]{pointLight};
        spotLights = new SpotLight[]{spotLight, spotLight1};
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

       float lightPos = spotLights[0].getPointLight().getPosition().z;
       if(window.isKeyPressed(GLFW.GLFW_KEY_N)){
            spotLights[0].getPointLight().getPosition().z = lightPos + 0.1f;
       }if(window.isKeyPressed(GLFW.GLFW_KEY_M)){
            spotLights[0].getPointLight().getPosition().z = lightPos - 0.1f;
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

        spotAngle += spotInc * 0.05f;
        if(spotAngle > 4){
            spotInc = -1;
        }else if(spotAngle < -4)
            spotInc = 1;

        double spotAngleRad = Math.toRadians(spotAngle);
        Vector3f coneDir = spotLights[0].getPointLight().getPosition();
        coneDir.y = (float) Math.sin(spotAngleRad);

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

        for(Entity entity : entities){
            renderer.processEntities(entity);
        }
    }

    @Override
    public void render() {
        renderer.render(camera, directionalLight, pointLights, spotLights);

    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
