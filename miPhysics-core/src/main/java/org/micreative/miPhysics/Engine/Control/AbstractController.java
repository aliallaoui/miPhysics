package org.micreative.miPhysics.Engine.Control;


import org.micreative.miPhysics.Engine.PhysicalModel;


public class AbstractController
{



    protected PhysicalModel pm;
    protected     String param;
    protected     String subsetName;

    protected float a;
    protected float b;
    protected float min;
    protected float max;
    protected float vmin;
    protected float vmax;

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public void setSubsetName(String subsetName) {
        this.subsetName = subsetName;
    }
    public String getSubsetName(){return subsetName;}

    public float getA() {
        return a;
    }

    public void setA(float a) {
        this.a = a;
    }

    public float getB() {
        return b;
    }

    public void setB(float b) {
        this.b = b;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }



    public float getVmin() {
        return vmin;
    }

    public void setVmin(float vmin) {
        this.vmin = vmin;
    }

    public float getVmax() {
        return vmax;
    }

    public void setVmax(float vmax) {
        this.vmax = vmax;
    }



    public AbstractController(PhysicalModel pm_,String name,String param_)
{
    pm=pm_;
    param = param_;
    subsetName = name;
}

protected float linearScale(float val)
{
    return a*val +b;
}

protected void computeLinearParams()
{
    a = (max-min)/(vmax-vmin);
    b = max - a*vmax;
}

}