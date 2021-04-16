package org.micreative.miPhysics.Engine;

public class ContainerController extends ModuleController{

    ContainerController(Module module_, DataProvider dataProvider_, String controlledData_) {
        super(module_, dataProvider_, controlledData_);
    }

    public void setData() throws Exception {
        module.getPositionsContainer().setParam(writeMethod,dataProvider.getData());
    }
}
