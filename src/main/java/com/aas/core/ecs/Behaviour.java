package com.aas.core.ecs;

public abstract class Behaviour extends Component {
    // Unity'deki Start() ve Update() metodları
    public abstract void start();
    public abstract void update(float deltaTime);

    // İhtiyaç duyarsak Input kontrollerini buraya da çekebiliriz
}