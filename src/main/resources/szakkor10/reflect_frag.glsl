#version 410

uniform samplerCube cubemap;
//uniform sampler2D texture;

uniform mat4 viewInv;
uniform vec3 cameraPos;

in vec3 worldNormal;
in vec3 modelPosition; // position
in vec3 worldPosition; // position
in vec3 ecNormal;
in vec3 ecPosition;
in vec2 uv;

out vec4 fragColor;

vec3 snoiseGrad(vec3 r) {
    vec3 s = vec3(7502, 22777, 4767);
    vec3 f = vec3(0.0, 0.0, 0.0);
    for(int i = 0; i < 16; i++) {
    f += cos( dot(s, r - vec3(32768, 32768, 32768)) / 65536.0) * s;
    s = mod(s, 32768.0) * 2.0 + floor(s / 32768.0);
    }
    return f / 65536.0;
}

void main() {

    vec3 normal=normalize(normalize(ecNormal)+0.1f*snoiseGrad(modelPosition));

    vec3 incident = normalize(ecPosition);

    vec3 reflect = mat3(viewInv) * reflect(incident, normal);
    vec3 reflectColor = vec3(texture(cubemap, reflect));

    float IOR =1./1.f;
    float offset = 0.01f;
    vec3 Tr = mat3(viewInv) * refract(incident, normal, IOR + offset);
    vec3 Tg = mat3(viewInv) * refract(incident, normal, IOR);
    vec3 Tb = mat3(viewInv) * refract(incident, normal, IOR - offset);

    vec3 refractColor;
    refractColor.r = texture(cubemap, Tr).r;
    refractColor.g = texture(cubemap, Tg).g;
    refractColor.b = texture(cubemap, Tb).b;


    float fresnel = clamp(dot(normal, -incident), 0.0f, 1.0f);
    fresnel = pow(fresnel, 1.55f);

    if(length(Tb) == 0.0f){ // total internal reflection
        fresnel = 0.;
    }

    vec3 color = mix(reflectColor, refractColor, fresnel);
//    vec3 color = vec3(worldNormal); // world normal

  fragColor = vec4(color, 1.0);
}
