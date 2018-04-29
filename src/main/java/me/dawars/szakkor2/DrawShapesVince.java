package me.dawars.szakkor2;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;
import processing.core.PMatrix3D;
import processing.opengl.PGraphicsOpenGL;


public class DrawShapesVince extends PApplet {
    public static void main(String[] args) {
        PApplet.main(DrawShapesVince.class);
    }

    @Override
    public void settings() {
        size(400, 400, P3D);
    }

    PShape triangle;
    PShape tetrahedron;
    PShape cylinder;
    PShape sphere;
    PShape paraboloid;
    PShape torus;
    float rotation = 0;

    PVector camera = new PVector(0, 0, 0);
    PVector cameraDir = new PVector(0, 0, -1);
    PVector cameraUp = new PVector(0, 1, 0);
    final float step = 2f;
    final float rStep = TAU / 200f;

    //PGraphicsOpenGL g;

    PMatrix3D baseTransform = new PMatrix3D(
            1, 0, 0, 200,
            0, 1, 0, 200,
            0, 0, 1, 0,
            0, 0, 0, 1
    );

    @Override
    public void setup() {
        sphere = createSphere(20);
        cylinder = createCylinder(20, 60);
        //paraboloid = createParaboloid();
        torus = createTorus(200, 80);

    }

    private void drawEnvironment() {
        final int rows = 10;
        final int cols = 3;
        final float spacing = 40 + 60;

        pushMatrix();
        for (int i = 0; i < cols; ++i) {
            translate(spacing, 0, 0);
            pushMatrix();
            for (int k = 0; k < rows; ++k) {
                translate(0, 0, -spacing);

                shape(cylinder);

                pushMatrix();
                translate(0, -20, 0);
                shape(sphere);
                popMatrix();
            }
            popMatrix();
        }
        popMatrix();
    }


    private void processInput() {
        if (keyPressed) {
            if (key != CODED) {
                switch (key) {
                    case 'W':
                    case 'w':
                        camera.add(PVector.mult(cameraDir, step));
                        break;
                    case 'S':
                    case 's':
                        camera.add(PVector.mult(cameraDir, -step));
                        break;
                    case 'D':
                    case 'd':
                        camera.add(cameraDir.cross(cameraUp).mult(step));
                        break;
                    case 'A':
                    case 'a':
                        camera.add(cameraDir.cross(cameraUp).mult(-step));
                        break;
                    case 'E':
                    case 'e':
                        camera.add(PVector.mult(cameraUp, -step));
                        break;
                    case 'Q':
                    case 'q':
                        camera.add(PVector.mult(cameraUp, step));
                        break;

                }
            } else {
                PMatrix3D rotation = new PMatrix3D();
                switch (keyCode) {
                    case LEFT://Positive Y rotation
                        rotation.set(
                                cos(rStep), 0, sin(rStep), 0,
                                0, 1, 0, 0,
                                -sin(rStep), 0, cos(rStep), 0,
                                0, 0, 0, 1
                        );
                        break;
                    case RIGHT://Negative Y
                        rotation.rotateY(-rStep);
                        break;
                    case UP://Positive X
                        rotation.set(
                                1, 0, 0, 0,
                                0, cos(rStep), -sin(rStep), 0,
                                0, sin(rStep), cos(rStep), 0,
                                0, 0, 0, 1
                        );
                        break;
                    case DOWN://Negative X
                        rotation.rotateX(-rStep);
                        break;
                    case CONTROL://Negative Z
                        rotation.set(
                                cos(-rStep), -sin(-rStep), 0, 0,
                                sin(-rStep), cos(-rStep), 0, 0,
                                0, 0, 1, 0,
                                0, 0, 0, 1
                        );
                        break;
                    case ALT://Positive Z
                        rotation.rotateZ(rStep);
                        break;
                }

                rotation.mult(cameraDir, cameraDir);
                rotation.mult(cameraUp, cameraUp);
            }
        }
    }

    private PMatrix3D lookAt(PVector eye, PVector center, PVector up) {
        PVector direction = PVector.sub(eye, center).normalize();
        PVector right = up.cross(direction).normalize();

        PMatrix3D view = new PMatrix3D(
                right.x, right.y, right.z, 0,
                up.x, up.y, up.z, 0,
                direction.x, direction.y, direction.z, 0,
                0, 0, 0, 1
        );
        view.translate(-eye.x, -eye.y, -eye.z);
        return view;
    }

    private PMatrix3D getProjectionMatrix(float fovy, float aspect, float near, float far) {
        float top = tan(fovy / 2) * near;
        float bottom = -top;
        float right = aspect * top;
        float left = -right;

        PMatrix3D proj = new PMatrix3D(
                (2 * near) / (right - left), 0, (right + left) / (right - left), 0,
                0, -(2 * near) / (top - bottom), (top + bottom) / (top - bottom), 0,
                0, 0, -(far + near) / (far - near), -(2 * far * near) / (far - near),
                0, 0, -1, 0
        );
        return proj;
    }

    @Override
    public void draw() {
        background(0);

        processInput();
        PVector center = PVector.add(camera, cameraDir);
        //camera(camera.x,camera.y,camera.z, center.x, center.y, center.z, cameraUp.x, cameraUp.y, cameraUp.z);


        final PMatrix3D view = lookAt(camera, center, cameraUp);
        final PMatrix3D proj = getProjectionMatrix(radians(90), width / height, 0.01f, 100000);

        PGraphicsOpenGL og = (PGraphicsOpenGL) this.g;
        og.modelview.set(view);
        og.modelviewInv.set(view);
        og.modelviewInv.invert();
        og.projection.set(proj);
        og.updateProjmodelview();
        drawEnvironment();
    }

    private PShape createTorus(float innerRadius, float outerRadius) {
        PShape shape = createShape();
        shape.beginShape(TRIANGLE_STRIP);
        shape.noStroke();

        float innerStep = TAU / 20;
        float outerStep = TAU / 20;

        for (float innerAngle = 0; innerAngle <= TAU; innerAngle += innerStep) {
            PVector p0 = new PVector(innerRadius * cos(innerAngle), 0, innerRadius * sin(innerAngle));
            PVector p1 = new PVector(innerRadius * cos(innerAngle + innerStep), 0, innerRadius * sin(innerAngle + innerStep));

            for (float outerAngle = 0; outerAngle <= (TAU + outerStep); outerAngle += outerStep) {
                PVector v0 = rotateAroundPointY(polarToDescartes(outerRadius, outerAngle, p0), p0, innerAngle);
                PVector v1 = rotateAroundPointY(polarToDescartes(outerRadius, outerAngle, p1), p1, innerAngle + innerStep);

                shape.fill(127, 0, 127);
                shape.vertex(v0.x, v0.y, v0.z);
                shape.fill(115, 40, 115);
                shape.vertex(v1.x, v1.y, v1.z);
            }
        }

        shape.endShape();
        return shape;
    }

    /**
     * Rotates a point around another point (Y)
     *
     * @param p     Point to rotate
     * @param ref   Point to rotate around
     * @param angle Angle to rotate by (radians)
     */
    private PVector rotateAroundPointY(PVector p, PVector ref, float angle) {
        PVector res = new PVector();
        res.x = (p.x - ref.x) * cos(angle) - (p.z - ref.z) * sin(angle) + ref.x;
        res.y = p.y;
        res.z = (p.x - ref.x) * sin(angle) + (p.z - ref.z) * cos(angle) + ref.z;
        return res;
    }

    /**
     * Converts polar to descartes coords relative to a point
     *
     * @param r      Radius
     * @param angle  Angle
     * @param center Point of reference
     */
    private PVector polarToDescartes(float r, float angle, PVector center) {
        PVector res = new PVector();
        res.x = r * cos(angle) + center.x;
        res.y = r * sin(angle) + center.y;
        res.z = center.z;
        return res;
    }

    private PShape createParaboloid(final int width, final float a, final float b, final int hyperbolic) {
        PShape shape = createShape();
        //Set this to 1 for an elliptic paraboloid or -1 for a hyperbolic one

        shape.beginShape(TRIANGLE_STRIP);
        shape.noStroke();

        float rotateStep = TAU / 100;

        float r = 0;
        float rStep = 1;
        for (float angle = 0; angle <= TAU; angle += rotateStep) {
            for (; (r <= width) && r >= 0; r += rStep) {
                float x0 = r * cos(angle);
                float y0 = r * sin(angle);
                float z0 = getParaboloidZ(x0, y0, a, b, hyperbolic);

                float x1 = (r + rStep) * cos(angle);
                float y1 = (r + rStep) * sin(angle);
                float z1 = getParaboloidZ(x1, y1, a, b, hyperbolic);

                float x2 = r * cos(angle + rotateStep);
                float y2 = r * sin(angle + rotateStep);
                float z2 = getParaboloidZ(x2, y2, a, b, hyperbolic);

                float x3 = (r + rStep) * cos(angle + rotateStep);
                float y3 = (r + rStep) * sin(angle + rotateStep);
                float z3 = getParaboloidZ(x3, y3, a, b, hyperbolic);

                shape.fill(127, 0, 127);
                shape.vertex(x0, y0, z0);
                shape.vertex(x1, y1, z1);
                shape.vertex(x2, y2, z2);
                shape.vertex(x3, y3, z3);
            }

            if (r > width)
                r = width;
            if (r < 0)
                r = 0;
            rStep *= -1;
        }

        shape.endShape();
        return shape;
    }

    private float getParaboloidZ(float x, float y, float a, float b, float hyperbolic) {
        return hyperbolic * (x * x) / (a * a) + (y * y) / (b * b);
    }

    private PShape createSphere(int radius) {
        PShape shape = createShape();
        int quality = 36;

        shape.beginShape(TRIANGLE_STRIP);
        shape.noStroke();

        float step = 360 / quality;
        for (float hAngle = 0; hAngle < 180; hAngle += step) {
            //Draw an arc of the sphere
            for (float vAngle = 0; vAngle <= 360; vAngle += step) {
                PVector v0 = rotateAroundY(
                        radius * cos(radians(vAngle)),
                        radius * sin(radians(vAngle)),
                        0,
                        radians(hAngle)
                );

                //shape.fill(127,0,127);
                shape.fill(0, 70, 0);//Green for trees
                shape.vertex(v0.x, v0.y, v0.z);

                PVector v1 = rotateAroundY(
                        v0.x,
                        v0.y,
                        v0.z,
                        radians(step)
                );
                /*if(vAngle % 15 == 0) { //Stripes for visibility
                    shape.fill(115,40,115);
                }*/
                //shape.fill(115,40,115);
                shape.vertex(v1.x, v1.y, v1.z);
            }
        }

        shape.endShape();
        return shape;
    }

    /**
     * Rotates a coord around the Y axis
     *
     * @param angle Angle in radians
     */
    private PVector rotateAroundY(float x, float y, float z, float angle) {
        PVector res = new PVector();
        res.x = x * cos(angle) - z * sin(angle);
        res.y = y;
        res.z = x * sin(angle) + z * cos(angle);
        return res;
    }


    private PShape createCylinder(int radius, int height) {
        PShape shape = createShape();
        int quality = 36;

        shape.beginShape(TRIANGLE_STRIP);
        shape.noStroke();

        int step = 360 / quality;
        for (int angle = 0; angle <= 360; angle += step) {
            float x = radius * cos(radians(angle));
            float z = radius * sin(radians(angle));

            //shape.fill(100,0,100);
            shape.fill(77, 38, 0);//brown for tree
            shape.vertex(x, 0, z);
            //shape.fill(0,100,0);
            shape.vertex(x, height, z);
        }

        shape.endShape();
        return shape;
    }

    private PShape createTetrahedron() {
        PShape shape = createShape();

        shape.beginShape(TRIANGLES);

        shape.fill(255, 0, 0);
        shape.vertex(0, 0, 100);
        shape.vertex(-50, 0, 0);
        shape.vertex(50, 0, 0);

        shape.fill(255, 0, 0);
        shape.vertex(0, 0, 100);
        shape.vertex(0, 100, 50);
        shape.vertex(-50, 0, 0);

        shape.fill(0, 255, 0);
        shape.vertex(0, 0, 100);
        shape.vertex(0, 100, 50);
        shape.vertex(50, 0, 0);

        shape.fill(0, 0, 255);
        shape.vertex(50, 0, 0);
        shape.vertex(-50, 0, 0);
        shape.vertex(0, 100, 50);

        shape.endShape();
        return shape;
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
}
