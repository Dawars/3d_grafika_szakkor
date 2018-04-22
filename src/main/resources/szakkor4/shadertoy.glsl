uniform vec2 iResolution;
uniform float iGlobalTime;

void mainImage(out vec4 fragColor, in vec2 fragCoord) {
    vec2 uv = fragCoord.xy / iResolution.xy;
    fragColor = vec4(uv, 1, 1);
}

void main() {
    mainImage(gl_FragColor, gl_FragCoord.xy);
}