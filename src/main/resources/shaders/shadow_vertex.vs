#version 400 core
layout (location = 0) in vec3 position;

uniform mat4 projectionMatrix; // Bu lightSpaceMatrix'i tutacak
uniform mat4 transformationMatrix;

void main() {
    gl_Position = projectionMatrix * transformationMatrix * vec4(position, 1.0);
}