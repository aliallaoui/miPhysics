package org.micreative.miPhysics.Engine;

import processing.core.PVector;

import org.micreative.miPhysics.*;
/**
 * Abstract class defining Material points.
 *
 * @author James Leonard / james.leonard@gipsa-lab.fr
 *
 */
public abstract class Mat {


    /**
     * Constructor method.
     *
     * @param M mass value.
     * @param initPos initial position.
     * @param initPosR delayed initial position.
     */
    public Mat(double M, Vect3D initPos, Vect3D initPosR) {
        m_pos = new Vect3D(0., 0., 0.);
        m_posR = new Vect3D(0., 0., 0.);
        m_frc = new Vect3D(0., 0., 0.);

        tmp = new Vect3D();


        changeMass(M);
        m_pos.set(initPos);
        m_posR.set(initPosR);

        m_frc.set(0., 0., 0.);
    }

    /**
     * Initialise the Mat module.
     * @param X initial position.
     * @param XR initial delayed position.
     */
    protected void init(Vect3D X, Vect3D XR) {
        this.m_pos = X;
        this.m_posR = XR;
    }

    /**
     * Compute the physics of the Mat module.
     *
     */
    public abstract void compute();

    /**
     * Apply external force to this Mat module.
     * @param force force to apply.
     */
    protected void applyExtForce(Vect3D force){
        m_frc.add(force);
    }

    //TODO should this be public ? only SpringDamper1D requires it
    /**
     * Get the current position of this Mat module.
     * @return the module position.
     */
    public Vect3D getPos() {
        return m_pos;
    }

    /**
     * Set the current position of this Mat module.
     * @param newPos the target position to set.
     * @return the module position.
     */
    protected void setPos(Vect3D newPos) {
        m_pos.set(newPos);
        m_posR.set(newPos);
    }



    /**
     * Get the current position of this Mat module (in a PVector format).
     * @return the module position.
     */
    protected PVector getPosVector() {
        return new PVector((float)m_pos.x,(float)m_pos.y,(float)m_pos.z);
    }

    //TODO should this be public ? only SpringDamper1D requires it
    /**
     * Get the delayed position of the module.
     * @return the delayed position.
     */
    public Vect3D getPosR() {
        return m_posR;
    }

    /**
     * Get the value in the force buffer.
     * @return force value.
     */
    protected Vect3D getFrc() {
        return m_frc;
    }

    /**
     * Set the mass parameter.
     * @param M mass value.
     * @return true if set the mass val
     */
    public boolean changeMass (double M) {
        m_invMass = 1 / M;
        return true;
    }

    /**
     * Set the stiffness parameter.
     * @param K stiffness value.
     * @return true if set the stiffness val
     */
    public boolean changeStiffness(double K){return false;}

    /**
     * Set the damping parameter.
     * @param Z damping value.
     * @return true if set the damping val
     */
    public boolean changeDamping(double Z){return false;}

    /**
     * Get the mass parameter.
     * @return the mass value
     */
    public double getMass () {
        return  1. / m_invMass;
    }

    /**
     * Get the stiffness parameter.
     * @return the stiffness value
     */
    public double getStiffness () {
        return  -1.;
    }

    /**
     * Get the damping parameter.
     * @return the damping value
     */
    public double getDamping () {
        return  -1.;
    }


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

    public String getType(){return getClass().toString();}
    /* Class attributes */

    protected Vect3D m_pos;
    protected Vect3D m_posR;
    protected Vect3D m_frc;

    protected Vect3D tmp;

    public double m_invMass;

    protected boolean m_controlled=false;
    protected Vect3D m_controlVelocity = new Vect3D(0,0,0);
}
