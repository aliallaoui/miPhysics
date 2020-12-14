package org.micreative.miPhysics.Engine;

import org.micreative.miPhysics.Vect3D;

/* LINK abstract class */

/**
 * Abstract Link class.
 * Defines generic structure and behavior for all possible Links.
 *
 * @author James Leonard / james.leonard@gipsa-lab.fr
 *
 */
public abstract class Link extends Module{

    /**
     * Constructor method.
     * @param distance resting distance of the Link.
     * @param m1 connected Mat at one end.
     * @param m2 connected Mat at other end.
     */
    public Link(double distance, Module m1, Module m2) {
        m_dRest = distance;
        m_mat1 = m1;
        m_mat2 = m2;

        m_invDist = 0;
    }


    public void computeMoves(){}

    //TODO should throw exception
     public Vect3D getPoint(String name,int index){return null;}
     public Vect3D getPointR(String name,int index) {return null;}

    /**
     * Access the first Mat connected to this Link.
     * @return the first Mat module.
     */
    public Module getMat1() {
        return m_mat1;
    }

    /**
     * Access the second Mat connected to this Link.
     * @return the second Mat module.
     */
    public Module getMat2() {
        return m_mat2;
    }




    public void init()
    {
        initDistances();
    }

    /**
     * Initialise distance and delayed distance for this Link.
     *
     */
    public void initDistances() {
        m_dist = m_mat1.getPoint(0).dist(m_mat2.getPoint(0));
        m_distR = m_mat1.getPointR(0).dist(m_mat2.getPointR(0));
    }

    /**
     * Calculate the euclidian distance between both Mats connected to this Link.
     * @return
     */
    protected double updateEuclidDist() {
        m_distR = m_dist;
        m_dist = m_mat1.getPoint(0).dist(m_mat2.getPoint(0));
        return m_dist;
    }

    // Experimental stuff
    protected double calcDelayedDistance() {
        return m_mat1.getPointR(0).dist(m_mat2.getPointR(0));
    }

    /**
     * Change resting distance for this Link.
     * @param d new resting distance.
     */
    public void setDRest(double d) {
        m_dRest = d;
    }

    /**
     * Get the resting distance of this link
     * @return the resting distance parameter
     */
    public double getDRest(){
        return m_dRest;
    }



    /**
     * Get delayed velocity (per sample) between the two connected Mat modules
     * @return per sample velocity value.
     */
    protected double getVel() {
        return m_dist - m_distR;
    }

    /**
     * Get the distance between mat modules connected by the link
     * @return distance value.
     */
    public double getDist() {
        return m_dist;
    }

    /**
     * Get the elongation (distance minus resting length between mat modules connected by the link
     * @return elongation value.
     */
    public double getElong() {
        return getDist() - m_dRest;
    }

    /**
     * Apply forces to the connected Mat modules
     * @param lnkFrc force to apply symetrically.
     */
    protected void applyForces(double lnkFrc) {

        getMat1().addFrc(lnkFrc,0,getMat2().getPoint(0));
        getMat2().addFrc(lnkFrc,0,getMat1().getPoint(0));
/*
        m_invDist = 1 / m_dist;

        double x_proj = (getMat1().m_pos.x - getMat2().m_pos.x) * m_invDist;
        double y_proj = (getMat1().m_pos.y - getMat2().m_pos.y) * m_invDist;
        double z_proj = (getMat1().m_pos.z - getMat2().m_pos.z) * m_invDist;

        getMat1().m_frc.x += lnkFrc * x_proj;
        getMat1().m_frc.y += lnkFrc * y_proj;
        getMat1().m_frc.z += lnkFrc * z_proj;

        getMat2().m_frc.x -= lnkFrc * x_proj;
        getMat2().m_frc.y -= lnkFrc * y_proj;
        getMat2().m_frc.z -= lnkFrc * z_proj;
  */
    }

    public int getNbPoints(){return 0;}
    public Vect3D getPoint(int index) {
        return null;
    }
    public Vect3D getPointR(int index) {
        return null;
    }

    public void addFrc(double frc,int i,Vect3D symPos){}
    public void setPoint(int index,Vect3D pos){}
    public void setPointX(int index,float pX){}
    public void setPointY(int index,float pY){}
    public void setPointZ(int index,float pZ){}

    /* Class attributes */

    private Module m_mat1;
    private Module m_mat2;

    protected double m_dist;
    protected double m_distR;
    protected double m_dRest;



    private double m_invDist;

    //protected double m_linkFrc;
}
