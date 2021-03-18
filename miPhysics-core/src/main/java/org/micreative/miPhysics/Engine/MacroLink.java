package org.micreative.miPhysics.Engine;

import org.micreative.miPhysics.Vect3D;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Class.forName;

public abstract class MacroLink extends Module {


    protected Double distance;
    protected Double distanceR;
    protected Module m1;
    protected Module m2;
    protected AbstractIterator iterator1;
    protected AbstractIterator iterator2;

    public MacroLink(Module m1,Module m2,
                     AbstractIterator iterator1,AbstractIterator iterator2)
    {
       this.iterator1 = iterator1;
       this.iterator2 = iterator2;
       this.m1 = m1;
       this.m2 = m2;
    }

    protected abstract double computeForce(Index i1,Index i2);

    public void computeForces() throws Exception{

        for(iteratorsBegin();!iteratorsEnd();iteratorsNext()) {
            updateEuclidDist(iterator1, iterator2);
            applyForces(computeForce(iterator1,iterator2),iterator1,iterator2);
         }

    }

    protected void updateEuclidDist(Index i,Index j)
    {
       distanceR=m1.getPointR(i).dist(m2.getPointR(j));
        //TODO [opt] compute dsitanceR only on first step
//       distanceR=distance;
       distance=m1.getPoint(i).dist(m2.getPoint(j));
    }

    public int getNbPoints(){return 0;}

    public void applyForces(double frc,Index i1,Index i2)
    {
        Vect3D unit = getForceUnitVector(i1,i2);
        // to reproduce historical bug, repeat this m1.getNbPoints()*m2.getNbPoints() times
        m1.addFrc(Vect3D.mult(unit,-frc),i1);
        m2.addFrc(Vect3D.mult(unit,frc),i2);
    }

    // Default implementation for radial forces, but could be overrided
    public Vect3D getForceUnitVector(Index i1,Index i2)
    {
       return m1.getPoint(i1).getUnitTo(m2.getPoint(i2));
    }

    /*
    public double getVel(int i)
    {
        return distance.get(i) - distanceR.get(i);
    }
*/
    protected void iteratorsNext() throws Exception
    {
        iterator1.next();
        iterator2.next();
    }

    protected void iteratorsBegin() throws Exception
    {
        iterator1.begin();
        iterator2.begin();
    }

    protected boolean iteratorsEnd() throws Exception
    {
        //both should be false or both should be true, we don't check... to do in debug mode
        return iterator1.end() || iterator2.end();
    }


    /**
     * Initialise distance and delayed distance for this Link.
     *
     */
    /*
    public void initDistances() throws Exception{
        iterator1.begin();
        iterator2.begin();
        for(;iteratorsNext();)
        {
            distance.set(iterator, m1.getPoint(iterator1).dist(m2.getPoint(iterator2)));
            distanceR.set(iterator, m1.getPointR(iterator1).dist(m2.getPointR(iterator2)));
        }
    }
*/

    public void init() throws Exception
    {
    //    initDistances();
    }

    public Vect3D getPoint(Index index) {
        return null;
    }
    public Vect3D getPointR(Index index) {
        return null;
    }

   // public void addFrc(double frc,Index i,Vect3D symPos){}
    public void setPoint(Index index,Vect3D pos){}
    public void setPointR(Index index,Vect3D pos){}
    public void setPointX(Index index,float pX){}
    public void setPointY(Index index,float pY){}
    public void setPointZ(Index index,float pZ){}

    static int[] dimensions=new int[1];
    public int[] getDimensions()
    {
        return dimensions;
    }
     public void addFrc(Vect3D force,Index i)
     {
         //should throw exception
     }

     public void computeMoves(){}
}
