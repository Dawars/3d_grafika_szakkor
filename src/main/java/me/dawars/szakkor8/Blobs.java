package me.dawars.szakkor8;

import processing.core.PVector;

import java.util.List;
import java.util.Random;

public class Blobs {
    public float x, y, z, velX, velY, velZ;
    public int strength;

    public static int scale = 1;

    private Random random = new Random();

    static int minX = 0;
    static int maxX = 15;
    static int minY = 0;
    static int maxY = 15;
    static int minZ = 0;
    static int maxZ = 15;

    public Blobs() {
        this.x = random.nextInt(maxX) - minX;
        this.y = random.nextInt(maxY) - minY;
        this.z = random.nextInt(maxZ) - minZ;
        this.strength = 4;

        PVector dir = PVector.random3D();
        velX = dir.x;
        velY = dir.y;
        velZ = dir.z;
    }

    public void setVelocity(float velX, float velY, float velZ) {
        this.velX = velX;
        this.velY = velY;
        this.velZ = velZ;
    }

    public void update() {
        this.update(1F);
    }

    public Blobs setBounds(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;

        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;

        return this;
    }

    public void update(float speed) {
        if (this.x > maxX || this.x < minX)
            this.velX *= -1F;

        if (this.z > maxZ || this.z < minZ)
            this.velZ *= -1F;

        if (this.y > maxY || this.y < minY)
            this.velY *= -1F;

        this.x += speed * this.velX;
        this.y += speed * this.velY;
        this.z += speed * this.velZ;
    }

    private static float f(float r) {
        return 1 / r;
    }

    public static void fieldStrength(List<Blobs> blobs, float[][][] field) {
//        if(field == null) field = new float[16][16][16]; // can be output?

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    field[x][y][z] = 0;
                    for (int i = 0; i < blobs.size(); i++) {
                        float xDist = blobs.get(i).x - x;
                        float yDist = blobs.get(i).y - y;
                        float zDist = blobs.get(i).z - z;
                        float r = xDist * xDist + yDist * yDist + zDist * zDist; //distance square

                        int strength = blobs.get(i).strength;
                        field[x][y][z] += strength / r;
                    }
                }
            }
        }
    }

    public static PVector normal(float x, float y, float z, List<Blobs> blobs) {

        PVector normal = new PVector(0, 0, 0);

        for (Blobs blob : blobs) {
            float xDist = blob.x - x;
            float yDist = blob.y - y;
            float zDist = blob.z - z;
            float r = xDist * xDist + yDist * yDist + zDist * zDist; //distance square

            float f = 1 / (r * r);
            normal.add(xDist * f, yDist * f, zDist * f);
        }
        return normal.mult(2);
    }


}