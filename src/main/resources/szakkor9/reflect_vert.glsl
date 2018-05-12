uniform mat4 transform;
uniform mat4 view;
uniform mat4 viewInv;
uniform mat4 modelview;
uniform mat4 projection;
uniform mat3 normalMatrix;

attribute vec4 position;
attribute vec3 normal;

out vec3 worldNormal;
out vec3 ecNormal;
out vec3 ecPosition;
out vec3 worldPosition; // position


void main() {
    mat4 model = modelview * viewInv;

    gl_Position = transform * position;

    worldPosition = (model*position).xyz; // position
    // (VM)^-1=M^-1*V^-1
//    mat3 normalMatrix = mat3(transpose(inverse(modelview)));
    worldNormal = mat3(transpose(inverse(model)))*normal; // normal in world space

    ecNormal = normalize(normalMatrix * normal); // Vertex in eye coordinates
    ecPosition = vec3(modelview * position); // Normal vector in eye coordinates
}
