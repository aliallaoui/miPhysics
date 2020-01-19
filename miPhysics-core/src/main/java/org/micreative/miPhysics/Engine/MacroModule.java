package org.micreative.miPhysics.Engine;


import org.micreative.miPhysics.Vect3D;

import java.util.List;

public abstract class MacroModule extends Module {

    protected List<Vect3D> m_pos;
    protected List<Vect3D> m_posR;
    protected List<Vect3D> m_frc;
    protected List<Double> distR;

    protected double friction;
    protected Vect3D gravity;
    protected int size;

    public int getSize() {
        return size;
    }

    public void setSize(int size)
    {
        this.size=size;
    }

    public int getNbMats()
    {
        return size;
    }

    public Vect3D getPos(int i){return m_pos.get(i);}
    public Vect3D getPosR(int i){return m_posR.get(i);}

    public void addFrc(double frc,int i,Vect3D symPos,int f_index)
    {

        double invDist = 1 / m_pos.get(i).dist(symPos);

        double x_proj = (m_pos.get(i).x - symPos.x) * invDist;
        double y_proj = (m_pos.get(i).y - symPos.y) * invDist;

        m_frc.get(f_index).x += frc * x_proj;
        m_frc.get(f_index).y += frc * y_proj;


    }

    public void setGravity(Vect3D grav) {
        if(gravity == null) gravity = grav;
        else gravity.set(grav);
    }
    public void setFriction(double fric) {
        friction= fric;
    }

}
