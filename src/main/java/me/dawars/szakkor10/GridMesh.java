package me.dawars.szakkor10;

import com.jogamp.opengl.GL;
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

public class GridMesh {
    private PApplet parent;
    private CalculateTerrain callback;
    private PShader shader;

    private final int numVertices;
    private final int cols, rows, res;

    private PJOGL pgl;
    private GL4 gl;


    float[] positions;
    float[] colors;
    float[] normals;
    float[] uvs;
    int[] indices;

    FloatBuffer posBuffer;
    FloatBuffer colorBuffer;
    FloatBuffer normalBuffer;
    FloatBuffer uvBuffer;
    IntBuffer indexBuffer;

    int posVboId;
    int colorVboId;
    int normalVboId;
    int uvVboId;
    int indexVboId;

    int posLoc;
    int colorLoc;
    int normalLoc;
    int uvLoc;


    public GridMesh(PApplet parent) {
        this(parent, 20, 20, 5);
    }

    public GridMesh(PApplet parent, int cols, int rows, int res) {
        this.parent = parent;

        this.cols = cols;
        this.rows = rows;
        this.res = res;

        numVertices = (cols) * (rows);
        positions = new float[numVertices * 4];
        colors = new float[numVertices * 4];
        normals = new float[numVertices * 3];
        uvs = new float[numVertices * 2];
        indices = new int[2 * cols * rows * 3];

        posBuffer = allocateDirectFloatBuffer(numVertices * 4);
        colorBuffer = allocateDirectFloatBuffer(numVertices * 4);
        normalBuffer = allocateDirectFloatBuffer(numVertices * 3);
        uvBuffer = allocateDirectFloatBuffer(numVertices * 2);
        indexBuffer = allocateDirectIntBuffer(2 * cols * rows * 3);

        pgl = (PJOGL) parent.beginPGL();
        gl = pgl.gl.getGL4();

        // Get GL ids for all the buffers
        IntBuffer intBuffer = IntBuffer.allocate(5);
        gl.glGenBuffers(5, intBuffer);
        posVboId = intBuffer.get(0);
        colorVboId = intBuffer.get(1);
        normalVboId = intBuffer.get(2);
        uvVboId = intBuffer.get(3);
        indexVboId = intBuffer.get(4);

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
        updateGeometry();
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
        normalLoc = gl.glGetAttribLocation(shader.glProgram, "normal");
        uvLoc = gl.glGetAttribLocation(shader.glProgram, "uv");
        shader.unbind();

    }

    public void draw() {
        pgl = (PJOGL) parent.beginPGL();
        gl = pgl.gl.getGL4();


        gl.glEnableVertexAttribArray(posLoc);
        gl.glEnableVertexAttribArray(colorLoc);
        gl.glEnableVertexAttribArray(normalLoc);
        gl.glEnableVertexAttribArray(uvLoc);

        // Copy vertex data to VBOs
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, posVboId);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * positions.length, posBuffer, GL.GL_DYNAMIC_DRAW);
        gl.glVertexAttribPointer(posLoc, 4, GL.GL_FLOAT, false, 4 * Float.BYTES, 0);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, colorVboId);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * colors.length, colorBuffer, GL.GL_DYNAMIC_DRAW);
        gl.glVertexAttribPointer(colorLoc, 4, GL.GL_FLOAT, false, 4 * Float.BYTES, 0);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, normalVboId);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * colors.length, normalBuffer, GL.GL_DYNAMIC_DRAW);
        gl.glVertexAttribPointer(normalLoc, 3, GL.GL_FLOAT, false, 3 * Float.BYTES, 0);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, uvVboId);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * colors.length, uvBuffer, GL.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(uvLoc, 2, GL.GL_FLOAT, false, 2 * Float.BYTES, 0);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

        // Draw the triangle elements
        gl.glBindBuffer(PGL.ELEMENT_ARRAY_BUFFER, indexVboId);
        pgl.bufferData(PGL.ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, indexBuffer, GL.GL_DYNAMIC_DRAW);
        gl.glDrawElements(PGL.TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
        gl.glBindBuffer(PGL.ELEMENT_ARRAY_BUFFER, 0);

        gl.glDisableVertexAttribArray(posLoc);
        gl.glDisableVertexAttribArray(colorLoc);
        gl.glDisableVertexAttribArray(normalLoc);
        gl.glDisableVertexAttribArray(uvLoc);

        parent.endPGL();

    }

    void updateGeometry() {
        int vert = 0;
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

        posBuffer.rewind();
        posBuffer.put(positions);
        posBuffer.rewind();

        colorBuffer.rewind();
        colorBuffer.put(colors);
        colorBuffer.rewind();

        uvBuffer.rewind();
        uvBuffer.put(uvs);
        uvBuffer.rewind();

        normalBuffer.rewind();
        normalBuffer.put(normals);
        normalBuffer.rewind();

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
