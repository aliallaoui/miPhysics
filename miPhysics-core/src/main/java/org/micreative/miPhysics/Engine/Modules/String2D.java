package org.micreative.miPhysics.Engine.Modules;

import org.apache.commons.beanutils.PropertyUtils;
import org.micreative.miPhysics.Engine.MacroModule;
import org.micreative.miPhysics.Vect3D;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class String2D extends MacroModule {



    private double restDistance;



    /* Class attributes */


    protected double stretchFactor;
    protected Vect3D direction;
    protected Vect3D center;

    public Vect3D getCenter() {
        return center;
    }

    public void setCenter(Vect3D center) {
        this.center = center;
    }


    public double getStretchFactor() {
        return stretchFactor;
    }

    public void setStretchFactor(double stretchFactor) {
        this.stretchFactor = stretchFactor;
        if (isInit)
        {
            Vect3D dir = new Vect3D(direction);
            dir.mult(restDistance* stretchFactor);
            Vect3D left = Vect3D.add(center,Vect3D.mult(dir,-(size+1.)/2.));
            Vect3D right = Vect3D.add(center,Vect3D.mult(dir,(size+1.)/2.));
            m_pos.set(0,left);
            m_pos.set(size+1,right);
        }
    }

    public Vect3D getDirection() {
        return direction;
    }

    public void setDirection(Vect3D direction) {
        this.direction = direction;
    }

    public double getRestDistance() {
        return restDistance;
    }

    public void setRestDistance(double restDistance) {
        this.restDistance = restDistance;
    }

    public String2D(Map<String,String> params)
    {
        params.forEach((k,v)->{
            try {
                PropertyDescriptor p  = PropertyUtils.getPropertyDescriptor(this, k);
                String type = p.getPropertyType().toString();
                Object value = null;
                if (type.equals("double")) value = Double.parseDouble(v);
                else if (type.equals("float")) value = Float.parseFloat(v);
                else if (type.equals("int")) value = Integer.parseInt(v);
                else if (type.equals("class org.micreative.miPhysics.Vect3D")) value = Vect3D.fromString(v);
                p.getWriteMethod().invoke(this,value);
            }
            catch(Exception e)
            {
                System.out.println("error creating string2D with " + params + " cause : " + e.getMessage());
            }
        });
        init();
    }

    public String2D(Map<String,String> defaultParams,Map<String,Object> params)
    {
        Map<String,Object> finalParams = new HashMap<>();

        defaultParams.forEach((k,v)->{
            try {
                PropertyDescriptor p  = PropertyUtils.getPropertyDescriptor(this, k);
                String type = p.getPropertyType().toString();
                Object value = null;
                if (type.equals("double")) value = Double.parseDouble(v);
                else if (type.equals("float")) value = Float.parseFloat(v);
                else if (type.equals("int")) value = Integer.parseInt(v);
                else if (type.equals("class org.micreative.miPhysics.Vect3D")) value = Vect3D.fromString(v);
                finalParams.put(k,value);
            }
            catch(Exception e)
            {
                System.out.println("error creating string2D with " + params + " cause : " + e.getMessage());
            }
        });
        finalParams.putAll(params);
        finalParams.forEach((k,v)->{
            try {
                PropertyDescriptor p  = PropertyUtils.getPropertyDescriptor(this, k);
                p.getWriteMethod().invoke(this,v);
            }
            catch(Exception e)
            {
                System.out.println("error creating string2D with " + params + " cause : " + e.getMessage());
            }
        });

    }


    public void init()
    {
        m_pos = new ArrayList<Vect3D>(size+2);
        m_posR = new ArrayList<Vect3D>(size+2);
        m_frc = new ArrayList<Vect3D>(size);
        distR = new ArrayList<Double>(size+1);
        Vect3D dir = direction;
        dir.mult(restDistance* stretchFactor);
        Vect3D left = Vect3D.add(center,Vect3D.mult(dir,-(size+1.)/2.));
        for(int i=0;i<size+2;i++)
        {
            Vect3D curPos = Vect3D.add(left,Vect3D.mult(dir,i));;
            m_pos.add(curPos);
            m_posR.add(new Vect3D(curPos));
            if(i<size) m_frc.add(new Vect3D(0,0,0));
            if(i< size+1) distR.add(restDistance* stretchFactor);
        }
        isInit = true;
    }

   /*
    public void setSize(int size)
    {
        m_pos = new ArrayList<Vect3D>(size+2);
        m_posR = new ArrayList<Vect3D>(size+2);
        m_frc = new ArrayList<Vect3D>(size);
        distR = new ArrayList<Double>(size+1);
    }
*/
    public void computeForces()
    {
        double curDist;
        double curFrc;
        for(int i=0;i<m_pos.size()-1;i++)
        {
            curDist = m_pos.get(i).dist(m_pos.get(i+1));
            curFrc =-(curDist-restDistance)*stiffness - (curDist - distR.get(i)) *  damping;
            applyForces(curFrc,curDist,i);
            distR.set(i,curDist);
        }
        curFrc=0;
        curFrc+=1;


    }

    /**
     * Apply forces to internal Mat modules
     * @param lnkFrc force to apply symetrically.
     */
    protected void applyForces(double lnkFrc,double dist,int i)
    {
        double invDist = 1 / dist;
        double x_proj,y_proj;

         x_proj = (m_pos.get(i).x - m_pos.get(i+1).x) * invDist;
         y_proj = (m_pos.get(i).y - m_pos.get(i+1).y) * invDist;


        if(i>0)
        {
            m_frc.get(i-1).x += lnkFrc * x_proj;
            m_frc.get(i-1).y += lnkFrc * y_proj;
        }


        if(i<m_pos.size()-2)
        {
            m_frc.get(i).x -= lnkFrc * x_proj;
            m_frc.get(i).y -= lnkFrc * y_proj;
        }
    }

    public void computeMoves()
    {
        Vect3D tmp = new Vect3D(0,0,0);

        for(int i=1;i<m_pos.size()-1;i++)
        {

            tmp.set(m_pos.get(i));
/*
        if (m_controlled) {
            m_pos.add(m_controlVelocity);
        }
        else
        {
        */

            // Calculate the update of the mass's position
            m_frc.get(i-1).mult(m_invMass);
            m_pos.get(i).mult(2 - m_invMass * friction);
            m_posR.get(i).mult(1 -m_invMass * friction);
            m_pos.get(i).sub(m_posR.get(i));
            m_pos.get(i).add(m_frc.get(i-1));
            m_pos.get(i).sub(gravity);

            m_posR.get(i).set(tmp);
            m_frc.get(i-1).set(0., 0., 0.);

            // Constrain to 2D Plane : keep Z axis value constant
            //m_pos.z = tmp.z; useless as forces are computed only on xy
        }
    }



    public void addFrc(double frc,int i,Vect3D symPos)
    {
        if(i>0 && i<m_pos.size()-1) addFrc(frc,i,symPos,i-1);
    }

    //getNbPoints should be reimplemented as nbMats = size+2 and not size as defined in MacroModule
    public int getNbPoints(){return size+2;}

    public void setPoint(int i,Vect3D pos)
    {
        m_pos.set(i,pos);
    }

    public void setPointX(int i,float pX) {
        this.m_pos.get(i).x = pX;
    }
    public void setPointY(int i,float pY) {
        this.m_pos.get(i).y = pY;
    }

    public void setPointZ(int i,float pZ) {
        this.m_pos.get(i).z = pZ;
    }

}
