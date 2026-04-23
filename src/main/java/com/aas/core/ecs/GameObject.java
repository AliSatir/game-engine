package com.aas.core.ecs;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private String name;
    private List<Component> components;

    public GameObject(String name) {
        this.name = name;
        this.components = new ArrayList<>();
    }

    // Sistemin kalbi: Bileşen ekleme
    public void addComponent(Component component) {
        component.gameObject = this;
        components.add(component);
    }

    // İstenilen bileşeni bulma (Generics kullanımı - Unity'deki GetComponent<T>() gibi)
    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                return componentClass.cast(c);
            }
        }
        return null;
    }

    public void start(){
        for(Component c : components){
            c.start();
        }
    }

    public void update(float deltaTime){
        for(Component c : components){
            c.update(deltaTime);
        }
    }


    public String getName() { return name; }
}