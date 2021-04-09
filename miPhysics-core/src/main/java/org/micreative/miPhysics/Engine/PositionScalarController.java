package org.micreative.miPhysics.Engine;

import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

public class PositionScalarController extends ModuleController {
    protected Index index;
    PositionScalarController(Module module_, DataProvider dataProvider_, String controlledData_, Index index_) {
        super(module_, dataProvider_, controlledData_);
        index=index_;
    }

    public void init() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        try {
            writeMethod = module.getClass().getMethod("set" + controlledData,Index.class,float.class);
        }
        catch(Exception e)
        {
            writeMethod = module.getClass().getSuperclass().getMethod("set" + controlledData,Index.class,float.class);
        }
    }

    public void setData() throws InvocationTargetException, IllegalAccessException {
        writeMethod.invoke(module,index,dataProvider.getData());
    }
//            writeMethod.invoke(this);

}
