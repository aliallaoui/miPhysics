package org.micreative.miPhysics;

import org.junit.BeforeClass;
import org.junit.Test;
import org.micreative.miPhysics.Engine.Control.MidiController;
import org.micreative.miPhysics.Engine.PhysicalModel;
import org.micreative.miPhysics.Engine.Sound.PhyUGen;

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
        pm.addString2D("string");
        pm.addMass3D("percMass", 100, new Vect3D(3, -4, 0.), new Vect3D(0, 2, 0.));
        pm.addMContact2D("perc","string","percMass");
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

        for(int i=0;i<simUGen.getMdl().getModule(0).getNbMats();i++)
            System.out.println(simUGen.getMdl().getModule(0).getPos(i));

        try {
            simUGen.getMdl().computeNSteps(300);
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
        pm.computeNSteps(300);

        //assertTrue(pm.getMatPosAt(0) == new Vect3D(0,0,0));
    }


}
