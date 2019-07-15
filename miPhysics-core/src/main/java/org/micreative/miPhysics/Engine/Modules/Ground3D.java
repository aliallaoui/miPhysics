package org.micreative.miPhysics.Engine.Modules;

import org.micreative.miPhysics.Engine.Mat;
import org.micreative.miPhysics.Vect3D;

/**
 * Fixed point Mat module.
 * @author James Leonard / james.leonard@gipsa-lab.fr
 *
 */
public class Ground3D extends Mat {
	public Ground3D(Vect3D initPos) {
		super(1., initPos, initPos); // the mass parameter is unused.
	}
	public void compute() {
		m_frc.set(0., 0., 0.);
	}
}
