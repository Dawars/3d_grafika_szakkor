#version 410

uniform mat4 transform;

layout(location = 0) in vec4 position;

out vec3 worldPosition; // position

void main() {
    // screen coordinate
    gl_Position = transform * position;

    worldPosition=position.xyz;
}