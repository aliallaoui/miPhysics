package org.micreative.miPhysics.Engine;

import org.apache.commons.beanutils.PropertyUtils;
import org.micreative.miPhysics.Vect3D;
import org.micreative.miPhysics.Engine.AbstractModule;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class Module implements AbstractModule {

    public Module()
    {
        isInit = false;
    }

    //TODO if this throws Exception, should be renamaed loadAllParameters
    public void loadParameters(Map<String,String> params) throws Exception {
        for(Map.Entry<String,String> param:params.entrySet()) {
            setParam(param.getKey(), param.getValue());
        }
        init();
    }

    public String getType()
    {
        return getClass().toString().split("\\.")[5];
    }

    //abstract public void addFrc(double frc,Index i,Vect3D symPos);
    abstract public int[] getDimensions();
    abstract public void addFrc(Vect3D force,Index i);

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

    public void setParam(String paramName,Object value) throws Exception{
        getSetMethod(paramName).invoke(this,value);
    }
    public void setParam(String paramName,String v) throws Exception{
        setParam(paramName,getParamValue(paramName,v));
    }

    public void setParam(Method setter,Object value) throws Exception{
        setter.invoke(this,value);
    }

    public Method getSetMethod(String param) throws Exception {
        return getParamDescriptor(param).getWriteMethod();
    }

    public Object getParamValue(String param,String value) throws Exception
    {
        PropertyDescriptor p = getParamDescriptor(param);
        String type = p.getPropertyType().toString();
        if (type.equals("double")) return Double.parseDouble(value);
        else if (type.equals("float")) return Float.parseFloat(value);
        else if (type.equals("int")) return Integer.parseInt(value);
        else if (type.equals("class org.micreative.miPhysics.Vect3D")) return Vect3D.fromString(value);
        else throw new RuntimeException("Unknow parameter type " + type);
    }

    public PropertyDescriptor getParamDescriptor(String param) throws Exception
    {
        PropertyDescriptor p  =  PropertyUtils.getPropertyDescriptor(this, param);
        if (p==null) throw new RuntimeException("Unknown parameter " + param);
        return p;
    }

    public boolean hasParam(String param) throws Exception
    {
       return PropertyUtils.getPropertyDescriptor(this, param)!=null;
    }
    public boolean hasContainerParam(String param) throws Exception {
        return false;
    }
    public AbstractContainer getPositionsContainer(){return null;}
    public AbstractContainer getPositionsRContainer(){return null;}
    public AbstractIterator getMassesIterator(){return null;}
    public AbstractIterator getFixedPointsIterator(){return null;}




}
