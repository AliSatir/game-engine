#version 400 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textureCoords;
layout (location = 2) in vec3 normal;

out vec2 fragTextureCoords;
out vec3 fragNormal;
out vec3 fragPos;
out vec4 fragPosLightSpace; // YENİ: Gölge testi için koordinat

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 lightSpaceMatrix; // YENİ: Java tarafından gelecek

void main() {
    vec4 worldPos = transformationMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * viewMatrix * worldPos;

    fragNormal = normalize((transformationMatrix * vec4(normal, 0.0)).xyz); // Düzeltme: Dünya normalleri
    fragPos = worldPos.xyz;
    fragTextureCoords = textureCoords;

    // Dünyadaki her noktayı ışığın kamerasına göre hesapla
    fragPosLightSpace = lightSpaceMatrix * worldPos;
}