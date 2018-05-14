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

    vec3 refract = refract(incident, normal, 1./1.1);
    refract = normalize(vec3(viewInv * vec4(refract, 0)));

    vec3 reflectColor = vec3(textureCube(cubemap, reflect));
    vec3 refractColor = vec3(textureCube(cubemap, refract));

    float fresnel = dot(incident, normal);
//    fresnel=1;

    vec3 color = fresnel * reflectColor + (1.-fresnel) * refractColor;
//    vec3 color = vec3(worldNormal); // world normal

  gl_FragColor = vec4(color, 1.0);
}
