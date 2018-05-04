package me.dawars.szakkor8;

import processing.core.PApplet;
import processing.core.PShape;
import processing.opengl.PShader;

import java.util.ArrayList;
import java.util.List;

public class Metaball extends PApplet {

    public static void main(String[] args) {
        PApplet.main(Metaball.class);
    }

    @Override
    public void settings() {
        size(1280, 720, P3D);
        pixelDensity(2);
    }

    private List<Blobs> blobs;
    private PShader voxelShader;
    private float angle;

    // params
    private int resolution = 50;
    private float isolation = 80;
    private int numBlobs = 20;

    private int size, size2, size3;
    float halfsize;
    private float delta;
    private int yd, zd;
    private float[] field;
    private float[] vlist, nlist;

    private float[] normal_cache;


    @Override
    public void setup() {
        //size of field

        this.size = resolution;
        this.size2 = this.size * this.size; // size squard
        this.size3 = this.size2 * this.size; // size cubed
        this.halfsize = this.size / 2f; // 25

        this.field = new float[this.size3]; // the grid 50^3
        this.normal_cache = new float[this.size3 * 3];

        Blobs.maxX = size;
        Blobs.maxY = size;
        Blobs.maxZ = size;

        // deltas

        this.delta = 2.0f / this.size; // 0.04
        this.yd = this.size; // index offset in field
        this.zd = this.size2;

        // temp buffers used in polygonize

        this.vlist = new float[12 * 3]; // max 12 edges vertices in a cube
        this.nlist = new float[12 * 3]; // vertex normals

        blobs = new ArrayList<>();

        for (int i = 0; i < numBlobs; i++) {
            blobs.add(new Blobs());
        }


        voxelShader = loadShader("szakkor8/frag.glsl", "szakkor8/vert.glsl");

    }

    final float scale = 100;

    @Override
    public void draw() {
        // reset values


        background(127);

        camera(0, 0, -200, 0, 0, 0, 0, 1, 0);

        pointLight(255, 255, 255, 500, -500, -500);
        fill(0, 127, 255);

        rotateY(angle);


        for (Blobs blob : blobs) {
            blob.update(0.2f);
            pushMatrix();

            float x = blob.x * scale / (Blobs.maxX - Blobs.minX) - scale / 2;
            float y = blob.y * scale / (Blobs.maxX - Blobs.minX) - scale / 2;
            float z = blob.z * scale / (Blobs.maxX - Blobs.minX) - scale / 2;
            translate(x, y, z);
            strokeWeight(2);
            renderAxis(10);

            popMatrix();
        }

        calcField(blobs);

        final float THRESHOLD = 5f;

        PShape sh = renderMarchingCubes(THRESHOLD);


        /*translate(-scale / 2, -scale / 2, -scale / 2);
        scale(scale / (Blobs.maxX - Blobs.minX),
                scale / (Blobs.maxY - Blobs.minY),
                scale / (Blobs.maxZ - Blobs.minZ));
*/

        scale(scale / 2);


        shader(voxelShader);
        shape(sh);
        angle += 0.01;
    }

    private void calcField(List<Blobs> blobs) {
        int x, y, z, y_offset, z_offset;

        for (z = 0; z < this.size; z++) {
            z_offset = this.size2 * z;

            for (y = 0; y < this.size; y++) {
                y_offset = z_offset + this.size * y;

                for (x = 0; x < this.size; x++) {
                    // reset vars
                    field[y_offset + x] = 0;
                    normal_cache[y_offset + x] = 0;
                    normal_cache[y_offset + x + 1] = 0;
                    normal_cache[y_offset + x + 2] = 0;


                    for (Blobs blob : blobs) {
                        float xDist = blob.x - x;
                        float yDist = blob.y - y;
                        float zDist = blob.z - z;
                        float r = xDist * xDist + yDist * yDist + zDist * zDist; //distance square

                        int strength = blob.strength;
                        field[y_offset + x] += strength / r;

                    }
                }

            }

        }

    }

    private PShape renderVoxelBlobs(float[][][] field, float threshold) {
        PShape sh = createShape();
        sh.beginShape(QUADS);

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
                    if (field[i][j][k] >= threshold) { // Cell is in the blob

//                        strokeWeight(0.1f);
//                        stroke(strength * 255);
//                        point(i, j, k);

                        if (j == 15 || field[i][j + 1][k] < threshold) { // neighbour is outside (or at space bound)
                            sh.normal(0, 1, 0);
                            sh.vertex((i), (j + 1), (k));
                            sh.vertex((i), (j + 1), (k + 1));
                            sh.vertex((i + 1), (j + 1), (k + 1));
                            sh.vertex((i + 1), (j + 1), (k));

                        }

                        if (j == 0 || (int) field[i][j - 1][k] < threshold) {
                            sh.normal(0, -1, 0);
                            sh.vertex((i), (j), (k + 1));
                            sh.vertex((i), (j), (k));
                            sh.vertex((i + 1), (j), (k));
                            sh.vertex((i + 1), (j), (k + 1));
                        }

                        if (k == 15 || (int) field[i][j][k + 1] < threshold) {
                            sh.normal(0, 0, 1);
                            sh.vertex((i), (j + 1), (k + 1));
                            sh.vertex((i), (j), (k + 1));
                            sh.vertex((i + 1), (j), (k + 1));
                            sh.vertex((i + 1), (j + 1), (k + 1));

                        }
                        if (k == 0 || (int) field[i][j][k - 1] < threshold) {
                            sh.normal(0, 0, -1);
                            sh.vertex((i + 1), (j + 1), (k));
                            sh.vertex((i + 1), (j), (k));
                            sh.vertex((i), (j), (k));
                            sh.vertex((i), (j + 1), (k));
                        }

                        if (i == 15 || (int) field[i + 1][j][k] < threshold) {
                            sh.normal(1, 0, 0);
                            sh.vertex((i + 1), (j + 1), (k + 1));
                            sh.vertex((i + 1), (j), (k + 1));
                            sh.vertex((i + 1), (j), (k));
                            sh.vertex((i + 1), (j + 1), (k));

                        }

                        if (i == 0 || (int) field[i - 1][j][k] < threshold) {
                            sh.normal(-1, 0, 0);
                            sh.vertex((i), (j), (k + 1));
                            sh.vertex((i), (j + 1), (k + 1));
                            sh.vertex((i), (j + 1), (k));
                            sh.vertex((i), (j), (k));
                        }
                    }
                }
        sh.endShape();

        return sh;
    }


    private PShape renderMarchingCubes(float isolevel) {

        PShape sh = createShape();
        sh.beginShape(TRIANGLES);

        int smin2 = this.size - 2; // bound check
        for (int z = 1; z < smin2; z++) {

            int z_offset = this.size2 * z;
            float fz = (z - this.halfsize) / this.halfsize; //+ 1

            for (int y = 1; y < smin2; y++) {

                int y_offset = z_offset + this.size * y;
                float fy = (y - this.halfsize) / this.halfsize; //+ 1 [-1, 1]

                for (int x = 1; x < smin2; x++) {

                    float fx = (x - this.halfsize) / this.halfsize; //+ 1
                    int q = y_offset + x;

                    pushMatrix();
                    scale(scale);
                    strokeWeight(1f);
//                        stroke(strength * 255);
                    point(fx, fy, fz);

                    popMatrix();

                    // cache indices of cubes
                    int q1 = q + 1; // +x
                    int qy = q + this.yd; // +y
                    int qz = q + this.zd; // +z
                    int q1y = q1 + this.yd; // +x +y
                    int q1z = q1 + this.zd; // +x +z
                    int qyz = q + this.yd + this.zd; // +y +z
                    int q1yz = q1 + this.yd + this.zd; // +x +y +z

                    int cubeindex = 0;
                    float field0 = this.field[q];
                    float field1 = this.field[q1];
                    float field2 = this.field[qy];
                    float field3 = this.field[q1y];
                    float field4 = this.field[qz];
                    float field5 = this.field[q1z];
                    float field6 = this.field[qyz];
                    float field7 = this.field[q1yz];

                    if (field0 < isolevel) cubeindex |= 1;
                    if (field1 < isolevel) cubeindex |= 2;
                    if (field2 < isolevel) cubeindex |= 8;
                    if (field3 < isolevel) cubeindex |= 4;
                    if (field4 < isolevel) cubeindex |= 16;
                    if (field5 < isolevel) cubeindex |= 32;
                    if (field6 < isolevel) cubeindex |= 128;
                    if (field7 < isolevel) cubeindex |= 64;

                    /* Cube is entirely in/out of the surface */
                    int bits = edgeTable[cubeindex];
                    if (bits == 0)
                        continue;

                    float d = this.delta;
                    float fx2 = fx + d;
                    float fy2 = fy + d;
                    float fz2 = fz + d;

                    // top of the cube

                    if ((bits & 1) != 0) {

                        this.compNorm(x, y, z);
                        this.compNorm(x + 1, y, z);
                        this.VIntX(q * 3, this.vlist, this.nlist, 0, isolevel, fx, fy, fz, field0, field1);

                    }

                    if ((bits & 2) != 0) {

                        this.compNorm(x + 1, y, z);
                        this.compNorm(x + 1, y + 1, z);
                        this.VIntY(q1 * 3, this.vlist, this.nlist, 3, isolevel, fx2, fy, fz, field1, field3);

                    }

                    if ((bits & 4) != 0) {

                        this.compNorm(x, y + 1, z);
                        this.compNorm(x + 1, y + 1, z);
                        this.VIntX(qy * 3, this.vlist, this.nlist, 6, isolevel, fx, fy2, fz, field2, field3);

                    }

                    if ((bits & 8) != 0) {

                        this.compNorm(x, y, z);
                        this.compNorm(x, y + 1, z);
                        this.VIntY(q * 3, this.vlist, this.nlist, 9, isolevel, fx, fy, fz, field0, field2);

                    }

                    // bottom of the cube

                    if ((bits & 16) != 0) {

                        this.compNorm(x, y, z + 1);
                        this.compNorm(x + 1, y, z + 1);
                        this.VIntX(qz * 3, this.vlist, this.nlist, 12, isolevel, fx, fy, fz2, field4, field5);

                    }

                    if ((bits & 32) != 0) {

                        this.compNorm(x + 1, y, z + 1);
                        this.compNorm(x + 1, y + 1, z + 1);
                        this.VIntY(q1z * 3, this.vlist, this.nlist, 15, isolevel, fx2, fy, fz2, field5, field7);

                    }

                    if ((bits & 64) != 0) {

                        this.compNorm(x, y + 1, z + 1);
                        this.compNorm(x + 1, y + 1, z + 1);
                        this.VIntX(qyz * 3, this.vlist, this.nlist, 18, isolevel, fx, fy2, fz2, field6, field7);

                    }

                    if ((bits & 128) != 0) {

                        this.compNorm(x, y, z + 1);
                        this.compNorm(x, y + 1, z + 1);
                        this.VIntY(qz * 3, this.vlist, this.nlist, 21, isolevel, fx, fy, fz2, field4, field6);

                    }

                    // vertical lines of the cube

                    if ((bits & 256) != 0) {

                        this.compNorm(x, y, z);
                        this.compNorm(x, y, z + 1);
                        this.VIntZ(q * 3, this.vlist, this.nlist, 24, isolevel, fx, fy, fz, field0, field4);

                    }

                    if ((bits & 512) != 0) {

                        this.compNorm(x + 1, y, z);
                        this.compNorm(x + 1, y, z + 1);
                        this.VIntZ(q1 * 3, this.vlist, this.nlist, 27, isolevel, fx2, fy, fz, field1, field5);

                    }

                    if ((bits & 1024) != 0) {

                        this.compNorm(x + 1, y + 1, z);
                        this.compNorm(x + 1, y + 1, z + 1);
                        this.VIntZ(q1y * 3, this.vlist, this.nlist, 30, isolevel, fx2, fy2, fz, field3, field7);

                    }

                    if ((bits & 2048) != 0) {

                        this.compNorm(x, y + 1, z);
                        this.compNorm(x, y + 1, z + 1);
                        this.VIntZ(qy * 3, this.vlist, this.nlist, 33, isolevel, fx, fy2, fz, field2, field6);

                    }

                    cubeindex <<= 4; // re-purpose cubeindex into an offset into triTable

                    int o1, o2, o3;
                    int i = 0;

                    // here is where triangles are created

                    while (triTable[cubeindex + i] != -1) {

                        o1 = cubeindex + i;
                        o2 = o1 + 1;
                        o3 = o1 + 2;

                        this.posnormtriv(sh, this.vlist, this.nlist,
                                3 * triTable[o1],
                                3 * triTable[o2],
                                3 * triTable[o3]);

                        i += 3;
                    }


                }
            }
        }

        sh.endShape();
        return sh;

    }


    /////////////////////////////////////
    // Immediate render mode simulator
    /////////////////////////////////////

    private void posnormtriv(PShape sh, float[] pos, float[] norm, int o1, int o2, int o3) {

        sh.normal(norm[o1], norm[o1 + 1], norm[o1 + 2]);
        sh.vertex(pos[o1], pos[o1 + 1], pos[o1 + 2]);

        sh.normal(norm[o2], norm[o2 + 1], norm[o2 + 2]);
        sh.vertex(pos[o2], pos[o2 + 1], pos[o2 + 2]);

        sh.normal(norm[o3], norm[o3 + 1], norm[o3 + 2]);
        sh.vertex(pos[o3], pos[o3 + 1], pos[o3 + 2]);
    }
/*
    private void compNorm(int q) {
        int q3 = q * 3;

        if (this.normal_cache[q3] == 0.0) {

            for (Blobs blob : blobs) {
                // calc normal from field strength at grid points and cache
                this.normal_cache[q3] += this.field[q - 1] - this.field[q + 1];
                this.normal_cache[q3 + 1] += this.field[q - this.yd] - this.field[q + this.yd];
                this.normal_cache[q3 + 2] += this.field[q - this.zd] - this.field[q + this.zd];
            }

        }

    }*/

    private void compNorm(int x, int y, int z) {
        int z_offset = this.size2 * z;
        int y_offset = z_offset + this.size * y;

        int q = y_offset + x;
        int q3 = q * 3;

        if (this.normal_cache[q3] == 0.0 && this.normal_cache[q3 + 1] == 0.0 && this.normal_cache[q3 + 2] == 0.0) {

            for (Blobs blob : blobs) {
                float xDist = blob.x - x;
                float yDist = blob.y - y;
                float zDist = blob.z - z;
                float r = xDist * xDist + yDist * yDist + zDist * zDist; //distance square

                float f = 1 / (r * r);

                // calc normal from field strength at grid points and cache
                this.normal_cache[q3] -= f * xDist;
                this.normal_cache[q3 + 1] -= f * yDist;
                this.normal_cache[q3 + 2] -= f * zDist;
            }
        }
    }

    /*
       Linearly interpolate the position where an isosurface cuts
       an edge between two vertices, each with their own scalar value
    */
    private void VIntX(int q, float[] pout, float[] nout, int offset, float isolevel, float x, float y, float z, float valp1, float valp2) {
        float mu = (isolevel - valp1) / (valp2 - valp1);
        float[] nc = this.normal_cache;

        pout[offset] = x + mu * this.delta;
        pout[offset + 1] = y;
        pout[offset + 2] = z;

        nout[offset] = lerp(nc[q], nc[q + 3], mu);
        nout[offset + 1] = lerp(nc[q + 1], nc[q + 4], mu);
        nout[offset + 2] = lerp(nc[q + 2], nc[q + 5], mu);
    }


    private void VIntY(int q, float[] pout, float[] nout, int offset, float isolevel, float x, float y, float z, float valp1, float valp2) {
        float mu = (isolevel - valp1) / (valp2 - valp1);
        float[] nc = this.normal_cache;

        pout[offset] = x;
        pout[offset + 1] = y + mu * this.delta;
        pout[offset + 2] = z;

        int q2 = q + this.yd * 3;

        nout[offset] = lerp(nc[q], nc[q2], mu);
        nout[offset + 1] = lerp(nc[q + 1], nc[q2 + 1], mu);
        nout[offset + 2] = lerp(nc[q + 2], nc[q2 + 2], mu);
    }

    private void VIntZ(int q, float[] pout, float[] nout, int offset, float isolevel, float x, float y, float z, float valp1, float valp2) {
        float mu = (isolevel - valp1) / (valp2 - valp1);
        float[] nc = this.normal_cache;

        pout[offset] = x;
        pout[offset + 1] = y;
        pout[offset + 2] = z + mu * this.delta;

        int q2 = q + this.zd * 3;

        // lerp between cached normals at grid points
        nout[offset] = lerp(nc[q], nc[q2], mu);
        nout[offset + 1] = lerp(nc[q + 1], nc[q2 + 1], mu);
        nout[offset + 2] = lerp(nc[q + 2], nc[q2 + 2], mu);
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


    final int[] edgeTable = new int[]{
            0x0, 0x109, 0x203, 0x30a, 0x406, 0x50f, 0x605, 0x70c,
            0x80c, 0x905, 0xa0f, 0xb06, 0xc0a, 0xd03, 0xe09, 0xf00,
            0x190, 0x99, 0x393, 0x29a, 0x596, 0x49f, 0x795, 0x69c,
            0x99c, 0x895, 0xb9f, 0xa96, 0xd9a, 0xc93, 0xf99, 0xe90,
            0x230, 0x339, 0x33, 0x13a, 0x636, 0x73f, 0x435, 0x53c,
            0xa3c, 0xb35, 0x83f, 0x936, 0xe3a, 0xf33, 0xc39, 0xd30,
            0x3a0, 0x2a9, 0x1a3, 0xaa, 0x7a6, 0x6af, 0x5a5, 0x4ac,
            0xbac, 0xaa5, 0x9af, 0x8a6, 0xfaa, 0xea3, 0xda9, 0xca0,
            0x460, 0x569, 0x663, 0x76a, 0x66, 0x16f, 0x265, 0x36c,
            0xc6c, 0xd65, 0xe6f, 0xf66, 0x86a, 0x963, 0xa69, 0xb60,
            0x5f0, 0x4f9, 0x7f3, 0x6fa, 0x1f6, 0xff, 0x3f5, 0x2fc,
            0xdfc, 0xcf5, 0xfff, 0xef6, 0x9fa, 0x8f3, 0xbf9, 0xaf0,
            0x650, 0x759, 0x453, 0x55a, 0x256, 0x35f, 0x55, 0x15c,
            0xe5c, 0xf55, 0xc5f, 0xd56, 0xa5a, 0xb53, 0x859, 0x950,
            0x7c0, 0x6c9, 0x5c3, 0x4ca, 0x3c6, 0x2cf, 0x1c5, 0xcc,
            0xfcc, 0xec5, 0xdcf, 0xcc6, 0xbca, 0xac3, 0x9c9, 0x8c0,
            0x8c0, 0x9c9, 0xac3, 0xbca, 0xcc6, 0xdcf, 0xec5, 0xfcc,
            0xcc, 0x1c5, 0x2cf, 0x3c6, 0x4ca, 0x5c3, 0x6c9, 0x7c0,
            0x950, 0x859, 0xb53, 0xa5a, 0xd56, 0xc5f, 0xf55, 0xe5c,
            0x15c, 0x55, 0x35f, 0x256, 0x55a, 0x453, 0x759, 0x650,
            0xaf0, 0xbf9, 0x8f3, 0x9fa, 0xef6, 0xfff, 0xcf5, 0xdfc,
            0x2fc, 0x3f5, 0xff, 0x1f6, 0x6fa, 0x7f3, 0x4f9, 0x5f0,
            0xb60, 0xa69, 0x963, 0x86a, 0xf66, 0xe6f, 0xd65, 0xc6c,
            0x36c, 0x265, 0x16f, 0x66, 0x76a, 0x663, 0x569, 0x460,
            0xca0, 0xda9, 0xea3, 0xfaa, 0x8a6, 0x9af, 0xaa5, 0xbac,
            0x4ac, 0x5a5, 0x6af, 0x7a6, 0xaa, 0x1a3, 0x2a9, 0x3a0,
            0xd30, 0xc39, 0xf33, 0xe3a, 0x936, 0x83f, 0xb35, 0xa3c,
            0x53c, 0x435, 0x73f, 0x636, 0x13a, 0x33, 0x339, 0x230,
            0xe90, 0xf99, 0xc93, 0xd9a, 0xa96, 0xb9f, 0x895, 0x99c,
            0x69c, 0x795, 0x49f, 0x596, 0x29a, 0x393, 0x99, 0x190,
            0xf00, 0xe09, 0xd03, 0xc0a, 0xb06, 0xa0f, 0x905, 0x80c,
            0x70c, 0x605, 0x50f, 0x406, 0x30a, 0x203, 0x109, 0x0};


    final int[] triTable =//[256][16]
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    0, 8, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    0, 1, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    1, 8, 3, 9, 8, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    1, 2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    0, 8, 3, 1, 2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    9, 2, 10, 0, 2, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    2, 8, 3, 2, 10, 8, 10, 9, 8, -1, -1, -1, -1, -1, -1, -1,
                    3, 11, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    0, 11, 2, 8, 11, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    1, 9, 0, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    1, 11, 2, 1, 9, 11, 9, 8, 11, -1, -1, -1, -1, -1, -1, -1,
                    3, 10, 1, 11, 10, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    0, 10, 1, 0, 8, 10, 8, 11, 10, -1, -1, -1, -1, -1, -1, -1,
                    3, 9, 0, 3, 11, 9, 11, 10, 9, -1, -1, -1, -1, -1, -1, -1,
                    9, 8, 10, 10, 8, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    4, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    4, 3, 0, 7, 3, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    0, 1, 9, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    4, 1, 9, 4, 7, 1, 7, 3, 1, -1, -1, -1, -1, -1, -1, -1,
                    1, 2, 10, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    3, 4, 7, 3, 0, 4, 1, 2, 10, -1, -1, -1, -1, -1, -1, -1,
                    9, 2, 10, 9, 0, 2, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1,
                    2, 10, 9, 2, 9, 7, 2, 7, 3, 7, 9, 4, -1, -1, -1, -1,
                    8, 4, 7, 3, 11, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    11, 4, 7, 11, 2, 4, 2, 0, 4, -1, -1, -1, -1, -1, -1, -1,
                    9, 0, 1, 8, 4, 7, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1,
                    4, 7, 11, 9, 4, 11, 9, 11, 2, 9, 2, 1, -1, -1, -1, -1,
                    3, 10, 1, 3, 11, 10, 7, 8, 4, -1, -1, -1, -1, -1, -1, -1,
                    1, 11, 10, 1, 4, 11, 1, 0, 4, 7, 11, 4, -1, -1, -1, -1,
                    4, 7, 8, 9, 0, 11, 9, 11, 10, 11, 0, 3, -1, -1, -1, -1,
                    4, 7, 11, 4, 11, 9, 9, 11, 10, -1, -1, -1, -1, -1, -1, -1,
                    9, 5, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    9, 5, 4, 0, 8, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    0, 5, 4, 1, 5, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    8, 5, 4, 8, 3, 5, 3, 1, 5, -1, -1, -1, -1, -1, -1, -1,
                    1, 2, 10, 9, 5, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    3, 0, 8, 1, 2, 10, 4, 9, 5, -1, -1, -1, -1, -1, -1, -1,
                    5, 2, 10, 5, 4, 2, 4, 0, 2, -1, -1, -1, -1, -1, -1, -1,
                    2, 10, 5, 3, 2, 5, 3, 5, 4, 3, 4, 8, -1, -1, -1, -1,
                    9, 5, 4, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    0, 11, 2, 0, 8, 11, 4, 9, 5, -1, -1, -1, -1, -1, -1, -1,
                    0, 5, 4, 0, 1, 5, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1,
                    2, 1, 5, 2, 5, 8, 2, 8, 11, 4, 8, 5, -1, -1, -1, -1,
                    10, 3, 11, 10, 1, 3, 9, 5, 4, -1, -1, -1, -1, -1, -1, -1,
                    4, 9, 5, 0, 8, 1, 8, 10, 1, 8, 11, 10, -1, -1, -1, -1,
                    5, 4, 0, 5, 0, 11, 5, 11, 10, 11, 0, 3, -1, -1, -1, -1,
                    5, 4, 8, 5, 8, 10, 10, 8, 11, -1, -1, -1, -1, -1, -1, -1,
                    9, 7, 8, 5, 7, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    9, 3, 0, 9, 5, 3, 5, 7, 3, -1, -1, -1, -1, -1, -1, -1,
                    0, 7, 8, 0, 1, 7, 1, 5, 7, -1, -1, -1, -1, -1, -1, -1,
                    1, 5, 3, 3, 5, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    9, 7, 8, 9, 5, 7, 10, 1, 2, -1, -1, -1, -1, -1, -1, -1,
                    10, 1, 2, 9, 5, 0, 5, 3, 0, 5, 7, 3, -1, -1, -1, -1,
                    8, 0, 2, 8, 2, 5, 8, 5, 7, 10, 5, 2, -1, -1, -1, -1,
                    2, 10, 5, 2, 5, 3, 3, 5, 7, -1, -1, -1, -1, -1, -1, -1,
                    7, 9, 5, 7, 8, 9, 3, 11, 2, -1, -1, -1, -1, -1, -1, -1,
                    9, 5, 7, 9, 7, 2, 9, 2, 0, 2, 7, 11, -1, -1, -1, -1,
                    2, 3, 11, 0, 1, 8, 1, 7, 8, 1, 5, 7, -1, -1, -1, -1,
                    11, 2, 1, 11, 1, 7, 7, 1, 5, -1, -1, -1, -1, -1, -1, -1,
                    9, 5, 8, 8, 5, 7, 10, 1, 3, 10, 3, 11, -1, -1, -1, -1,
                    5, 7, 0, 5, 0, 9, 7, 11, 0, 1, 0, 10, 11, 10, 0, -1,
                    11, 10, 0, 11, 0, 3, 10, 5, 0, 8, 0, 7, 5, 7, 0, -1,
                    11, 10, 5, 7, 11, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    10, 6, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    0, 8, 3, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    9, 0, 1, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    1, 8, 3, 1, 9, 8, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1,
                    1, 6, 5, 2, 6, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    1, 6, 5, 1, 2, 6, 3, 0, 8, -1, -1, -1, -1, -1, -1, -1,
                    9, 6, 5, 9, 0, 6, 0, 2, 6, -1, -1, -1, -1, -1, -1, -1,
                    5, 9, 8, 5, 8, 2, 5, 2, 6, 3, 2, 8, -1, -1, -1, -1,
                    2, 3, 11, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    11, 0, 8, 11, 2, 0, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1,
                    0, 1, 9, 2, 3, 11, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1,
                    5, 10, 6, 1, 9, 2, 9, 11, 2, 9, 8, 11, -1, -1, -1, -1,
                    6, 3, 11, 6, 5, 3, 5, 1, 3, -1, -1, -1, -1, -1, -1, -1,
                    0, 8, 11, 0, 11, 5, 0, 5, 1, 5, 11, 6, -1, -1, -1, -1,
                    3, 11, 6, 0, 3, 6, 0, 6, 5, 0, 5, 9, -1, -1, -1, -1,
                    6, 5, 9, 6, 9, 11, 11, 9, 8, -1, -1, -1, -1, -1, -1, -1,
                    5, 10, 6, 4, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    4, 3, 0, 4, 7, 3, 6, 5, 10, -1, -1, -1, -1, -1, -1, -1,
                    1, 9, 0, 5, 10, 6, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1,
                    10, 6, 5, 1, 9, 7, 1, 7, 3, 7, 9, 4, -1, -1, -1, -1,
                    6, 1, 2, 6, 5, 1, 4, 7, 8, -1, -1, -1, -1, -1, -1, -1,
                    1, 2, 5, 5, 2, 6, 3, 0, 4, 3, 4, 7, -1, -1, -1, -1,
                    8, 4, 7, 9, 0, 5, 0, 6, 5, 0, 2, 6, -1, -1, -1, -1,
                    7, 3, 9, 7, 9, 4, 3, 2, 9, 5, 9, 6, 2, 6, 9, -1,
                    3, 11, 2, 7, 8, 4, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1,
                    5, 10, 6, 4, 7, 2, 4, 2, 0, 2, 7, 11, -1, -1, -1, -1,
                    0, 1, 9, 4, 7, 8, 2, 3, 11, 5, 10, 6, -1, -1, -1, -1,
                    9, 2, 1, 9, 11, 2, 9, 4, 11, 7, 11, 4, 5, 10, 6, -1,
                    8, 4, 7, 3, 11, 5, 3, 5, 1, 5, 11, 6, -1, -1, -1, -1,
                    5, 1, 11, 5, 11, 6, 1, 0, 11, 7, 11, 4, 0, 4, 11, -1,
                    0, 5, 9, 0, 6, 5, 0, 3, 6, 11, 6, 3, 8, 4, 7, -1,
                    6, 5, 9, 6, 9, 11, 4, 7, 9, 7, 11, 9, -1, -1, -1, -1,
                    10, 4, 9, 6, 4, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    4, 10, 6, 4, 9, 10, 0, 8, 3, -1, -1, -1, -1, -1, -1, -1,
                    10, 0, 1, 10, 6, 0, 6, 4, 0, -1, -1, -1, -1, -1, -1, -1,
                    8, 3, 1, 8, 1, 6, 8, 6, 4, 6, 1, 10, -1, -1, -1, -1,
                    1, 4, 9, 1, 2, 4, 2, 6, 4, -1, -1, -1, -1, -1, -1, -1,
                    3, 0, 8, 1, 2, 9, 2, 4, 9, 2, 6, 4, -1, -1, -1, -1,
                    0, 2, 4, 4, 2, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    8, 3, 2, 8, 2, 4, 4, 2, 6, -1, -1, -1, -1, -1, -1, -1,
                    10, 4, 9, 10, 6, 4, 11, 2, 3, -1, -1, -1, -1, -1, -1, -1,
                    0, 8, 2, 2, 8, 11, 4, 9, 10, 4, 10, 6, -1, -1, -1, -1,
                    3, 11, 2, 0, 1, 6, 0, 6, 4, 6, 1, 10, -1, -1, -1, -1,
                    6, 4, 1, 6, 1, 10, 4, 8, 1, 2, 1, 11, 8, 11, 1, -1,
                    9, 6, 4, 9, 3, 6, 9, 1, 3, 11, 6, 3, -1, -1, -1, -1,
                    8, 11, 1, 8, 1, 0, 11, 6, 1, 9, 1, 4, 6, 4, 1, -1,
                    3, 11, 6, 3, 6, 0, 0, 6, 4, -1, -1, -1, -1, -1, -1, -1,
                    6, 4, 8, 11, 6, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    7, 10, 6, 7, 8, 10, 8, 9, 10, -1, -1, -1, -1, -1, -1, -1,
                    0, 7, 3, 0, 10, 7, 0, 9, 10, 6, 7, 10, -1, -1, -1, -1,
                    10, 6, 7, 1, 10, 7, 1, 7, 8, 1, 8, 0, -1, -1, -1, -1,
                    10, 6, 7, 10, 7, 1, 1, 7, 3, -1, -1, -1, -1, -1, -1, -1,
                    1, 2, 6, 1, 6, 8, 1, 8, 9, 8, 6, 7, -1, -1, -1, -1,
                    2, 6, 9, 2, 9, 1, 6, 7, 9, 0, 9, 3, 7, 3, 9, -1,
                    7, 8, 0, 7, 0, 6, 6, 0, 2, -1, -1, -1, -1, -1, -1, -1,
                    7, 3, 2, 6, 7, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    2, 3, 11, 10, 6, 8, 10, 8, 9, 8, 6, 7, -1, -1, -1, -1,
                    2, 0, 7, 2, 7, 11, 0, 9, 7, 6, 7, 10, 9, 10, 7, -1,
                    1, 8, 0, 1, 7, 8, 1, 10, 7, 6, 7, 10, 2, 3, 11, -1,
                    11, 2, 1, 11, 1, 7, 10, 6, 1, 6, 7, 1, -1, -1, -1, -1,
                    8, 9, 6, 8, 6, 7, 9, 1, 6, 11, 6, 3, 1, 3, 6, -1,
                    0, 9, 1, 11, 6, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    7, 8, 0, 7, 0, 6, 3, 11, 0, 11, 6, 0, -1, -1, -1, -1,
                    7, 11, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    7, 6, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    3, 0, 8, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    0, 1, 9, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    8, 1, 9, 8, 3, 1, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1,
                    10, 1, 2, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    1, 2, 10, 3, 0, 8, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1,
                    2, 9, 0, 2, 10, 9, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1,
                    6, 11, 7, 2, 10, 3, 10, 8, 3, 10, 9, 8, -1, -1, -1, -1,
                    7, 2, 3, 6, 2, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    7, 0, 8, 7, 6, 0, 6, 2, 0, -1, -1, -1, -1, -1, -1, -1,
                    2, 7, 6, 2, 3, 7, 0, 1, 9, -1, -1, -1, -1, -1, -1, -1,
                    1, 6, 2, 1, 8, 6, 1, 9, 8, 8, 7, 6, -1, -1, -1, -1,
                    10, 7, 6, 10, 1, 7, 1, 3, 7, -1, -1, -1, -1, -1, -1, -1,
                    10, 7, 6, 1, 7, 10, 1, 8, 7, 1, 0, 8, -1, -1, -1, -1,
                    0, 3, 7, 0, 7, 10, 0, 10, 9, 6, 10, 7, -1, -1, -1, -1,
                    7, 6, 10, 7, 10, 8, 8, 10, 9, -1, -1, -1, -1, -1, -1, -1,
                    6, 8, 4, 11, 8, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    3, 6, 11, 3, 0, 6, 0, 4, 6, -1, -1, -1, -1, -1, -1, -1,
                    8, 6, 11, 8, 4, 6, 9, 0, 1, -1, -1, -1, -1, -1, -1, -1,
                    9, 4, 6, 9, 6, 3, 9, 3, 1, 11, 3, 6, -1, -1, -1, -1,
                    6, 8, 4, 6, 11, 8, 2, 10, 1, -1, -1, -1, -1, -1, -1, -1,
                    1, 2, 10, 3, 0, 11, 0, 6, 11, 0, 4, 6, -1, -1, -1, -1,
                    4, 11, 8, 4, 6, 11, 0, 2, 9, 2, 10, 9, -1, -1, -1, -1,
                    10, 9, 3, 10, 3, 2, 9, 4, 3, 11, 3, 6, 4, 6, 3, -1,
                    8, 2, 3, 8, 4, 2, 4, 6, 2, -1, -1, -1, -1, -1, -1, -1,
                    0, 4, 2, 4, 6, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    1, 9, 0, 2, 3, 4, 2, 4, 6, 4, 3, 8, -1, -1, -1, -1,
                    1, 9, 4, 1, 4, 2, 2, 4, 6, -1, -1, -1, -1, -1, -1, -1,
                    8, 1, 3, 8, 6, 1, 8, 4, 6, 6, 10, 1, -1, -1, -1, -1,
                    10, 1, 0, 10, 0, 6, 6, 0, 4, -1, -1, -1, -1, -1, -1, -1,
                    4, 6, 3, 4, 3, 8, 6, 10, 3, 0, 3, 9, 10, 9, 3, -1,
                    10, 9, 4, 6, 10, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    4, 9, 5, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    0, 8, 3, 4, 9, 5, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1,
                    5, 0, 1, 5, 4, 0, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1,
                    11, 7, 6, 8, 3, 4, 3, 5, 4, 3, 1, 5, -1, -1, -1, -1,
                    9, 5, 4, 10, 1, 2, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1,
                    6, 11, 7, 1, 2, 10, 0, 8, 3, 4, 9, 5, -1, -1, -1, -1,
                    7, 6, 11, 5, 4, 10, 4, 2, 10, 4, 0, 2, -1, -1, -1, -1,
                    3, 4, 8, 3, 5, 4, 3, 2, 5, 10, 5, 2, 11, 7, 6, -1,
                    7, 2, 3, 7, 6, 2, 5, 4, 9, -1, -1, -1, -1, -1, -1, -1,
                    9, 5, 4, 0, 8, 6, 0, 6, 2, 6, 8, 7, -1, -1, -1, -1,
                    3, 6, 2, 3, 7, 6, 1, 5, 0, 5, 4, 0, -1, -1, -1, -1,
                    6, 2, 8, 6, 8, 7, 2, 1, 8, 4, 8, 5, 1, 5, 8, -1,
                    9, 5, 4, 10, 1, 6, 1, 7, 6, 1, 3, 7, -1, -1, -1, -1,
                    1, 6, 10, 1, 7, 6, 1, 0, 7, 8, 7, 0, 9, 5, 4, -1,
                    4, 0, 10, 4, 10, 5, 0, 3, 10, 6, 10, 7, 3, 7, 10, -1,
                    7, 6, 10, 7, 10, 8, 5, 4, 10, 4, 8, 10, -1, -1, -1, -1,
                    6, 9, 5, 6, 11, 9, 11, 8, 9, -1, -1, -1, -1, -1, -1, -1,
                    3, 6, 11, 0, 6, 3, 0, 5, 6, 0, 9, 5, -1, -1, -1, -1,
                    0, 11, 8, 0, 5, 11, 0, 1, 5, 5, 6, 11, -1, -1, -1, -1,
                    6, 11, 3, 6, 3, 5, 5, 3, 1, -1, -1, -1, -1, -1, -1, -1,
                    1, 2, 10, 9, 5, 11, 9, 11, 8, 11, 5, 6, -1, -1, -1, -1,
                    0, 11, 3, 0, 6, 11, 0, 9, 6, 5, 6, 9, 1, 2, 10, -1,
                    11, 8, 5, 11, 5, 6, 8, 0, 5, 10, 5, 2, 0, 2, 5, -1,
                    6, 11, 3, 6, 3, 5, 2, 10, 3, 10, 5, 3, -1, -1, -1, -1,
                    5, 8, 9, 5, 2, 8, 5, 6, 2, 3, 8, 2, -1, -1, -1, -1,
                    9, 5, 6, 9, 6, 0, 0, 6, 2, -1, -1, -1, -1, -1, -1, -1,
                    1, 5, 8, 1, 8, 0, 5, 6, 8, 3, 8, 2, 6, 2, 8, -1,
                    1, 5, 6, 2, 1, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    1, 3, 6, 1, 6, 10, 3, 8, 6, 5, 6, 9, 8, 9, 6, -1,
                    10, 1, 0, 10, 0, 6, 9, 5, 0, 5, 6, 0, -1, -1, -1, -1,
                    0, 3, 8, 5, 6, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    10, 5, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    11, 5, 10, 7, 5, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    11, 5, 10, 11, 7, 5, 8, 3, 0, -1, -1, -1, -1, -1, -1, -1,
                    5, 11, 7, 5, 10, 11, 1, 9, 0, -1, -1, -1, -1, -1, -1, -1,
                    10, 7, 5, 10, 11, 7, 9, 8, 1, 8, 3, 1, -1, -1, -1, -1,
                    11, 1, 2, 11, 7, 1, 7, 5, 1, -1, -1, -1, -1, -1, -1, -1,
                    0, 8, 3, 1, 2, 7, 1, 7, 5, 7, 2, 11, -1, -1, -1, -1,
                    9, 7, 5, 9, 2, 7, 9, 0, 2, 2, 11, 7, -1, -1, -1, -1,
                    7, 5, 2, 7, 2, 11, 5, 9, 2, 3, 2, 8, 9, 8, 2, -1,
                    2, 5, 10, 2, 3, 5, 3, 7, 5, -1, -1, -1, -1, -1, -1, -1,
                    8, 2, 0, 8, 5, 2, 8, 7, 5, 10, 2, 5, -1, -1, -1, -1,
                    9, 0, 1, 5, 10, 3, 5, 3, 7, 3, 10, 2, -1, -1, -1, -1,
                    9, 8, 2, 9, 2, 1, 8, 7, 2, 10, 2, 5, 7, 5, 2, -1,
                    1, 3, 5, 3, 7, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    0, 8, 7, 0, 7, 1, 1, 7, 5, -1, -1, -1, -1, -1, -1, -1,
                    9, 0, 3, 9, 3, 5, 5, 3, 7, -1, -1, -1, -1, -1, -1, -1,
                    9, 8, 7, 5, 9, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    5, 8, 4, 5, 10, 8, 10, 11, 8, -1, -1, -1, -1, -1, -1, -1,
                    5, 0, 4, 5, 11, 0, 5, 10, 11, 11, 3, 0, -1, -1, -1, -1,
                    0, 1, 9, 8, 4, 10, 8, 10, 11, 10, 4, 5, -1, -1, -1, -1,
                    10, 11, 4, 10, 4, 5, 11, 3, 4, 9, 4, 1, 3, 1, 4, -1,
                    2, 5, 1, 2, 8, 5, 2, 11, 8, 4, 5, 8, -1, -1, -1, -1,
                    0, 4, 11, 0, 11, 3, 4, 5, 11, 2, 11, 1, 5, 1, 11, -1,
                    0, 2, 5, 0, 5, 9, 2, 11, 5, 4, 5, 8, 11, 8, 5, -1,
                    9, 4, 5, 2, 11, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    2, 5, 10, 3, 5, 2, 3, 4, 5, 3, 8, 4, -1, -1, -1, -1,
                    5, 10, 2, 5, 2, 4, 4, 2, 0, -1, -1, -1, -1, -1, -1, -1,
                    3, 10, 2, 3, 5, 10, 3, 8, 5, 4, 5, 8, 0, 1, 9, -1,
                    5, 10, 2, 5, 2, 4, 1, 9, 2, 9, 4, 2, -1, -1, -1, -1,
                    8, 4, 5, 8, 5, 3, 3, 5, 1, -1, -1, -1, -1, -1, -1, -1,
                    0, 4, 5, 1, 0, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    8, 4, 5, 8, 5, 3, 9, 0, 5, 0, 3, 5, -1, -1, -1, -1,
                    9, 4, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    4, 11, 7, 4, 9, 11, 9, 10, 11, -1, -1, -1, -1, -1, -1, -1,
                    0, 8, 3, 4, 9, 7, 9, 11, 7, 9, 10, 11, -1, -1, -1, -1,
                    1, 10, 11, 1, 11, 4, 1, 4, 0, 7, 4, 11, -1, -1, -1, -1,
                    3, 1, 4, 3, 4, 8, 1, 10, 4, 7, 4, 11, 10, 11, 4, -1,
                    4, 11, 7, 9, 11, 4, 9, 2, 11, 9, 1, 2, -1, -1, -1, -1,
                    9, 7, 4, 9, 11, 7, 9, 1, 11, 2, 11, 1, 0, 8, 3, -1,
                    11, 7, 4, 11, 4, 2, 2, 4, 0, -1, -1, -1, -1, -1, -1, -1,
                    11, 7, 4, 11, 4, 2, 8, 3, 4, 3, 2, 4, -1, -1, -1, -1,
                    2, 9, 10, 2, 7, 9, 2, 3, 7, 7, 4, 9, -1, -1, -1, -1,
                    9, 10, 7, 9, 7, 4, 10, 2, 7, 8, 7, 0, 2, 0, 7, -1,
                    3, 7, 10, 3, 10, 2, 7, 4, 10, 1, 10, 0, 4, 0, 10, -1,
                    1, 10, 2, 8, 7, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    4, 9, 1, 4, 1, 7, 7, 1, 3, -1, -1, -1, -1, -1, -1, -1,
                    4, 9, 1, 4, 1, 7, 0, 8, 1, 8, 7, 1, -1, -1, -1, -1,
                    4, 0, 3, 7, 4, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    4, 8, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    9, 10, 8, 10, 11, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    3, 0, 9, 3, 9, 11, 11, 9, 10, -1, -1, -1, -1, -1, -1, -1,
                    0, 1, 10, 0, 10, 8, 8, 10, 11, -1, -1, -1, -1, -1, -1, -1,
                    3, 1, 10, 11, 3, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    1, 2, 11, 1, 11, 9, 9, 11, 8, -1, -1, -1, -1, -1, -1, -1,
                    3, 0, 9, 3, 9, 11, 1, 2, 9, 2, 11, 9, -1, -1, -1, -1,
                    0, 2, 11, 8, 0, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    3, 2, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    2, 3, 8, 2, 8, 10, 10, 8, 9, -1, -1, -1, -1, -1, -1, -1,
                    9, 10, 2, 0, 9, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    2, 3, 8, 2, 8, 10, 0, 1, 8, 1, 10, 8, -1, -1, -1, -1,
                    1, 10, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    1, 3, 8, 9, 1, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    0, 9, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    0, 3, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
            };
}
