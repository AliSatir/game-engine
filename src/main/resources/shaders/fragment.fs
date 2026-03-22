#version 400

in vec2 fragTextureCoords;

out vec4 fragColour;

uniform sampler2D textureSampler;

void main() {
    fragColour = texture(textureSampler, fragTextureCoords);
}