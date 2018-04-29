package me.dawars.szakkor2;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

public class DrawShapesBende extends PApplet {
    public static void main(String[] args) {
        PApplet.main(DrawShapesBende.class);
    }

    @Override
    public void settings() {
        size(400, 400, P3D);
    }

    PShape tetra;

    PShape cylinder;

    PShape sphere;

    PShape paraboloid;

    PShape sphere2;

    PShape torus;

    /*float rotateAngle = 0;
    float zEye = -300;
    float radiusCenter = 100;
    float camrotateAngle = 0;
    float xUp,yUp;
    float a;
    float b;*/

    @Override
    public void setup() {

        cylinder = createCylinder ();
        sphere = createSphere();
        paraboloid = createParaboloid();
        tetra = createTetrahidron();
        sphere2 = createDuplaParabola();
        //torus = createTorus();
    }

    float xEye = 100, yEye = 0, zEye = 0, angle = 5;
    public PVector dőlésszög = new PVector(-1,0,0);

    float r = 100;


    @Override
    public void draw() {
        background(102);
        line(-1000,0,0,1000,0,0);
        line(0,-1000,0,0,1000,0);
        line(0,0,-1000,0,0,1000);
        camera(xEye,yEye,zEye,xEye + forgatás.x,yEye + forgatás.y + dőlésszög.y,zEye + forgatás.z + dőlésszög.z,0,dőlésszög.y + 1,dőlésszög.z);
        sphere(r);

        r -= 0.05;

        if (r <= 0){
            float rv = r;
            r = -rv;
        }
    }

    public void keyPressed() {

        if (key == ' '){
            yEye += 10;
        }
        if (key == 'u'){
            yEye -= 10;
        }
        if (key == 'w'){
            xEye -= 10;
        }
        if (key == 's'){
            xEye += 10;
        }
        if (key == 'd'){
            zEye -= 10;
        }
        if (key == 'a'){
            zEye += 10;
        }
        if (key == 'e'){
            dőlésszög.z = dőlésszög.z * cos(radians(angle)) - dőlésszög.y * sin(radians(angle));
            dőlésszög.y = dőlésszög.z * sin(radians(angle)) + dőlésszög.y * cos(radians(angle));
        }
        if (key == 'q'){
            dőlésszög.z = dőlésszög.z * cos(radians(-angle)) - dőlésszög.y * sin(radians(-angle));
            dőlésszög.y = dőlésszög.z * sin(radians(-angle)) + dőlésszög.y * cos(radians(-angle));
        }
        //dőlés

        //NYILAK
        if (key == CODED){
            if (keyCode == RIGHT){
                forgatás.x = forgatás.x * cos(radians(angle)) - forgatás.z * sin(radians(angle));
                forgatás.z = forgatás.x * sin(radians(angle)) + forgatás.z * cos(radians(angle));
            }
            if (keyCode == LEFT) {
                forgatás.x = forgatás.x * cos(radians(-angle)) - forgatás.z * sin(radians(-angle));
                forgatás.z = forgatás.x * sin(radians(-angle)) + forgatás.z * cos(radians(-angle));
            }
            if (keyCode == UP){
                forgatás.x = forgatás.x * cos(radians(angle)) - forgatás.y * sin(radians(angle));
                forgatás.y = forgatás.x * sin(radians(angle)) + forgatás.y * cos(radians(angle));
            }
            if (keyCode == DOWN) {
                forgatás.x = forgatás.x * cos(radians(-angle)) - forgatás.y * sin(radians(-angle));
                forgatás.y = forgatás.x * sin(radians(-angle)) + forgatás.y * cos(radians(-angle));
            }
        }

    }

    public PVector forgatás = new PVector(-1,0,0);


    /*@Override
    public void draw() {
        a = 0;
        b = 0;
        background(255,255,255);
        //sphere(100);
        line(-100,0,0,100,0,0);
        line(0,-100,0,0,100,0);
        line(0,0,-100,0,0,100);
        float xCenter = cos(radians(rotateAngle)) * radiusCenter;// position of object (x coordinate)
        float yCenter = sin(radians(rotateAngle)) *radiusCenter;
        xUp = cos(radians(camrotateAngle));
        yUp = sin(radians(camrotateAngle));
        //ortho(-150,150,-150, 150, 10,350);
        //perspective(20,1,1,350);
        camera(a,b,zEye,xCenter,yCenter,0,xUp,yUp,0);

        //rotateAngle += 7;
        zEye -= 1; //távolodáshoz ;)

        keyPressed();

        //shape(tetra);
        //shape(cylinder);
        //shape(torus);
        shape(sphere);
        //shape(sphere2);
        //shape(paraboloid);

        // nagyon szeretem a tejet tökmind1

    }    public void keyPressed(){
        if (key == 'e'){
            rotateAngle -= 3;
            camrotateAngle += 1;
        }
        if (key == 'q'){
            rotateAngle += 3;
            camrotateAngle -= 1;
        }
        if (key == ' '){
            float x = rotateAngle;
            rotateAngle = x; }*/

    private PShape createParaboloid() {
        PShape shape = createShape();

        shape.beginShape(TRIANGLE_STRIP);

        float radius = 60;
        float maxheight = 120;
        int rotateBy = 15;
        int layerHeight = 5;
        float T = sq(radius)*PI; //Terület


        for (float rotateAngle = 0; rotateAngle < 360; rotateAngle += rotateBy) {

            for (int height = 0; height <= maxheight; height += layerHeight) {
                float TNew = height/radius * T;
                float radiusNow = sqrt(TNew/PI);
                float x = cos(radians(rotateAngle)) * radiusNow;
                float y = sin(radians(rotateAngle)) * radiusNow;

                shape.fill(100,50,100);
                shape.vertex(x,y,height);

                x = cos(radians(rotateAngle+rotateBy)) * radiusNow;
                y = sin(radians(rotateAngle+rotateBy)) * radiusNow;
                shape.vertex(x,y,height);
            }
        }
        shape.endShape();
        return shape;

    }

    private PShape createDuplaParabola() {
        PShape shape = createShape();

        shape.beginShape(TRIANGLE_STRIP);

        float radius = 60;
        float maxheight = radius * 2;
        int rotateBy = 15;
        int layerHeight = 5;
        float T = sq(radius)*PI; //Terület


        for (float rotateAngle = 0; rotateAngle < 360; rotateAngle += rotateBy) {

            for (int height = 0; height <= maxheight; height += layerHeight) {
                float TNew = T;
                float radiusNow = radius;
                if (height<radius) { TNew = height/radius * T; } //radius = félmagassággal
                if (height>radius) { TNew = (maxheight-height) / radius * T; }

                radiusNow = sqrt(TNew/PI);
                float x = cos(radians(rotateAngle)) * radiusNow;
                float y = sin(radians(rotateAngle)) * radiusNow;

                shape.fill(100,50,100);
                shape.vertex(x,y,height);

                x = cos(radians(rotateAngle+rotateBy)) * radiusNow;
                y = sin(radians(rotateAngle+rotateBy)) * radiusNow;
                shape.vertex(x,y,height);
            }
        }
        shape.endShape();
        return shape;

    }

    private PShape createBugocsiga() {
        PShape shape = createShape();

        shape.beginShape(TRIANGLE_STRIP);

        int radius = 60;
        int maxheight = radius * 2;
        int rotateBy = 15;
        int layerHeight = 5;
        //float conference = radius*2*PI; //kerület


        for (float rotateAngle = 0; rotateAngle < 360; rotateAngle += rotateBy) {

            for (int height = 0; height <= maxheight; height += layerHeight) {
                float radiusNow = radius;
                if (height<radius) { radiusNow = radius * height/radius; }
                if (height>radius) { radiusNow = radius * (maxheight-height) / radius; }
                float x = cos(radians(rotateAngle)) * radiusNow;
                float y = sin(radians(rotateAngle)) * radiusNow;

                shape.fill(100,50,100);
                shape.vertex(x,y,height);

                x = cos(radians(rotateAngle+rotateBy)) * radiusNow;
                y = sin(radians(rotateAngle+rotateBy)) * radiusNow;
                shape.vertex(x,y,height);
            }
        }
        shape.endShape();
        return shape;

    }

    /*private PShape createSphere() {
        PShape shape = createShape();

        shape.beginShape(TRIANGLE_STRIP);
        //shape.noStroke();

        int radius = 100;
        int rotateAngle = 10;

        for (int angle = 0 ; angle <= 360; angle += rotateAngle){
            float x = cos(radians(angle)) * radius;
            float y = sin(radians(angle)) * radius;
            float z = 0;
            float xx = cos(radians(angle+rotateAngle)) * radius;
            float yx = sin(radians(angle+rotateAngle)) * radius;

            for (int i = 0 ; i <= 180 ; i += rotateAngle) {
                float x2 = x * cos(radians(i)) - z * sin(radians(i));
                float z2 = x * sin(radians(i)) + z * cos(radians(i)) ;
                float x2x = xx * cos(radians(i)) - z * sin(radians(i));
                float z2x = xx * sin(radians(i)) + z * cos(radians(i)) ;
                float x3 = x * cos(radians(i + rotateAngle)) - z * sin(radians(i + rotateAngle));
                float z3 = x * sin(radians(i + rotateAngle)) + z * cos(radians(i + rotateAngle)) ;
                float x3x = xx * cos(radians(i + rotateAngle)) - z * sin(radians(i + rotateAngle));
                float z3x = xx * sin(radians(i + rotateAngle)) + z * cos(radians(i + rotateAngle)) ;
                shape.fill(0,255,0);
                shape.vertex(x2,y,z2);
                shape.fill(0,0,255);
                shape.vertex(x2x,yx,z2x);
                shape.fill(255,0,0);
                shape.vertex(x3,y,z3);
                shape.fill(50,10,100);
                shape.vertex(x3x,yx,z3x);
            }
        }
        shape.endShape();
        return shape;
    }*/

    /*private PShape createTorus() {
        PShape shape = createShape();
        shape.beginShape(POINTS);
        shape.stroke(255, 0, 0);
        shape.strokeWeight(4);
        int KörSűrűség = 5; //minél nagyobb annál ritkábban vannak körök
        int radius = 100;
        int rotateBy = 35; //kiskörök pontjainak sűrűsége
        int kiskörsugár = 25;

        for (int angle = 0; angle <= 360; angle += KörSűrűség){
            float x = cos(radians(angle)) * radius;
            float y = sin(radians(angle)) * radius;
            for (int i = 0; i <= 360; i += rotateBy){
                float yk = cos(radians(i)) * kiskörsugár + y;
                float zk = sin(radians(i)) * kiskörsugár + 0; //z = 0
                shape.fill(0,255,0);
                shape.vertex(x,yk,zk);
            }
        }
        shape.endShape();
        return shape;
    }

    private PVector rotatePointAroundZ(int radius,int kiskörsugár,int i) {

    }*/

    private PShape createSphere() {
        PShape shape = createShape();
        shape.beginShape(TRIANGLE_STRIP);
        int radius = 50;
        int rotateAngle = 10;
        int rotateAngle2 = 15;

        for (int angle = 0 ; angle <= 360; angle+= rotateAngle) {
            PVector v1 = PolarToRectengular(angle,radius);
            float x = v1.x;
            float y = v1.y;
            PVector v2 = PolarToRectengular(angle + rotateAngle,radius);
            float x2 = v2.x;
            float y2 = v2.y;
            int z = 0;
            int z2 = 0;
            for(int i = 0; i <= 180; i += rotateAngle2){
                float xv = x * cos(radians(i)) - z * sin(radians(i));
                float zv = x * sin(radians(i)) + z * cos(radians(i));
                float x2v = x2 * cos(radians(i)) - z2 * sin(radians(i));
                float z2v = x2 * sin(radians(i)) + z2 * cos(radians(i));
                shape.fill(0,255,0);
                shape.vertex(xv,y,zv);
                shape.fill(255,0,0);
                shape.vertex(x2v,y2,z2v);
            }
        }
        shape.endShape();
        return shape;
    }

    private PVector PolarToRectengular(int theta, int radius) {
        float y = sin(radians(theta)) * radius;
        float x = cos(radians(theta)) * radius;
        return new PVector(x,y);
    }

    /*private PShape createSphere() {
        PShape shape = createShape();


        shape.beginShape(POINTS);

        int radius = 30;

        int maxheight = radius*2;

        int sliceBy = 20;

        int rotateBy = 20;

        int midheight = radius;


        for(int height = 0; height < maxheight; height += sliceBy) {

            int radiusnow = radius;


            if (height < midheight) {
                radiusnow = height / midheight * radius;
            }
            if (height > midheight) {
                radiusnow = (maxheight-height) / midheight * radius;
            }

            for (int angle = 0; angle <= 360; angle += rotateBy ){ //minden alkalommal lejátszódó folyamat

                float x = cos(radians(angle)) * radiusnow;

                float y = sin(radians(angle)) * radiusnow;


                shape.fill(100,50,100);
                shape.vertex(x,y,height);
            }
        }
        shape.endShape();
        return shape;
    }*/

    private PShape createCylinder() {
        PShape shape = createShape();

        shape.beginShape(TRIANGLE_STRIP);
        shape.noStroke();
        int height = 100;
        int rotateBy = 15;
        int radius = 25;

        for (int angle = 0; angle <= 360; angle += rotateBy) {
            float x = sin(radians(angle))*radius;
            float y = cos(radians(angle))*radius;

            shape.fill(250,50,0);
            shape.vertex(x,y,0);
            shape.fill(50,0,250);
            shape.vertex(x,y,height);
        }
        shape.endShape();
        return shape;
    }

    /*private PShape createCylinder() {
        PShape shape = createShape();

        shape.beginShape(TRIANGLE_STRIP);
        shape.noStroke(); //háromszögnek nem rajzol körvonalat
        int height = 150;
        int radius = 45;
        int rotatingBy = 5;

        for(int angle = 0; angle <= 360; angle += rotatingBy) {
            float x = cos(radians(angle))*radius; // beszorozzuk sugárral (pl 30) -cal, hogy úgy működhessen mint az egyes kör
            float y = sin(radians(angle))*radius;

            shape.fill(100,50,100);
            shape.vertex(x,y,0);
            shape.fill(150,50,50);
            shape.vertex(x,y,height);
        }
        shape.endShape();
        return shape;
    }*/

    private PShape createTetrahidron() {
        PShape shape = createShape();

        shape.beginShape(TRIANGLES);
        shape.fill(0,255,0);
        shape.vertex(70,70,0);
        shape.fill(255,0,0);
        shape.vertex(50,0,0);
        shape.fill(0,0,255);
        shape.vertex(0,50,0);

        shape.fill(0,255,0);
        shape.vertex(70,70,0);
        shape.fill(255,0,0);
        shape.vertex(50,50,50);
        shape.fill(0,0,255);
        shape.vertex(0,50,0);

        shape.fill(0,255,0);
        shape.vertex(70,70,0);
        shape.fill(255,0,0);
        shape.vertex(50,0,0);
        shape.fill(0,0,255);
        shape.vertex(50,50,50);

        shape.fill(0,255,0);
        shape.vertex(50,50,50);
        shape.fill(255,0,0);
        shape.vertex(50,0,0);
        shape.fill(0,0,255);
        shape.vertex(0,50,0);

        shape.endShape();
        return shape;
    }

    /*private PShape createTetrahidron() {
        PShape shape = createShape();

        shape.beginShape(TRIANGLES);
        shape.fill(0,0,255);
        shape.vertex(0,-100,0);
        shape.vertex(100,100,0);
        shape.vertex(-100,100,0);

        shape.fill(0,150,200);
        shape.vertex(0,-100,0);
        shape.vertex(100,100,0);
        shape.vertex(0,0,150);

        shape.fill(100,0,150);
        shape.vertex(0,-100,0);
        shape.vertex(-100,100,0);
        shape.vertex(0,0,150);

        shape.fill(150,0,50);
        shape.vertex(100,100,0);
        shape.vertex(-100,100,0);
        shape.vertex(0,0,150);
        shape.endShape();

        return shape;
    }*/

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
}
