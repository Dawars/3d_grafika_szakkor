package me.dawars.szakkor12;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PShapeOBJ;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.event.KeyEvent;
import processing.opengl.PShader;
import processing.opengl.PShapeOpenGL;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

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

    @Override
    public void setup() {
        hand = (PShapeOpenGL) loadShape("models/mano/hand_mean.obj");
        shader = loadShader("szakkor12/frag.glsl", "szakkor12/vert.glsl");

        PShapeOBJ mano;
        try {
            mano = new PShapeOBJ(this, new BufferedReader(new FileReader("models/mano/hand_mean.obj")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


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


        rotateY(angle);
        for (PVector light : lights) {
            pushMatrix();
            float x = light.x /* cos(angle)*/;
            float y = light.y /* sin(angle)*/;
            float z = light.z /* sin(angle)*/;

            pointLight(255, 255, 255, x, y, z);
            translate(x, y, z);
            sphere(1);

            popMatrix();
        }
//        rotateX(-PI / 4f);

        scale(10);


        // jointsJSON
        for (float[] joint : joints) {

            pushMatrix();
            translate(joint[0], joint[1], joint[2]);
            strokeWeight(0.1f);
            renderAxis(1);
            popMatrix();
        }

        // hand
        strokeWeight(0.3f);
        renderAxis(10);

        shader(shader);
        shape(hand);

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
