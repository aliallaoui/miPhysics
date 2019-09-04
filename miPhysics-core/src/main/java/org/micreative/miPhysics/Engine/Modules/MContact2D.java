package org.micreative.miPhysics.Engine.Modules;

import org.micreative.miPhysics.Engine.MacroLink;
import org.micreative.miPhysics.Engine.Module;

import java.util.ArrayList;
import java.util.List;

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
        for (int i = 0; i < m1.getNbMats(); i++) {
            for (int j = 0; j < m2.getNbMats(); j++) {
                if (m_dist.get(i * m2.getNbMats() + j) < distance)
                    this.applyForces(-(m_dist.get(i * m2.getNbMats() + j) - distance) * stiffness - getVel(i * m2.getNbMats() + j) * damping, i, j);
            }
        }
    }

    public void computeMoves()
    {

    }
}