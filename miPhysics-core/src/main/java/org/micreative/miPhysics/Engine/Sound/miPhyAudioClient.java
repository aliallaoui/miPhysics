package org.micreative.miPhysics.Engine.Sound;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.maven.doxia.macro.EchoMacro;
import org.jaudiolibs.audioservers.AudioClient;
import org.jaudiolibs.audioservers.AudioConfiguration;
import org.jaudiolibs.audioservers.AudioServer;
import org.jaudiolibs.audioservers.AudioServerProvider;
import org.jaudiolibs.audioservers.ext.ClientID;
import org.jaudiolibs.audioservers.ext.Connections;
import org.micreative.miPhysics.Engine.*;
import org.micreative.miPhysics.Vect3D;


/* Based on SineAudioClient, in the example project of jaudiolibs */
public class miPhyAudioClient extends PhysicalModel implements  AudioClient {

    final protected AudioServer server;

    private float[] data;
    private Thread runner;

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    private boolean mute = true;


    public static miPhyAudioClient miPhyJack(float sampleRate,int inputChannelCount, int outputChannelCount)
    {
        try {
            return new miPhyAudioClient(sampleRate, inputChannelCount, outputChannelCount, bufferSize, "JACK");
        }
        catch(Exception e)
        {
            Logger.getLogger(miPhyAudioClient.class.getName()).log(Level.SEVERE, null, "Could not create a jack miPhyAudioClient");
        }
        return null;
    }

    public static miPhyAudioClient miPhyClassic(float sampleRate,int inputChannelCount, int outputChannelCount)
    {
        try {
            return new miPhyAudioClient(sampleRate, inputChannelCount, outputChannelCount, bufferSize, "JavaSound");
        }
        catch(Exception e)
        {
            Logger.getLogger(miPhyAudioClient.class.getName()).log(Level.SEVERE, null, "Could not create a jack miPhyAudioClient");
        }
        return null;
    }

    public miPhyAudioClient(float sampleRate,int inputChannelCount, int outputChannelCount, int bufferSize, String serverType) throws Exception
    {
        super("AudioPhysicalModel",(int)sampleRate); //maybe not a good thing to have a default name
        outputBuffers = new ArrayList<OutputBuffer>(outputChannelCount);
        for(int i=0;i< inputChannelCount;i++) addAudioInputChannel(i);

        AudioServerProvider provider = null;
        for (AudioServerProvider p : ServiceLoader.load(AudioServerProvider.class)) {
            if (serverType.equals(p.getLibraryName())) {
                provider = p;
                break;
            }
        }
        if (provider == null) {
            throw new NullPointerException("No AudioServer found that matches : " + serverType);
        }

        AudioConfiguration config = new AudioConfiguration(
                sampleRate, //sample rate
                inputChannelCount, // input channels
                outputChannelCount, // output channels
                bufferSize, //buffer size
                // extensions
                new ClientID("miPhy"),
                Connections.OUTPUT);
        server = provider.createServer(config, this);

         //displayRate should be removed form PhysicalModel constructor

        /* Create a Thread to run our server. All servers require a Thread to run in.
         */
        runner = new Thread(new Runnable() {
            public void run() {
                // The server's run method can throw an Exception so we need to wrap it
                try {
                    server.run();
                } catch (Exception ex) {
                    Logger.getLogger(miPhyAudioClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        // set the Thread priority as high as possible.
        runner.setPriority(Thread.MAX_PRIORITY);
    }

    public void configure(AudioConfiguration context) throws Exception {
        /* Check the configuration of the passed in context, and set up any
         * necessary resources. Throw an Exception if the sample rate, buffer
         * size, etc. cannot be handled. DO NOT assume that the context matches
         * the configuration you passed in to create the server - it will
         * be a best match.
         */

    }
    public boolean process(long time, List<FloatBuffer> inputs, List<FloatBuffer> outputs, int nframes) {

        try {
            for (InputBuffer ib : getInputBuffers()) {
                AudioInputChannel aic = (AudioInputChannel) ib;
                aic.copyFromInputs(inputs);
            }
        }
        catch (Exception e)
        {

        }
        // always use nframes as the number of samples to process
        //System.out.println("input=" + inputs.get(0).get(0));
        synchronized (getLock()) {
            try {
                computeNSteps(nframes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for(OutputBuffer ob:outputBuffers) {
            AudioOutputChannel aof =(AudioOutputChannel) ob;
            if (mute) aof.fillZeroBuffer();
            aof.copyToOutputs(outputs);
        }
            return true;
    }

    public void addAudioOutputChannel(int channel,DataProvider observer)
    {
        outputBuffers.add(new AudioOutputChannel(channel,bufferSize,observer));
    }

    public void addAudioInputChannel(int channel)
    {
        dataProviders.put("audioInput" + Integer.toString(channel),
                new AudioInputChannel(channel,bufferSize));
    }



    public void shutdown() {
        //dispose resources.

    }

    public void start()
    {
        runner.start();
    }

    public List<InputBuffer> getInputBuffers()
    {
        ArrayList<InputBuffer> ibs = new ArrayList<>();
        for(Map.Entry<String, DataProvider> dataProvider:dataProviders.entrySet()) {
            if (dataProvider.getValue() instanceof InputBuffer)
                ibs.add((InputBuffer) dataProvider.getValue());
        }
        return ibs;
    }

    public static void main(String[] args) throws Exception {
        miPhyAudioClient pm = miPhyAudioClient.miPhyJack(44100.f,5,2);
        int[] dim = new int[1];
        dim[0] = 3;
        Index iA = new Index(0);
        Index iB = new Index(1);
        pm.addMacroMass("macro","BoundedIterator","LEFT1|RIGHT1","GridContainer",dim);
//  pm.getModule("macro").setGravity(new Vect3D(0,-0.001,0));
        pm.addMacroInteraction("SpringDamper","string",
                "macro","macro",
                "BoundedIterator","LEFT0|RIGHT1",
                "BoundedIterator","LEFT1|RIGHT0"
        );
        pm.getModule("string").setParam("restLength",5);
        pm.addModule("Mass3D","perc");
        pm.addMacroInteraction("Contact","ping",
                "macro","perc",
                "StaticIterator","1","StaticIterator","0");

        pm.addPositionScalarObserver("micro","macro",iB,new Vect3D(0,1,0));
        pm.addAudioOutputChannel(0,pm.getDataProvider("micro"));
        pm.addAudioOutputChannel(1,pm.getDataProvider("micro"));
     /*   pm.addPositionScalarController("percControl","perc",
                "audioInput0","PointY",new Index(0));
        pm.addModuleController("stiffControl","string","audioInput1","stiffness");
        pm.addModuleController("dampingControl","string","audioInput2","damping");
        pm.addModuleController("frictionControl","string","audioInput3","friction");
       */
        pm.addModuleController("stretchControl","macro","audioInput4","stretchFactor");
AbstractIterator it = pm.getModule("macro").getMassesIterator();
        pm.init();
        pm.getModule("macro").setPointR(iB,
                Vect3D.add(new Vect3D(0,1,0),pm.getModule("macro").getPoint(iB)));

        pm.setComputePhysics(true);
        pm.setMute(false);
        pm.start();


    }
}
