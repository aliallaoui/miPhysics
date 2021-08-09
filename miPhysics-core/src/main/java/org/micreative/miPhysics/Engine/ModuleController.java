package org.micreative.miPhysics.Engine;

import java.lang.reflect.Method;


public class ModuleController implements AbstractController
{
    protected Module module;
    protected DataProvider dataProvider;
    protected String controlledData;
    protected Method writeMethod;


    protected String name;
    ModuleController(Module module_, DataProvider dataProvider_, String controlledData_)
    {
        module = module_;
        dataProvider=dataProvider_;
        controlledData=controlledData_;
    }

    public void init() throws Exception {
        writeMethod = module.getSetMethod(controlledData);
    }

    public void setData() throws Exception {
 //       System.out.println("control " + controlledData + " on " + module.getType());
        module.setParam(writeMethod,dataProvider.getData());
    }




}