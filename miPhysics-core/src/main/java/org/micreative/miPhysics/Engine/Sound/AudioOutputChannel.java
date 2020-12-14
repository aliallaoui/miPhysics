package org.micreative.miPhysics.Engine.Sound;
import org.micreative.miPhysics.Engine.OutputBuffer;
import org.micreative.miPhysics.Engine.ModuleObserver; // would it be interesting to have a more generic DataProvider ?

import java.nio.FloatBuffer;
import java.util.List;

//should inherit from a bufferOutput
public class AudioOutputChannel extends OutputBuffer {

    AudioOutputChannel(int channel_, int bufferSize_,
                       ModuleObserver observer_)
    {
        super(bufferSize_,observer_);
        channel = channel_;
    }
    public void copyToOutputs(List<FloatBuffer> outputs)
    {
        outputs.get(channel).put(buffer);
    }


}
