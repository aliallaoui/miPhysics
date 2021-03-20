package org.micreative.miPhysics.Engine.Sound;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jaudiolibs.audioservers.AudioClient;
import org.jaudiolibs.audioservers.AudioConfiguration;
import org.jaudiolibs.audioservers.AudioServer;
import org.jaudiolibs.audioservers.AudioServerProvider;
import org.jaudiolibs.audioservers.ext.ClientID;
import org.jaudiolibs.audioservers.ext.Connections;
import org.micreative.miPhysics.Engine.*;


/* Based on SineAudioClient, in the example project of jaudiolibs */
public class miPhyAudioClient extends PhysicalModel implements  AudioClient {

    final protected AudioServer server;

    protected long step;

    protected ArrayList<AudioOutputChannel> audioOutputChannels;

    private float[] data;
    private Thread runner;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    static public int bufferSize = 1024;
    private int state = 0; // should be an enum : 0 = not started, 1=started, not computing, initializing params
                           // 2= params initialized, start simulation
                           // 3= listening to simulation


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
            return new miPhyAudioClient(sampleRate, inputChannelCount, outputChannelCount, 1024, "JavaSound");
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

        for(InputBuffer ib:getInputBuffers()) {
            AudioInputChannel aic = (AudioInputChannel)ib;
            aic.copyFromInputs(inputs);
        }
        // always use nframes as the number of samples to process
        //System.out.println("input=" + inputs.get(0).get(0));
        synchronized (getLock()) {

            try {
                computeNSteps(nframes,state==1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for(OutputBuffer ob:outputBuffers) {
            AudioOutputChannel aof =(AudioOutputChannel) ob;
            if (state <= 2) aof.fillZeroBuffer();
            aof.copyToOutputs(outputs);
        }
            return true;
    }

    public void addAudioOutputChannel(int channel,String moduleName, ModuleObserver observer)
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
        state = 1;
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
        miPhyAudioClient simUGen = miPhyAudioClient.miPhyJack(44100.f,2,2);//new miPhyAudioClient(44100.f,0,2,1024,"JACK"); //<>// //<>//
/*
        simUGen.getMdl().setGravity(0);
        simUGen.getMdl().setFriction(0);

        simUGen.getMdl().addMass3D("percMass", 100, new Vect3D(0, -4, 0.), new Vect3D(0, 2, 0.));

        simUGen.getMdl().addString2D("string");

        simUGen.getMdl().addMContact2D("perc","percMass","string");

        String[] listeningPoints = new String[2];
        listeningPoints[0] = "string";
        int[] listeningPointsInd = new int[2];
        listeningPointsInd[0] = 3;
        listeningPoints[1] ="string";
        listeningPointsInd[1]= 2;
        simUGen.getMdl().addPositionController("osc_perc",0,"percMass",0,new Vect3D(0,10,0),new Vect3D(0,0,0));
        simUGen.setListeningPoint(listeningPoints,listeningPointsInd);
        simUGen.getMdl().addSimpleParamController("osc_perc_ctrl","osc_perc","pointAy",1);
        simUGen.getMdl().init();
*/
        simUGen.start();


    }
}
