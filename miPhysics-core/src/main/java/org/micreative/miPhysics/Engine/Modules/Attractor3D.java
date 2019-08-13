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
        this.attrFactor = attrFactor;
    }

    public void compute() {

        updateEuclidDist();
        if(m_dist > m_dRest)
            this.applyForces(-attrFactor / (m_dist*m_dist));
    }


    public double getAttrFactor() {
        return attrFactor;
    }

    public void setAttrFactor(double attrFactor) {
        this.attrFactor = attrFactor;
    }

    private double attrFactor;
}
