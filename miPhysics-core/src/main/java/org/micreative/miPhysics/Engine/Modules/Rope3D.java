package org.micreative.miPhysics.Engine.Modules;


import org.micreative.miPhysics.Engine.Link;
import org.micreative.miPhysics.Engine.Mat;

/**
 * Rope interaction: rope-like interaction between two Mat elements.
 * When the distance is lower than the threshold distance, no force is applied.
 * When the distance is higher than the threshold distance, a viscoelastic force is applied.
 * @author James Leonard / james.leonard@gipsa-lab.fr
 *
 */

// TODO: This is identical to the bubble algorithm : REFACTOR
// (but keep distinct versions in the model creation)

public class Rope3D extends Link {

    public Rope3D(double distance, double K_param, double Z_param, Mat m1, Mat m2) {
        super(distance, m1, m2);
        m_K = K_param;
        m_Z = Z_param;
    }

    public void compute() {
        updateEuclidDist();
        if (m_dist > m_dRest)
            applyForces( -(m_dist - m_dRest) * m_K - getVel() *  m_Z );
    }

}
