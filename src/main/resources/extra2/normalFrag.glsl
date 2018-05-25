#version 410

in vec4 vertColor;
in vec3 vertNormal;
in vec2 vertUV;

out vec4 fragColor;

const vec3 lightDir = vec3(1,1,1);

void main() {
    vec3 normal = normalize(vertNormal);

    float lambert = dot(normal, normalize(lightDir));

    vec3 color = normal;

    fragColor = vec4(color, 1);
}