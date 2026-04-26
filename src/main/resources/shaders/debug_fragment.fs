#version 400 core

out vec4 fragColour;

// Java'dan çizgilerin rengini göndereceğiz (Örn: Yeşil)
uniform vec3 colour;

void main() {
    fragColour = vec4(colour, 1.0); // Şeffaflık istersen burayı 0.5f yapabilirsin
}