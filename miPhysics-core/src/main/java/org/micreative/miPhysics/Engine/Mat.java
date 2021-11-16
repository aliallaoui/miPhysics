package org.micreative.miPhysics.Engine;

import processing.core.PVector;

import org.micreative.miPhysics.*;
/**
 * Abstract class defining Material points.
 *
 * @author James Leonard / james.leonard@gipsa-lab.fr
 *
 */
public abstract class Mat extends Module{


    /**
     * Constructor method.
     *
     * @param initPos initial position.
     * @param initPosR delayed initial position.
     */
    public Mat(Vect3D initPos, Vect3D initPosR) {
        m_pos = new Vect3D(0., 0., 0.);
        m_posR = new Vect3D(0., 0., 0.);
        m_frc = new Vect3D(0., 0., 0.);

        tmp = new Vect3D();
        m_pos.set(initPos);
        m_posR.set(initPosR);

        m_frc.set(0., 0., 0.);
        gravity = new Vect3D(0,0,0);
    }
    public Mat() {
        m_pos = new Vect3D(0., 0., 0.);
        m_posR = new Vect3D(0., 0., 0.);
        m_frc = new Vect3D(0., 0., 0.);
        tmp = new Vect3D();
        gravity = new Vect3D(0,0,0);
    }
    /**
     * Compute the physics of the Mat module.
     *
     */
    public int[] getDimensions()
    {
        int[] ret = new int[1];
        ret[0]=1;
        return ret;
    }

    public void computeForces(){}

    public void init(){}
    /**
     * Apply external force to this Mat module.
     * @param force force to apply.
     */
    protected void applyExtForce(Vect3D force){
        m_frc.add(force);
    }

    public void addFrc(double frc,Index i,Vect3D symPos)
    {
        //TODO should throw exception if i > 0
        //This should be in a lambda function, choosen at init with string param in values in ["XY","XZ,"XYZ","X"...]
        double invDist = 1 / m_pos.dist(symPos);
        double x_proj = (m_pos.x - symPos.x) * invDist;
        double y_proj = (m_pos.y - symPos.y) * invDist;

        m_frc.x += frc * x_proj;
        m_frc.y += frc * y_proj;
    }
    public Vect3D getPoint(){return m_pos;}
    public Vect3D getPointR(){return m_posR;}

    public Vect3D getPoint(Index i){return m_pos;}
    public Vect3D getPointR(Index i){return m_posR;}

    public void setPoint(Vect3D pos)
    {
        m_pos.set(pos);
    }
    public void setPointR(Vect3D pos) {
        m_posR.set(pos);
    }

    public void setPoint(Index i,Vect3D pos)
    {
        m_pos.set(pos);
    }
    public void setPointR(Index i,Vect3D pos)
    {
        m_posR.set(pos);
    }

    public void setPointX(Index i,float pX) {
        this.m_pos.x = pX;
    }
    public void setPointY(Index i,float pY) {
        this.m_pos.y = pY;
    }

    public void setPointZ(Index i,float pZ) {
        this.m_pos.z = pZ;
    }
    public void setForceX(Index index,float fX){}
    public void setForceY(Index index,float fY){}
    public void setForceZ(Index index,float fZ){}
    /**
     * Get the value in the force buffer.
     * @return force value.
     */
    protected Vect3D getFrc() {
        return m_frc;
    }

    public void addFrc(Vect3D force,Index i)
    {
        m_frc.add(force);
    }

    /**
     * Set the mass parameter.
     * @param M mass value.
     */
    public void setMass (double M) {
        m_invMass = 1 / M;
    }


    /**
     * Get the mass parameter.
     * @return the mass value
     */
    public double getMass () {
        return  1. / m_invMass;
    }


    public int getNbPoints() {return 1;}

    public void setGravity(Vect3D grav) {
        gravity.set(grav);
    }
    public void setFriction(double fric) {
        friction= fric;
    }

    /* Class attributes */
    protected double friction;
    protected Vect3D gravity;
    /**
     * Trigger a temporary velocity control
     * @param v the velocity used to displace the mat at each step
     */

    public void triggerVelocityControl(Vect3D v)
    {
        m_controlled = true;
        m_controlVelocity = v;
    }

    /**
     * Stop the current temporary velocity control
     */
    public void stopVelocityControl()
    {
        m_controlled = false;
    }

    /**
     * Get the velocity of the Mat module at index i. Returns a zero filled 3D Vector
     * if the Mat is not found.
     *
     * @param i
     *            the index of the Mat module
     * @return the 3D X,Y,Z velocity coordinates of the module.
     */
 /*   public Vect3D getVelocity(int simRate) {
        if (getNumberOfModules() > i) {
            Vect3D vel = new Vect3D();
            vel.set(modules.get(i).getPoint(0));
            return (vel.sub(modules.get(i).getPointR(0)).mult(simRate));
        }
        else
            return new Vect3D(0., 0., 0.);
    }
   */
    /* Class attributes */

    protected Vect3D m_pos;
    protected Vect3D m_posR;
    protected Vect3D m_frc;

    protected Vect3D tmp;

    public double m_invMass;

    protected boolean m_controlled=false;
    protected Vect3D m_controlVelocity = new Vect3D(0,0,0);
}
