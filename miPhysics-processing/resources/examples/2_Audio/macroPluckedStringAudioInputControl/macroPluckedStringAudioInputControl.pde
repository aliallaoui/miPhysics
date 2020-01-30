
import ddf.minim.*;
import ddf.minim.ugens.*;
import peasy.*;
import org.micreative.miPhysics.Processing.Utility.ModelRenderer.*;
import org.micreative.miPhysics.Engine.Control.*;
import org.micreative.miPhysics.Engine.Sound.miPhyAudioClient;
import org.micreative.miPhysics.Vect3D;
import themidibus.*; //Import the library

MidiBus myBus; // The MidiBus
int baseFrameRate = 60;

int mouseDragged = 0;

int gridSpacing = 2;
int xOffset= 0;
int yOffset= 0;

private Object lock = new Object();

float currAudio = 0;
float gainVal = 1.;


PeasyCam cam;

float percsize = 200;

Minim minim;
miPhyAudioClient simUGen;
Gain gain;

AudioOutput out;
AudioRecorder recorder;

ModelRenderer renderer;

float speed = 0;
float pos = 100;

ArrayList<MidiController> midiCtrls = new ArrayList<MidiController>();
ArrayList<midiNote> midiNotes = new ArrayList<midiNote>();

float x_avg;
float y_avg;
float smooth=.01;

/* Phyiscal parameters for the model */
float m = 1.0;
float k = 0.4;
float z = 0.004;
float l0 = 0.055;
float dist = 0.075;
float fric = 0.00000;
float grav = 0.;

float c_dist = 0.75;
float c_gnd = 0.65;
float c_k = 0.1;
float c_z = -0.1;

int nbmass = 350;
///////////////////////////////////////

void setup()
{
  size(1000, 700, P3D);
  //fullScreen(P3D, 2);
  cam = new PeasyCam(this, 100);
  cam.setMinimumDistance(50);
  cam.setMaximumDistance(2500);

  //simUGen = miPhyAudioClient.miPhyClassic(22050,0,2);
  simUGen = miPhyAudioClient.miPhyJack(22050,1,2);
   simUGen.getMdl().setGravity(grav);
    simUGen.getMdl().setFriction(fric);

    simUGen.getMdl().addMass2DPlane("guideM1", 1000000000, new Vect3D(2, -4, 0.), new Vect3D(0, 2, 0.));
  simUGen.getMdl().addMass2DPlane("guideM2", 1000000000, new Vect3D(4, -4, 0.), new Vect3D(0, 2, 0.));
   simUGen.getMdl().addMass2DPlane("guideM3", 1000000000, new Vect3D(3, -3, 0.), new Vect3D(0, 2, 0.));
    simUGen.getMdl().addMass3D("percMass", 100, new Vect3D(0, -4, 0.), new Vect3D(0, 2, 0.));
   simUGen.getMdl().addSpringDamper3D("test", 1, 1., 1., "guideM1", "percMass");
  simUGen.getMdl().addSpringDamper3D("test", 1, 1., 1., "guideM2", "percMass");
  simUGen.getMdl().addSpringDamper3D("test", 1, 1., 1., "guideM3", "percMass");

   simUGen.getMdl().addString2D("string");

   simUGen.getMdl().addMContact2D("perc","percMass","string");
   /*
    for(int i= 0; i< nbmass; i++)
      simUGen.getMdl().addContact3D("col", c_dist, c_k, c_z, "percMass", "str"+i);
    simUGen.getMdl().addContact3D("col", c_gnd, 10, c_z, "percMass", "gnd0");
    simUGen.getMdl().addContact3D("col", c_gnd, 10, c_z, "percMass", "gnd1");
*/

  simUGen.getMdl().addPositionController("osc_perc",0,"percMass",0,new Vect3D(0,10,0),new Vect3D(0,0,0));

        String[] listeningPoints = new String[2];
        listeningPoints[0] = "string";
        int[] listeningPointsInd = new int[2];
        listeningPointsInd[0] = 3;
        listeningPoints[1] ="string";
        listeningPointsInd[1]= 2;
    simUGen.setListeningPoint(listeningPoints,listeningPointsInd);

    simUGen.getMdl().init();

  //Adjust this to your settings using
   MidiBus.list();
  // Knowing that first integer paramater below is the input MIDI device and the second the output MIDI device
  myBus = new MidiBus(this, 0, 1); // Create a new MidiBus with no input device and the default Java Sound Synthesizer as the output device.

  midiCtrls.add(MidiController.addMidiController(simUGen.getMdl(),1, 0.01, 0.3, "string", "stiffness",0.05));
  midiCtrls.add(MidiController.addMidiController(simUGen.getMdl(),2, 0.0001, 0.1, "string", "damping",0.05));
  midiCtrls.add(MidiController.addMidiController(simUGen.getMdl(),3, 0.5, 1.5, "string", "mass",0.05));
  midiCtrls.add(MidiController.addMidiController(simUGen.getMdl(),4, 0.5, 1.5, "string", "stretchFactor",0.05));

  //midiNotes.add(new midiNote(0.01, 0.9, "str",0,simUGen.nbmass - 1,"Y","impulse"));
 // midiNotes.add(new midiNote(0.1, 10, "str",0,simUGen.getMdl().getNumberOfMats() - 1,"Y","pluck"));


  renderer = new ModelRenderer(this);

  renderer.setZoomVector(100,100,100);

  renderer.displayMats(true);
  renderer.setSize("Mass3D", 40.);
  renderer.setColor("Mass3D", 140, 140, 40);
  renderer.setSize("Mass2DPlane", 10.);
  renderer.setColor("Mass2DPlane", 120, 0, 140);
  renderer.setSize("Ground3D", 25.);
  renderer.setColor("Ground3D", 30, 100, 100);

  renderer.setColor("SpringDamper3D", 135, 70, 70, 255);
  renderer.setStrainGradient("SpringDamper3D", true, 0.1);
  renderer.setStrainColor("SpringDamper3D", 105, 100, 200, 255);
  renderer.init(simUGen.getMdl());
  cam.setDistance(500);  // distance from looked-at point

  frameRate(baseFrameRate);
simUGen.start();
}

void draw()
{
  background(0, 0, 25);

  directionalLight(126, 126, 126, 100, 0, -1);
  ambientLight(182, 182, 182);

    float x = 30*(float)mouseX / width - 15;
    float y = 30*(float)mouseY / height - 15;

    x_avg = (1-smooth) * x_avg + (smooth) * x;
    y_avg = (1-smooth) * y_avg + (smooth) * y;

    simUGen.getMdl().setMatPosition("guideM1",new Vect3D(x_avg-1, y_avg, 0));
    simUGen.getMdl().setMatPosition("guideM2",new Vect3D(x_avg+1, y_avg, 0));
    simUGen.getMdl().setMatPosition("guideM3",new Vect3D(x_avg, y_avg-1, 0));
    //simUGen.getMdl().computeStep();
  renderer.renderModel(simUGen.getMdl());
 // simUGen.

  cam.beginHUD();
  stroke(125, 125, 255);
  strokeWeight(2);
  fill(0, 0, 60, 220);
  rect(0, 0, 250, 50);
  textSize(16);
  fill(255, 255, 255);
  text("Curr Audio: " + currAudio, 10, 30);
  text("stiffness: " + simUGen.getMdl().getParam("string","stiffness"),10,50);
  text("damping: " + simUGen.getMdl().getParam("string","damping"),10,70);
  text("mass: " + simUGen.getMdl().getParam("string","mass"),10,90);
   text("stretchFactor: " + simUGen.getMdl().getParam("string","stretchFactor"),10,110);
  cam.endHUD();
}


void mouseDragged() {
  mouseDragged = 1;
}

void mouseReleased() {
  mouseDragged = 0;
}


void keyPressed() {

  if (keyCode ==UP) {
    simUGen.getMdl().triggerForceImpulse("guideM1", 0, -10000, 0);
    simUGen.getMdl().triggerForceImpulse("guideM2", 0, -10000, 0);
    simUGen.getMdl().triggerForceImpulse("guideM3", 0, -10000, 0);
  } else if (keyCode ==DOWN) {
    simUGen.getMdl().triggerForceImpulse("guideM1", 0, 10000, 0);
    simUGen.getMdl().triggerForceImpulse("guideM2", 0, 10000, 0);
    simUGen.getMdl().triggerForceImpulse("guideM3", 0, 10000, 0);
  } else if (keyCode ==LEFT) {
    simUGen.getMdl().triggerForceImpulse("guideM1", -5000, 0, 0);
    simUGen.getMdl().triggerForceImpulse("guideM2", -5000, 0, 0);
    simUGen.getMdl().triggerForceImpulse("guideM3", -5000, 0, 0);
  } else if (keyCode ==RIGHT) {
    simUGen.getMdl().triggerForceImpulse("guideM1", 5000, 0, 0);
    simUGen.getMdl().triggerForceImpulse("guideM2", 5000, 0, 0);
    simUGen.getMdl().triggerForceImpulse("guideM3", 5000, 0, 0);
  }
}

void keyReleased() {
  if (key == ' ')
    simUGen.getMdl().setGravity(0.000);
}

void controllerChange(int channel, int number, int value) {
  synchronized(lock)
  {
    for (MidiController mc : midiCtrls)
    {
      mc.changeParam(number, value);
    }
  }
}

void noteOn(int channel, int pitch, int velocity) {
  synchronized(lock)
  {
  for (midiNote mn : midiNotes)
  {
    mn.on(pitch, velocity);
  }
  }
}

void noteOff(int channel,int pitch,int velocity)
{
  synchronized(lock)
  {
  for (midiNote mn : midiNotes)
  {
    mn.off(pitch, velocity);
  }
  }
}
