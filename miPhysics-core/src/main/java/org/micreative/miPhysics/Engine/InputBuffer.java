package org.micreative.miPhysics.Engine;

public class InputBuffer extends DataProvider{
    protected int channel;
    protected float[] buffer;
    protected int bufferSize;

    public InputBuffer(int bufferSize_)
    {
        buffer = new float[bufferSize_];
        bufferSize = bufferSize_;
    }

    @Override
    public void gatherData() throws Exception {
        data = buffer[PhysicalModel.loopFrame];
    }
}
