uniform samplerCube cubemap;
//uniform sampler2D texture;

uniform mat4 viewInv;
uniform vec3 cameraPos;

in vec3 worldNormal;
in vec3 worldPosition; // position
in vec3 ecNormal;
in vec3 ecPosition;
in vec2 uv;

void main() {

    vec3 normal = normalize(ecNormal);

    vec3 incident = normalize(ecPosition); //  beeso fenysugar iranya

    vec3 reflected = mat3(viewInv) * reflect(incident, ecNormal);
    vec3 reflectedColor = texture(cubemap, reflected).rgb;

    float IOR = 1. / 1.1f; // Index of Refraction
    float offset = 0.01f;
    vec3 Tr = mat3(viewInv) * refract(incident, normal, IOR + offset);
    vec3 Tg = mat3(viewInv) * refract(incident, normal, IOR);
    vec3 Tb = mat3(viewInv) * refract(incident, normal, IOR - offset);

    vec3 refractColor;
    refractColor.r = texture(cubemap, Tr).r;
    refractColor.g = texture(cubemap, Tg).g;
    refractColor.b = texture(cubemap, Tb).b;


    float fresnel = clamp(dot(normal, -incident), 0., 1.);
    if(length(Tg) == 0.){
        fresnel = 0;
    }

    vec3 color = mix(reflectedColor, refractColor, fresnel); //  final color

  gl_FragColor = vec4(color, 1.0);
}
