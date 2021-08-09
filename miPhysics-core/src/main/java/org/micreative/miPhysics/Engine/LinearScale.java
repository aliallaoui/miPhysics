package org.micreative.miPhysics.Engine;

public abstract class LinearScale extends DataProvider{



    protected float a;
    protected float b;
    protected float min;
    protected float max;
    protected float vmin;
    protected float vmax;

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
        computeLinearParams();
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
        computeLinearParams();
    }

    public float getVmax() {
        return vmax;
    }

    public void setVmax(float vmax) {
        this.vmax = vmax;
        computeLinearParams();
    }

    public float getVmin() {
        return vmin;
    }

    public void setVmin(float vmin) {
        this.vmin = vmin;
        computeLinearParams();
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
