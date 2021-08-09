package org.micreative.miPhysics.Engine;

import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;

public class MidiControlProvider extends LinearScale{

    public int getControl() {
        return control;
    }

    public void setControl(int control) {
        this.control = control;
    }

    private int control;
    public MidiControlProvider()
    {
        vmin = 0;
        vmax = 127;
    }

    public void changeParam(int ctrl,int value)
    {
        if(control == ctrl) data = linearScale(value);
    }

    @Override
    public void gatherData() throws Exception {

    }

    public PropertyDescriptor getParamDescriptor(String param) throws Exception
    {
        PropertyDescriptor p  =  PropertyUtils.getPropertyDescriptor(this, param);
        if (p==null) throw new RuntimeException("Unknown parameter " + param);
        return p;
    }
}
