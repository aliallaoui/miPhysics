package org.micreative.miPhysics.Engine;

abstract public class DataProvider {


    public float getData() {
        return data;
    }

    public void setData(float data) {
        this.data = data;
    }

    public abstract void gatherData() throws Exception;

    protected float data;

}
