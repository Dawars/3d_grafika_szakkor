package me.dawars.szakkor8;

import processing.core.PApplet;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

public class Metaball extends PApplet {

    private List<Blobs> blobs;

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
    }

    final float scale = 150;

    @Override
    public void draw() {
        background(127);

        camera(0, 0, 200, 0, 0, 0, 0, 1, 0);


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

        fill(255, 0, 255);
        beginShape(QUADS);

        for (int i = Blobs.minX; i <= Blobs.maxX; i++)
            for (int j = Blobs.minY; j <= Blobs.maxY; j++)
                for (int k = Blobs.minZ; k <= Blobs.maxZ; k++) {
                    float strength = field[i][j][k];
                    float x = i * scale / (Blobs.maxX - Blobs.minX) - scale / 2;
                    float y = j * scale / (Blobs.maxX - Blobs.minX) - scale / 2;
                    float z = k * scale / (Blobs.maxX - Blobs.minX) - scale / 2;

                    strokeWeight(2f);
                    stroke(strength * 255);
                    if (strength > 0.7f)
                        point(x, y, z);

//                    textSize(1f);
//                    fill(strength * 255);
//                    if (strength > 0.02f)
//                        text(strength, x, y, z);
/*
                    if (field[i][j][k] >= THRESHOLD) { // Cell is in the blob

                        if (j == 15 || field[i][j + 1][k] < THRESHOLD) { // neighbour is outside (or at space bound)
                            vertex((i) / 16F, (j + 1) / 16F, (k) / 16F);
                            vertex((i) / 16F, (j + 1) / 16F, (k + 1) / 16F);
                            vertex((i + 1) / 16F, (j + 1) / 16F, (k + 1) / 16F);
                            vertex((i + 1) / 16F, (j + 1) / 16F, (k) / 16F);

                        }

                        if (j == 0 || (int) field[i][j - 1][k] < THRESHOLD) {
                            vertex((i) / 16F, (j) / 16F, (k + 1) / 16F);
                            vertex((i) / 16F, (j) / 16F, (k) / 16F);
                            vertex((i + 1) / 16F, (j) / 16F, (k) / 16F);
                            vertex((i + 1) / 16F, (j) / 16F, (k + 1) / 16F);
                        }

                        if (k == 15 || (int) field[i][j][k + 1] < THRESHOLD) {
                            vertex((i) / 16F, (j + 1) / 16F, (k + 1) / 16F);
                            vertex((i) / 16F, (j) / 16F, (k + 1) / 16F);
                            vertex((i + 1) / 16F, (j) / 16F, (k + 1) / 16F);
                            vertex((i + 1) / 16F, (j + 1) / 16F, (k + 1) / 16F);

                        }
                        if (k == 0 || (int) field[i][j][k - 1] < THRESHOLD) {
                            vertex((i + 1) / 16F, (j + 1) / 16F, (k) / 16F);
                            vertex((i + 1) / 16F, (j) / 16F, (k) / 16F);
                            vertex((i) / 16F, (j) / 16F, (k) / 16F);
                            vertex((i) / 16F, (j + 1) / 16F, (k) / 16F);
                        }

                        if (i == 15 || (int) field[i + 1][j][k] < THRESHOLD) {
                            vertex((i + 1) / 16F, (j + 1) / 16F, (k + 1) / 16F);
                            vertex((i + 1) / 16F, (j) / 16F, (k + 1) / 16F);
                            vertex((i + 1) / 16F, (j) / 16F, (k) / 16F);
                            vertex((i + 1) / 16F, (j + 1) / 16F, (k) / 16F);

                        }

                        if (i == 0 || (int) field[i - 1][j][k] < THRESHOLD) {
                            vertex((i) / 16F, (j) / 16F, (k + 1) / 16F);
                            vertex((i) / 16F, (j + 1) / 16F, (k + 1) / 16F);
                            vertex((i) / 16F, (j + 1) / 16F, (k) / 16F);
                            vertex((i) / 16F, (j) / 16F, (k) / 16F);
                        }
                    }*/
                }
        endShape();
    }

    public static float[][][] fieldStrength(List<Blobs> blobs) {
        float result[][][] = new float[16][16][16];

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    for (int i = 0; i < blobs.size(); i++) {
                        float xDist = blobs.get(i).x - x;
                        float yDist = blobs.get(i).y - y;
                        float zDist = blobs.get(i).z - z;
                        float r2 = xDist * xDist + yDist * yDist + zDist * zDist; //distance square
//                        result[x][y][z] += 100 * Blobs.metaball(r2);
                    }
                }
            }
        }

        return result;
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
