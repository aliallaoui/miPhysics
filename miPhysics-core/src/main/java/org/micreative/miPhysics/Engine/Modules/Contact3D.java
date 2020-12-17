package org.micreative.miPhysics.Engine.Modules;


import org.micreative.miPhysics.Engine.Link;
import org.micreative.miPhysics.Engine.Mat;

/**
 * Contact interaction: collision between two Mat elements.
 * When the distance is lower than the threshold distance, a viscoelastic force is applied.
 *
 * @author James Leonard / james.leonard@gipsa-lab.fr
 *
 */
public class Contact3D extends Link {

    /**
     * @param distance
     * @param K_param
     * @param Z_param
     * @param m1
     * @param m2
     */
    public Contact3D( Mat m1, Mat m2) {
        super( m1, m2);
    }

    public void init()
    {
        m_dRsquared = restDistance * restDistance;
    }
    public void computeForces() {
        updateEuclidDist();
        if (m_dist < restDistance)
            this.applyForces(  -(m_dist - restDistance) * stiffness - getVel() * damping);
    }


    /* Reimplement in order to store squared distance */
    public void setDRest(double d) {
        restDistance = d;
        m_dRsquared = restDistance * restDistance;
    }

    private double m_dRsquared;
}
