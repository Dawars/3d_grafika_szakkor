#version 410

uniform samplerCube cubemap;

in vec3 worldPosition; // position
out vec4 fragColor;

void main() {
    vec3 diffuseColor = texture(cubemap, worldPosition).rgb;  // todo 1: sample cubemap
    fragColor = vec4( diffuseColor, 1);
}