package com.aas.test;

import com.aas.core.*;
import com.aas.core.entity.*;
import com.aas.core.lighting.DirectionalLight;
import com.aas.core.lighting.PointLight;
import com.aas.core.lighting.SpotLight;
import com.aas.core.rendering.RenderManager;
import com.aas.core.entity.terrain.Terrain;
import com.aas.core.utils.Contents;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.aas.core.utils.Contents.CAMERA_MOVE_SPEED;

public class TestGame implements ILogic {


    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;
    private SceneManager sceneManager;
    private Camera camera;
    Vector3f cameraInk;

    public TestGame(){
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        camera = new Camera();
        cameraInk = new Vector3f(0,0,0);
        sceneManager = new SceneManager(-90);
    }


    @Override
    public void init() throws Exception {
        renderer.init();

        Model model = loader.loadOBJModel("/models/cube.obj");
        model.setTexture(new Texture(loader.loadTexture("textures/blue.jpg")),1f);


        Terrain terrain = new Terrain(new Vector3f(0, -1, -800),loader,new Material(new Texture(loader.loadTexture("textures/blue.jpg")), 0.1f));
        Terrain terrain2 = new Terrain(new Vector3f(-800, -1, -800),loader,new Material(new Texture(loader.loadTexture("textures/crate_1.jpg")), 0.1f));
        sceneManager.addTerrain(terrain);
        sceneManager.addTerrain(terrain2);


        Random rnd = new Random();
        for(int i = 0; i < 2000; i++){
            float x = rnd.nextFloat() * 800;
            float z = rnd.nextFloat() * -800;
            sceneManager.addEntity(new Entity(model, new Vector3f(x,2, z),
                    new Vector3f(0,0, 0),1));
        }
        sceneManager.addEntity(new Entity(model, new Vector3f(0,2, -5),
                new Vector3f(0,0,0),1));

        float lightIntensity = 1.0f;

        //pointLight
        Vector3f lightPos = new Vector3f(-0.5f,-0.5f,-3.2f);
        Vector3f lightColor = new Vector3f(1,1,1);
        PointLight pointLight = new PointLight(lightColor, lightPos, lightIntensity, 0f,0f,1f);

        //spot light
        Vector3f coneDir = new Vector3f(0,-50,0);
        float cutoff = (float) Math.cos(Math.toRadians(140));
        lightIntensity = 50000f;
        SpotLight spotLight = new SpotLight(new PointLight(new Vector3f(0.25f,0f,0), new Vector3f(1f,50f, -5f),
                lightIntensity, 0,0,0.02f),coneDir, cutoff);
        //spot light
        coneDir = new Vector3f(0,-50,0);
        cutoff = (float) Math.cos(Math.toRadians(140));
        lightIntensity = 50000f;
        SpotLight spotLight1 = new SpotLight(new PointLight(new Vector3f(0,0.25f,0), new Vector3f(1f,50f, -5f),
                lightIntensity, 0,0,0.02f),coneDir, cutoff);


        //directional light
        lightPos = new Vector3f(-1,0,0);
        lightColor = new Vector3f(1,1,1);
        sceneManager.setDirectionalLight(new DirectionalLight(lightColor, lightPos, lightIntensity));

        sceneManager.setPointLights(new PointLight[]{pointLight});
        sceneManager.setSpotLights(new SpotLight[]{spotLight,spotLight1});
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

       float lightPos = sceneManager.getSpotLights()[0].getPointLight().getPosition().z;
       if(window.isKeyPressed(GLFW.GLFW_KEY_N)){
           sceneManager.getSpotLights()[0].getPointLight().getPosition().z = lightPos + 0.1f;
       }if(window.isKeyPressed(GLFW.GLFW_KEY_M)){
            sceneManager.getSpotLights()[0].getPointLight().getPosition().z = lightPos - 0.1f;
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
        sceneManager.incSpotAngle(0.75f);
        if(sceneManager.getSpotAnle() > 9600){
            sceneManager.setSpotInc(-1);
        }else if(sceneManager.getSpotAnle() <= -9600)
            sceneManager.setSpotInc(-1);

        double spotAngleRad = Math.toRadians(sceneManager.getSpotAnle());
        Vector3f coneDir = sceneManager.getSpotLights()[0].getPointLight().getPosition();
        coneDir.x = (float) Math.sin(spotAngleRad);

        coneDir = sceneManager.getSpotLights()[1].getPointLight().getPosition();
        coneDir.z = (float) Math.cos(spotAngleRad * 0.15);

        sceneManager.incLightAngle(1.1f);
        if(sceneManager.getLightAnle() > 90) {
            sceneManager.getDirectionalLight().setIntensity(0);
            if(sceneManager.getLightAnle() >= 360)
                sceneManager.setLightAnle(-90);
        }else if(sceneManager.getLightAnle() <= -80 || sceneManager.getLightAnle() >= 80){
            float factor = 1 - (Math.abs(sceneManager.getLightAnle()) -80) / 10f;
            sceneManager.getDirectionalLight().setIntensity(factor);
            sceneManager.getDirectionalLight().getColour().y = Math.max(factor, 0.9f);
            sceneManager.getDirectionalLight().getColour().z = Math.max(factor, 0.5f);
        }else{
            sceneManager.getDirectionalLight().setIntensity(1);
            sceneManager.getDirectionalLight().getColour().x = 1;
            sceneManager.getDirectionalLight().getColour().y = 1;
            sceneManager.getDirectionalLight().getColour().z = 1;
        }
        double angRad = Math.toRadians(sceneManager.getLightAnle());
        sceneManager.getDirectionalLight().getDirection().x = (float) Math.sin(angRad);
        sceneManager.getDirectionalLight().getDirection().y = (float) Math.cos(angRad);

        for(Entity entity : sceneManager.getEntities()){
            renderer.processEntities(entity);
        }
        for(Terrain terrain : sceneManager.getTerrains()){
            renderer.processTerrain(terrain);
        }
    }

    @Override
    public void render() {
        renderer.render(camera, sceneManager);

    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
