package org.micreative.miPhysics.Engine.Control;

import org.micreative.miPhysics.Engine.ModuleController;
import org.micreative.miPhysics.Engine.PhysicalModel;
import org.micreative.miPhysics.Vect3D;

import org.apache.commons.beanutils.PropertyUtils;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public class RampTimeController
{


    private long step=0;
    private float previous_value;
    private boolean inRamp = false;
    private Vect3D center;
    protected Method writeMethod;
    private float rampTime;
    private boolean moduleController = true;


    public RampTimeController(PhysicalModel pm_, float rampTime, String name, String param_ ) {
        //super(pm_,name,param_);

    }

/*
    public void updateParams()
    {
        if(inRamp) {
            step++;
            if (step <= vmax) {
                pm.setParam(name, writeMethod,linearScale(step));
            }
            else inRamp = false;
        }
    }

    public void triggerRamp(float value)
    {
        min=previous_value;
        max=value;
        vmax=rampTime*pm.getSimRate();//in case rampTime changed
        computeLinearParams();
        inRamp = true;
        step = 0;
        previous_value = value;
    }

    public void init(float value)
    {
        previous_value = value;
        min=value;
        max=value;
        computeLinearParams();

        pm.setParamForSubset(value,name,writeMethod);

        System.out.println("controller for param " + param + " of subset " + name + " initialized with value " + value);
    }
*/
}
