
package org.micreative.miPhysics.Engine.Control;
//import org.micreative.miPhysics.Engine.Control.AbstractController;
import org.micreative.miPhysics.Engine.PhysicalModel;

public class MidiController extends AbstractController
{

    int control;


    public MidiController(PhysicalModel pm_,int ctrl_,float min_,float max_,String name_,String param_,float rampTime )
    {
        super(pm_,name_,param_);
        control = ctrl_;
        min=min_;
        max=max_;


        vmin = new Float(0);
        vmax = new Float(127);
        computeLinearParams();

        //if(pm.get)
        pm.addParamController(subsetName + "_" + param,subsetName,param,rampTime);
        pm.getParamController(subsetName + "_" + param).init((max+min)/2);
    }

    public static MidiController addMidiController(PhysicalModel pm_,int ctrl_,float min_,float max_,String name_,String param_ ,float rampTime)
    {
        return new MidiController(pm_,ctrl_,min_,max_,name_,param_,rampTime);
    }

    public static MidiController addMidiController(PhysicalModel pm_,int ctrl_,float min_,float max_,String name_,String param_ )
    {
        return new MidiController(pm_,ctrl_,min_,max_,name_,param_,new Float(1)/pm_.getSimRate());
    }



    public void changeParam(int ctrl,int value)
    {
        if(control == ctrl) changeParam(value);
    }

    public void changeParam(int value)
    {
        pm.getParamController(subsetName + "_" + param).triggerRamp(linearScale(value));
    }
}