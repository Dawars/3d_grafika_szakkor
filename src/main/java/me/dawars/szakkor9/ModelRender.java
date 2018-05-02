package me.dawars.szakkor9;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;

public class ModelRender extends PApplet {
    private PShape radioShape;
    private PImage radioTexture;
    private PShader shader;
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

        cubeMap = new PImage[]{
                loadImage("szakkor9/negx512.jpg"),
                loadImage("szakkor9/negy512.jpg"),
                loadImage("szakkor9/negz512.jpg"),
                loadImage("szakkor9/posx512.jpg"),
                loadImage("szakkor9/posy512.jpg"),
                loadImage("szakkor9/posz512.jpg")
        };

        shader.set("cubemap1", cubeMap[0]);
        shader.set("cubemap2", cubeMap[1]);
        shader.set("cubemap3", cubeMap[2]);
        shader.set("cubemap4", cubeMap[3]);
        shader.set("cubemap5", cubeMap[4]);
        shader.set("cubemap6", cubeMap[5]);

        pg = (PGraphicsOpenGL) this.g;
    }

    private PVector[] lights = {new PVector(100, 80, -100)};

    @Override
    public void draw() {
        background(0);
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


        scale(2);
        shader(shader);
        radioShape.setTexture(radioTexture);
        shape(radioShape);


        angle += 0.01f;
    }

}
