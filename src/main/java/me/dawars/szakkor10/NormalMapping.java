package me.dawars.szakkor10;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.opengl.PShader;

public class NormalMapping extends PApplet {
    private PShape radioShape;
    private PImage diffTexture;
    private PShader shader;
    private float angle;
    private PImage normalTexture;
    private boolean rotate;

    public static void main(String[] args) {
        PApplet.main(NormalMapping.class);
    }

    @Override
    public void settings() {
        size(720, 720, P3D);
        pixelDensity(2);
    }

    @Override
    public void setup() {
        textureMode(NORMAL);

        diffTexture = loadImage("szakkor10/rocks.png");
        normalTexture = loadImage("szakkor10/rocks_normal.png");
        shader = loadShader("szakkor10/frag.glsl", "szakkor10/vert.glsl");
        shader.set("normalTexture", normalTexture);
    }

    @Override
    public void draw() {
        background(0);
        resetShader();

        camera(0, 30, 300, 0, 0, 0, 0, -1, 0);
        beginCamera();
        if (rotate) {
            rotateY(PI / 4 * sin(angle));
            rotateX(PI / 4 * cos(angle));
        }
        endCamera();

        float x = 50 * cos(angle) /* cos(angle)*/;
        float y = 50 * sin(angle) /* sin(angle)*/;
        float z = 50 /* sin(angle)*/;


        shader(shader);
        pointLight(255, 255, 255, x, y, z);


        noStroke();
        beginShape(QUADS);

        texture(diffTexture);

        attrib("tangent", 1f, 0f, 0f);
        attrib("bitangent", 0f, 1f, 0f);

        normal(0, 0, 1);
//        fill(50, 50, 200);
        vertex(-100, +100, 0, 1);
        vertex(+100, +100, 1, 1);
//        fill(200, 50, 50);
        vertex(+100, -100, 1, 0);
        vertex(-100, -100, 0, 0);
        endShape();

        // light visualization
        resetShader();
        strokeWeight(5);
        stroke(255);
        point(x, y, z);

        angle += 0.01f;
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (event.getKey() == '1') {
            rotate = false;
        } else if (event.getKey() == '2') {
            rotate = true;
        }
    }
}

