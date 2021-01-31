package org.micreative.miPhysics.Engine;

import org.apache.commons.beanutils.PropertyUtils;
import org.micreative.miPhysics.Vect3D;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

//maybe it would be better to have common base class ScalarObserver for those two
public class PositionScalarObserver extends ModuleObserver {
    protected Index posIndex;
    protected Vect3D projection;
    PositionScalarObserver(Module module_, Index posIndex_, Vect3D projection_)
    {
        super(module_,"position");
        posIndex=posIndex_;
        projection=projection_;
    }
    public void init() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        PropertyDescriptor p = null;
        p = PropertyUtils.getPropertyDescriptor(module,observedData);
        readMethod = PropertyUtils.getReadMethod(p);
    }
    @Override
    public void gatherData() throws Exception
    {
        data=module.getPoint(posIndex).scalarProduct(projection);
    }
}
