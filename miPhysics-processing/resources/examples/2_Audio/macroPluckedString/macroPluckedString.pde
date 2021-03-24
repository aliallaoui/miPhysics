import org.micreative.miPhysics.Engine.Sound.miPhyAudioClient;
import org.micreative.miPhysics.Vect3D;
import org.micreative.miPhysics.Engine.Index;

import peasy.*;


PeasyCam cam;
Index iA = new Index(0);
Index iB = new Index(1);
miPhyAudioClient  pm=miPhyAudioClient.miPhyClassic(22050,0,2);
void setup()
{
size(1000,700,P3D);
//fullScreen(P3D, 2);
cam=new PeasyCam(this,100);
cam.setMinimumDistance(50);
cam.setMaximumDistance(2500);


int[] dim = new int[1];
dim[0] = 3;

try
        {
pm.addMacroMass("macro","BoundedIterator","LEFT1|RIGHT1","GridContainer",dim);
//  pm.getModule("macro").setGravity(new Vect3D(0,-0.001,0));
pm.addMacroInteraction("SpringDamper","string",
"macro","macro",
"BoundedIterator","LEFT0|RIGHT1",
"BoundedIterator","LEFT1|RIGHT0"
);

pm.addPositionScalarObserver("micro","macro",iB,new Vect3D(0,1,0));
pm.addAudioOutputChannel(0,pm.getDataProvider("micro"));
pm.addAudioOutputChannel(1,pm.getDataProvider("micro"));

pm.init();
pm.getModule("macro").setPointR(iB,
Vect3D.add(new Vect3D(0.1,0,0),pm.getModule("macro").getPoint(iB)));

pm.init();
        }
        catch(Exception e)
        {
        ;
        }

}
void draw()
{
lights();
scale(10);
strokeWeight(1 / 10f);
background(0);
fill(255, 0, 0);
Vect3D A = pm.getModule("macro").getPoint(iB);
pushMatrix();
translate((float)A.x(),(float)A.y(),(float)A.z());
sphere(5);
popMatrix();
//pm.computeNSteps(50, false);

}
