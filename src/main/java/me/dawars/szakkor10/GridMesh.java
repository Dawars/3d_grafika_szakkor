package me.dawars.szakkor10;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL4;
import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PGL;
import processing.opengl.PJOGL;
import processing.opengl.PShader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static processing.core.PApplet.cos;
import static processing.core.PApplet.sin;
import static processing.core.PConstants.HALF_PI;

public class GridMesh {
    private PApplet parent;
    private CalculateTerrain callback;
    private PShader shader;

    //    private final int numVertices;
    private final int cols, rows, res;

    float[] positions;
    float[] colors;
    int[] indices;

    FloatBuffer posBuffer;
    FloatBuffer colorBuffer;
    IntBuffer indexBuffer;


    int posVboId;
    int colorVboId;
    int indexVboId;

    int posLoc;
    int colorLoc;

    PJOGL pgl;
    GL4 gl;

    public GridMesh(PApplet parent) {
        this(parent, 20, 20, 5);
    }

    public GridMesh(PApplet parent, int cols, int rows, int res) {
        this.parent = parent;

        this.cols = cols;
        this.rows = rows;
        this.res = res;


        positions = new float[32];
        colors = new float[32];
        indices = new int[12];

        posBuffer = allocateDirectFloatBuffer(32);
        colorBuffer = allocateDirectFloatBuffer(32);
        indexBuffer = allocateDirectIntBuffer(12);

        pgl = (PJOGL) parent.beginPGL();
        gl = pgl.gl.getGL4();

        // Get GL ids for all the buffers
        IntBuffer intBuffer = IntBuffer.allocate(3);
        gl.glGenBuffers(3, intBuffer);
        posVboId = intBuffer.get(0);
        colorVboId = intBuffer.get(1);
        indexVboId = intBuffer.get(2);

        // Get the location of the attribute variables.
//        shader.bind();
//        posLoc = gl.glGetAttribLocation(shader.glProgram, "position");
//        colorLoc = gl.glGetAttribLocation(shader.glProgram, "color");
//        shader.unbind();

        parent.endPGL();
    }

    /**
     * Defines the height field and the normal vectors
     * Call once after the constructor
     *
     * @param callback
     */
    public void setTerrainFunctions(CalculateTerrain callback) {
        this.callback = callback;
    }

    /**
     * Set shader to get attribute locations
     * Should be called once before first rendering this with a new shader
     *
     * @param shader
     */
    public void setShader(PShader shader) {

        this.shader = shader;
        // Get the location of the attribute variables.

        shader.bind();
        posLoc = gl.glGetAttribLocation(shader.glProgram, "position");
        colorLoc = gl.glGetAttribLocation(shader.glProgram, "color");
//        normalLoc = gl.glGetAttribLocation(shader.glProgram, "normal");
//        uvLoc = gl.glGetAttribLocation(shader.glProgram, "uv");
        shader.unbind();

    }
    public void draw() {
        updateGeometry();


        pgl = (PJOGL) parent.beginPGL();
        gl = pgl.gl.getGL4();

        shader.bind();
        gl.glEnableVertexAttribArray(posLoc);
        gl.glEnableVertexAttribArray(colorLoc);

        // Copy vertex data to VBOs
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, posVboId);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * positions.length, posBuffer, GL.GL_DYNAMIC_DRAW);
        gl.glVertexAttribPointer(posLoc, 4, GL.GL_FLOAT, false, 4 * Float.BYTES, 0);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, colorVboId);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * colors.length, colorBuffer, GL.GL_DYNAMIC_DRAW);
        gl.glVertexAttribPointer(colorLoc, 4, GL.GL_FLOAT, false, 4 * Float.BYTES, 0);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

        // Draw the triangle elements
        gl.glBindBuffer(PGL.ELEMENT_ARRAY_BUFFER, indexVboId);
        pgl.bufferData(PGL.ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, indexBuffer, GL.GL_DYNAMIC_DRAW);
        gl.glDrawElements(PGL.TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
        gl.glBindBuffer(PGL.ELEMENT_ARRAY_BUFFER, 0);

        gl.glDisableVertexAttribArray(posLoc);
        gl.glDisableVertexAttribArray(colorLoc);
        shader.unbind();

        parent.endPGL();

    }

    void updateGeometry() {
       /* int vert = 0;
        for (int z = 0; z < rows; z++) {
            for (int x = 0; x < cols; x++) {

                positions[vert*4 + 0] = res * x;
                positions[vert*4 + 1] = callback.getHeightAt(x, z);
                positions[vert*4 + 2] = res * z;
                positions[vert*4 + 3] = 1;

                colors[vert*4 + 0] = 1.0f;
                colors[vert*4 + 1] = 1.0f;
                colors[vert*4 + 2] = 1.0f;
                colors[vert*4 + 3] = 1.0f;

                PVector normal = callback.getNormalAt(x, z);
                normals[vert*3 + 0] = normal.x;
                normals[vert*3 + 1] = normal.y;
                normals[vert*3 + 2] = normal.z;


                uvs[vert*2 + 0] = x / cols;
                uvs[vert*2 + 1] = z / rows;

                vert++;
            }
        }
        int index = 0;
        for (int z = 0; z < rows - 1; ++z) {
            for (int x = 0; x < cols - 1; ++x) {
                indices[index++] = x + z * cols;
                indices[index++] = x + (z + 1) * cols;
                indices[index++] = x + z * cols + 1;

                indices[index++] = x + z * cols + 1;
                indices[index++] = x + (z + 1) * cols;
                indices[index++] = x + (z + 1) * cols + 1;
            }
        }
*/

        // Vertex 1
        positions[0] = -200;
        positions[1] = -200;
        positions[2] = 0;
        positions[3] = 1;

        colors[0] = 1.0f;
        colors[1] = 0.0f;
        colors[2] = 0.0f;
        colors[3] = 1.0f;

        // Vertex 2
        positions[4] = +200;
        positions[5] = -200;
        positions[6] = 0;
        positions[7] = 1;

        colors[4] = 1.0f;
        colors[5] = 1.0f;
        colors[6] = 0.0f;
        colors[7] = 1.0f;

        // Vertex 3
        positions[8] = -200;
        positions[9] = +200;
        positions[10] = 0;
        positions[11] = 1;

        colors[8] = 0.0f;
        colors[9] = 1.0f;
        colors[10] = 0.0f;
        colors[11] = 1.0f;

        // Vertex 4
        positions[12] = +200;
        positions[13] = +200;
        positions[14] = 0;
        positions[15] = 1;

        colors[12] = 0.0f;
        colors[13] = 1.0f;
        colors[14] = 1.0f;
        colors[15] = 1.0f;

        // Vertex 5
        positions[16] = -200;
        positions[17] = -200 * cos(HALF_PI);
        positions[18] = -200 * sin(HALF_PI);
        positions[19] = 1;

        colors[16] = 0.0f;
        colors[17] = 0.0f;
        colors[18] = 1.0f;
        colors[19] = 1.0f;

        // Vertex 6
        positions[20] = +200;
        positions[21] = -200 * cos(HALF_PI);
        positions[22] = -200 * sin(HALF_PI);
        positions[23] = 1;

        colors[20] = 1.0f;
        colors[21] = 0.0f;
        colors[22] = 1.0f;
        colors[23] = 1.0f;

        // Vertex 7
        positions[24] = -200;
        positions[25] = +200 * cos(HALF_PI);
        positions[26] = +200 * sin(HALF_PI);
        positions[27] = 1;

        colors[24] = 0.0f;
        colors[25] = 0.0f;
        colors[26] = 0.0f;
        colors[27] = 1.0f;

        // Vertex 8
        positions[28] = +200;
        positions[29] = +200 * cos(HALF_PI);
        positions[30] = +200 * sin(HALF_PI);
        positions[31] = 1;

        colors[28] = 1.0f;
        colors[29] = 1.0f;
        colors[30] = 1.1f;
        colors[31] = 1.0f;

        // Triangle 1
        indices[0] = 0;
        indices[1] = 1;
        indices[2] = 2;

        // Triangle 2
        indices[3] = 2;
        indices[4] = 3;
        indices[5] = 1;

        // Triangle 3
        indices[6] = 4;
        indices[7] = 5;
        indices[8] = 6;

        // Triangle 4
        indices[9] = 6;
        indices[10] = 7;
        indices[11] = 5;

        posBuffer.rewind();
        posBuffer.put(positions);
        posBuffer.rewind();

        colorBuffer.rewind();
        colorBuffer.put(colors);
        colorBuffer.rewind();

        indexBuffer.rewind();
        indexBuffer.put(indices);
        indexBuffer.rewind();
    }

    public interface CalculateTerrain {
        public float getHeightAt(int x, int z);

        public PVector getNormalAt(int x, int z);
    }

    FloatBuffer allocateDirectFloatBuffer(int n) {
        return ByteBuffer.allocateDirect(n * Float.BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    IntBuffer allocateDirectIntBuffer(int n) {
        return ByteBuffer.allocateDirect(n * Integer.BYTES).order(ByteOrder.nativeOrder()).asIntBuffer();
    }
}
