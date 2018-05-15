uniform mat4 transform;
uniform mat4 viewInv;
uniform mat4 modelview;
uniform mat3 normalMatrix;
uniform mat3 texMatrix;

attribute vec4 position;
attribute vec3 normal;
in vec2 texCoord;

out vec3 ecNormal;
out vec3 ecPosition;
out vec2 uv;


void main() {
    gl_Position = transform * position;

    uv = (texMatrix*vec3(texCoord, 1)).st;

    ecNormal = normalize(normalMatrix * normal); // Vertex in eye coordinates
    ecPosition = vec3(modelview * position); // Normal vector in eye coordinates
}
