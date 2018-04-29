package me.dawars.szakkor6;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class TextureShader extends PApplet {
    public static void main(String[] args) {
        PApplet.main(TextureShader.class);
    }

    PImage label;
    PShape can;
    float angle;

    PShader texShader;

    @Override
    public void settings() {
        size(640, 360, P3D);

    }

    public void setup() {
        textureWrap(REPEAT);
        label = loadImage("checkerboard.jpg");
        can = createCan(100, 200, 32, label);
        texShader = loadShader("szakkor4/frag.glsl", "szakkor4/vert.glsl");
    }

    public void draw() {
        background(0);

        shader(texShader);

        translate(width / 2, height / 2);
        rotateY(angle);




        beginShape(QUADS);
        texture(label);
        normal(0, 0, 1);
        fill(50, 50, 200);
        vertex(-100, +100, 0, 1);
        vertex(+100, +100, 1, 1);
        fill(200, 50, 50);
        vertex(+100, -100, 1, 0);
        vertex(-100, -100, 0, 0);
        endShape();

        angle += 0.01;
    }

    PShape createCan(float r, float h, int detail, PImage tex) {
        textureMode(NORMAL);
        PShape sh = createShape();
        sh.beginShape(QUAD_STRIP);
        sh.noStroke();
        sh.texture(tex);
        for (int i = 0; i <= detail; i++) {
            float angle = TWO_PI / detail;
            float x = sin(i * angle);
            float z = cos(i * angle);
            float u = (float) i / detail;
            sh.normal(x, 0, z);
            sh.vertex(x * r, -h / 2, z * r, u, 0);
            sh.vertex(x * r, +h / 2, z * r, u, 1);
        }
        sh.endShape();
        return sh;
    }
}
