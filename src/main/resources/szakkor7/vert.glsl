uniform mat4 transform;

attribute vec4 position;
void main() {
     // normalized device coordinates [-1,1]
     gl_Position = transform * position;
}
