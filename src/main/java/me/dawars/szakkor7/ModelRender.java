package me.dawars.szakkor7;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PShader;

public class ModelRender extends PApplet {
    private PShape radioShape;
    private PImage radioTexture;
    private PShader shader;
    private float angle;

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
        radioShape = loadShape("models/radio.obj");
        radioTexture = loadImage("models/radio.png");
        shader = loadShader("szakkor4/frag.glsl", "szakkor4/vert.glsl");
    }

    private PVector[] lights = {new PVector(100, 80, 100)};

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

        renderAxis();


        scale(2);
        shader(shader);
        radioShape.setTexture(radioTexture);
        shape(radioShape);

        /*
        pushMatrix();
        translate(0, -1, 0);
        box(1000, 1, 1000);
        popMatrix();*/

        angle += 0.01f;
    }

    private void renderAxis() {
        strokeWeight(2);
        stroke(255, 0, 0);
        line(0, 0, 0, 100, 0, 0);
        stroke(0, 255, 0);
        line(0, 0, 0, 0, 100, 0);
        stroke(0, 0, 255);
        line(0, 0, 0, 0, 0, 100);
        noStroke();
    }
}
