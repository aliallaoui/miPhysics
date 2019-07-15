package org.micreative.miPhysics.Engine.Modules;


import org.micreative.miPhysics.Engine.Link;
import org.micreative.miPhysics.Engine.Mat;

/**
 * Attraction interaction: distant pull between two Mat elements.
 * Experimental implementation !
 *
 * @author James Leonard / james.leonard@gipsa-lab.fr
 *
 */
public class Attractor3D extends Link {

    /**
     * @param limitDist
     * @param attrFactor
     * @param m1
     * @param m2
     */
    public Attractor3D(double limitDist, double attrFactor, Mat m1, Mat m2) {
        super(limitDist, m1, m2);
        m_attrFactor = attrFactor;
    }

    public void compute() {

        updateEuclidDist();
        if(m_dist > m_dRest)
            this.applyForces(-m_attrFactor / (m_dist*m_dist));
    }


    public boolean changeStiffness(double stiff){
        return false;
    }

    public boolean changeDamping(double damp){
        return false;
    }

    /* Reimplement in order to store squared distance */
    public boolean changeDRest(double d) {
        //m_dRest = d;
        //m_dRsquared = m_dRest * m_dRest;
        return false;
    }

    private double m_attrFactor;
}