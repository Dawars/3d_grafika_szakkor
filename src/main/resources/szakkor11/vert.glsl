#version 410 core

uniform mat4 transform;
uniform mat4 modelview;
uniform mat3 normalMatrix;
uniform mat4 texMatrix;


layout(location=0) in vec4 position;
layout(location=1) in vec3 normal;
layout(location=2) in vec2 texCoord;

out vec3 ecNormal; // normal
out vec3 ecPosition; // position
out vec2 uv;
out vec3 lightDir;

void main() {
    // screen coordinate
    gl_Position = transform * position;

    // camera space
    ecPosition = vec3(modelview * position); // eye coordinates

    // normal
    ecNormal = normalMatrix * normal;

    uv = (texMatrix * vec4(texCoord, 1.0, 1.0)).st;

}