#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D texture;
//uniform samplerCube cubemap;

varying vec4 vertColor; // color
varying vec4 vertTexCoord; // texture
varying vec3 ecNormal; // normal
varying vec3 ecPosition; // position
varying vec3 worldPosition; // position
varying vec3 lightDir; // light

const float kA=0.1;
const float kD=0.5;
const float kS=0.4;

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
    // normalize vectors
    vec3 direction = normalize(lightDir);
    vec3 normal = normalize(ecNormal);
    vec3 camDir = normalize(-ecPosition);


// perturb normals
// calc tangent space basis
     vec3 tangent1 = cross(normal, vec3(0.0, 0.0, 1.0));
    vec3 tangent2 = cross(normal, vec3(0.0, 1.0, 0.0));
    vec3 tangent = length(tangent1) < length(tangent2) ? tangent2 : tangent1;
    vec3 bitangent = cross(tangent, normal);

    vec3 noisegrad = snoiseGrad(worldPosition);

    normal = normalize(normal + 0.1*noisegrad);

//  gl_FragColor = vec4( vertTexCoord.st, 0, 1);
    vec3 diffuseColor = texture2D(texture, vertTexCoord.st).rgb;
    float alpha = texture2D(texture, vertTexCoord.st).a;

    //float attenuation = 100. / length(lightDir);

    float lightDistance = length(lightDir);
    float lightRadius = 250.0;

    //float attenuation = 1.0/(1.0 + dot(lightDit, lightDir)/lightRadius/lightRadius);
    float attenuation = 1.0/(1.0 + lightDistance*lightDistance/lightRadius/lightRadius);

    //float attenuation = clamp(1.0 - lightDistance*lightDistance/lightRadius/lightRadius, 0.0, 1.0);

    //lightDistance /= lightRadius;
    //float attenuation = 1.0/(1.0 + 0.1*lightDistance + 0.01*lightDistance*lightDistance);

// diffuse lambert
    float lambertian = max(0, dot(normal, direction));

// specular
    vec3 reflectDir = normalize(reflect(-direction, normal));
    vec3 halfDir = normalize(direction + camDir); // blinn phong
//    float specAngle = max(dot(reflectDir, viewDir), 0.0); // phong
    float specAngle = max(dot(halfDir, normal), 0.0); // blinn phong

    float specular = pow(specAngle, 25.0);

    vec3 color =
         kA * diffuseColor+ // ambient
        kD * attenuation * lambertian * diffuseColor + // diffuse
        kS * attenuation * specular * vec3(1.0)
        ;


    // reflect
//    color = texture(cubemap, reflect(-lightDir, normal)).rgb;

    gl_FragColor = vec4(color, 1);
    //gl_FragColor = vec4(attenuation, attenuation, attenuation, 1);
    //gl_FragColor = vec4(lightDistance, lightDistance, lightDistance, 1);

}