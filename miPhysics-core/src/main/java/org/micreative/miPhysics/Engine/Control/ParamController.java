package org.micreative.miPhysics.Engine.Control;

import org.micreative.miPhysics.Engine.PhysicalModel;
import org.micreative.miPhysics.Vect3D;

import org.apache.commons.beanutils.PropertyUtils;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public class ParamController extends AbstractController
{


    private long step=0;
    private float previous_value;
    private boolean inRamp = false;
    private Vect3D center;
    private Method writeMethod;
    private float rampTime;


    public ParamController(PhysicalModel pm_, float rampTime, String name, String param_ ) {
        super(pm_,name,param_);
      this.rampTime =rampTime;
      this.vmin=new Float(0);
      this.vmax=rampTime*pm.getSimRate();
        PropertyDescriptor p;
      try {
          p = PropertyUtils.getPropertyDescriptor(pm.getFirstModuleOfSubset(name), param_);
          writeMethod = PropertyUtils.getWriteMethod(p);
      }
      catch(Exception e)
      {
          System.out.println("could not initialize controller : " + e.getMessage());
      }
    }


    public void updateParams()
    {
        if(inRamp) {
            step++;
            if (step <= vmax) {
                /*if (param.equals("distX")) pm.changeDistXBetweenSubset(center, linearScale(step), subsetName);
                else*/ pm.setParamForSubset(linearScale(step), subsetName, writeMethod);
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
        if(param.equals("distX"))
        {
          center = pm.getBarycenterOfSubset(subsetName);
            System.out.println("controller distX init with center " + center.toString());
          pm.changeDistXBetweenSubset(center,value,subsetName);
        }
        else pm.changeParamOfSubset(value,subsetName,param);
        System.out.println("controller for param " + param + " of subset " + subsetName + " initialized with value " + value);
    }
}