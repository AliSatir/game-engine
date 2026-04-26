#version 400 core

layout (location = 0) in vec3 position;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

void main() {
    // Standart dönüşüm, doku koordinatı veya normal yok
    gl_Position = projectionMatrix * viewMatrix * transformationMatrix * vec4(position, 1.0);
}