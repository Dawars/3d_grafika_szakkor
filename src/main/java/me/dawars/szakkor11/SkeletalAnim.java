package me.dawars.szakkor11;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GL4;
import processing.core.PApplet;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.event.KeyEvent;
import processing.opengl.PJOGL;
import processing.opengl.PShader;
import processing.opengl.PShapeOpenGL;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class SkeletalAnim extends PApplet {

    private float angle;
    private PShader shader;
    private JSONArray jointsJSON;
    private JSONArray weightsJSON;
    private float[][] joints;
    private PShapeOpenGL hand;
    private float[][] weights;
    private int NUM_JOINTS;
    private int NUM_VERTICES;


    public static void main(String[] args) {
        PApplet.main(SkeletalAnim.class);
    }

    @Override
    public void settings() {
        size(1280, 720, P3D);
        pixelDensity(2);
    }

    PJOGL pgl;
    GL4 gl;

    float a;

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

    @Override
    public void setup() {
        hand = (PShapeOpenGL) loadShape("models/mano/hand_mean.obj");
        shader = loadShader("szakkor11/frag.glsl", "szakkor11/vert.glsl");

// low opengl

        positions = new float[32];
        colors = new float[32];
        indices = new int[12];

        posBuffer = FloatBuffer.allocate(32);
        colorBuffer = FloatBuffer.allocate(32);
        indexBuffer = IntBuffer.allocate(12);

        pgl = (PJOGL) beginPGL();
        gl = pgl.gl.getGL4();

        // Get GL ids for all the buffers
        IntBuffer intBuffer = IntBuffer.allocate(3);
        gl.glGenBuffers(3, intBuffer);
        posVboId = intBuffer.get(0);
        colorVboId = intBuffer.get(1);
        indexVboId = intBuffer.get(2);

        // Get the location of the attribute variables.
        shader.bind();
        posLoc = gl.glGetAttribLocation(shader.glProgram, "position");
        colorLoc = gl.glGetAttribLocation(shader.glProgram, "color");
        shader.unbind();

        endPGL();


        // load hand


        jointsJSON = loadJSONArray("models/mano/joints.json");
        NUM_JOINTS = jointsJSON.size();

        joints = new float[NUM_JOINTS][];
        for (int i = 0; i < NUM_JOINTS; i++) {
            joints[i] = jointsJSON.getJSONArray(i).getFloatArray();
        }

        weightsJSON = loadJSONArray("models/mano/weights.json");
        NUM_VERTICES = weightsJSON.size();

        weights = new float[NUM_VERTICES][]; // [778x16]
        for (int i = 0; i < NUM_VERTICES; i++) {
            weights[i] = weightsJSON.getJSONArray(i).getFloatArray();
        }

        setWeightAttrib(1);


        PJOGL pgl = (PJOGL) beginPGL();
        GL3 gl = pgl.gl.getGL3();

    }

    private void setWeightAttrib(int v) {
        int vertexCount = 0; // fixme
        for (int i = 0; i < vertexCount; i++) {
            hand.setAttrib("blendWeight", i, weights[i][v]);
        }
    }

    private PVector[] lights = {new PVector(100, 80, -100)};

    @Override
    public void draw() {
        background(127);
        resetShader();

        camera(0, -100, 300, 0, 0, 0, 0, 1, 0);

        scale(1, -1, 1);

        shader(shader);


        angle += 0.01f;

    }

    private void renderAxis(int len) {
        stroke(255, 0, 0);
        line(0, 0, 0, len, 0, 0);
        stroke(0, 255, 0);
        line(0, 0, 0, 0, len, 0);
        stroke(0, 0, 255);
        line(0, 0, 0, 0, 0, len);
        noStroke();
    }

    @Override
    public void keyPressed(KeyEvent event) {
        char num = event.getKey();
        if (Character.isDigit(num)) {
            setWeightAttrib(Character.getNumericValue(num));
        }
    }
}
