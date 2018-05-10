package me.dawars.szakkor9;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PGL;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;

import java.nio.IntBuffer;

public class ModelRender extends PApplet {
    private PShape radioShape, sphere;
    private PImage radioTexture;
    private PShader shader, shaderBg;
    private float angle;
    private PImage[] cubeMap;
    private PGraphicsOpenGL pg;

    public static void main(String[] args) {
        PApplet.main(ModelRender.class);
    }

    @Override
    public void settings() {
        size(720, 720, P3D);
        pixelDensity(2);
    }

    @Override
    public void setup() {
        textureMode(NORMAL);
        radioShape = loadShape("models/radio.obj");
        radioTexture = loadImage("models/radio.png");
        shader = loadShader("szakkor9/frag.glsl", "szakkor9/vert.glsl");

        shaderBg = loadShader("szakkor9/bg_frag.glsl", "szakkor9/bg_vert.glsl");

        cubeMap = new PImage[]{
                loadImage("szakkor9/posx512.jpg"),
                loadImage("szakkor9/negx512.jpg"),
                loadImage("szakkor9/posy512.jpg"),
                loadImage("szakkor9/negy512.jpg"),
                loadImage("szakkor9/posz512.jpg"),
                loadImage("szakkor9/negz512.jpg"),
        };

        sphere = createShape(SPHERE, 150);


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

        String[] textureNames = {"posx512.jpg", "negx512.jpg", "posy512.jpg", "negy512.jpg", "posz512.jpg", "negz512.jpg"};
        PImage[] textures = new PImage[textureNames.length];
        for (int i = 0; i < textures.length; i++) {
            textures[i] = loadImage("szakkor9/" + textureNames[i]);

            //Uncomment this for smoother reflections. This downsamples the textures
            // textures[i].resize(20,20);
        }

// put the textures in the cubeMap
        for (int i = 0; i < textures.length; i++) {
            int w = textures[i].width;
            int h = textures[i].height;
            textures[i].loadPixels();
            int[] pix = textures[i].pixels;
            pgl.texImage2D(PGL.TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, PGL.RGBA, w, h, 0, PGL.RGBA, PGL.UNSIGNED_BYTE, java.nio.IntBuffer.wrap(pix));
        }

        endPGL();

// Load cubemap shader.
        shaderBg.set("cubemap", 1);
    }

    private PVector[] lights = {new PVector(100, 80, -100)};

    @Override
    public void draw() {

        background(0);
        resetShader();

        camera(0, -100, 300, 0, 0, 0, 0, 1, 0);

        scale(1, -1, 1);


        rotateY(angle);

//        shaderBg.set("rayDirMat", pg.camera projmodelview.invert());


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


        scale(2);
        shader(shaderBg);

        shaderBg.set("perturb", 0f);
        noStroke();
        sphere(150);

        shader(shaderBg);
        shaderBg.set("perturb", 1f);

        radioShape.setTexture(radioTexture);
        shape(radioShape);


        angle += 0.01f;
    }
}
