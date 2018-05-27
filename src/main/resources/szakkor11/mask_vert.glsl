#version 410 core

uniform mat4 transform;
uniform mat4 texMatrix;

layout(location=0) in vec4 position;
layout(location=2) in vec2 texCoord;

out vec2 uv;

void main() {
    // screen coordinate
    gl_Position = transform * position;
    uv = (texMatrix * vec4(texCoord, 1.0, 1.0)).st;

}