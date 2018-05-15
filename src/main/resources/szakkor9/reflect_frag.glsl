uniform samplerCube cubemap;
//uniform sampler2D texture;

uniform mat4 viewInv;

in vec3 worldNormal;
in vec3 ecNormal;
in vec3 ecPosition;
in vec2 uv;
in vec3 worldPosition; // position

vec3 snoiseGrad(vec3 r) {
    vec3 s = vec3(7502, 22777, 4767);
    vec3 f = vec3(0.0, 0.0, 0.0);
    for(int i=0; i<16; i++) {
    f += cos( dot(s, r - vec3(32768, 32768, 32768))
    / 65536.0) * s;
    s = mod(s, 32768.0) * 2.0 + floor(s / 32768.0);
    }
    return f / 65536.0;
}
void main() {

    vec3 normal=normalize(ecNormal);

    vec3 incident = normalize(ecPosition);
    vec3 reflect = reflect(incident, normal);
    reflect = normalize(vec3(viewInv * vec4(reflect, 0)));

    float IOR =1./1.1f;
    float offset = 0.005f;
    vec3 Tr = refract(incident, normal, IOR + offset);
    vec3 Tg = refract(incident, normal, IOR);
    vec3 Tb = refract(incident, normal, IOR - offset);

    vec3 refractColor;
    refractColor.r = texture(cubemap, normalize(vec3(viewInv * vec4(Tr, 0)))).r;
    refractColor.g = texture(cubemap, normalize(vec3(viewInv * vec4(Tg, 0)))).g;
    refractColor.b = texture(cubemap, normalize(vec3(viewInv * vec4(Tb, 0)))).b;

//  refract = normalize(vec3(viewInv * vec4(refract, 0)));

    vec3 reflectColor = vec3(textureCube(cubemap, reflect));

    float fresnel = clamp(-dot(normal, incident), 0.0f, 1.0f);

   fresnel = 1-pow(fresnel, 1.55f);
    if(length(Tb) <= 0.01f){ // total internal reflection
        fresnel = 1.0f;
    }

    vec3 color = fresnel * reflectColor + (1.-fresnel) * refractColor;
//    vec3 color = vec3(worldNormal); // world normal

  gl_FragColor = vec4(color, 1.0);
}
