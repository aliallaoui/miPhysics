package org.micreative.miPhysics.Engine;

import org.apache.commons.beanutils.PropertyUtils;
import org.micreative.miPhysics.Vect3D;
import org.micreative.miPhysics.Engine.AbstractModule;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

public abstract class Module implements AbstractModule {

    public Module()
    {
        isInit = false;
    }

    public Module(Map<String,String> params)
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

    public Module(Map<String,String> defaultParams,Map<String,Object> params)
    {


    }
    public void loadParameters(Map<String,String> params)
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

    public void loadParameters(Map<String,String> defaultParams,Map<String,Object> params) throws Exception
    {

        Map<String,Object> finalParams = new HashMap<>();
    //    try {
        for(Map.Entry<String,String> defaultParam:defaultParams.entrySet()) {
            String param = defaultParam.getKey();
            String svalue = defaultParam.getValue();

            PropertyDescriptor p = null;
            p = PropertyUtils.getPropertyDescriptor(this, param);
            String type = p.getPropertyType().toString();
            Object value = null;
            if (type.equals("double")) value = Double.parseDouble(svalue);
            else if (type.equals("float")) value = Float.parseFloat(svalue);
            else if (type.equals("int")) value = Integer.parseInt(svalue);
            else if (type.equals("class org.micreative.miPhysics.Vect3D")) value = Vect3D.fromString(svalue);
            finalParams.put(param, value);

        }
        finalParams.putAll(params);
        for(Map.Entry<String,Object> finalParam:finalParams.entrySet()) {
            PropertyDescriptor p = PropertyUtils.getPropertyDescriptor(this, finalParam.getKey());
            p.getWriteMethod().invoke(this, finalParam.getValue());
        }

    }

    public String getType()
    {
        return getClass().toString().split("\\.")[5];
    }

    abstract public void addFrc(double frc,Index i,Vect3D symPos);
    abstract public void setPoint(Index index,Vect3D pos);
    abstract public void setPointR(Index index,Vect3D pos);

    public Vect3D getPoint(String name,Index index)
    {
        return getPoint(index);
    }
    public Vect3D getPointR(String name,Index index)
    {
        return getPointR(index);
    }



    abstract public Vect3D getPoint(Index i);
    abstract public Vect3D getPointR(Index i);
    public abstract void setPointX(Index index,float pX);
    public abstract void setPointY(Index index,float pY);
    public abstract void setPointZ(Index index,float pZ);


    protected double friction;
    protected Vect3D gravity;

    protected double stiffness;
    protected double damping;
    protected double m_invMass;
    protected double mass;// for beans convenience only
    protected boolean isInit;

    /**
     * Change the stiffness of this Link.
     * @param k stiffness value.
     * @return true if succesfully changed
     */
    public void setStiffness(double k) {
        stiffness = k; }

    /**
     * Change the damping of this Link.
     * @param z the damping value.
     * @return true if succesfully changed
     */
    public void setDamping(double z){
        damping = z; }

    /**
     * Get the stiffness of this link element
     * @return the stiffness parameter
     */
    public double getStiffness(){return stiffness;}

    /**
     * Get the damping of this link element
     * @return the damping parameter
     */
    public double getDamping(){return damping;}

    /**
     * Set the mass parameter.
     * @param M mass value.
     */
    public void setMass(double M) {
        m_invMass = 1 / M;
    }
    public double getMass() {
        return 1/m_invMass;
    }
    public void setGravity(Vect3D grav) {
        if(gravity == null) gravity = grav;
        else gravity.set(grav);
    }
    public void setFriction(double fric) {
        friction= fric;
    }
    public double getFriction() {
        return friction;
    }


}
