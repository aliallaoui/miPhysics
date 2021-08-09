package org.micreative.miPhysics.Engine;


import org.micreative.miPhysics.Engine.Modules.SpringDamper3D;

public class RampInterpolator extends LinearScale{

    private boolean inRamp = false;
    private float rampTime;
    private long step=0;
    private float previous_value;

    public RampInterpolator()
    {
        vmin = 0;
        min = 0;
        max = 0;
        previous_value = 0;
    }

    public void setMax(float target_value) {
        this.max = target_value;
        triggerRamp();
    }

    public float getRampTime() {
        return rampTime;
    }

    public void setRampTime(float rampTime) {
        this.rampTime = rampTime;
    }

    @Override
    public void gatherData() throws Exception {
        if(this.max != data) {
            step++;
            if (step <= vmax) {
                data = linearScale(step);
            }
            else inRamp = false;
        }
    }

    public void triggerRamp()
    {
        System.out.println("triggerRamp previous_value" + previous_value);
        if (max !=previous_value) {
            min = previous_value;
            vmax = rampTime * PhysicalModel.simRate;//in case rampTime changed
            computeLinearParams();
            step = 0;
            data = linearScale(0);
            previous_value = max;
        }
    }



}
