#version 410 core

uniform sampler2D texture;

in vec3 ecNormal;
in vec4 ecPosition;
in vec2 uv;

out vec4 fragColor;

void main(){
    vec3 normal = normalize(ecNormal);
    vec3 light = normalize(vec3(1,1,0.2));


    float lambert = clamp(dot(normal, light), 0, 1);

    vec4 texel = texture(texture, uv);

    vec3 color = 0.9 * lambert * texel.rgb;

    fragColor = vec4(color, 1);
}