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
    public Contact3D(double distance, double K_param, double Z_param, Mat m1, Mat m2) {
        super(distance, m1, m2);
        stiffness = K_param;
        damping = Z_param;

        m_dRsquared = m_dRest*m_dRest;
    }

    public void compute() {
        updateEuclidDist();
        if (m_dist < m_dRest)
            this.applyForces(  -(m_dist - m_dRest) * stiffness - getVel() * damping);
    }


    /* Reimplement in order to store squared distance */
    public void setDRest(double d) {
        m_dRest = d;
        m_dRsquared = m_dRest * m_dRest;
    }

    private double m_dRsquared;
}
