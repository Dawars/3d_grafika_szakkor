package me.dawars.szakkor8;

import processing.core.PApplet;
import processing.opengl.PShader;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

public class Metaball extends PApplet {

    private List<Blobs> blobs;
    private PShader voxelShader;

    public static void main(String[] args) {
        PApplet.main(Metaball.class);
    }

    @Override
    public void settings() {
        size(1280, 720, P3D);
        pixelDensity(2);
    }

    @Override
    public void setup() {
        blobs = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            blobs.add(new Blobs());
        }

        voxelShader = loadShader("szakkor8/frag.glsl", "szakkor8/vert.glsl");

    }

    final float scale = 150;

    @Override
    public void draw() {
        background(127);

        camera(0, 0, 300, 0, 0, 0, 0, 1, 0);

        pointLight(255, 255, 255, 400, 400, 400);
        fill(0, 127, 255);

        for (Blobs blob : blobs) {
            blob.update(0.1f);
            pushMatrix();

            float x = blob.x * scale / (Blobs.maxX - Blobs.minX) - scale / 2;
            float y = blob.y * scale / (Blobs.maxX - Blobs.minX) - scale / 2;
            float z = blob.z * scale / (Blobs.maxX - Blobs.minX) - scale / 2;
            translate(x, y, z);
            strokeWeight(2);
            renderAxis(10);

            popMatrix();
        }

        float[][][] field = Blobs.fieldStrength(blobs);

        final float THRESHOLD = 1;

        beginShape(QUADS);

        translate(-scale / 2, -scale / 2, -scale / 2);
        scale(scale / (Blobs.maxX - Blobs.minX),
                scale / (Blobs.maxY - Blobs.minY),
                scale / (Blobs.maxZ - Blobs.minZ));

        shader(voxelShader);
//        resetShader();
        for (int i = Blobs.minX; i <= Blobs.maxX; i++)
            for (int j = Blobs.minY; j <= Blobs.maxY; j++)
                for (int k = Blobs.minZ; k <= Blobs.maxZ; k++) {
                    float strength = field[i][j][k];
//                    float x = i * scale / (Blobs.maxX - Blobs.minX) - scale / 2;
//                    float y = j * scale / (Blobs.maxX - Blobs.minX) - scale / 2;
//                    float z = k * scale / (Blobs.maxX - Blobs.minX) - scale / 2;


                   /* textSize(1f);
                    fill(strength * 255);
                    if (strength > 1f)
                        text(strength, x, y, z);*/
                    if (field[i][j][k] >= THRESHOLD) { // Cell is in the blob

//                        strokeWeight(0.1f);
//                        stroke(strength * 255);
//                        point(i, j, k);

                        if (j == 15 || field[i][j + 1][k] < THRESHOLD) { // neighbour is outside (or at space bound)
                            normal(0, 1, 0);
                            vertex((i), (j + 1), (k));
                            vertex((i), (j + 1), (k + 1));
                            vertex((i + 1), (j + 1), (k + 1));
                            vertex((i + 1), (j + 1), (k));

                        }

                        if (j == 0 || (int) field[i][j - 1][k] < THRESHOLD) {
                            normal(0, -1, 0);
                            vertex((i), (j), (k + 1));
                            vertex((i), (j), (k));
                            vertex((i + 1), (j), (k));
                            vertex((i + 1), (j), (k + 1));
                        }

                        if (k == 15 || (int) field[i][j][k + 1] < THRESHOLD) {
                            normal(0, 0, 1);
                            vertex((i), (j + 1), (k + 1));
                            vertex((i), (j), (k + 1));
                            vertex((i + 1), (j), (k + 1));
                            vertex((i + 1), (j + 1), (k + 1));

                        }
                        if (k == 0 || (int) field[i][j][k - 1] < THRESHOLD) {
                            normal(0, 0, -1);
                            vertex((i + 1), (j + 1), (k));
                            vertex((i + 1), (j), (k));
                            vertex((i), (j), (k));
                            vertex((i), (j + 1), (k));
                        }

                        if (i == 15 || (int) field[i + 1][j][k] < THRESHOLD) {
                            normal(1, 0, 0);
                            vertex((i + 1), (j + 1), (k + 1));
                            vertex((i + 1), (j), (k + 1));
                            vertex((i + 1), (j), (k));
                            vertex((i + 1), (j + 1), (k));

                        }

                        if (i == 0 || (int) field[i - 1][j][k] < THRESHOLD) {
                            normal(-1, 0, 0);
                            vertex((i), (j), (k + 1));
                            vertex((i), (j + 1), (k + 1));
                            vertex((i), (j + 1), (k));
                            vertex((i), (j), (k));
                        }
                    }
                }
        endShape();
    }

    private void renderAxis(int len) {
        stroke(255, 0, 0);
        line(0, 0, 0, len, 0, 0);
        stroke(0, 255, 0);
        line(0, 0, 0, 0, len, 0);
        stroke(0, 0, 255);
        line(0, 0, 0, 0, 0, len);
        noStroke();
    }
}
