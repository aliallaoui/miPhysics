package org.micreative.miPhysics.Engine;

import org.apache.commons.beanutils.PropertyUtils;
import org.micreative.miPhysics.Vect3D;

import java.beans.PropertyDescriptor;
import java.util.Map;


//This should called GridMat, there are other topologies -> there should be an abstract class
public class MacroMass extends Module{

    AbstractContainer<Vect3D> positions;
    AbstractContainer<Vect3D> positionsR;
    AbstractContainer<Vect3D> forces;
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
    public void init() throws Exception{
        positions.init("gridVector");
        positionsR.init("gridVector");
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

     public void loadParameters(Map<String,String> params) throws Exception
    {
        params.forEach((k,v)->{
            try {
                PropertyDescriptor p  = PropertyUtils.getPropertyDescriptor(this, k);
                boolean positions_parameter=false;
                if(p==null) {
                    p =  PropertyUtils.getPropertyDescriptor(positions, k);
                    positions_parameter=true;
                }
                String type = p.getPropertyType().toString();
                Object value = null;
                if (type.equals("double")) value = Double.parseDouble(v);
                else if (type.equals("float")) value = Float.parseFloat(v);
                else if (type.equals("int")) value = Integer.parseInt(v);
                else if (type.equals("class org.micreative.miPhysics.Vect3D")) value = Vect3D.fromString(v);
                if(positions_parameter)
                {
                    p.getWriteMethod().invoke(positions,value);
                    p.getWriteMethod().invoke(positionsR,value);
                }
                else p.getWriteMethod().invoke(this,value);
            }
            catch(Exception e)
            {
                System.out.println("error creating string2D with " + params + " cause : " + e.getMessage());
            }
        });
        init();
    }
}
