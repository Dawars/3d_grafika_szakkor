package me.dawars.szakkor11;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import processing.core.PApplet;
import processing.data.JSONArray;
import processing.opengl.PGL;
import processing.opengl.PJOGL;
import processing.opengl.PShader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@SuppressWarnings("Duplicates")
public class AdvancedOpenGl extends PApplet {


    PShader shader;
    float a;

    int posVboId;
    int colorVboId;
    int indexVboId;

    int posLoc;
    int colorLoc;

    PJOGL pgl;
    GL4 gl;
    private int[] vaoID = new int[1];

    public static void main(String[] args) {
        PApplet.main(AdvancedOpenGl.class);
    }

    @Override
    public void settings() {
        size(1280, 720, P3D);
        pixelDensity(2);
        PJOGL.profile = 4;
    }


    private int NUM_JOINTS;
    private float[][] joints;
    private int NUM_VERTICES;
    private float[][] weights;
    private int NUM_FACES;
    private float[] manoIndex;
    private FloatBuffer manoIndexBuffer;

    private float[] manoVerts;
    private FloatBuffer manoVertBuffer;


    @Override
    public void setup() {

        shader = loadShader("frag.glsl", "vert.glsl");

        // Load MANO
        // Vertices

        JSONArray vertsJSON = loadJSONArray("models/mano/mano_mean.json");
        NUM_VERTICES = vertsJSON.size();

        manoVerts = new float[3 * NUM_VERTICES];

        for (int i = 0; i < NUM_VERTICES; i++) {
            float[] vert = vertsJSON.getJSONArray(i).getFloatArray();
            manoVerts[3 * i + 0] = vert[0];
            manoVerts[3 * i + 1] = vert[1];
            manoVerts[3 * i + 2] = vert[2];
        }

        manoVertBuffer = allocateDirectFloatBuffer(3 * NUM_VERTICES);
        manoVertBuffer.put(manoVerts);

        // Index Array
        JSONArray indicesJSON = loadJSONArray("models/mano/index_buffer.json");
        NUM_FACES = indicesJSON.size();

        manoIndex = new float[3 * NUM_FACES];

        for (int i = 0; i < NUM_FACES; i++) {
            float[] vert = indicesJSON.getJSONArray(i).getFloatArray();
            manoIndex[3 * i + 0] = vert[0];
            manoIndex[3 * i + 1] = vert[1];
            manoIndex[3 * i + 2] = vert[2];
        }

        manoIndexBuffer = allocateDirectFloatBuffer(3 * NUM_FACES);
        manoIndexBuffer.put(manoIndex);

        // Joints
        JSONArray jointsJSON = loadJSONArray("models/mano/joints.json");
        NUM_JOINTS = jointsJSON.size();

        joints = new float[NUM_JOINTS][];
        for (int i = 0; i < NUM_JOINTS; i++) {
            joints[i] = jointsJSON.getJSONArray(i).getFloatArray();
        }

        // Blend Weights
       /* JSONArray weightsJSON = loadJSONArray("models/mano/weights.json");

        weights = new float[NUM_VERTICES][]; // [778x16]
        for (int i = 0; i < NUM_VERTICES; i++) {
            weights[i] = weightsJSON.getJSONArray(i).getFloatArray();
        }*/

// create VAOs and VBOs
        pgl = (PJOGL) beginPGL();
        gl = pgl.gl.getGL4();

        gl.glGenVertexArrays(1, vaoID, 0);

        // Get GL ids for all the buffers
        IntBuffer intBuffer = IntBuffer.allocate(2);
        gl.glGenBuffers(2, intBuffer);
        posVboId = intBuffer.get(0);
        indexVboId = intBuffer.get(1);

        // Get the location of the attribute variables.
        shader.bind();
        posLoc = gl.glGetAttribLocation(shader.glProgram, "position");

        gl.glBindVertexArray(vaoID[0]);

        gl.glEnableVertexAttribArray(posLoc);

        // Copy vertex data to VBOs
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, posVboId);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * manoVerts.length, manoVertBuffer, GL.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(posLoc, 3, GL.GL_FLOAT, false, 3 * Float.BYTES, 0);
//
//        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, colorVboId);
//        gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * colors.length, colorBuffer, GL.GL_DYNAMIC_DRAW);
//        gl.glVertexAttribPointer(colorLoc, 4, GL.GL_FLOAT, false, 4 * Float.BYTES, 0);


        gl.glBindBuffer(PGL.ELEMENT_ARRAY_BUFFER, indexVboId);
        pgl.bufferData(PGL.ELEMENT_ARRAY_BUFFER, Integer.BYTES * manoIndex.length, manoIndexBuffer, GL.GL_STATIC_DRAW);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

        shader.unbind();

        endPGL();
    }


    public void draw() {
        background(255);

        // Geometry transformations from Processing are automatically passed to the shader
        // as long as the uniforms in the shader have the right names.
        translate(width / 2, height / 2);
        rotateX(a);
        rotateY(a * 2);


        pgl = (PJOGL) beginPGL();
        gl = pgl.gl.getGL4();

        shader.bind();
        gl.glBindVertexArray(vaoID[0]);

        // Draw the triangle elements
        gl.glDrawElements(PGL.TRIANGLES, manoIndex.length, GL.GL_UNSIGNED_INT, 0);
        gl.glBindBuffer(PGL.ELEMENT_ARRAY_BUFFER, 0);

        gl.glDisableVertexAttribArray(posLoc);
//        gl.glDisableVertexAttribArray(colorLoc);
        shader.unbind();

        endPGL();

        a += 0.01;
    }

    FloatBuffer allocateDirectFloatBuffer(int n) {
        return ByteBuffer.allocateDirect(n * Float.BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    IntBuffer allocateDirectIntBuffer(int n) {
        return ByteBuffer.allocateDirect(n * Integer.BYTES).order(ByteOrder.nativeOrder()).asIntBuffer();
    }
}
