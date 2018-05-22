// vert.glsl
#version 400

uniform mat4 transform;

in vec4 position;
in vec4 color;

out vec4 vertColor;

void main() {
  gl_Position = transform * position;
  vertColor = color;
}