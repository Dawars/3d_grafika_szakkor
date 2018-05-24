package me.dawars.extra2;

import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.PJOGL;
import processing.opengl.PShader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Terrain extends PApplet {


    PShader shader;
    float a;

    private GridMesh gridMesh;

    public static void main(String[] args) {
        PApplet.main(Terrain.class);
    }

    @Override
    public void settings() {
        size(1280, 720, P3D);
        pixelDensity(2);
        PJOGL.profile = 4;
    }


    public void setup() {
//        textureMode(NORMAL);
        shader = loadShader("extra2/normalFrag.glsl", "extra2/normalVert.glsl");

        gridMesh = new GridMesh(this);
        gridMesh.setTerrainFunctions(new GridMesh.CalculateTerrain() {
            @Override
            public float getHeightAt(float x, float z) {
                return sin(x/10);
            }

            PVector normal = new PVector();
            PVector dx = new PVector();
            PVector dz = new PVector();

            final float EPS = 0.001f;

            @Override
            public PVector getNormalAt(float x, float z) {
                float height = getHeightAt(x, z);
                dx.set(x + EPS, getHeightAt(x + EPS, z), z).sub(x, height, z).div(EPS);
                dz.set(x, getHeightAt(x, z + EPS), z + EPS).sub(x, height, z).div(EPS);

                PVector.cross(dx, dz, normal);
                return normal;
            }
        });
        gridMesh.setShader(shader);
    }

    public void draw() {
        background(255);
        camera(50, 100, 300, 50, 0, 50, 0, -1, 0);

        // Geometry transformations from Processing are automatically passed to the shader
        // as long as the uniforms in the shader have the right names.
//        translate(width/2,height/2);
//        translate(-100, 0, -100);
//        rotateX(radians(1));
//        rotateY(a * 2);
//        scale(3);

        shader(shader);
        gridMesh.draw();

        a += 0.01;
    }


    FloatBuffer allocateDirectFloatBuffer(int n) {
        return ByteBuffer.allocateDirect(n * Float.BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    IntBuffer allocateDirectIntBuffer(int n) {
        return ByteBuffer.allocateDirect(n * Integer.BYTES).order(ByteOrder.nativeOrder()).asIntBuffer();
    }
}
