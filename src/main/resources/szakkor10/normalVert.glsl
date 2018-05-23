#version 410

uniform mat4 transform;
uniform mat4 modelview;

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
  vertNormal = mat3(inverse(transpose(modelview))) * normal;
  vertUV = uv;
}