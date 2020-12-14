package org.micreative.miPhysics.Engine;


import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class ModuleController
{
    protected Module module;
    protected DataProvider dataProvider;
    protected String controlledData;
    protected Method writeMethod;

    ModuleController(Module module_, DataProvider dataProvider_, String controlledData_)
    {
        module = module_;
        dataProvider=dataProvider_;
        controlledData=controlledData_;
    }

    public void init() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        PropertyDescriptor p = null;
        p = PropertyUtils.getPropertyDescriptor(module,controlledData);
        writeMethod = PropertyUtils.getWriteMethod(p);
    }

    public void setData() throws InvocationTargetException, IllegalAccessException {
        writeMethod.invoke(module,dataProvider.getData());
    }
/*
    protected     String param;
    protected     String name;

    protected float a;
    protected float b;
    protected float min;
    protected float max;
    protected float vmin;
    protected float vmax;

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public void setSubsetName(String subsetName) {
        this.name = subsetName;
    }
    public String getSubsetName(){return name;}

    public float getA() {
        return a;
    }

    public void setA(float a) {
        this.a = a;
    }

    public float getB() {
        return b;
    }

    public void setB(float b) {
        this.b = b;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }



    public float getVmin() {
        return vmin;
    }

    public void setVmin(float vmin) {
        this.vmin = vmin;
    }

    public float getVmax() {
        return vmax;
    }

    public void setVmax(float vmax) {
        this.vmax = vmax;
    }



    public AbstractController(PhysicalModel pm_,String name_,String param_)
{
    pm=pm_;
    param = param_;
    name = name_;
}

protected float linearScale(float val)
{
    return a*val +b;
}

protected void computeLinearParams()
{
    a = (max-min)/(vmax-vmin);
    b = max - a*vmax;
}
*/
}