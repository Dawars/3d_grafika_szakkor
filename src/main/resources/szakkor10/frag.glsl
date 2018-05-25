#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D texture;
uniform sampler2D normalTexture;
//uniform samplerCube cubemap;

varying vec4 vertTexCoord; // texture
varying vec3 ecNormal; // normal
varying vec3 ecPosition; // position
varying vec3 lightDir; // light
varying mat3 TBN;

const float kA=0.1;
const float kD=0.5;
const float kS=0.4;

void main() {
    vec3 normalMap = texture2D(normalTexture, vertTexCoord.st).rgb;
    normalMap = (2 * normalMap) - 1;

    // normalize vectors
    vec3 direction = normalize(lightDir);
    vec3 normal = normalize(TBN * normalMap);
    vec3 camDir = normalize(-ecPosition);

//  gl_FragColor = vec4( vertTexCoord.st, 0, 1);
    vec3 diffuseColor = texture2D(texture, vertTexCoord.st).rgb;
    float alpha = texture2D(texture, vertTexCoord.st).a;


// diffuse lambert
    float lambertian = clamp(dot(normal, direction), 0, 1);

// specular
    vec3 reflectDir = normalize(reflect(-direction, normal));
    vec3 halfDir = normalize(direction + camDir); // blinn phong
//    float specAngle = max(dot(reflectDir, viewDir), 0.0); // phong
    float specAngle = clamp(dot(halfDir, normal), 0, 1); // blinn phong

    float specular = pow(specAngle, 25.0);

    vec3 color =
         kA * diffuseColor+ // ambient
        kD * lambertian * diffuseColor + // diffuse
        kS * specular * vec3(1.0)
        ;

    gl_FragColor = vec4(color, 1);

}