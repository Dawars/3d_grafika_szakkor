package me.dawars.szakkor2;

import processing.core.PApplet;
import processing.core.PShape;

public class DrawShapes extends PApplet {
    private float angle;

    public static void main(String[] args) {
        PApplet.main(DrawShapes.class);
    }

    @Override
    public void settings() {
        size(400, 400, P3D);
    }

    PShape triangle, sphere, torus;

    @Override
    public void setup() {

        triangle = createTriangle();
        sphere = createSphere(100, 18);
        torus = createTorus(100, 30, 18, 36);
    }

    @Override
    public void draw() {
        background(0);

        translate(width / 2, height / 2);
        rotateX(-PI / 6);
        rotateY(angle);

        shape(sphere);

        angle += 0.01f;
    }

    private PShape createTriangle() {
        PShape shape = createShape();

        shape.beginShape(TRIANGLES);
        shape.fill(0, 0, 255);
        shape.vertex(0, -100);
        shape.fill(0, 255, 0);
        shape.vertex(100, 100);
        shape.fill(255, 0, 0);
        shape.vertex(-100, 100);
        shape.endShape();

        return shape;
    }

    private PShape createSphere(float radius, int res) {
        PShape shape = createShape();

        shape.beginShape(POINTS);
        shape.fill(0, 0, 255);
        shape.stroke(0, 0, 255);
        shape.strokeWeight(4);

        for (int i = 0; i <= 180; i += res) {
            // mantle

            float r = radius * sin(radians(i));
            float z = radius * cos(radians(i));

            for (int j = 0; j <= 360; j += res) {
                // circle

                float x = r * cos(radians(j));
                float y = r * sin(radians(j));

                shape.vertex(x, z, y);


            }

        }


        shape.endShape();

        return shape;
    }

    private PShape createTorus(float radiusBig, float radiusSmall, int resBig, int resSmall) {
        PShape shape = createShape();

        shape.beginShape(POINTS);
        shape.stroke(0, 0, 255);
        shape.strokeWeight(4);

        shape.fill(0, 0, 255);

        for (int i = 0; i <= 360; i += resBig) {

            for (int j = 0; j <= 360; j += resSmall) {
                // small circle

                float x = radiusSmall * cos(radians(j)) + radiusBig;
                float y = radiusSmall * sin(radians(j));


                shape.vertex(x * cos(radians(i)), y, x * sin(radians(i)));

            }
        }


        shape.endShape();

        return shape;
    }
}
