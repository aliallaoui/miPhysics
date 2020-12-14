package org.micreative.miPhysics.Engine;

import org.micreative.miPhysics.Vect3D;

import java.util.ArrayList;
import java.util.List;

public abstract class MacroLink extends Module {


    protected List<Double> m_dist;
    protected List<Double> m_distR;
    protected Module m1;
    protected Module m2;

    public MacroLink(Module m1,Module m2)
    {
       int n1 =m1.getNbPoints();
       int n2=m2.getNbPoints();

       m_dist = new ArrayList<>(n1*n2);
       m_distR = new ArrayList<>(n1*n2);
       this.m1 = m1;
       this.m2 = m2;
    }

    protected void updateEuclidDist()
    {
        for(int i=0;i<m1.getNbPoints();i++)
        {
            for(int j=0;j<m2.getNbPoints();j++)
            {
                m_distR.set(i*m2.getNbPoints()+j,m_dist.get(i*m2.getNbPoints()+j));
                m_dist.set(i*m2.getNbPoints()+j,m1.getPoint(i).dist(m2.getPoint(j)));
            }
        }
    }

    protected void updateEuclidDist(int i,int j)
    {
       m_distR.set(i*m2.getNbPoints()+j,m_dist.get(i*m2.getNbPoints()+j));
       m_dist.set(i*m2.getNbPoints()+j,m1.getPoint(i).dist(m2.getPoint(j)));
    }

    public int getNbPoints(){return 0;}

    public void applyForces(double frc,int indM1,int indM2)
    {
        // to reproduce historical bug, repeat this m1.getNbPoints()*m2.getNbPoints() times
        m1.addFrc(frc,indM1,m2.getPoint(indM2));
        m2.addFrc(-frc,indM2,m1.getPoint(indM1));
    }

    public double getVel(int i)
    {
        return m_dist.get(i) - m_distR.get(i);
    }

    /**
     * Initialise distance and delayed distance for this Link.
     *
     */
    public void initDistances() {
        for (int i = 0; i < m1.getNbPoints(); i++) {
            for (int j = 0; j < m2.getNbPoints(); j++) {
                //i * m2.getNbPoints() + j iteration
                m_dist.add( m1.getPoint(i).dist(m2.getPoint(j)));
                m_distR.add( m1.getPointR(i).dist(m2.getPointR(j)));
            }
        }
    }

    public void init()
    {
        initDistances();
    }

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
}
