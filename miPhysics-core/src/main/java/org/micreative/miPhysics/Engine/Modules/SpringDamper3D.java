package org.micreative.miPhysics.Engine.Modules;


import org.micreative.miPhysics.Engine.Link;
import org.micreative.miPhysics.Engine.Module;

/**
 * Spring-Damper interaction: viscoelastic interaction between two Mat elements.
 * @author James Leonard / james.leonard@gipsa-lab.fr
 *
 */
public class SpringDamper3D extends Link {

    public SpringDamper3D(Module m1, Module m2) {
        super(m1, m2);
    }

    public void init()
    {
        // for experimental implementation
        m_PrevD = calcDelayedDistance();
    }

    public void computeForces() {
        updateEuclidDist();
        applyForces( -(m_dist- restDistance)*(stiffness) - getVel() *  damping );
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