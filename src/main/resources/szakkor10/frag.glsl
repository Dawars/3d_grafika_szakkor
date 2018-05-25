#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D texture;
uniform sampler2D normalTexture;
//uniform samplerCube cubemap;

varying vec4 uv; // texture
varying vec3 ecNormal; // normal
varying vec3 ecPosition; // position
varying vec3 lightDir; // light
varying mat3 TBN;

void main() {
    vec3 normalMap = texture2D(normalTexture, uv.st).rgb;
    normalMap.r = 1-normalMap.r; // flip red channel
    // TODO 1: Convert colors to direction [0,1]->[-1,1]

    // normalize vectors
    vec3 direction = normalize(lightDir);
    vec3 normal = normalize(ecNormal); // Todo 2: calculate new normal direction

    vec3 diffuseColor = texture2D(texture, uv.st).rgb;
    float alpha = texture2D(texture, uv.st).a;


// diffuse lambert
    float lambertian = clamp(dot(normal, direction), 0, 1);

    vec3 color = lambertian * diffuseColor + 0.1 * diffuseColor;

    gl_FragColor = vec4(color, 1);

}