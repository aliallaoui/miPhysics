import org.micreative.miPhysics.Engine.Sound.miPhyAudioClient;
import org.micreative.miPhysics.Vect3D;
import org.micreative.miPhysics.Engine.Index;
import org.micreative.miPhysics.Renderer.*;
import org.micreative.miPhysics.Engine.MidiControlProvider;
import peasy.*;
import themidibus.*; //Import the library

MidiBus myBus;

        PeasyCam cam;
        Index iA = new Index(0);
        Index iB = new Index(1);
        Index iC = new Index(2);
        miPhyAudioClient  pm=miPhyAudioClient.miPhyClassic(22050,0,2);
        CircleRenderer circles = new CircleRenderer(this);

        void setup()
        {
        size(1000,700);
        int[] dim = new int[1];
        dim[0] = 10;

        try
        {
        pm.addMacroMass("macro","BoundedIterator","LEFT1|RIGHT1","GridContainer",dim);
//  pm.getModule("macro").setGravity(new Vect3D(0,-0.001,0));
        pm.addMacroInteraction("SpringDamper","string",
        "macro","macro",
        "BoundedIterator","LEFT0|RIGHT1",
        "BoundedIterator","LEFT1|RIGHT0"
        );

        pm.getModule("string").setParam("restLength",10./(dim[0]-1));
        pm.addModule("Mass3D","perc");
        pm.addMacroInteraction("Contact","ping",
        "macro","perc",
        "StaticIterator","1","StaticIterator","0");

        pm.addPositionScalarObserver("micro","macro",iB,new Vect3D(0,1,0));
        pm.addAudioOutputChannel(0,pm.getDataProvider("micro"));
        pm.addAudioOutputChannel(1,pm.getDataProvider("micro"));

        pm.addDataProvider("MidiControlProvider","midictrl1");
        pm.getDataProvider("midictrl1").setParam("control",0);
        pm.getDataProvider("midictrl1").setParam("min",0.001);
        pm.getDataProvider("midictrl1").setParam("max",0.01);

        pm.addDataProvider("RampInterpolator","stiffctrl1");
        pm.getDataProvider("stiffctrl1").setParam("rampTime",0.005);

        pm.addDataProviderController("rampctrl1","stiffctrl1","midictrl1","max");

        pm.addModuleController("stiffnessStrCtrl","string","stiffctrl1","stiffness");
        pm.init();
        //  pm.getModule("macro").setPointR(iB,
        //  Vect3D.add(new Vect3D(0,1,0),pm.getModule("perc").getPoint(iA)));

        circles.addContainer(pm.getModule("macro").getPositionsContainer());
        circles.addIterator(pm.getModule("macro").getMassesIterator());

        MidiBus.list();
        // Knowing that first integer paramater below is the input MIDI device and the second the output MIDI device
        myBus = new MidiBus(this, 2, 3); // Create a new MidiBus with no input device and the default Java Sound Synthesizer as the output device.

        pm.setMute(true);
        pm.setComputePhysics(false);
        pm.start();
        }
        catch(Exception e)
        {
        System.out.println(e);
        }

        }
        void draw()
        {
        background(0);
synchronized(pm.getLock())
        {
        try
        {
        circles.render();}
        catch(Exception e)
        {
        ;
        }
        }
        textSize(16);
        fill(255, 255, 255);
        text("muted :" +pm.isMute() + " compute phy:"+pm.isComputePhysics(),10,30);
        text("stiffness 1: " + pm.getParam("string","stiffness"),10,50);
        text("damping 1: " + pm.getParam("string","damping"),10,70);
        text("friction 1: " + pm.getParam("string","friction"),10,90);
        // text("stretchFactor 1: " + pm.getParam("macro","stretchFactor"),10,110);
        }

        void keyPressed() {

        if (key == 'm') pm.setMute(!pm.isMute());
        if (key == 'p') pm.setComputePhysics(!pm.isComputePhysics());


        }
        void controllerChange(int channel, int number, int value) {
synchronized(pm.getLock())
        {
        try
        {
        for (MidiControlProvider mc : pm.getMidiControlProviders())
        {
        mc.changeParam(number, value);
        pm.getAsynchronousControllerByDataProvider(mc.getName()).setData();

        }
        }
        catch(Exception e)
        {
        ;
        }
        }
        }