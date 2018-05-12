uniform samplerCube cubemap;
uniform int perturb;
uniform vec3 cameraPos; // camera

in vec3 worldNormal;

in vec3 ecNormal;
in vec3 ecPosition;
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

//    vec3 normal=normalize(ecNormal);

    vec3 I = normalize(worldPosition - cameraPos);
    vec3 R = reflect(I, worldNormal);
//    R.y*=-1;
    vec3 color = vec3(textureCube(cubemap, R));
//    vec3 color = vec3(worldNormal); // world normal

  gl_FragColor = vec4(color, 1.0);
}
