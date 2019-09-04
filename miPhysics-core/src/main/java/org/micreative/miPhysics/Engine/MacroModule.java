package org.micreative.miPhysics.Engine;


import org.micreative.miPhysics.Vect3D;

import java.util.List;

public abstract class MacroModule extends Module {

    protected List<Vect3D> m_pos;
    protected List<Vect3D> m_posR;
    protected List<Vect3D> m_frc;
    protected List<Double> distR;


    public int getNbMats()
    {
        return m_pos.size();
    }

    public Vect3D getPos(int i){return m_pos.get(i);}
    public Vect3D getPosR(int i){return m_posR.get(i);}

    public void addFrc(double frc,int i,Vect3D symPos)
    {

        double invDist = 1 / m_pos.get(i).dist(symPos);

        double x_proj = (m_pos.get(i).x - symPos.x) * invDist;
        double y_proj = (m_pos.get(i).y - symPos.y) * invDist;


        if(i>0 && i<m_pos.size()+1)
        {
            m_frc.get(i).x += frc * x_proj;
            m_frc.get(i).y += frc * y_proj;
        }

    }

}
