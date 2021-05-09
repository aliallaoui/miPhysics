package org.micreative.miPhysics.Renderer;

import org.micreative.miPhysics.Vect3D;
import processing.core.PApplet;

public class CircleRenderer extends AbstractRenderer{
    private float radius;
    private int[] color=new int[3];

    protected Vect3D leftUpCorner;
    protected float canvasWidth;

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Vect3D getLeftUpCorner() {
        return leftUpCorner;
    }

    public void setLeftUpCorner(Vect3D leftUpCorner) {
        this.leftUpCorner = leftUpCorner;
    }

    public float getCanvasWidth() {
        return canvasWidth;
    }

    public void setCanvasWidth(float canvasWidth) {
        this.canvasWidth = canvasWidth;
        canvasHeight=canvasWidth*app.height/app.width;
    }

    public float getCanvasHeight() {
        return canvasHeight;
    }

    public void setCanvasHeight(float canvasHeight) {
        this.canvasHeight = canvasHeight;
        this.canvasWidth=canvasHeight*app.width/app.height;
    }

    protected float canvasHeight;

    public CircleRenderer(PApplet parent) {
        super(parent);
        setColorRGB(255,0,0);
        radius=10;
        leftUpCorner=new Vect3D(-6,10,0);
        canvasWidth=12;
        canvasHeight=canvasWidth*app.height/app.width;
    }

    public void setColorRGB(int r,int g,int b)
    {
        color[0]=r;
        color[1]=g;
        color[2]=b;
    }

    @Override
    public void render() throws Exception {

        app.fill(color[0], color[1], color[2]);
        for(iterators.get(0).begin();!iterators.get(0).end();iterators.get(0).next())
        {
            Vect3D A = containers.get(0).get(iterators.get(0));
            // x_canvas/canvasWidth = x/app.width
            float x = ((float)A.x()-(float)leftUpCorner.x())*app.width/canvasWidth;
            float y = ((float)leftUpCorner.y()-(float)A.y())*app.height/canvasHeight;
            app.ellipse(x,y,radius,radius);
        }
    }
}
