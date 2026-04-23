package com.aas.core.components;

import com.aas.core.ecs.Component;
import com.aas.core.entity.Model;

public class MeshRenderer extends Component {
    private Model model;

    public MeshRenderer(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    public void start() {}

    @Override
    public void update(float deltaTime) {}
}