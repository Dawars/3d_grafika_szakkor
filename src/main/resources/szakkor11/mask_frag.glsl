#version 410 core

uniform sampler2D texture;
in vec2 uv;

out vec4 fragColor;

void main(){
    fragColor = texture(texture, uv);
}