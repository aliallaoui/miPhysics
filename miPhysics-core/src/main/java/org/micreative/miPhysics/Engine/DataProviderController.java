package org.micreative.miPhysics.Engine;

import java.lang.reflect.Method;

public class DataProviderController implements AbstractController{

    DataProvider controlledProvider;
    protected DataProvider dataProvider;
    protected String controlledData;
    protected Method writeMethod;

    DataProviderController(DataProvider controlledProvider_, DataProvider dataProvider_, String controlledData_)
    {
        controlledProvider = controlledProvider_;
        dataProvider=dataProvider_;
        controlledData=controlledData_;
    }

    public void init() throws Exception {
        writeMethod = controlledProvider.getSetMethod(controlledData);
    }

    public void setData() throws Exception {
        controlledProvider.setParam(writeMethod,dataProvider.getData());
    }

}
