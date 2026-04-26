package com.aas.core.physics;

import com.aas.core.components.BoxCollider;
import com.aas.core.components.SphereCollider;
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

    public static boolean checkSphereSphere(SphereCollider s1, SphereCollider s2) {
        float distanceSq = s1.getCenter().distanceSquared(s2.getCenter());
        float combinedRadius = s1.getTransformedRadius() + s2.getTransformedRadius();
        return distanceSq <= (combinedRadius * combinedRadius);
    }

    // Küre - Kutu (AABB) Çarpışması
    public static boolean checkSphereAABB(SphereCollider s, BoxCollider b) {
        Vector3f center = s.getCenter();
        Vector3f bMin = b.getMin();
        Vector3f bMax = b.getMax();

        // Kutunun üzerinde kürenin merkezine en yakın noktayı bul (Clamping)
        float closestX = Math.max(bMin.x, Math.min(center.x, bMax.x));
        float closestY = Math.max(bMin.y, Math.min(center.y, bMax.y));
        float closestZ = Math.max(bMin.z, Math.min(center.z, bMax.z));

        // Bu en yakın nokta ile küre merkezi arasındaki mesafeyi hesapla
        float distanceSq = (center.x - closestX) * (center.x - closestX) +
                (center.y - closestY) * (center.y - closestY) +
                (center.z - closestZ) * (center.z - closestZ);

        float radius = s.getTransformedRadius();
        return distanceSq <= (radius * radius);
    }

}