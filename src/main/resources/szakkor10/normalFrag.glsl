#version 410

in vec4 vertColor;
in vec3 vertNormal;
in vec2 vertUV;

out vec4 fragColor;

void main() {
    vec3 normal = normalize(vertNormal);

    fragColor = vertColor;
}