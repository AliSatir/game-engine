package com.aas.test;

import com.aas.core.EngineManager;
import com.aas.core.WindowManager;
import com.aas.core.utils.Contents;

public class Launcher {
    private static WindowManager window;
    private static TestGame game;



    public static void main(String[] args) {


        window = new WindowManager(Contents.TITLE, 1360,720,false);
        game = new TestGame();
        EngineManager engine = new EngineManager();
        try {
            engine.start();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public static WindowManager getWindow(){
        return window;
    }

    public static TestGame getGame(){
        return game;
    }


}