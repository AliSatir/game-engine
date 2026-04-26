package com.aas.core.physics;

import com.aas.core.components.BoxCollider;
import com.aas.core.components.Rigidbody;
import com.aas.core.components.SphereCollider;
import com.aas.core.components.Transform;
import com.aas.core.ecs.GameObject;
import org.joml.Vector3f;

import java.util.List;

public class PhysicsSystem {
    private final float GRAVITY_ACCEL = -9.81f; // Dünyadaki yer çekimi ivmesi
    private final Vector3f frameVelocity = new Vector3f();

    public void update(List<GameObject> objects, float deltaTime) {
        for (GameObject obj : objects) {
            Rigidbody rb = obj.getComponent(Rigidbody.class);
            Transform transform = obj.getComponent(Transform.class);
            BoxCollider myCollider = obj.getComponent(BoxCollider.class);

            if (rb != null && transform != null) {
                // 1. Yer çekimi uygula
                if (rb.useGravity) {
                    rb.velocity.y += (GRAVITY_ACCEL * rb.gravityScale) * deltaTime;
                }

                // 2. Y ekseninde hareket ve kontrol (Dikey)
                float moveY = rb.velocity.y * deltaTime;
                transform.position.y += moveY;
                if (checkCollisions(obj, objects)) {
                    transform.position.y -= moveY; // Çarptıysa geri al
                    rb.velocity.y = 0;             // Enerjiyi sıfırla
                }

                // 3. X ve Z ekseninde hareket (Yatay)
                float moveX = rb.velocity.x * deltaTime;
                transform.position.x += moveX;
                if (checkCollisions(obj, objects)) {
                    transform.position.x -= moveX;
                    rb.velocity.x = 0;
                }

                float moveZ = rb.velocity.z * deltaTime;
                transform.position.z += moveZ;
                if (checkCollisions(obj, objects)) {
                    transform.position.z -= moveZ;
                    rb.velocity.z = 0;
                }
            }
        }
    }

    // Yardımcı metod: Çarpışma var mı?
    private boolean checkCollisions(GameObject obj, List<GameObject> allObjects) {
        BoxCollider myBox = obj.getComponent(BoxCollider.class);
        SphereCollider mySphere = obj.getComponent(SphereCollider.class);

        for (GameObject other : allObjects) {
            if (other == obj) continue;

            BoxCollider otherBox = other.getComponent(BoxCollider.class);
            SphereCollider otherSphere = other.getComponent(SphereCollider.class);

            // 1. Kutu - Kutu
            if (myBox != null && otherBox != null) {
                if (CollisionDetector.checkAABB(myBox, otherBox)) return true;
            }
            // 2. Küre - Küre
            if (mySphere != null && otherSphere != null) {
                if (CollisionDetector.checkSphereSphere(mySphere, otherSphere)) return true;
            }
            // 3. Küre - Kutu
            if (mySphere != null && otherBox != null) {
                if (CollisionDetector.checkSphereAABB(mySphere, otherBox)) return true;
            }
            // 4. Kutu - Küre (Ters kontrol)
            if (myBox != null && otherSphere != null) {
                if (CollisionDetector.checkSphereAABB(otherSphere, myBox)) return true;
            }
        }
        return false;
    }
}