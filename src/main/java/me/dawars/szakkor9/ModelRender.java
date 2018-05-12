package me.dawars.szakkor9;

import processing.core.*;
import processing.opengl.PGL;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;
import processing.opengl.PShapeOpenGL;

import java.nio.IntBuffer;

public class ModelRender extends PApplet {
    private PShape radioShape, cube, sphere;
    private PImage radioTexture;
    private PShader shader, reflectShader;
    private float angle;
    private PImage[] cubeMap;
    private PGraphicsOpenGL pg;
    private PShader skyboxShader;

    public static void main(String[] args) {
        PApplet.main(ModelRender.class);
    }

    @Override
    public void settings() {
        size(720, 720, P3D);
        pixelDensity(2);
    }

    // Returns an array of colours in argb format
// The array is created if rgba is null or of different length
    void toRGBa(PImage img) {
        img.loadPixels();
        int[] argb = img.pixels;
        int[] rgba = new int[argb.length];
//        int i = 0;
//        for (int p : argb) rgba[i++] = p << 8 | p >>> 24;


        for (int j = 0; j < argb.length; j++) {
            int pixel = argb[j];
            rgba[j] = 0xFF000000 | ((pixel & 0xFF) << 16) | ((pixel & 0xFF0000) >> 16) | (pixel & 0x0000FF00);
        }

        img.pixels = rgba;
        img.updatePixels();
    }

    @Override
    public void setup() {
        hint(DISABLE_OPTIMIZED_STROKE); // https://github.com/processing/processing/wiki/Advanced-OpenGL#vertex-coordinates-are-in-model-space

        textureMode(NORMAL);
        noStroke();

        radioShape = loadShape("models/radio/radio.obj");
        radioTexture = loadImage("models/radio/radio.png");
        shader = loadShader("szakkor9/frag.glsl", "szakkor9/vert.glsl");

        skyboxShader = loadShader("szakkor9/skybox_frag.glsl", "szakkor9/skybox_vert.glsl");

        reflectShader = loadShader("szakkor9/reflect_frag.glsl", "szakkor9/reflect_vert.glsl");

//        cubeMap = new PImage[6];
        cubeMap = new PImage[]{
                loadImage("szakkor9/px.png"),
                loadImage("szakkor9/nx.png"),
                loadImage("szakkor9/py.png"),
                loadImage("szakkor9/ny.png"),
                loadImage("szakkor9/pz.png"),
                loadImage("szakkor9/nz.png"),
        };

        for (PImage img : cubeMap) {
            toRGBa(img);
        }


        cube = createShape(BOX, 5000);
        sphere = createShape(SPHERE, 50);

        PGL pgl = beginPGL();
// create the OpenGL-based cubeMap
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


//Load in textures
        IntBuffer glTextureId = IntBuffer.allocate(1);


// put the textures in the cubeMap
        for (int i = 0; i < cubeMap.length; i++) {
//            cubeMap[i].resize(512, 512); // for performance
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
        camera(0, 100, 300, 0, 0, 0, 0, -1, 0);
        beginCamera();
        rotateY(angle);
        endCamera();


//skybox
//        rotateY(angle);

        shader(skyboxShader);
        shape(cube);

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
//        rotateX(-PI / 4f);

//        rotateZ(angle);

        shader(reflectShader);

/*
        System.out.println("ModelView");
//        pg.modelview.translate(360, 360, 500);
//        pg.updateProjmodelview();
        pg.modelview.print();
        System.out.println("View/Camera");
        pg.camera.print();
        System.out.println("Model");
        PMatrix3D model = new PMatrix3D();
//        PMatrix3D modelInv = new PMatrix3D();
        model.apply(pg.modelview);
        model.apply(pg.cameraInv);
//        modelInv.apply(model);
//        modelInv.invert();
        model.print();
        System.out.println(cameraPos);*/
        PVector cameraPos = new PVector(pg.camera.m03, -pg.camera.m13, pg.camera.m23);

        reflectShader.set("view", pg.camera);
        reflectShader.set("viewInv", pg.cameraInv);
        reflectShader.set("cameraPos", cameraPos);
        shape(sphere);


//        radioShape.setTexture(radioTexture);
        shape(radioShape);


        angle += 0.005f;
    }
}
