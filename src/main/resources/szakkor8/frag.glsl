#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif


uniform sampler2D texture;


varying vec4 vertColor; // color
varying vec3 lightDir; // light
varying vec3 ecNormal; // normal
varying vec3 ecPosition; // vert pos
varying vec4 vertTexCoord; // texture

void main() {

    // normalize vectors
    vec3 direction = normalize(lightDir);
    vec3 normal = normalize(ecNormal);


    vec3 diffuseColor = vertColor.xyz;

// diffuse lambert
    float lambertian = max(0, dot(normal, direction));

    vec3 color = lambertian * diffuseColor;
//    vec3 color = texture2D(texture, vertTexCoord.xy).rgb;

    gl_FragColor = vec4(color, 1);
}
