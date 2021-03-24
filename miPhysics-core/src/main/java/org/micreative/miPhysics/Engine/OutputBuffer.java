package org.micreative.miPhysics.Engine;

public class OutputBuffer {
    protected int channel;
    protected DataProvider observer;
    protected float[] buffer;
    protected int bufferSize;

    public OutputBuffer(int bufferSize_, DataProvider observer_)
    {
        observer= observer_;
        buffer = new float[bufferSize_];
        bufferSize = bufferSize_;
    }

    public void fillBuffer()
    {
        buffer[PhysicalModel.loopFrame]=observer.getData();
    }

    public void fillZeroBuffer()
    {
        for(int i=0;i<bufferSize;i++) buffer[i]=0.f;
    }
}
