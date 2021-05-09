package org.micreative.miPhysics.Engine;

import org.apache.commons.beanutils.PropertyUtils;
import org.micreative.miPhysics.Vect3D;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;


//This should called GridMat, there are other topologies -> there should be an abstract class
public class MacroMass extends Module{

    AbstractContainer positions;
    AbstractContainer positionsR;
    AbstractContainer forces;
    AbstractIterator iterator;

    public MacroMass(int[] dimensions, AbstractIterator iterator_, String containerType)
    {
        try {
            positions = (AbstractContainer) Class.forName("org.micreative.miPhysics.Engine." + containerType)
                    .getDeclaredConstructor(dimensions.getClass())
                    .newInstance(dimensions);// .newInstance(new Object[] {dimensions});
            positionsR = (AbstractContainer) Class.forName("org.micreative.miPhysics.Engine." + containerType)
                    .getDeclaredConstructor(dimensions.getClass())
                    .newInstance(dimensions);
            forces = (AbstractContainer) Class.forName("org.micreative.miPhysics.Engine." + containerType)
                    .getDeclaredConstructor(dimensions.getClass())
                    .newInstance(dimensions);
        }
        catch(Exception e)
        {
            System.out.println("MacroMat build failed, unknown containerType " + containerType);
        }
        iterator = iterator_;
    }

    public int[] getDimensions()
    {
        return positions.dimensions;
    }
    @Override
    public void computeForces() throws Exception{

    }

    @Override
    public void computeMoves() throws Exception{

        for(iterator.begin();!iterator.end();iterator.next())
        {
            Vect3D tmp = new Vect3D(positions.get(iterator));
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
    public void init() throws Exception{
        iterator.init();
        positions.init("gridVector");
        positionsR.init("gridVector");
        AbstractIterator inverseIterator = iterator.getInverseIterator();
        inverseIterator.init();
        positions.setFixedPointIterator(inverseIterator);
        positionsR.setFixedPointIterator(inverseIterator);
       forces.init("zeroVector");
    }

    @Override
    public int getNbPoints() {
        return positions.size;
    }

      /*  @Override

    public void addFrc(double frc, Index i, Vect3D symPos) {
        //TODO should be a Vect3D method
        double invDist = 1 / positions.get(i).dist(symPos);
        double x_proj = (positions.get(i).x - symPos.x) * invDist;
        double y_proj = (positions.get(i).y - symPos.y) * invDist;

        forces.get(i).x += frc * x_proj;
        forces.get(i).y += frc * y_proj;
//TODO        forces.add(i,new Vect3D(frc*x_proj,frc*y_proj,0));
    }
*/

    public void addFrc(Vect3D force,Index i)
    {
        forces.add(i,force);
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

    public PropertyDescriptor getParamDescriptor(String param) throws Exception
    {
        PropertyDescriptor p  = PropertyUtils.getPropertyDescriptor(this, param);
        if(p==null) {
            p = PropertyUtils.getPropertyDescriptor(positions, param);
        }
        if (p==null) throw new RuntimeException("Unknown parameter " + param);
        return p;
    }

    public void setParam(String param,Object value) throws Exception
    {
        PropertyDescriptor p  = PropertyUtils.getPropertyDescriptor(this, param);
        boolean positions_parameter = (p==null);
        if(positions_parameter)
        {
           getSetMethod(param).invoke(positions,value);
           getSetMethod(param).invoke(positionsR,value);
        }
        else getSetMethod(param).invoke(this,value);
    }

    public void setParam(Method setter, String param,Object value) throws Exception
    {
        PropertyDescriptor p  = PropertyUtils.getPropertyDescriptor(this, param);
        boolean positions_parameter = (p==null);
        if(positions_parameter)
        {
            setter.invoke(positions,value);
            setter.invoke(positionsR,value);
        }
        else setter.invoke(this,value);
    }
    public boolean hasContainerParam(String param) throws Exception {
        return PropertyUtils.getPropertyDescriptor(positions, param) != null;
    }

    public AbstractContainer getPositionsContainer(){return positions;}
    public AbstractContainer getPositionsRContainer(){return positionsR;}
    public AbstractIterator getMassesIterator(){return iterator;}
    public AbstractIterator getFixedPointsIterator(){return iterator.getInverseIterator();}
}
