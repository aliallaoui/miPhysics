package org.micreative.miPhysics.Engine;

import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

abstract public class DataProvider {

    public float getData() {
        return data;
    }

    public void setData(float data) {
        this.data = data;
    }

    public abstract void gatherData() throws Exception;

    protected float data;

    public Method getSetMethod(String param) throws Exception {
        return getParamDescriptor(param).getWriteMethod();
    }
    public PropertyDescriptor getParamDescriptor(String param) throws Exception
    {
        PropertyDescriptor p  =  PropertyUtils.getPropertyDescriptor(this, param);
        if (p==null) throw new RuntimeException("Unknown parameter " + param);
        return p;
    }

    public void setParam(Method setter,Object value) throws Exception{
        setter.invoke(this,value);
    }

    public void setParam(String param, Object value)throws Exception{
        Method setter = getSetMethod(param);
        setter.invoke(this,value);
    }


    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
