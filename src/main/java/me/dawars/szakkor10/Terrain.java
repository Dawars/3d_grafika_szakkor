package me.dawars.szakkor10;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.PGL;
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
        shader = loadShader("frag.glsl", "vert.glsl");

        gridMesh = new GridMesh(this);
        gridMesh.setTerrainFunctions(new GridMesh.CalculateTerrain() {
            @Override
            public float getHeightAt(int x, int z) {
                return 0;
            }

            PVector vec = new PVector();

            @Override
            public PVector getNormalAt(int x, int z) {
                return vec.set(0, 1, 0);
            }
        });
        gridMesh.setShader(shader);
    }

    public void draw() {
        background(255);

        // Geometry transformations from Processing are automatically passed to the shader
        // as long as the uniforms in the shader have the right names.
        translate(width / 2, height / 2);
        rotateX(a);
        rotateY(a * 2);

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
