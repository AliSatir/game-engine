package com.aas.core.physics;

import com.aas.core.components.BoxCollider;
import org.joml.Vector3f;

public class CollisionDetector {
    public static boolean checkAABB(BoxCollider a, BoxCollider b) {
        Vector3f aMin = a.getMin();
        Vector3f aMax = a.getMax();
        Vector3f bMin = b.getMin();
        Vector3f bMax = b.getMax();

        return (aMin.x <= bMax.x && aMax.x >= bMin.x) &&
                (aMin.y <= bMax.y && aMax.y >= bMin.y) &&
                (aMin.z <= bMax.z && aMax.z >= bMin.z);
    }
}