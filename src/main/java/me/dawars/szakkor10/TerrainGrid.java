package me.dawars.szakkor10;

import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.PJOGL;
import processing.opengl.PShader;


public class TerrainGrid extends PApplet {

    private PShader normalShader, shader;
    private GridMesh grid;

    public static void main(String[] args) {
        PApplet.main(TerrainGrid.class);
    }

    @Override
    public void settings() {
        size(1280, 720, P3D);
        PJOGL.profile = 4;
    }


    @Override
    public void setup() {
        normalShader = loadShader("szakkor10/normalFrag.glsl", "szakkor10/normalVert.glsl");
//        shader = loadShader("szakkor10/frag.glsl", "szakkor10/vert.glsl");

        grid = new GridMesh(this);
        grid.setShader(normalShader);

        grid.setTerrainFunctions(new GridMesh.CalculateTerrain() {
            PVector vec = new PVector();

            @Override
            public float getHeightAt(int x, int z) {
                return 0; // TODO 1: change
            }

            @Override
            public PVector getNormalAt(int x, int z) {
                vec.set(0, 1, 0); // TODO 2: change
                return vec;
            }
        });
    }


    float getHeight(float x, float z) {
        return x * z;
    }

    PVector calcNormal() {
        return null;
    }

    float EPS = 0.01f;

    @Override
    public void draw() {
        background(0);
        noStroke();
        camera(0, 100, 0, 0, 0, 0, 0, -1, 0);

//        pointLight(255, 255, 255, 0, 100, 0);

        shader(normalShader);

//        translate(-50, -50);
        grid.draw();
    }
}
