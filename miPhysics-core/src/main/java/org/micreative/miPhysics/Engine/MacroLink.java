package org.micreative.miPhysics.Engine;

import java.util.ArrayList;
import java.util.List;

public abstract class MacroLink extends Module {


    protected List<Double> m_dist;
    protected List<Double> m_distR;
    protected Module m1;
    protected Module m2;

    public MacroLink(Module m1,Module m2)
    {
       int n1 =m1.getNbMats();
       int n2=m2.getNbMats();

       m_dist = new ArrayList<>(n1*n2);
       m_distR = new ArrayList<>(n1*n2);
       this.m1 = m1;
       this.m2 = m2;
    }

    protected void updateEuclidDist()
    {
        for(int i=0;i<m1.getNbMats();i++)
        {
            for(int j=0;j<m2.getNbMats();j++)
            {
                m_distR.set(i*m2.getNbMats()+j,m_dist.get(i*m2.getNbMats()+j));
                m_dist.set(i*m2.getNbMats()+j,m1.getPos(i).dist(m2.getPos(j)));
            }
        }
    }

    protected void updateEuclidDist(int i,int j)
    {
       m_distR.set(i*m2.getNbMats()+j,m_dist.get(i*m2.getNbMats()+j));
       m_dist.set(i*m2.getNbMats()+j,m1.getPos(i).dist(m2.getPos(j)));
    }

    public int getNbMats(){return 0;}

    public void applyForces(double frc,int indM1,int indM2)
    {
        for(int i=0;i<m1.getNbMats();i++) {
            for (int j = 0; j < m2.getNbMats(); j++) {

/*                    int ind = m2.getNbMats()*i +j;
                    double m_invDist = 1 / m_dist.get(ind);

                    double x_proj = (m1.getPos(i).x - m2.getPos(j).x) * m_invDist;
                    double y_proj = (m1.getPos(i).y - m2.getPos(j).y) * m_invDist;
                    double z_proj = (m1.getPos(i).z - m2.getPos(j).z) * m_invDist;
*/
                    m1.addFrc(frc,i,m2.getPos(j));
                    m2.addFrc(-frc,j,m1.getPos(i));
/*                    m1.getPos(i).m_frc.x += frc * x_proj;
                    m1.getPos(i).m_frc.y += frc * y_proj;
                    m1.getPos(i).m_frc.z += frc * z_proj;

                    getMat2().m_frc.x -= frc * x_proj;
                    getMat2().m_frc.y -= frc * y_proj;
                    getMat2().m_frc.z -= frc * z_proj;
                    */

                }
            }

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
        for (int i = 0; i < m1.getNbMats(); i++) {
            for (int j = 0; j < m2.getNbMats(); j++) {
                //i * m2.getNbMats() + j iteration
                m_dist.add( m1.getPos(i).dist(m2.getPos(j)));
                m_distR.add( m1.getPosR(i).dist(m2.getPosR(j)));
            }
        }
    }

    public void init()
    {
        initDistances();
    }

}
