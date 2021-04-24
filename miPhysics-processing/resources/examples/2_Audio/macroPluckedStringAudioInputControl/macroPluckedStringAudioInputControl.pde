import org.micreative.miPhysics.Engine.Sound.miPhyAudioClient;
import org.micreative.miPhysics.Vect3D;
import org.micreative.miPhysics.Engine.Index;

import peasy.*;


PeasyCam cam;
        Index iA = new Index(0);
        Index iB = new Index(1);
        Index iC = new Index(2);
        miPhyAudioClient  pm=miPhyAudioClient.miPhyJack(22050,5,2);
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
        pm.addModule("Mass3D","perc");
        pm.addMacroInteraction("Contact","ping",
        "macro","perc",
        "StaticIterator","1","StaticIterator","0");

        pm.addPositionScalarObserver("micro","macro",iB,new Vect3D(0,1,0));
        pm.addAudioOutputChannel(0,pm.getDataProvider("micro"));
        pm.addAudioOutputChannel(1,pm.getDataProvider("micro"));
        pm.addPositionScalarController("percControl","perc",
        "audioInput0","PointY",new Index(0));
        pm.addModuleController("stiffControl","string","audioInput1","stiffness");
        pm.addModuleController("dampingControl","string","audioInput2","damping");
        pm.addModuleController("frictionControl","string","audioInput3","friction");
        pm.addModuleController("stretchControl","macro","audioInput4","stretchFactor");

        pm.init();
        pm.getModule("macro").setPointR(iB,
        Vect3D.add(new Vect3D(0,1,0),pm.getModule("macro").getPoint(iB)));

        pm.setComputePhysics(false);
        pm.setMute(true);
        pm.start();
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
        fill(0, 255, 0);

        fill(0, 0, 255);
        Vect3D B = pm.getModule("macro").getPoint(iA);
        pushMatrix();
        translate((float)B.x(),(float)B.y(),(float)B.z());
        sphere(5);
        popMatrix();
        Vect3D C = pm.getModule("macro").getPoint(iC);
        pushMatrix();
        translate((float)C.x(),(float)C.y(),(float)C.z());
        sphere(5);
        popMatrix();

        Vect3D perc = pm.getModule("perc").getPoint(iA);
        pushMatrix();
        translate((float)perc.x(),(float)perc.y(),(float)perc.z());
        sphere(5);
        popMatrix();
        cam.beginHUD();
        stroke(125, 125, 255);
        strokeWeight(2);
        fill(0, 0, 60, 220);
        rect(0, 0, 250, 50);
        textSize(16);
        fill(255, 255, 255);
        text("muted :" +pm.isMute() + " compute phy:"+pm.isComputePhysics(),10,30);
        text("stiffness 1: " + pm.getParam("string","stiffness"),10,50);
        text("damping 1: " + pm.getParam("string","damping"),10,70);
        text("friction 1: " + pm.getParam("string","friction"),10,90);
       // text("stretchFactor 1: " + pm.getParam("macro","stretchFactor"),10,110);
        cam.endHUD();
//pm.computeNSteps(50, false);

        }

        void keyPressed() {

        if (key == 'm') pm.setMute(!pm.isMute());
        if (key == 'p') pm.setComputePhysics(!pm.isComputePhysics());


        }
