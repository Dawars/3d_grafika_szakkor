package me.dawars.szakkor4;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;

public class ShaderTest extends PApplet {
    private PShape cylinder;
    private PImage img;
    private float angle;

    public static void main(String[] args) {
        PApplet.main(ShaderTest.class);
    }

    @Override
    public void settings() {
        size(640, 360, P3D);

    }

    PShader shaderObj;

    @Override
    public void setup() {
        img = loadImage("souya_tex_01.jpg");
        cylinder = createCan(100, 200, 16);
        shaderObj = loadShader("szakkor4/frag.glsl", "szakkor4/vert.glsl");
    }

    PShape createCan(float r, float h, int detail) {
        textureMode(NORMAL);
        PShape sh = createShape();
        sh.beginShape(QUAD_STRIP);
        sh.noStroke();
        for (int i = 0; i <= detail; i++) {
            float angle = TWO_PI / detail;
            float x = sin(i * angle);
            float z = cos(i * angle);
            float u = (float) i / detail;
            sh.normal(x, 0, z);

            sh.fill(255, 0, 255);
            sh.vertex(x * r, -h / 2, z * r, u, 0);

            sh.fill(255, 255, 0);
            sh.vertex(x * r, +h / 2, z * r, u, 1);
        }
        sh.endShape();
        return sh;
    }

    @Override
    public void draw() {
        background(255);

        translate(width/2, height/2);

        shader(shaderObj);

        texture(img);
        rotateY(angle);
        angle+= 0.1f;
        shape(cylinder);
//filter();
        /*beginShape(QUADS);

//        fill(255, 0, 255);
        vertex(-100, +100, 0, 0, 1);
        vertex(+100, +100, 0, 1, 1);
//        fill(0, 255, 255);
        vertex(+100, -100, 0, 1, 0);
        vertex(-100, -100, 0, 0, 0);

        endShape();*/
    }
}
