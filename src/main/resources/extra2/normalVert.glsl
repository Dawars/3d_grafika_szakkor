#version 410

uniform mat4 transform;
uniform mat3 normalMatrix;
uniform mat4 texMatrix;

in vec4 position;
in vec4 color;
in vec3 normal;
in vec2 uv;

out vec4 vertColor;
out vec3 vertNormal;
out vec2 vertUV;

void main() {
  gl_Position = transform * position;
  vertColor = color;
  vertNormal = normalize(normalMatrix * normal);
  vertUV =uv;

}