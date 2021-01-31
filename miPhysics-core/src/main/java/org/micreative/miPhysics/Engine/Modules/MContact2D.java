package org.micreative.miPhysics.Engine.Modules;

import org.micreative.miPhysics.Engine.Index;
import org.micreative.miPhysics.Engine.MacroLink;
import org.micreative.miPhysics.Engine.Module;

public class MContact2D extends MacroLink {



    protected double distance;


    /**
     * @param distance
     * @param K_param
     * @param Z_param
     * @param m1
     * @param m2
     */
    public MContact2D(double distance, double K_param, double Z_param,Module m1,Module m2)
    {
        super(m1,m2);
        this.distance = distance;
        stiffness = K_param;
        damping = Z_param;
    }

    public void computeForces() {
        updateEuclidDist();
        for (int i = 0; i < m1.getNbPoints(); i++) {
            for (int j = 0; j < m2.getNbPoints(); j++) {
                if (m_dist.get(i * m2.getNbPoints() + j) < distance)
                    this.applyForces((m_dist.get(i * m2.getNbPoints() + j) - distance) * stiffness + getVel(i * m2.getNbPoints() + j) * damping,
                            new Index(i), new Index(j)); // TODO there should be an index iterator
            }
        }
    }

    public void computeMoves()
    {

    }

}