package me.dawars.szakkor4;

import processing.core.PApplet;
import processing.opengl.PShader;

public class Raytracing extends PApplet {
    public static void main(String[] args) {
        PApplet.main(Raytracing.class);
    }


    @Override
    public void settings() {
        size(640, 360, P3D);
    }


    PShader Shrtoy;

    public void setup() {
        noStroke();
        Shrtoy = loadShader("szakkor4/shadertoy.glsl");
        Shrtoy.set("iResolution", (float) width, (float) height);
    }

    public void draw() {
        background(0);
        Shrtoy.set("iGlobalTime", millis() / 1000f);
        filter(Shrtoy);
    }
}
