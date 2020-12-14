package org.micreative.miPhysics.Engine;

import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

public class PositionScalarController extends ModuleController {
    protected int index;
    PositionScalarController(Module module_, DataProvider dataProvider_, String controlledData_, int index_) {
        super(module_, dataProvider_, controlledData_);
        index=index_;
    }

    public void init() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        writeMethod = this.getClass().getMethod("set"+ controlledData);
    }

    public void setData() throws InvocationTargetException, IllegalAccessException {
        writeMethod.invoke(module,index,dataProvider.getData());
    }
//            writeMethod.invoke(this);

}
