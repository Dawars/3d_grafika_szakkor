package me.dawars.szakkor11;

import ch.bildspur.postfx.PostFXSupervisor;
import ch.bildspur.postfx.builder.PostFX;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PJOGL;
import processing.opengl.PShader;

public class PostProcessing extends PApplet {
    private PShape radioShape;
    private PImage radioTexture;
    private PShader shader;
    private float angle;

    private PostFX fx;
    private PGraphics glowCanvas;
    private PShader maskShader;
    private PostFXSupervisor supervisor;

    public static void main(String[] args) {
        PApplet.main(PostProcessing.class);
    }

    @Override
    public void settings() {
        size(1280, 720, P3D);
//        pixelDensity(2);
    }

    @Override
    public void setup() {
        textureMode(NORMAL);

        radioShape = loadShape("models/switch/switch.obj");
        radioTexture = loadImage("models/switch/switch.png");
        radioShape.setTexture(radioTexture);

        shader = loadShader("szakkor11/frag.glsl", "szakkor11/vert.glsl");
        maskShader = loadShader("szakkor11/mask_frag.glsl", "szakkor11/mask_vert.glsl");

        fx = new PostFX(this);
        supervisor = new PostFXSupervisor(this);

        glowCanvas = createGraphics(width, height, P3D);

//        AddGlow glowPass = new AddGlow()


    }

    @Override
    public void draw() {

        blendMode(BLEND);
        g.background(0);
        shader(shader);
        renderToCanvas(g);

        shader(maskShader);
        renderToCanvas(glowCanvas);

        blendMode(ADD);

        fx.render(glowCanvas) // bloom
                .blur(20, 40)
                .compose();


        angle += 0.01f;
    }

    private void renderToCanvas(PGraphics canvas) {
        canvas.beginDraw();

        canvas.background(0);


        PJOGL pgl = (PJOGL) beginPGL();
        GL2ES2 gl = pgl.gl.getGL2ES2();
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glCullFace(GL.GL_BACK);

        canvas.camera(0, 30, 100, 0, 30, 0, 0, -1, 0);
        canvas.beginCamera();
        canvas.rotateY(angle);
        canvas.endCamera();

        canvas.shape(radioShape);

        canvas.endDraw();
    }
}
