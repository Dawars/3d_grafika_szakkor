uniform samplerCube cubemap;
uniform float perturb;

varying vec3 ecNormal;
varying vec3 ecVertex;
varying vec3 worldPosition; // position

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

     vec3 tangent1 = cross(normal, vec3(0.0, 0.0, 1.0));
      vec3 tangent2 = cross(normal, vec3(0.0, 1.0, 0.0));
      vec3 tangent = length(tangent1) < length(tangent2) ? tangent2 : tangent1;
      vec3 bitangent = cross(tangent, normal);

      vec3 noisegrad = perturb*snoiseGrad(worldPosition);

       normal = normalize(normal + 0.1f * noisegrad);

    vec3 reflectDir = reflect(ecVertex, normal);

  vec3 color = vec3(textureCube(cubemap, reflectDir));
  gl_FragColor = vec4(color, 1.0);      
}
