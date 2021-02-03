package org.micreative.miPhysics.Engine;

import org.micreative.miPhysics.Vect3D;

import java.util.ArrayList;
import java.util.List;

//This should called GridMat, there are other topologies -> there should be an abstract class
public class MacroMat extends Module{

    Container<Vect3D> positions;
    Container<Vect3D> positionsR;
    Container<Vect3D> forces;
    Index iterator;

    public MacroMat(int[] dimensions,String fixedPoints)
    {
        positions = new Container<Vect3D>(dimensions);
        positionsR = new Container<Vect3D>(dimensions);
        forces = new Container<Vect3D>(dimensions);
        List<Index> fixedPointsIndexes = new ArrayList<Index>();
        if(dimensions.length ==1) {
            if (fixedPoints.contains("LEFT")) fixedPointsIndexes.add(new Index(0));
            if (fixedPoints.contains("RIGHT")) fixedPointsIndexes.add(new Index(dimensions[0]-1));
        }
        iterator = new Index(dimensions,fixedPointsIndexes);
    }

    @Override
    public void computeForces() {

    }

    @Override
    public void computeMoves() {
        Vect3D tmp = new Vect3D(0,0,0);

        for(iterator.begin();iterator.next();)
        {

            tmp.set(positions.get(iterator));
/*
        if (m_controlled) {
            m_pos.add(m_controlVelocity);
        }
        else
        {
        */

            // Calculate the update of the mass's position
            forces.get(iterator).mult(m_invMass);
            positions.get(iterator).mult(2 - m_invMass * friction);
            positionsR.get(iterator).mult(1 -m_invMass * friction);
            positions.get(iterator).sub(positionsR.get(iterator));
            positions.get(iterator).add(forces.get(iterator));
            positions.get(iterator).sub(gravity);

            positionsR.set(iterator,tmp);
            forces.set(iterator, new Vect3D(0., 0., 0.));//TODO reset method in container and Vect3D ?

            // Constrain to 2D Plane : keep Z axis value constant
            //m_pos.z = tmp.z; useless as forces are computed only on xy
        }
    }

    @Override
    public void init() {

    }

    @Override
    public int getNbPoints() {
        return 0;
    }

    @Override
    public void addFrc(double frc, Index i, Vect3D symPos) {
        //TODO should be a Vect3D method
        double invDist = 1 / positions.get(i).dist(symPos);
        double x_proj = (positions.get(i).x - symPos.x) * invDist;
        double y_proj = (positions.get(i).y - symPos.y) * invDist;

        forces.get(i).x += frc * x_proj;
        forces.get(i).y += frc * y_proj;
//TODO        forces.add(i,new Vect3D(frc*x_proj,frc*y_proj,0));
    }

    @Override
    public void setPoint(Index index, Vect3D pos) {
        positions.set(index,pos);
    }

    @Override
    public void setPointR(Index index, Vect3D pos) {
        positionsR.set(index,pos);
    }

    @Override
    public Vect3D getPoint(Index i) {
        return positions.get(i);
    }

    @Override
    public Vect3D getPointR(Index i) {
        return positionsR.get(i);
    }

    @Override
    public void setPointX(Index index, float pX) {
        positions.get(index).x = pX;
    }

    @Override
    public void setPointY(Index index, float pY) {
        positions.get(index).y = pY;
    }

    @Override
    public void setPointZ(Index index, float pZ) {
        positions.get(index).z = pZ;
    }
}
