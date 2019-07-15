package org.micreative.miPhysics.Engine.Modules;


import org.micreative.miPhysics.Engine.Link;
import org.micreative.miPhysics.Engine.Mat;

/**
 * Spring-Damper interaction: viscoelastic interaction between two Mat elements.
 * @author James Leonard / james.leonard@gipsa-lab.fr
 *
 */
public class SpringDamper3D extends Link {

    public SpringDamper3D(double distance, double K_param, double Z_param, Mat m1, Mat m2) {
        super(distance, m1, m2);
        m_K = K_param;
        m_Z = Z_param;

        // for experimental implementation
        m_PrevD = calcDelayedDistance();
    }

    public void compute() {
        updateEuclidDist();
        applyForces( -(m_dist-m_dRest)*(m_K) - getVel() *  m_Z );
    }
  
    // Experimental alternate computation algorithm
    /*public void compute() {
	    d = updateEuclidDist();
	    double invD = 1./d;
	    double f_algo = -K * (1 - dRest*invD) - Z * (1 - PrevD*invD);
	    this.applyForces(new Vect3D(f_algo*getDx(), f_algo*getDy(), f_algo*getDz()));		
	    PrevD = d;
	  }*/

    public double m_PrevD;
}