package com.aas.core.utils;

import com.aas.core.ModelLoader;
import com.aas.core.ObjectLoader;
import com.aas.core.components.BoxCollider;
import com.aas.core.components.MeshRenderer;
import com.aas.core.components.Rigidbody;
import com.aas.core.components.Transform;
import com.aas.core.ecs.GameObject;
import com.aas.core.entity.Model;
import com.aas.core.scripts_test.PlayerController;
import com.aas.test.Launcher;
import org.joml.Vector3f;

public class Prefab {
    private static ObjectLoader loader = new ObjectLoader();

    // Örnek: Bir "Düşman" prefab'ı oluşturan metod
    public static GameObject createLimonMan(Model model, Vector3f position){
        GameObject go = new GameObject("Limon_Man_Clone");

        // Rastgele bir başlangıç rotasyonu verelim ki hepsi aynı bakmasın
        float randomYaw = (float) (Math.random() * 360);

        go.addComponent(new Transform(position, new Vector3f(0, randomYaw, 0), 50.0f));
        go.addComponent(new MeshRenderer(model));

        // Fizik bileşenleri
        Rigidbody rb = new Rigidbody();
        rb.gravityScale = 20.0f; // Daha tok bir düşüş için
        go.addComponent(rb);

        // Çarpışma kutusu (Modelin boyutuna göre 1,1,1 idealdir)
        go.addComponent(new BoxCollider(new Vector3f(0.1f, 0.01f, 1f)));
        go.addComponent(new PlayerController(Launcher.getWindow()));

        return go;
    }
}