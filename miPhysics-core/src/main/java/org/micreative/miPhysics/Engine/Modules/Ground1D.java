package org.micreative.miPhysics.Engine.Modules;

import org.micreative.miPhysics.Engine.Mat;
import org.micreative.miPhysics.Vect3D;

public class Ground1D extends Mat {

	public Ground1D(Vect3D initPos) {
		super(1., initPos, initPos); // the mass parameter is unused.
	}

	public void compute() {
		m_frc.set(0., 0., 0.);
	}
}
