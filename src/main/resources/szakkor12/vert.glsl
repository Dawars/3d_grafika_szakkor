uniform mat4 transform;
uniform mat4 modelview;
uniform mat3 normalMatrix;

uniform vec4 lightPosition;

attribute vec4 position;
attribute vec4 color;
attribute vec3 normal;
attribute float blendWeight;

varying vec4 vertColor; // color
varying vec3 ecNormal; // normal
varying vec3 ecPosition; // position
varying vec3 lightDir; // light
varying float weight; // blend weight

void main() {
    // screen coordinate
    gl_Position = transform * position;

    // camera space
    ecPosition = vec3(modelview * position); // eye coordinates

    // vertex color
    vertColor = color;

    // normal
    ecNormal = normalMatrix * normal;

    // incident light
    lightDir = lightPosition.xyz - ecPosition; // eye coordinates

    weight = blendWeight;
}