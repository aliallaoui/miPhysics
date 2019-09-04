package org.micreative.miPhysics.Engine;

import org.micreative.miPhysics.Vect3D;

public abstract class Module {

    public Module()
    {

    }

    public abstract void computeForces();

    public abstract void computeMoves();

    public String getType()
    {
        return getClass().toString().split("\\.")[5];
    }


    protected double stiffness;
    protected double damping;

    /**
     * Change the stiffness of this Link.
     * @param k stiffness value.
     * @return true if succesfully changed
     */
    public void setStiffness(double k) {
        stiffness = k; }

    /**
     * Change the damping of this Link.
     * @param z the damping value.
     * @return true if succesfully changed
     */
    public void setDamping(double z){
        damping = z; }

    /**
     * Get the stiffness of this link element
     * @return the stiffness parameter
     */
    public double getStiffness(){return stiffness;}

    /**
     * Get the damping of this link element
     * @return the damping parameter
     */
    public double getDamping(){return damping;}

    public abstract int getNbMats();

    public Vect3D getPos(int i){return null;}
    public Vect3D getPosR(int i){return null;}

    public void addFrc(double frc,int i,Vect3D symPos){}
    public void initDistances(){}
}
