package org.micreative.miPhysics.Engine.Modules;

import org.micreative.miPhysics.Engine.Mat;
import org.micreative.miPhysics.Vect3D;

public class Osc1D extends Mat {

	public Osc1D(double M, double K_param, double Z_param, Vect3D initPos, Vect3D initPosR, double friction, Vect3D grav) {
		super(M, initPos, initPosR);

		m_pRest = initPos.z;

		stiffness = K_param;
		damping = Z_param;

		updateAB();
		this.friction = friction;
		gravity = new Vect3D();
		gravity.set(grav);
	}

	public void compute() {

		m_pos.z -= m_pRest;
		m_posR.z -= m_pRest;

		newPos = m_A * m_pos.z - m_B * m_posR.z + m_frc.z * m_invMass;
		m_posR.z = m_pos.z;
		m_pos.z = newPos;
		m_frc.set(0., 0., 0.);

		m_pos.z += m_pRest;
		m_posR.z += m_pRest;

	}


	
	public void updateAB()
	{
		m_A = 2. - m_invMass * stiffness - m_invMass * (damping+friction) ;
		m_B = 1. -m_invMass * (friction + damping) ;
	}

	public double distRest() {  // AJOUT JV Permet de sortir le DeltaX relatif entre mas et sol d'une cel
		return (m_pos.z - m_pRest);
	}

	/* Class attributes */

	private double m_A;
	private double m_B;

	private double stiffness;
	private double damping;

	double m_pRest;
	double newPos;
}
