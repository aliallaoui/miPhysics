package org.micreative.miPhysics.Engine.Sound;

import ddf.minim.*;
import org.micreative.miPhysics.Engine.PhysicalModel;

import java.util.Arrays;

public class PhyUGen extends UGen { //TODO maybe PhyUGen should inherits also PhysicalModel

    protected long step;

    public PhysicalModel getMdl() {
        return mdl;
    }

    protected PhysicalModel mdl;

    public void setListeningPoint(String[] listeningPoint) {
        this.listeningPoint = listeningPoint;
    }
    public void setListeningPoint(String[] listeningPoint,int[] listeningPointsInd) {
        this.listeningPoint = listeningPoint;
        this.listeningPointsInd = listeningPointsInd;
    }

    protected String[] listeningPoint;
    protected int[] listeningPointsInd;
//    private float    oneOverSampleRate;
     protected float    audioOut;
    protected float    currAudio;
    // public ArrayList <PVector> modelPos;
//    public ArrayList <PVector> modelVel;

    float prevSample;

    public PhyUGen(int sampleRate,int baseFrameRate) {
        super();

        this.mdl = new PhysicalModel(sampleRate, (int) baseFrameRate);
        audioOut = 0;
    }

    protected void uGenerate(float[] channels)
    {
        float sample;
     //   synchronized(lock) {


            this.mdl.computeStep();
            step++;

            // calculate the sample value
            if(mdl.matExists(listeningPoint[0]))
            {
                sample =(float)((mdl.getMatPosition(listeningPoint[0]).y)* 2.);

                /* High pass filter to remove the DC offset */
                audioOut =(float)( sample - prevSample + 0.95 * audioOut);
                prevSample = sample;
            }
            else if(mdl.moduleExists(listeningPoint[0]))
            {
                sample =(float)((mdl.getMatPosition(listeningPoint[0],listeningPointsInd[0]).y)* 2.);

                /* High pass filter to remove the DC offset */
                audioOut =(float)( sample - prevSample + 0.95 * audioOut);
                prevSample = sample;
            }
                sample = 0;
       // }
        Arrays.fill( channels, audioOut );
        currAudio = audioOut;
    }

}
