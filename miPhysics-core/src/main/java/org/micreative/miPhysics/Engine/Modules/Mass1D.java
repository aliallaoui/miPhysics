package org.micreative.miPhysics.Engine.Modules;

import org.micreative.miPhysics.Engine.Mat;
import org.micreative.miPhysics.Vect3D;

public class Mass1D extends Mat {

	public Mass1D(double M, Vect3D initPos, Vect3D initPosR, double friction, Vect3D grav) {
		super(M, initPos, initPosR);

		this.friction = friction;
		gravity = new Vect3D();
		gravity.set(grav);
	}

	public void compute() {
		double newPos;
		newPos = (2 - m_invMass * friction) * m_pos.z - (1 - m_invMass * friction) * m_posR.z + m_frc.z * m_invMass;
		m_posR.z = m_pos.z;
		m_pos.z = newPos;
		m_frc.set(0., 0., 0.);
	}

}
