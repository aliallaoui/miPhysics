package org.micreative.miPhysics.Renderer;

import org.micreative.miPhysics.Vect3D;
import processing.core.PApplet;


public class SphereRenderer extends AbstractRenderer{

    private float radius;
    private int[] color=new int[3];

    public SphereRenderer(PApplet parent) {
        super(parent);
        setColorRGB(255,0,0);
        radius=1;
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
            app.pushMatrix();
            app.translate((float)A.x(),(float)A.y(),(float)A.z());
            app.sphere(radius);
            app.popMatrix();
        }
    }
}
