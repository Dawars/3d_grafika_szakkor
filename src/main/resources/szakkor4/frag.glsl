#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D texture;

varying vec4 vertColor; // color
varying vec4 vertTexCoord; // texture
varying vec3 ecNormal; // normal
varying vec3 ecPosition; // position
varying vec3 lightDir; // light

const float kA=0.1;
const float kD=1;
const float kS=1;

void main() {
    // normalize vectors
    vec3 direction = normalize(lightDir);
    vec3 normal = normalize(ecNormal);


//  gl_FragColor = vec4( vertTexCoord.st, 0, 1);
    vec3 diffuseColor = texture2D(texture, vertTexCoord.st).rgb;
    float alpha = texture2D(texture, vertTexCoord.st).a;

    float attenuation = 100. / length(lightDir);

// diffuse lambert
    float lambertian = max(0, dot(normal, direction));

// specular
    vec3 reflectDir = normalize(reflect(-direction, normal));
    vec3 viewDir = normalize(-ecPosition);
    float specAngle = max(dot(reflectDir, viewDir), 0.0);
    float specular = pow(specAngle, 25.0);

    vec3 color =
         kA * diffuseColor+ // ambient
        attenuation * lambertian * diffuseColor + // diffuse
        attenuation * specular * vec3(1.0)
        ;

    gl_FragColor = vec4(color, 1);

}