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
    private PImage floor, wall;

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
//        radioShape = ; // todo load model
//        radioTexture = ; // todo load texture
//        shader = ; // todo load szakkor7 shaders
    }


    @Override
    public void draw() {
        background(0);
        resetShader();

        camera(0, -100, 300, 0, 0, 0, 0, 1, 0);

        scale(1, -1, 1); // flip coordinate system (+Y up)


        rotateY(angle);

        // light
        pushMatrix();
        PVector light = new PVector(100, 80, -100);
        float x = light.x /* cos(angle)*/;
        float y = light.y /* sin(angle)*/;
        float z = light.z /* sin(angle)*/;

        pointLight(255, 255, 255, x, y, z);
        translate(x, y, z);
        sphere(1);
        popMatrix();

        renderAxis();

        scale(2);

        shader(shader); // set active shader
        radioShape.setTexture(radioTexture); // set texture for model
        shape(radioShape); //  render model

        angle += 0.01f;
    }

    /**
     * Render coordinate axis
     */
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