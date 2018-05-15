package me.dawars.szakkor9;

import processing.core.*;
import processing.opengl.PGL;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;

import java.nio.IntBuffer;

public class EnvMapping extends PApplet {
    private float angle;
    private PGraphicsOpenGL pg;

    private PShape radioShape, cube, sphere;

    private PImage[] cubeMap;
    private PImage radioTexture;

    private PShader reflectShader;
    private PShader skyboxShader;

    public static void main(String[] args) {
        PApplet.main(EnvMapping.class);
    }

    @Override
    public void settings() {
        size(720, 720, P3D);
        pixelDensity(2);
        fullScreen();
    }

    // Returns an array of colours in argb format
    void toRGBa(PImage img) {
        img.loadPixels();
        int[] argb = img.pixels;
        int[] rgba = new int[argb.length];

        for (int j = 0; j < argb.length; j++) {
            int pixel = argb[j];
            rgba[j] = 0xFF000000 | ((pixel & 0xFF) << 16) | ((pixel & 0xFF0000) >> 16) | (pixel & 0x0000FF00);
        }

        img.pixels = rgba;
        img.updatePixels();
    }

    @Override
    public void setup() {
        textureMode(NORMAL);
//        noStroke(); // todo uncomment

        cube = createShape(BOX, 5000); // skybox
        sphere = createShape(SPHERE, 50); // test object

        radioShape = loadShape("models/radio/radio.obj");
        radioTexture = loadImage("models/radio/radio.png");
//        radioShape.setTexture(radioTexture);  // doesn't work on mac yet

        skyboxShader = loadShader("szakkor9/skybox_frag.glsl", "szakkor9/skybox_vert.glsl");
        reflectShader = loadShader("szakkor9/reflect_frag.glsl", "szakkor9/reflect_vert.glsl");

        cubeMap = new PImage[]{
                loadImage("szakkor9/witcher_px.png"),
                loadImage("szakkor9/witcher_nx.png"),
                loadImage("szakkor9/witcher_py.png"),
                loadImage("szakkor9/witcher_ny.png"),
                loadImage("szakkor9/witcher_pz.png"),
                loadImage("szakkor9/witcher_nz.png"),
        };

        for (PImage img : cubeMap) {
            toRGBa(img);
        }


        PGL pgl = beginPGL(); // advanced OpenGl
        IntBuffer envMapTextureID = IntBuffer.allocate(1);
        pgl.genTextures(1, envMapTextureID);
        pgl.activeTexture(PGL.TEXTURE1);
        pgl.enable(PGL.TEXTURE_CUBE_MAP);
        pgl.bindTexture(PGL.TEXTURE_CUBE_MAP, envMapTextureID.get(0));
        pgl.texParameteri(PGL.TEXTURE_CUBE_MAP, PGL.TEXTURE_WRAP_S, PGL.CLAMP_TO_EDGE);
        pgl.texParameteri(PGL.TEXTURE_CUBE_MAP, PGL.TEXTURE_WRAP_T, PGL.CLAMP_TO_EDGE);
        pgl.texParameteri(PGL.TEXTURE_CUBE_MAP, PGL.TEXTURE_WRAP_R, PGL.CLAMP_TO_EDGE);
        pgl.texParameteri(PGL.TEXTURE_CUBE_MAP, PGL.TEXTURE_MIN_FILTER, PGL.LINEAR);
        pgl.texParameteri(PGL.TEXTURE_CUBE_MAP, PGL.TEXTURE_MAG_FILTER, PGL.LINEAR);


        // put the textures in the cubeMap
        for (int i = 0; i < cubeMap.length; i++) {
//            cubeMap[i].resize(256, 256); // for performance
//            cubeMap[i].resize(20, 20); // for smooth reflections

            int w = cubeMap[i].width;
            int h = cubeMap[i].height;
            cubeMap[i].loadPixels();
            int[] pix = cubeMap[i].pixels;
            pgl.texImage2D(PGL.TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, PGL.RGBA, w, h, 0, PGL.RGBA, PGL.UNSIGNED_BYTE, java.nio.IntBuffer.wrap(pix));
        }

        endPGL();

        // Load cubemap shader.
        reflectShader.set("cubemap", 1);
        skyboxShader.set("cubemap", 1);

        pg = (PGraphicsOpenGL) this.g;
    }

    private PVector[] lights = {new PVector(100, 80, -100)};

    @Override
    public void draw() {

        background(0);
        resetShader();
        camera(0, 50, 150, 0, 0, 0, 0, -1, 0);
        beginCamera();
        rotateY(angle);
        endCamera();


        //skybox
        shader(skyboxShader);
        shape(cube);

        // lights
        for (PVector light : lights) {
            pushMatrix();
            float x = light.x /* cos(angle)*/;
            float y = light.y /* sin(angle)*/;
            float z = light.z /* sin(angle)*/;
            fill(255f);
            pointLight(255, 255, 255, x, y, z);
            translate(x, y, z);
            sphere(1);

            popMatrix();
        }

//        scale(2);

        shader(reflectShader);

        reflectShader.set("view", pg.camera);
        reflectShader.set("viewInv", pg.cameraInv);
        reflectShader.set("cameraPos", pg.modelviewInv.m03, pg.modelviewInv.m13, pg.modelviewInv.m23);

        shape(sphere);
        shape(radioShape);

        angle += 0.005f;
    }
}
