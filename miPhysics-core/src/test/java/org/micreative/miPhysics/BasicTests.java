package org.micreative.miPhysics;

import org.junit.BeforeClass;
import org.junit.Test;
import org.micreative.miPhysics.Engine.Control.MidiController;
import org.micreative.miPhysics.Engine.PhysicalModel;

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

        pm.init();

        pm.computeNSteps(300);

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
