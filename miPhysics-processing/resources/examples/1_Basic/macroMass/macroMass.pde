import org.micreative.miPhysics.Engine.PhysicalModel;
import org.micreative.miPhysics.Vect3D;
import org.micreative.miPhysics.Engine.Index;

import peasy.*;


PeasyCam cam;
Index iA = new Index(0);
Index iB = new Index(1);
PhysicalModel  pm=new PhysicalModel("macroMassTest",50);
void setup()
        {
        size(1000,700,P3D);
        //fullScreen(P3D, 2);
        cam=new PeasyCam(this,100);
        cam.setMinimumDistance(50);
        cam.setMaximumDistance(2500);


        int[] dim = new int[1];
        dim[0] = 2;

        pm.addMacroMass("macro","BoundedIterator","LEFT0|RIGHT0","GridContainer",dim);
        //  pm.getModule("macro").setGravity(new Vect3D(0,-0.001,0));

        pm.init();
        pm.getModule("macro").setPointR(iA,
        Vect3D.add(new Vect3D(0,0.001,0),pm.getModule("macro").getPoint(iA)));

        }
        void draw()
        {
        lights();
        scale(10);
        strokeWeight(1 / 10f);
        background(0);
        fill(255, 0, 0);
            Vect3D A = pm.getModule("macro").getPoint(iA);
            pushMatrix();
            translate((float)A.x(),(float)A.y(),(float)A.z());
            sphere(5);
            popMatrix();
            pm.computeNSteps(50, false);

        }
