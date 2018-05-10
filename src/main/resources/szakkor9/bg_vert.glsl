uniform mat4 transform;
uniform mat4 modelview;
uniform mat3 normalMatrix;

attribute vec4 position;
attribute vec3 normal;

varying vec3 ecNormal;
varying vec3 ecVertex;
varying vec3 worldPosition; // position


void main() {
  gl_Position = transform * position;

worldPosition = position.xyz; // position

  ecNormal = normalize(normalMatrix * normal); // Vertex in eye coordinates
  ecVertex = vec3(modelview * position); // Normal vector in eye coordinates
}
