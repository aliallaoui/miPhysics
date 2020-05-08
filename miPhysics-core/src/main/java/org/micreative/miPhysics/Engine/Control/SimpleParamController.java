package org.micreative.miPhysics.Engine.Control;


import org.micreative.miPhysics.Engine.PhysicalModel;

public class SimpleParamController extends ParamController {

    protected float value;
    protected  int inputIndex;

    public int getInputIndex() {
        return inputIndex;
    }

    public void setInputIndex(int inputIndex) {
        this.inputIndex = inputIndex;
    }
    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }


    public SimpleParamController(PhysicalModel pm_, String name, String param_, int inputIndex ) {
        super(pm_, 1 / pm_.getSimRate(), name, param_);
        this.inputIndex = inputIndex;
    }

    public void updateParams()
    {
         pm.setParamForSubset(value, subsetName, writeMethod);
    }


}
