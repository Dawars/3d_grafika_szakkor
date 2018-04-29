package me.dawars.szakkor3;

import processing.core.PApplet;
import processing.core.PShape;

public class MVPScene extends PApplet {

    public static void main(String[] args) {
        PApplet.main(MVPScene.class);
    }

    @Override
    public void settings() {
        size(400, 400, P3D);
    }


    @Override
    public void setup() {

    }

    @Override
    public void draw() {
        background(100);
        translate(width/2, height/2);

        box(100);
        sphere(100);
    }
}
