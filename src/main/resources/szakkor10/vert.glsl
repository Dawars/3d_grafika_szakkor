uniform mat4 transform;
uniform mat4 modelview;
uniform mat3 normalMatrix;
uniform mat4 texMatrix;

uniform vec4 lightPosition;

attribute vec4 position;
attribute vec3 normal;
attribute vec3 tangent;
attribute vec3 bitangent;
attribute vec2 texCoord;

varying vec4 vertTexCoord; // texture
varying vec3 ecNormal; // normal
varying vec3 ecPosition; // position
varying vec3 vertTangent; // light
varying vec3 lightDir; // light
varying mat3 TBN;

void main() {
    // screen coordinate
    gl_Position = transform * position;

    // camera space
    ecPosition = vec3(modelview * position); // eye coordinates

    // texture
    vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);

    // normal
    ecNormal = normalMatrix * normal;

    // incident light
    lightDir = lightPosition.xyz - ecPosition; // eye coordinates

    vec3 T = normalize(mat3(normalMatrix) * vec3(1,0,0));
    vec3 B = normalize(mat3(normalMatrix) * vec3(0,1,0));
    TBN = mat3(T, B, ecNormal);

}