package org.micreative.miPhysics.Engine.Modules;

import org.micreative.miPhysics.Engine.MacroModule;
import org.micreative.miPhysics.Vect3D;

import java.util.ArrayList;
import java.util.List;

public class String2D extends MacroModule {

    private double restDistance;
    private double m_invMass;
    private double mass;// for beans convenience only


    /* Class attributes */
    protected double friction;
    protected Vect3D gravity;


  //  protected Vect3D leftSol;
  //  protected Vect3D rightSol;


    public String2D(double restDistance, double K_param, double Z_param,double M_param,int size, double stretchRatio,Vect3D left, Vect3D direction)
    {
        stiffness = K_param;
        damping = Z_param;
        m_invMass = 1/M_param;
        this.restDistance = restDistance;

        m_pos = new ArrayList<Vect3D>(size+2);
        m_posR = new ArrayList<Vect3D>(size+2);
        m_frc = new ArrayList<Vect3D>(size);
        distR = new ArrayList<Double>(size+1);

        for(int i=0;i<size+2;i++)
        {
            m_pos.add(left.add(direction.mult(i*restDistance*stretchRatio)));
            m_posR.add(left.add(direction.mult(i*restDistance*stretchRatio)));
            if(i<size) m_frc.add(new Vect3D(0,0,0));
            if(i< size+1) distR.add(restDistance*stretchRatio);
        }
//        distR.add(restDistance*stretchRatio);
 //       leftSol = left.add(direction.mult(-restDistance*stretchRatio));
 //       rightSol = left.add(direction.mult(restDistance*stretchRatio*size));

    }



    public void computeForces()
    {
        double curDist;
        double curFrc;
        for(int i=0;i<m_pos.size()-1;i++)
        {
            curDist = m_pos.get(i).dist(m_pos.get(i+1));
            curFrc =-(curDist-restDistance)*stiffness - (curDist - distR.get(i)) *  damping;
            applyForces(curFrc,curDist,i);
            distR.set(i,curDist);
        }

    }

    /**
     * Apply forces to internal Mat modules
     * @param lnkFrc force to apply symetrically.
     */
    protected void applyForces(double lnkFrc,double dist,int i)
    {
        double invDist = 1 / dist;
        double x_proj,y_proj;

         x_proj = (m_pos.get(i).x - m_pos.get(i+1).x) * invDist;
         y_proj = (m_pos.get(i).y - m_pos.get(i+1).y) * invDist;


        if(i>0)
        {
            m_frc.get(i-1).x += lnkFrc * x_proj;
            m_frc.get(i-1).y += lnkFrc * y_proj;
        }


        if(i<m_pos.size()+1)
        {
            m_frc.get(i).x -= lnkFrc * x_proj;
            m_frc.get(i).y -= lnkFrc * y_proj;
        }
    }

    public void computeMoves()
    {
        Vect3D tmp = new Vect3D(0,0,0);

        for(int i=1;i<m_pos.size()-1;i++)
        {

            tmp.set(m_pos.get(i));
/*
        if (m_controlled) {
            m_pos.add(m_controlVelocity);
        }
        else
        {
        */

            // Calculate the update of the mass's position
            m_frc.get(i-1).mult(m_invMass);
            m_pos.get(i).mult(2 - m_invMass * friction);
            m_posR.get(i).mult(1 -m_invMass * friction);
            m_pos.get(i).sub(m_posR.get(i));
            m_pos.get(i).add(m_frc.get(i-1));
            m_pos.get(i).sub(gravity);

            m_posR.get(i).set(tmp);
            m_frc.get(i-1).set(0., 0., 0.);

            // Constrain to 2D Plane : keep Z axis value constant
            //m_pos.z = tmp.z; useless as forces are computed only on xy
        }
    }

    public void setGravity(Vect3D grav) {
        gravity.set(grav);
    }
    public void setFriction(double fric) {
        friction= fric;
    }
    /**
     * Set the mass parameter.
     * @param M mass value.
     */
    public void setMass(double M) {
        m_invMass = 1 / M;
    }
    public double getMass() {
        return 1/m_invMass;
    }

    public void addFrc(double frc,int i,Vect3D symPos)
    {
        if(i>0 && i<m_pos.size()+1) super.addFrc(frc,i,symPos);
    }
}
