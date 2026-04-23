package com.aas.core.ecs;

public abstract class Component {
    public GameObject gameObject; // Bu bileşenin bağlı olduğu nesne

    public abstract void start();
    public abstract void update(float deltaTime);
}
