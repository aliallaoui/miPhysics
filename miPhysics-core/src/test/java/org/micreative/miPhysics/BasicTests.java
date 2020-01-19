package org.micreative.miPhysics;

import org.junit.BeforeClass;
import org.junit.Test;
import org.micreative.miPhysics.Engine.Control.MidiController;
import org.micreative.miPhysics.Engine.PhysicalModel;
import org.micreative.miPhysics.Engine.Sound.PhyUGen;

import java.util.HashMap;
import java.util.Map;

public class BasicTests {

    public @Test void testOneMass1D() throws Exception
    {
        PhysicalModel pm = new PhysicalModel();
        pm.addMass1D("m",1,new Vect3D(0,0,0),new Vect3D(0,0,0));

        pm.init();

        pm.computeNSteps(300);

        //assertTrue(pm.getMatPosAt(0) == new Vect3D(0,0,0));
    }

    public @Test void testString2D() throws Exception
    {
        PhysicalModel pm = new PhysicalModel();
        Map<String,Object> params = new HashMap<>();
        params.put("size",7);
        pm.addString2D("string" ,params);
        pm.setParam("string","stretchFactor",1.2);
        pm.addMass3D("percMass", 100, new Vect3D(3, -4, 0.), new Vect3D(0, 2, 0.));
        pm.addMContact2D("perc","string","percMass");
        pm.init();

//        for(int i=0;i<pm.getModule(0).getNbMats();i++)
 //       System.out.println(pm.getModule(0).getPos(i));
        int nbMats = pm.getNumberOfMats();


        pm.computeNSteps(500);

        //assertTrue(pm.getMatPosAt(0) == new Vect3D(0,0,0));
    }

    public @Test void testOsc2DControl() throws Exception
    {
        PhysicalModel pm = new PhysicalModel();
        pm.addGround3D("ground",new Vect3D(0,0,0));
        pm.addMass3D("osc", 100, new Vect3D(3, -4, 0.), new Vect3D(0, 2, 0.));
        pm.addSpringDamper3D("spring",1,0.1,0.0001,"ground","osc");

        pm.addParamController("spring_stiff","spring","stiffness",0.1f);
        pm.init();

        for(int i=0;i<pm.getModule(0).getNbMats();i++)
            System.out.println(pm.getModule(0).getPos(i));

        pm.computeNSteps(500);

        //assertTrue(pm.getMatPosAt(0) == new Vect3D(0,0,0));
    }

    public @Test void testString2DControl() throws Exception
    {
        PhysicalModel pm = new PhysicalModel();
        pm.addGround3D("ground",new Vect3D(0,0,0));
        pm.addMass3D("osc1", 100, new Vect3D(3, -4, 0.), new Vect3D(0, 2, 0.));
        pm.addMass3D("osc2", 100, new Vect3D(4, -4, 0.), new Vect3D(0, 2, 0.));
        pm.addSpringDamper3D("spring1",1,0.1,0.0001,"ground","osc1");
        pm.addSpringDamper3D("spring2",1,0.1,0.0001,"osc1","osc2");

        pm.createLinkSubset("spring");
        pm.addLinkToSubset("spring1","spring");
        pm.addLinkToSubset("spring2","spring");
        pm.addParamController("spring_stiff","spring","stiffness",0.1f);
        pm.init();

        for(int i=0;i<pm.getModule(0).getNbMats();i++)
            System.out.println(pm.getModule(0).getPos(i));

        pm.computeNSteps(500);

        //assertTrue(pm.getMatPosAt(0) == new Vect3D(0,0,0));
    }


    public @Test void testString2DAudio() throws Exception
    {

        PhyUGen simUGen = new PhyUGen(22050,50); //<>// //<>//


        simUGen.getMdl().setGravity(0);
        simUGen.getMdl().setFriction(0);

        simUGen.getMdl().addMass2DPlane("guideM1", 1000000000, new Vect3D(2, -4, 0.), new Vect3D(0, 2, 0.));
        simUGen.getMdl().addMass2DPlane("guideM2", 1000000000, new Vect3D(4, -4, 0.), new Vect3D(0, 2, 0.));
        simUGen.getMdl().addMass2DPlane("guideM3", 1000000000, new Vect3D(3, -3, 0.), new Vect3D(0, 2, 0.));
        simUGen.getMdl().addMass3D("percMass", 100, new Vect3D(3, -4, 0.), new Vect3D(0, 2, 0.));
        simUGen.getMdl().addSpringDamper3D("test", 1, 1., 1., "guideM1", "percMass");
        simUGen.getMdl().addSpringDamper3D("test", 1, 1., 1., "guideM2", "percMass");
        simUGen.getMdl().addSpringDamper3D("test", 1, 1., 1., "guideM3", "percMass");

        simUGen.getMdl().addString2D("string");

        simUGen.getMdl().addMContact2D("perc","percMass","string");
   /*
    for(int i= 0; i< nbmass; i++)
      simUGen.getMdl().addContact3D("col", c_dist, c_k, c_z, "percMass", "str"+i);
    simUGen.getMdl().addContact3D("col", c_gnd, 10, c_z, "percMass", "gnd0");
    simUGen.getMdl().addContact3D("col", c_gnd, 10, c_z, "percMass", "gnd1");
*/
        String[] listeningPoints = new String[1];
        listeningPoints[0] = "string";
        int[] listeningPointsInd = new int[1];
        listeningPointsInd[0] = 3;
        simUGen.setListeningPoint(listeningPoints,listeningPointsInd);

        simUGen.getMdl().init();

        int nbMats = simUGen.getMdl().getNumberOfMats();

        for(int i=0;i<simUGen.getMdl().getModule(0).getNbMats();i++)
            System.out.println(simUGen.getMdl().getModule(0).getPos(i));

        try {
            //simUGen.testUGen();
            simUGen.getMdl().computeNSteps(1);
            simUGen.getMdl().setParam("string","stretchFactor",1.1);
        }
        catch(Exception e)
        {
            System.out.println("An error occured : " + e.getMessage()) ;
        }
        //assertTrue(pm.getMatPosAt(0) == new Vect3D(0,0,0));
    }
    public @Test void testString2DMidi() throws Exception
    {
        PhysicalModel pm = new PhysicalModel();
        pm.addString2D("string");

        pm.init();
        MidiController mc = MidiController.addMidiController(pm,3, 0.5f, 1.5f, "string", "mass", 0.05f);
        MidiController mc2 = MidiController.addMidiController(pm,4, 0.5f, 1.5f, "string", "stretchFactor", 0.05f);
        pm.computeNSteps(3);
        mc2.changeParam(67);
        pm.computeNSteps(300);
        //assertTrue(pm.getMatPosAt(0) == new Vect3D(0,0,0));
    }


}
