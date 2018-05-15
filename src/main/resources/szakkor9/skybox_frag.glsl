#version 410

uniform samplerCube cubemap;

in vec3 worldPosition; // position
out vec4 fragColor;

void main() {
    vec3 diffuseColor = texture(cubemap, vec3(0,0,0)).rgb;  // todo 1: sample cubemap
    fragColor = vec4( diffuseColor, 1);
}