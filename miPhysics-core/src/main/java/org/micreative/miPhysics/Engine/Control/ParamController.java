package org.micreative.miPhysics.Engine.Control;

import org.micreative.miPhysics.Engine.PhysicalModel;
import org.micreative.miPhysics.Vect3D;

public class ParamController extends AbstractController
{


    private long step=0;
    private float previous_value;
    private boolean inRamp = false;
    private Vect3D center;

    public ParamController(PhysicalModel pm_, float rampTime, String name, String param_ ) {
        super(pm_,name,param_);
      params.put("rampTime",rampTime);
      params.put("vmin",new Float(0));
      params.put("vmax",rampTime*pm.getSimRate());
    }


    public void updateParams()
    {
        if(inRamp) {
            step++;
            if (step <= params.get("vmax")) {
                //System.out.println("change  param " + param + " of subset " + subsetName + " with value " + linearScale(step));
                if (param.equals("distX")) pm.changeDistXBetweenSubset(center, linearScale(step), subsetName);
                else pm.changeParamOfSubset(linearScale(step), subsetName, param);
            }
            else inRamp = false;
        }
    }

    public void triggerRamp(float value)
    {
        params.put("min",previous_value);
        params.put("max",value);
        params.put("vmax",params.get("rampTime")*pm.getSimRate());//in case it changed
        computeLinearParams();
        //System.out.println("trigger ramp for " + params.get("rampTime")*pm.getSimRate() + " steps from " + previous_value + " to " + value + " a=" + a + " b="+b);
        inRamp = true;
        step = 0;
        previous_value = value;
    }

    public void init(float value)
    {
        previous_value = value;
        params.put("min",value);
        params.put("max",value);
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