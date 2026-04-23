package com.aas.core.physics;

import com.aas.core.components.BoxCollider;
import com.aas.core.components.Rigidbody;
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

            // Eğer nesnede hem Rigidbody hem de Transform varsa fizik uygula
            if (rb != null && transform != null) {

                if (rb.useGravity) {
                    // Hız denklemi: v = v0 + a * dt
                    rb.velocity.y += (GRAVITY_ACCEL * rb.gravityScale) * deltaTime;
                }

                // Pozisyon denklemi: p = p0 + v * dt
                frameVelocity.set(rb.velocity).mul(deltaTime);
                transform.position.add(frameVelocity);

                // Basit Zemin Kontrolü (Collision Detection öncesi geçici çözüm)
                if (transform.position.y < -1.0f) {
                    transform.position.y = -1.0f;
                    rb.velocity.y = 0;
                }
                // ÇARPIŞMA KONTROLÜ
                BoxCollider myCollider = obj.getComponent(BoxCollider.class);
                if (myCollider != null) {
                    // Bu nesneyi sahnede bulunan DİĞER tüm nesnelerle karşılaştırıyoruz
                    for (GameObject other : objects) {
                        if (other == obj) continue; // Kendisiyle çarpışmasın

                        BoxCollider otherCollider = other.getComponent(BoxCollider.class);
                        if (otherCollider != null) {
                            if (CollisionDetector.checkAABB(myCollider, otherCollider)) {
                                // ÇARPIŞMA OLDU!
                                // Hareketi geri al ve hızı sıfırla
                                transform.position.sub(frameVelocity);
                                rb.velocity.y = 0;
                            }
                        }
                    }
                }
            }
        }
    }
}