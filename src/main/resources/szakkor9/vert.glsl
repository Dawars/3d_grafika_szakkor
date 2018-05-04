uniform mat4 transform;
uniform mat4 modelview;
uniform mat3 normalMatrix;
uniform mat4 texMatrix;

uniform vec4 lightPosition;

attribute vec4 position;
attribute vec4 color;
attribute vec3 normal;
attribute vec2 texCoord;

varying vec4 vertColor; // color
varying vec4 vertTexCoord; // texture
varying vec3 ecNormal; // normal
varying vec3 ecPosition; // position
varying vec3 worldPosition; // position
varying vec3 lightDir; // light

void main() {
    // screen coordinate
    gl_Position = transform * position;

    // camera space
    ecPosition = vec3(modelview * position); // eye coordinates
    worldPosition=position.xyz;

    // vertex color
    vertColor = color;

    // texture
    vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);


    // normal
    ecNormal = normalMatrix * normal;

    // incident light
    lightDir = lightPosition.xyz - ecPosition; // eye coordinates

}