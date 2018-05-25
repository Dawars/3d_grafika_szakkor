#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D texture;
uniform sampler2D normalTexture;
uniform sampler2D dudvTexture;
//uniform samplerCube cubemap;

varying vec4 uv; // texture
varying vec3 ecNormal; // normal
varying vec3 ecPosition; // position
varying vec3 lightDir; // light
varying mat3 TBN;

const float scale = 0.03f;

void main() {
    vec2 dudv = texture2D(dudvTexture, uv.st).st;
    dudv = (2*dudv)-1;

    vec3 normalMap = texture2D(normalTexture, uv.st + scale * dudv).rgb;
    normalMap.r = 1-normalMap.r; // flip red channel
    normalMap = (2 * normalMap) - 1;

    // normalize vectors
    vec3 direction = normalize(lightDir);
    vec3 normal = normalize(TBN * normalMap);

    vec3 diffuseColor = texture2D(texture, uv.st + scale * dudv).rgb;


// diffuse lambert
    float lambertian = clamp(dot(normal, direction), 0, 1);

    vec3 color = lambertian * diffuseColor + 0.1 * diffuseColor;

    gl_FragColor = vec4(color, 1);

}