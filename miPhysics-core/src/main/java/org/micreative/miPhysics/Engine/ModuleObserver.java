package org.micreative.miPhysics.Engine;

import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ModuleObserver extends DataProvider {

    protected Module module;
    protected String observedData;
    protected Method readMethod;

    ModuleObserver(Module module_,String observedData_)
    {
        module = module_;
        observedData = observedData_;
    }

    public void init() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
         PropertyDescriptor p = null;
        p = PropertyUtils.getPropertyDescriptor(module,observedData);
        readMethod = PropertyUtils.getReadMethod(p);
    }
    @Override
    public void gatherData() throws Exception
    {
        data=(float)readMethod.invoke(module);
    }
}
