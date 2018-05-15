uniform samplerCube cubemap;
//uniform sampler2D texture;

uniform mat4 viewInv;
uniform vec3 cameraPos;

in vec3 worldNormal;
in vec3 worldPosition; // position
in vec3 ecNormal;
in vec3 ecPosition;
in vec2 uv;

void main() {

    vec3 normal = normalize(ecNormal);

    vec3 incident; // todo beeso fenysugar iranya

    vec3 color = vec3(1, 0, 1); // todo final color

  gl_FragColor = vec4(color, 1.0);
}
