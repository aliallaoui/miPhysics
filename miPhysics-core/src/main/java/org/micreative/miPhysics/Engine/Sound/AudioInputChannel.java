package org.micreative.miPhysics.Engine.Sound;

import org.micreative.miPhysics.Engine.InputBuffer;
import org.micreative.miPhysics.Engine.ModuleObserver;

import java.nio.FloatBuffer;
import java.util.List;

public class AudioInputChannel extends InputBuffer {
    AudioInputChannel(int channel_, int bufferSize_)
    {
        super(bufferSize_);
        channel = channel_;
    }
    public void copyFromInputs(List<FloatBuffer> inputs)
    {
//        buffer =
        inputs.get(channel).get(buffer);
    }
}
