package org.micreative.miPhysics.Engine.Modules;


/* 1D SPRING OBJECT */

import org.micreative.miPhysics.Engine.Link;
import org.micreative.miPhysics.Engine.Mat;

/**
 * Spring interaction: elastic interaction between two Mat elements.
 * @author James Leonard / james.leonard@gipsa-lab.fr
 *
 */
public class SpringDamper1D extends Link {

	public SpringDamper1D(double distance, double K_param, double Z_param, Mat m1, Mat m2) {
		super(distance, m1, m2);
		stiffness = K_param;
		damping = Z_param;
	}

	public void compute() {
		m_dist_1D = calcDist1D();
		applyForces1D( (m_dist_1D - m_dRest)* stiffness + getVel() *  damping );
		m_distR_1D = m_dist_1D;
	}


	public void initDistances() {
		m_dist_1D = this.getMat1().getPos().distZ(this.getMat2().getPos());
		m_distR_1D = this.getMat1().getPosR().distZ(this.getMat2().getPosR());
	}

	protected double getVel() {
		return m_dist_1D - m_distR_1D;
	}

	public double m_dist_1D;
	public double m_distR_1D;

}