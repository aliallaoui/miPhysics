package org.micreative.miPhysics.Engine;

import java.io.FileReader;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.locks.*;
import java.lang.Math;

import org.micreative.miPhysics.Engine.Control.ParamController;
import org.micreative.miPhysics.Engine.Modules.*;
import org.micreative.miPhysics.Vect3D;
import processing.core.*;
import processing.core.PVector;

//TODO two subclasses : STPhysicalModel (Static Topology) & DTPhysicalModel (Dynamic  Topology)
/**
 * This is the main class in which to create a 3D mass-interaction physical
 * model. A global physical context is created, then populated with modules,
 * which can then be computed.
 *
 * Within this physical context, modules can be accessed in two ways: by using
 * their identification String (a unique name for each module) or by using their
 * index in the Mat and Link module tables. See example: HelloMass
 *
 *
 *
 */

// removed until sorting out taglet compile in new Java:
// (the tag example followed by the name of an example included in folder
// 'examples' will automatically include the example in the javadoc.)
// @example HelloMass

public class PhysicalModel {

	// dummy comment

	// myParent is a reference to the parent sketch
	PApplet myParent;

	private Lock m_lock;

	/* List of modules and Links that compose the physical model */
	//private ArrayList<Mat> modules;
	//private ArrayList<Link> links;
	private ArrayList<Module> modules;

	/*
	 * Mat and Link index lists: matches module name to index of module in ArrayList
	 */

	private ArrayList<String> moduleIndexList;

	/* Super dirty but works as a dummy for plane-based interactions */
	private Mat fakePlaneMat;

	/* The simulation rate (mono rate only) */
	private int simRate;

	/* The processing sketch display rate */
	private int displayRate;

	private double simDisplayFactor;
	private int nbStepsToSim;
	private double residue;
	private long nbStepsSimulated=0;
	private Timestamp timestamp ;

	/* Global friction and gravity characteristics for the model */
	private double friction;

	private Vect3D g_vector;
	private double g_magnitude;
	private Vect3D g_scaled;


	private Map<String, ArrayList<Integer>> mat_subsets;
	private Map<String, ArrayList<Integer>> link_subsets;

	private Map<String, ParamController> param_controllers;

	/* Library version */
	public final static String VERSION = "##library.prettyVersion##";

	public String defaultParamsPropertiesPath = "/home/ali/sketchbook/libraries/miPhysics/defaultParams.properties";
	/**
	 * Constructor method. Call this in the setup to create a physical context with
	 * a given simulation rate.
	 *
	 * @param sRate
	 *            the sample rate for the physics simulation
	 * @param displayRate
	 *            the display Rate for the processing sketch
	 */
	public PhysicalModel(int sRate, int displayRate) {
		/* Create empty Mat and Link arrays */
		modules = new ArrayList<Module>();
		moduleIndexList = new ArrayList<String>();

		/* Initialise the Mat and Link subset groups */
		mat_subsets = new HashMap<String, ArrayList<Integer>>();
		link_subsets = new HashMap<String, ArrayList<Integer>>();

		Vect3D tmp = new Vect3D(0., 0., 0.);
		fakePlaneMat = new Ground3D(tmp);

		g_vector = new Vect3D(0., 0., 1.);
		g_scaled = new Vect3D(0., 0., 0.);


		if (sRate > 0)
			setSimRate(sRate);
		else {
			System.out.println("Invalid simulation Rate: defaulting to 50 Hz");
			setSimRate(50);
		}

		this.displayRate = displayRate;
		this.residue = 0;

		this.calculateSimDisplayFactor();

		m_lock = new ReentrantLock();

		param_controllers = new HashMap<String,ParamController>();


		System.out.println("Physical Model Class Initialised");
	}



	/**
	 * Constructor without specifying the sketch display rate (defaults to 30 FPS),
	 * or the parameter system (defaults to algo parameters)
	 *
	 * @param sRate
	 *            the physics sample rate
	 *
	 */
	public PhysicalModel(int sRate) {
		this(sRate, 30);
		System.out.println("No specified display Rate: defaulting to 30 FPS");
	}

	/**
	 * Constructor without specifying the sketch display rate (defaults to 30 FPS).
	 *
	 *
	 */
	public PhysicalModel() {
		this(300, 30);
		System.out.println("No specified display Rate: defaulting to 30 FPS");
	}

	private void calculateSimDisplayFactor() {
		simDisplayFactor = (float) simRate / (float) displayRate;
	}

	/*************************************************/
	/* Some utility functions for the class */
	/*************************************************/

	/**
	 * Get the simulation's sample rate.
	 *
	 * @return the simulation rate
	 */
	public int getSimRate() {
		return simRate;
	}

	/**
	 * Set the simulation's sample rate.
	 *
	 * @param rate
	 *            the rate to set the simulation to (physics frame-per-second).
	 */
	public void setSimRate(int rate) {
		simRate = rate;
		this.calculateSimDisplayFactor();
	}

	/**
	 * Get the simulation's display rate (should be same as Sketch's frame rate).
	 *
	 * @return the simulation rate
	 */
	public int getDisplayRate() {
		return displayRate;
	}

	/**
	 * Set the simulation's display rate (should be same as Sketch's frame rate).
	 *
	 * @param rate
	 *            the rate to set the display to (FPS).
	 */
	public void setDisplayRate(int rate) {
		displayRate = rate;
		this.calculateSimDisplayFactor();
	}

	/**
	 * Delete all modules in the model and start from scratch.
	 */
	public void clearModel() {
		for (int i = modules.size() - 1; i >= 0; i--) {
			modules.remove(i);
		}
	}

	/**
	 * Initialise the physical model once all the modules have been created.
	 */
	public void init() {

		System.out.println("Initialisation of the physical model: ");
		System.out.println("Nb of modules int model: " + getNumberOfModules());
		System.out.println("Nb of Links in model: " + getNumberOfLinks());

		/* Initialise the stored distances for the springs */
		for (int i = 0; i < modules.size(); i++) {
			modules.get(i).initDistances();
		}


		// Should init grav and friction here, in case they were set after the module
		// creation...
		System.out.println("Finished model init.\n");
	}

	/**
	 * Get the index of a Mat module identified by a given string.
	 *
	 * @param name
	 *            Mat module identifier.
	 * @return
	 */
	public int getMatIndex(String name) {
		int ret = moduleIndexList.indexOf(name);
		if(modules.get(ret) instanceof Mat) return ret;
		else return -1;
	}

	/**
	 * Get the index of a Link module identified by a given string.
	 *
	 * @param name
	 *            Link module identifier.
	 * @return
	 */
	public int getLinkIndex(String name) {
		int ret = moduleIndexList.indexOf(name);
		if(modules.get(ret) instanceof Link) return ret;
		else return -1;
	}

	/**
	 * Get the index of a Module module identified by a given string.
	 *
	 * @param name
	 *            Module module identifier.
	 * @return
	 */
	public int getModuleIndex(String name) {
		return moduleIndexList.indexOf(name);
	}


	/**
	 * Get the position of a Mat module identified by its name.
	 *
	 * @param masName
	 *            identifier of the Mat module.
	 * @return a Vect3D containing the position (in double format)
	 */
	public Vect3D getMatPosition(String masName) {
		try {
			int mat_index = getMatIndex(masName);
			if (mat_index > -1) {
				return modules.get(mat_index).getPos(0);
			} else {
				throw new Exception("The module name already exists!");
			}
		} catch (Exception e) {
			System.out.println("Error accessing Module " + masName + ": " + e);
			System.exit(1);
		}
		return new Vect3D();
	}

	/**
	 * Get the position of a Mat module identified by its name.
	 *
	 * @param masName
	 *            identifier of the Mat module.
	 * @return a Vect3D containing the position (in double format)
	 */
	public Vect3D getMatPosition(String masName,int index) {
		try {
			int mod_index = getModuleIndex(masName);
			if (mod_index > -1) {
				return modules.get(mod_index).getPos(index);
			} else {
				throw new Exception("The module name already exists!");
			}
		} catch (Exception e) {
			System.out.println("Error accessing Module " + masName + ": " + e);
			System.exit(1);
		}
		return new Vect3D();
	}


	/**
	 * Get the position of a Mat module identified by its name.
	 *
	 * @param masName
	 *            identifier of the Mat module.
	 * @return a PVector containing the position (in float format).
	 */
	public PVector getMatPVector(String masName) {
		try {
			int mat_index = getMatIndex(masName);
			if (mat_index > -1) {
				return modules.get(mat_index).getPos(0).toPVector();
			} else {
				throw new Exception("The module name already exists!");
			}
		} catch (Exception e) {
			System.out.println("Error accessing Module " + masName + ": " + e);
			System.exit(1);
		}
		return new PVector();
	}

	

	/**
	 * Construct delayed position values based on initial position and initial
	 * velocity. Converts the velocity in [distance unit]/[second] to [distance
	 * unit]/[sample] then calculates the delayed position for the initialisation of
	 * the masses.
	 *
	 * @param pos
	 *            initial position
	 * @param vel_mps
	 *            initial velocity in distance unit per second
	 * @return
	 */
	private Vect3D constructDelayedPos(Vect3D pos, Vect3D vel_mps) {
		Vect3D velPerSample = new Vect3D();
		Vect3D initPosR = new Vect3D();

		velPerSample.set(vel_mps);
		velPerSample.div(this.getSimRate());

		initPosR.set(pos);
		initPosR.sub(velPerSample);

		return initPosR;
	}

	/**
	 * Get number of Mat modules in current model.
	 *
	 * @return the number of Mat modules in this model.
	 */
	public int getNumberOfMats() {
		int m = 0;
		for(Module mod:modules)
		{
			m+=mod.getNbMats();
		}
		return m;
	}
	/**
	 * get number of Link modules in current model.
	 *
	 * @return the number of Link modules in this model.
	 */
	public int getNumberOfLinks() {
		return 0;//TODO links.size();
	}

	/**
	 * get number of  modules in current model.
	 *
	 * @return the number of  modules in this model.
	 */
	public int getNumberOfModules() {
		return modules.size();
	}

	/**
	 * Check if a Mat module with a given identifier exists in the current model.
	 *
	 * @param mName
	 *            the identifier of the Mat module.
	 * @return True of the module exists, False otherwise.
	 */
	public boolean matExists(String mName) {
		return getMatIndex(mName) >= 0;
	}

	/**
	 * Check if a Link module with a given identifier exists in the current model.
	 *
	 * @param lName
	 *            the identifier of the Link module.
	 * @return True of the module exists, False otherwise.
	 */
	public boolean linkExists(String lName) {
		return getLinkIndex(lName) >= 0;
	}


	/**
	 * Check if a Module module with a given identifier exists in the current model.
	 *
	 * @param lName
	 *            the identifier of the Module module.
	 * @return True of the module exists, False otherwise.
	 */
	public boolean moduleExists(String lName) {
		return getModuleIndex(lName) >= 0;
	}



	/**
	 * Check the type (mass, ground, osc) of Mat module at index i
	 *
	 * @param i
	 *            the index of the Mat module.
	 * @return the type of the Mat module.
	 */
	public String getMatTypeAt(int i) {
		if (getNumberOfModules() > i)
			return modules.get(i).getType();
		else
			return "";
	}

	/**
	 * Get the name (identifier) of the Mat module at index i
	 *
	 * @param i
	 *            the index of the Mat module.
	 * @return the identifier String.
	 */
	public String getMatNameAt(int i) {
		if (getNumberOfModules() > i)
			return moduleIndexList.get(i);
		else
			return "None";
	}

	/**
	 * Get the 3D position of Mat module at index i. Returns a zero filled 3D Vector
	 * if the Mat is not found.
	 *
	 * @param i
	 *            the index of the Mat module
	 * @return the 3D X,Y,Z coordinates of the module.
	 */
	public Vect3D getMatPosAt(int i) {
		if (getNumberOfModules() > i)
			return modules.get(i).getPos(0);
		else
			return new Vect3D(0., 0., 0.);
	}


	/**
	 * Get the 3D delayed position of the Mat module at index i. Returns a zero filled 3D Vector
	 * if the Mat is not found.
	 *
	 * @param i
	 *            the index of the Mat module
	 * @return the delayed 3D X,Y,Z coordinates of the module.
	 */
	public Vect3D getMatDelayedPosAt(int i) {
		if (getNumberOfModules() > i)
			return modules.get(i).getPosR(0);
		else
			return new Vect3D(0., 0., 0.);
	}

	/**
	 * Get the velocity of the Mat module at index i. Returns a zero filled 3D Vector
	 * if the Mat is not found.
	 *
	 * @param i
	 *            the index of the Mat module
	 * @return the 3D X,Y,Z velocity coordinates of the module.
	 */
	public Vect3D getMatVelAt(int i) {
		if (getNumberOfModules() > i) {
			Vect3D vel = new Vect3D();
			vel.set(modules.get(i).getPos(0));
			return (vel.sub(modules.get(i).getPosR(0)).mult(simRate));
		}
		else
			return new Vect3D(0., 0., 0.);
	}


	public List<Vect3D> getAllPositions()
	{
		List<Vect3D> ret = new ArrayList<>();
		for(Mat m:getMats()) ret.add(m.getPos());
		for(Module m:getMultiPointModules()){
			for(int i=0;i<m.getNbMats();i++) ret.add(m.getPos(i));
		}
		return ret;
	}

	public List<Vect3D> getAllLinkPositions()
	{
		List<Vect3D> ret = new ArrayList<>();
		for(Link l:getLinks()){
			ret.add(l.getMat1().getPos());
			ret.add(l.getMat2().getPos());
		}
	/*	for(Module m:getMultiPointModules()){
			for(int i=0;i<m.getNbMats();i++) ret.add(m.getPos(i));
		}
		*/
		return ret;
	}

	/**
	 * Check the type (spring, rope, contact...) of Link module at index i
	 *
	 * @param i
	 *            index of the Link module.
	 * @return the module type.
	 */
	public String getLinkTypeAt(int i) {
		if (getNumberOfLinks() > i)
			return modules.get(i).getType();
		else
			return "";
	}

	/**
	 * Get the name (identifier) of the Link module at index i
	 *
	 * @param i
	 *            index of the Link module.
	 * @return the identifier String for the module.
	 */
	public String getLinkNameAt(int i) {
		if (getNumberOfLinks() > i)
			return moduleIndexList.get(i);
		else
			return "None";
	}

	/**
	 * Get the position of the Mat connected to the 1st end of the Link at index i.
	 *
	 * @param i
	 *            the index of the Link.
	 * @return the 3D position of the Mat connected to the 1st end of this Link.
	 */
	public Vect3D getLinkPos1At(int i) {
		if (getNumberOfLinks() > i)
			return ((Link)(modules.get(i))).getMat1().getPos(0);
		else
			return new Vect3D(0., 0., 0.);
	}

	/**
	 * Get the position of the Mat connected to the 2nd end of the Link at index i.
	 *
	 * @param i
	 *            the index of the Link.
	 * @return the 3D position of the Mat connected to the 2nd end of this Link.
	 */
	public Vect3D getLinkPos2At(int i) {
		if (getNumberOfLinks() > i)
			return ((Link)(modules.get(i))).getMat2().getPos(0);
		else
			return new Vect3D(0., 0., 0.);
	}


	/**
	 * Get the index of the Mat connected to the first input of a Link Module
	 * @param i index of the Link Module.
	 * @return the index of the Mat module (in the Mat list)
	 */
	public int getLinkMat1IdxAt(int i) {
		if (getNumberOfLinks() > i) {
			Mat theMat1 = ((Link) (modules.get(i))).getMat1();
			return modules.indexOf(theMat1);
		}
		else
			return -1;
	}

	/**
	 * Get the index of the Mat connected to the second input of a Link Module
	 * @param i index of the Link Module.
	 * @return the index of the Mat module (in the Mat list)
	 */
	public int getLinkMat2IdxAt(int i) {
		if (getNumberOfLinks() > i) {
			Mat theMat2 =((Link) (modules.get(i))).getMat2();
			return modules.indexOf(theMat2);
		}
		else
			return -1;
	}

	/**
	 * Get the name of the Mat connected to the first input of a Link Module
	 * @param i index of the Link Module
	 * @return the name of the Mat
	 */
	public String getLinkMat1NameAt(int i) {
		if (getLinkMat1IdxAt(i) > -1)
			return getMatNameAt(getLinkMat1IdxAt(i));
		else
			return "";
	}

	/**
	 * Get the name of the Mat connected to the second input of a Link Module
	 * @param i index of the Link Module
	 * @return the name of the Mat
	 */
	public String getLinkMat2NameAt(int i) {
		if (getLinkMat2IdxAt(i) > -1)
			return getMatNameAt(getLinkMat2IdxAt(i));
		else
			return "";
	}


	/*************************************************/
	/* Compute simulation steps */
	/*************************************************/

	/**
	 * Run the physics simulation (call once every draw method). Automatically
	 * computes the correct number of steps depending on the simulation rate /
	 * display rate ratio. Should be called once the model creation is finished and
	 * the init() method has been called.
	 *
	 */
	public void draw_physics() {
		synchronized (m_lock) {
			double floatFrames = this.simDisplayFactor + this.residue;
			int nbSteps = (int) Math.floor(floatFrames);
			this.residue = floatFrames - (double) nbSteps;

			for (int j = 0; j < nbSteps; j++) {
				//modules.parallelStream().forEach(o -> o.compute());
				//links.parallelStream().forEach(o ->o.compute());

				for (int i = 0; i < modules.size(); i++) {
					modules.get(i).computeMoves();
				}
				for (int i = 0; i < modules.size(); i++) {
					modules.get(i).computeForces();
				}
			}
		}
	}

	/**
	 * Explicitly compute N steps of the physical simulation. Should be called once
	 * the model creation is finished and the init() method has been called.
	 *
	 * @param N
	 *            number of steps to compute.
	 */
	public void computeNSteps(int N) {
		synchronized (m_lock) {
			for (int j = 0; j < N; j++) {
				if(nbStepsSimulated== 0) timestamp = new Timestamp(System.currentTimeMillis());

				if(!param_controllers.isEmpty()) param_controllers.forEach((k,v)-> v.updateParams());

				//for (int i = 0; i < modules.size(); i++) 	modules.get(i).compute();

				for(Module m:modules) 	m.computeMoves();
				//modules.parallelStream().forEach(o -> o.compute());
				for(Module m:modules)	m.computeForces();

				//for (int i = 0; i < links.size(); i++) 	links.get(i).compute();

				//links.parallelStream().forEach(o ->o.compute());
				}
		}
		nbStepsSimulated+=N;
		if(nbStepsSimulated%simRate == 0)
		{
			Timestamp current = new Timestamp(System.currentTimeMillis());
			double secs = (double)(current.getTime() - timestamp.getTime())/1000.;
			double sim_secs = (double)nbStepsSimulated/(double)simRate;
			if(secs > sim_secs) System.out.println(secs + " seconds ellapsed " +  sim_secs + " simulated");
		}
	}

	/**
	 * Compute a single step of the physical simulation. Should be called once the
	 * model creation is finished and the init() method has been called.
	 */
	public void computeStep() {
		computeNSteps(1);
	}

	/*************************************************/
	/* Add modules to the model ! */
	/*************************************************/

	/**
	 * Add a 3D Mass module to the model (this Mass is subject to gravity).
	 *
	 * @param name
	 *            the identifier of the Mass
	 * @param mass
	 *            the mass' inertia value.
	 * @param initPos
	 *            the mass' initial position
	 * @param initVel
	 *            the mass' initial velocity (in distance unit per second)
	 * @return 0 if everything goes well.
	 */
	public int addMass3D(String name, double mass, Vect3D initPos, Vect3D initVel) {
		try {
			if (moduleIndexList.contains(name)) {

				System.out.println("The module name already exists!");
				throw new Exception("The module name already exists!");
			}
			modules.add(new Mass3D(mass, initPos, constructDelayedPos(initPos, initVel), friction, g_scaled));
			moduleIndexList.add(name);
		} catch (Exception e) {
			System.out.println("Error adding Module " + name + ": " + e);
			System.exit(1);
		}
		return 0;
	}

	/**
	 * Add a 2D Mass module (constant on Z plane) to the model.
	 *
	 * @param name
	 *            the identifier of the Mass
	 * @param mass
	 *            the mass' inertia value.
	 * @param initPos
	 *            the mass' initial position
	 * @param initVel
	 *            the mass' initial velocity (in distance unit per second)
	 * @return 0 if everything goes well.
	 */
	public int addMass2DPlane(String name, double mass, Vect3D initPos, Vect3D initVel) {
		try {
			if (moduleIndexList.contains(name)) {

				System.out.println("The module name already exists!");
				throw new Exception("The module name already exists!");
			}
			modules.add(new Mass2DPlane(mass, initPos, constructDelayedPos(initPos, initVel), friction, g_scaled));
			moduleIndexList.add(name);
		} catch (Exception e) {
			System.out.println("Error adding Module " + name + ": " + e);
			System.exit(1);
		}
		return 0;
	}

	/**
	 * Add a 1D Mass module (that only moves along the Z axis) to the model
	 *
	 * @param name
	 *            the identifier of the Mass
	 * @param mass
	 *            the mass' inertia value.
	 * @param initPos
	 *            the mass' initial position
	 * @param initVel
	 *            the mass' initial velocity (in distance unit per second)
	 * @return 0 if everything goes well.
	 */
	public int addMass1D(String name, double mass, Vect3D initPos, Vect3D initVel) {
		try {
			if (moduleIndexList.contains(name)) {

				System.out.println("The module name already exists!");
				throw new Exception("The module name already exists!");
			}
			modules.add(new Mass1D(mass, initPos, constructDelayedPos(initPos, initVel), friction, g_scaled));
			moduleIndexList.add(name);
		} catch (Exception e) {
			System.out.println("Error adding Module " + name + ": " + e);
			System.exit(1);
		}
		return 0;
	}

	/**
	 * Add a 3D Simple Mass module to the model (this Mass not subject to gravity).
	 *
	 * @param name
	 *            the identifier of the Mass
	 * @param mass
	 *            the mass' inertia value.
	 * @param initPos
	 *            the mass' initial position
	 * @param initVel
	 *            the mass' initial velocity (in distance unit per second)
	 * @return 0 if everything goes well.
	 */
	public int addMass3DSimple(String name, double mass, Vect3D initPos, Vect3D initVel) {
		try {
			if (moduleIndexList.contains(name)) {

				System.out.println("The module name already exists!");
				throw new Exception("The module name already exists!");
			}
			modules.add(new Mass3DSimple(mass, initPos, constructDelayedPos(initPos, initVel)));
			moduleIndexList.add(name);

		} catch (Exception e) {
			System.out.println("Error adding Module " + name + ": " + e);
			System.exit(1);
		}
		return 0;
	}

	/**
	 * Add a fixed point to the model (a Mat module that will never move from it's
	 * initial position).
	 *
	 * @param name
	 *            the name of the Ground module.
	 * @param initPos
	 *            initial position of the Ground module.
	 * @return 0 if everything goes well.
	 */
	public int addGround3D(String name, Vect3D initPos) {
		try {
			if (moduleIndexList.contains(name)) {

				System.out.println("The module name already exists!");
				throw new Exception("The module name already exists!");
			}
			modules.add(new Ground3D(initPos));
			System.out.println(modules.get(modules.size()-1).getType() + " added");
			moduleIndexList.add(name);
		} catch (Exception e) {
			System.out.println("Error adding Module " + name + ": " + e);
			System.exit(1);
		}
		return 0;
	}

	public int addGround1D(String name, Vect3D initPos) {
		try {
			if (moduleIndexList.contains(name)) {

				System.out.println("The module name already exists!");
				throw new Exception("The module name already exists!");
			}
			modules.add(new Ground1D(initPos));
			moduleIndexList.add(name);

		} catch (Exception e) {
			System.out.println("Error adding Module " + name + ": " + e);
			System.exit(1);
		}
		return 0;
	}

	/**
	 * Add a 3D oscillator (mass-spring-ground) to the model model.
	 *
	 * @param name
	 *            the name of the Oscillator module.
	 * @param mass
	 *            the Oscillator's mass value
	 * @param K
	 *            the Oscillator's stiffness value
	 * @param Z
	 *            the Oscillator's damping value
	 * @param initPos
	 *            initial position of the Oscillator module.
	 * @param initVel
	 *            initial velocity of the Oscillator module (in distance unit per
	 *            second).
	 * @return 0 if everything goes well.
	 */
	public int addOsc3D(String name, double mass, double K, double Z, Vect3D initPos, Vect3D initVel) {


		try {
			if (moduleIndexList.contains(name)) {
				System.out.println("The module name already exists!");
				throw new Exception("The module name already exists!");
			}
			modules.add(new Osc3D(mass, K, Z, initPos, constructDelayedPos(initPos, initVel), friction, g_scaled));
			moduleIndexList.add(name);

		} catch (Exception e) {
			System.out.println("Error adding Module " + name + ": " + e);
			System.exit(1);
		}
		return 0;
	}

	public int addOsc1D(String name, double mass, double K, double Z, Vect3D initPos, Vect3D initVel) {


		try {
			if (moduleIndexList.contains(name)) {
				System.out.println("The module name already exists!");
				throw new Exception("The module name already exists!");
			}
			modules.add(new Osc1D(mass, K, Z, initPos, constructDelayedPos(initPos, initVel), friction, g_scaled));
			moduleIndexList.add(name);

		} catch (Exception e) {
			System.out.println("Error adding Module " + name + ": " + e);
			System.exit(1);
		}
		return 0;
	}


	public int addLink(String name,String type, double dist, double paramK,double paramZ, String m1_Name, String m2_Name)
	{
		int mat1_index = getMatIndex(m1_Name);
		int mat2_index = getMatIndex(m2_Name);
		try {
			Link l = null;
			if (type == "Spring3D") l = new Spring3D(dist, paramK,(Mat) modules.get(mat1_index),(Mat) modules.get(mat2_index));
			else if (type == "SpringDamper3D") l = new SpringDamper3D(dist, paramK,paramZ,(Mat) modules.get(mat1_index),(Mat) modules.get(mat2_index));
			else if (type == "SpringDamper1D") l = new SpringDamper1D(dist, paramK,paramZ,(Mat) modules.get(mat1_index),(Mat) modules.get(mat2_index));
			else if (type == "Rope3D") l = new SpringDamper3D(dist, paramK,paramZ,(Mat) modules.get(mat1_index),(Mat) modules.get(mat2_index));
			else if (type == "Contact3D") l = new Contact3D(dist, paramK,paramZ,(Mat) modules.get(mat1_index),(Mat) modules.get(mat2_index));
			else if (type == "Bubble3D") l = new Bubble3D(dist, paramK,paramZ,(Mat) modules.get(mat1_index),(Mat) modules.get(mat2_index));
			else if (type == "Damper3D") l = new Damper3D(paramZ,(Mat) modules.get(mat1_index),(Mat) modules.get(mat2_index));
		//	else if (type == "PlaneContact") l = new PlaneContact(dist, paramK,paramZ,(Mat) modules.get(mat1_index),(Mat) modules.get(mat2_index));
			else {
				System.out.println("Error allocating the Spring module");
				System.exit(1);
			}

			modules.add(l);
			moduleIndexList.add(name);
		} catch (Exception e) {
			System.out.println("Error allocating the Spring module");
			System.exit(1);
		}
		return 0;
	}


	/* Add a 3D Spring module to the model */
	/**
	 * Add a 3D Spring to the model.
	 *
	 * @param name
	 *            identifier of the Spring.
	 * @param dist
	 *            resting distance.
	 * @param paramK
	 *            stiffness.
	 * @param m1_Name
	 *            name of Mat module connected to 1st end
	 * @param m2_Name
	 *            name of Mat module connected to 2nd end
	 * @return O if all goes well.
	 */
	public int addSpring3D(String name, double dist, double paramK, String m1_Name, String m2_Name) {

		return addLink(name,"Spring3D",dist,paramK,0,m1_Name,m2_Name);
	}

	/**
	 * Add a 3D Spring and Damper module to the model.
	 *
	 * @param name
	 *            identifier of the Spring-Damper.
	 * @param dist
	 *            resting distance.
	 * @param paramK
	 *            stiffness value.
	 * @param paramZ
	 *            damping value.
	 * @param m1_Name
	 *            name of Mat module connected to 1st end
	 * @param m2_Name
	 *            name of Mat module connected to 2nd end
	 * @return O if all goes well.
	 */
	public int addSpringDamper3D(String name, double dist, double paramK, double paramZ, String m1_Name,
								 String m2_Name) {
		return addLink(name,"SpringDamper3D",dist,paramK,paramZ,m1_Name,m2_Name);
	}

	public int addSpringDamper1D(String name, double dist, double paramK, double paramZ, String m1_Name,
								 String m2_Name) {
		return addLink(name,"SpringDamper1D",dist,paramK,paramZ,m1_Name,m2_Name);
	}

	/**
	 * Add a 3D "rope-like" Spring  and Damper module to the model. This interaction
	 * will only be active in case of a positive elongation. If the rope is not
	 * tight (elongation smaller than resting distance) the interaction does
	 * nothing.
	 *
	 * @param name
	 *            identifier of the Rope.
	 * @param dist
	 *            resting distance.
	 * @param paramK
	 *            stiffness value.
	 * @param paramZ
	 *            damping value.
	 * @param m1_Name
	 *            name of Mat module connected to 1st end
	 * @param m2_Name
	 *            name of Mat module connected to 2nd end
	 * @return O if all goes well.
	 */
	public int addRope3D(String name, double dist, double paramK, double paramZ, String m1_Name, String m2_Name) {
		return addLink(name,"Rope3D",dist,paramK,paramZ,m1_Name,m2_Name);
	}

	/**
	 * Add a 3D Contact module to the model.
	 *
	 * @param name
	 *            identifier of the Contact module.
	 * @param dist
	 *            (threshold) below which the Contact becomes active.
	 * @param paramK
	 *            stiffness value.
	 * @param paramZ
	 *            damping value.
	 * @param m1_Name
	 *            name of Mat module connected to 1st end
	 * @param m2_Name
	 *            name of Mat module connected to 2nd end
	 * @return O if all goes well.
	 */
	public int addContact3D(String name, double dist, double paramK, double paramZ, String m1_Name, String m2_Name) {
		return addLink(name,"Contact3D",dist,paramK,paramZ,m1_Name,m2_Name);
	}


	/**
	 * Add a 3D Bubble (enclosing circle module to the model.
	 *
	 * @param name
	 *            identifier of the Bubble module.
	 * @param dist
	 *            radius of the circle (distance above which the interaction will become
	 *            active).
	 * @param paramK
	 *            stiffness value.
	 * @param paramZ
	 *            damping value.
	 * @param m1_Name
	 *            name of Mat module connected to 1st end
	 * @param m2_Name
	 *            name of Mat module connected to 2nd end
	 * @return O if all goes well.
	 */
	public int addBubble3D(String name, double dist, double paramK, double paramZ, String m1_Name, String m2_Name) {
		return addLink(name,"Bubble3D",dist,paramK,paramZ,m1_Name,m2_Name);
	}

	/**
	 * Add a 3D Friction-based Damper module to the model.
	 *
	 * @param name
	 *            identifier of the Damper.
	 * @param paramZ
	 *            damping value.
	 * @param m1_Name
	 *            name of Mat module connected to 1st end
	 * @param m2_Name
	 *            name of Mat module connected to 2nd end
	 * @return O if all goes well.
	 */
	public int addDamper3D(String name, double paramZ, String m1_Name, String m2_Name) {
		return addLink(name,"Damper3D",0,0,paramZ,m1_Name,m2_Name);
	}

	/**
	 * Add an interaction with a 2D plane.
	 *
	 * @param name
	 *            name of the Plane Interaction module.
	 * @param l0
	 *            distance below which the interaction becomes active.
	 * @param paramK
	 *            stiffness value.
	 * @param paramZ
	 *            damping value.
	 * @param or
	 *            orientation of the plane (0: x-plane, 1: y-plane, 2: z-plane)
	 * @param pos
	 *            position of the plane along the axis defined by or.
	 * @param m1_Name
	 *            name of the Mat module connected to this Plane.
	 * @return
	 */
	/*public int addPlaneContact(String name, double l0, double paramK, double paramZ, int or, double pos,
							   String m1_Name) {


		int mat1_index = getMatIndex(m1_Name);
		try {
			modules.add(new PlaneContact(l0, paramK, paramZ, modules.get(mat1_index), fakePlaneMat, or, pos));
			moduleIndexList.add(name);
		} catch (Exception e) {
			System.out.println("Error allocating the Bounce on Plane module");
			System.exit(1);
		}
		return 0;
	}
*/



	/**
	 * Add a 3D Attractor module to the model.

	 * @return O if all goes well.
	 */
/*	public int addAttractor3D(String name, double dLim, double attr, String m1_Name, String m2_Name) {

	//	if (unit_system == paramSystem.REAL_UNITS) {
	//		paramK = paramK / (simRate * simRate);
	//		paramZ = paramZ / simRate;
	//	}

		int mat1_index = getMatIndex(m1_Name);
		int mat2_index = getMatIndex(m2_Name);
		try {
			modules.add(new Attractor3D(dLim, attr, modules.get(mat1_index), modules.get(mat2_index)));
			moduleIndexList.add(name);
		} catch (Exception e) {
			System.out.println("Error allocating the Attractor module: " + e);
			System.exit(1);
		}
		return 0;
	}
*/
	protected Properties getPropertySubset(Properties prop, String key)
	{
		final Properties p = new Properties();

		for (String s : prop.stringPropertyNames()) {
			if (s.startsWith(key) && s.length() > key.length())
			{
				p.put(s.substring(key.length()), p.getProperty(s));
			}
		}
		return p;
	}

	protected Map<String,String> getPropertySubsetAsMap(Properties prop, String key)
	{
		final Map<String,String> p = new HashMap<>();

		for (String s : prop.stringPropertyNames()) {
			if (s.startsWith(key) && s.length() > key.length())
			{
				p.put(s.substring(key.length()), prop.getProperty(s));
			}
		}
		return p;
	}

	public void addString2D(String name)
	{
		try(InputStream input = PhysicalModel.class.getClassLoader().getResourceAsStream("defaultParams.properties"))
		{
			Properties p = new Properties();
			if(input == null)
			{
				System.out.println("defaultParams.properties not found");
				return ;
			}
			//p.load(new FileReader(defaultParamsPropertiesPath));
			p.load(input);
/*
			modules.add(new String2D(Double.parseDouble(p.getProperty("String2D.restDistance")), Double.parseDouble(p.getProperty("String2D.stiffness")),

					Double.parseDouble(p.getProperty("String2D.viscosity")),Double.parseDouble(p.getProperty("String2D.mass")),
					Integer.parseInt(p.getProperty("String2D.size")),Double.parseDouble(p.getProperty("String2D.stretchFactor")),
					Vect3D.fromString(p.getProperty("String2D.left")),Vect3D.fromString(p.getProperty("String2D.direction")),0,new Vect3D(0,0,0)));
*/
			Map params = getPropertySubsetAsMap(p,"String2D.");
			params.putAll(getPropertySubsetAsMap(p,"Global."));
			modules.add(new String2D(params));
			moduleIndexList.add(name);
		}
		catch (Exception e)
		{
			System.out.println("Error allocating the String2D macro module: " + e);
			System.exit(1);
		}
	}

	public void addMContact2D(String name,String module1,String module2)
	{
		try(InputStream input = PhysicalModel.class.getClassLoader().getResourceAsStream("defaultParams.properties"))
		{
			Properties p = new Properties();
			if(input == null)
			{
				System.out.println("defaultParams.properties not found");
				return ;
			}
			//p.load(new FileReader(defaultParamsPropertiesPath));
			p.load(input);
			modules.add(new MContact2D(Double.parseDouble(p.getProperty("MContact2D.distance")), Double.parseDouble(p.getProperty("MContact2D.stiffness")),
					Double.parseDouble(p.getProperty("MContact2D.viscosity")),getModule(module1),getModule(module2)));
			moduleIndexList.add(name);
		}
		catch (Exception e)
		{
			System.out.println("Error allocating the MContact2D macro module: " + e);
			System.exit(1);
		}
	}


	/***************************************************/

	/**
	 * Remove Mat module at index mIndex from the model. This function is private: a
	 * Mat module cannot safely be removed without removing associated Links,
	 * therefore a higher level function is provided for the user.
	 *
	 * @param mIndex
	 *            index of the Mat module to remove.
	 * @return 0 if success, throws error if Mat cannot be found or removed.
	 */
	private int removeMat(int mIndex) {
		// find mat and remove from the mat array list.
		try {
			// first check if the index can be in the list
			if ((modules.size() > mIndex) && (moduleIndexList.size() > mIndex))
				modules.remove(mIndex);
			moduleIndexList.remove(mIndex);
		} catch (Exception e) {
			System.out.println("Error removing mat Module at " + mIndex + ": " + e);
			System.exit(1);
		}
		return 0;
	}

	/**
	 * Remove Mat module (identified by name) from the Model This function is
	 * private: a Mat module cannot safely be removed without removing associated
	 * Links, therefore a higher level function is provided for the user.
	 *
	 * @param name
	 *            identifier of the Mat module to remove.
	 * @return 0 if success, throws error otherwise.
	 */
	private int removeMat(String name) {
		int mat_index = getMatIndex(name);
		return removeMat(mat_index);
	}

	// Links can be removed without further steps: public function
	/**
	 * Remove a Link module from the Model (at lIndex)
	 *
	 * @param lIndex
	 *            the index of the Link module to remove
	 * @return 0 if success, throws error otherwise.
	 */
	public int removeLink(int lIndex) {
		synchronized (m_lock) {
			try {
				// first check if the index can be in the list
				if ((modules.size() > lIndex) && (moduleIndexList.size() > lIndex))
					modules.remove(lIndex);
				moduleIndexList.remove(lIndex);
			} catch (Exception e) {
				System.out.println("Error removing link Module at " + lIndex + ": " + e);
				System.exit(1);
			}
		}
		return 0;
	}

	// Links can be removed without further steps: public function
	/**
	 * Remove a Link module from the Model (by name)
	 *
	 * @param name
	 *            identifier of the Link module to remove.
	 * @return 0 if success, throws error otherwise.
	 */
	public synchronized int removeLink(String name) {
		int mat_index = getLinkIndex(name);
		return removeLink(mat_index);
	}

	/**
	 * Remove a Mat module along with any connected Links.
	 *
	 * @param mIndex
	 *            the index of the Mat module to remove.
	 * @return 0 if success, throws error otherwise.
	 */
	public int removeMatAndConnectedLinks(int mIndex) {
		synchronized (m_lock) {
			try {
				for (int i = modules.size() - 1; i >= 0; i--) {
					// Will this work?
					if (modules.get(i) instanceof Link && ((Link)(modules.get(i))).getMat1() == modules.get(mIndex))
						removeLink(i);
					else if (modules.get(i) instanceof Link && ((Link)modules.get(i)).getMat2() == modules.get(mIndex))
						removeLink(i);
				}
				removeMat(mIndex);
				return 0;

			} catch (Exception e) {
				System.out.println("Issue removing connected links to mass!" + e);
				System.exit(1);
			}
		}
		return -1;
	}

	/**
	 * Remove a Mat module along with any connected Links.
	 *
	 * @param mName
	 *            name of the Mat module to remove.
	 * @return 0 if success, throw error otherwise.
	 */
	public int removeMatAndConnectedLinks(String mName) {
		int mat_index = getMatIndex(mName);
		return removeMatAndConnectedLinks(mat_index);
	}





	/**
	 * Quick module substitution (for boundary conditions).
	 *
	 * @param masName
	 *            identifier of the Mat module.
	 * @return
	 */
	public void changeToFixedPoint(String masName) {

		int mat_index = getMatIndex(masName);

		try {

			Vect3D pos = new Vect3D(0,0,0);
			pos = getMatPosAt(mat_index);

			modules.set(mat_index, new Ground3D(pos));

		} catch (Exception e) {
			System.out.println("Couldn't change into fixed point:  " + masName + ": " + e);
			System.exit(1);
		}
		return;
	}



	/**
	 * Get stiffness of a Mat module at given index (if it has a stiffness value)
	 * @param index
	 * @return the stiffness parameter
	 */
	public double getmodulestiffnessAt(int index) {

		if (index >= modules.size()) {
			System.out.println("Trying to get stiffness value in out of bounds mat.");
			return 0;
		}

		return  modules.get(index).getStiffness();

	}


	/**
	 * DIRTY !!
	 * Get orientation of a plane contact module at given index
	 * @param index
	 * @return the orientation parameter
	 */
/*	public int getPlaneOrientationAt(int index) {

		if (index >= links.size()) {
			System.out.println("Trying to get orientation value in out of bounds link.");
			return 0;
		}

		return 0;
		//TODO if not erased
	}
*/

	/**
	 * DIRTY !!
	 * Get position of a plane contact module at given index
	 * @param index
	 * @return the position parameter
	 */
/*	public double getPlanePositionAt(int index) {

		if (index >= links.size()) {
			System.out.println("Trying to get position value in out of bounds link.");
			return 0;
		}
		return 0;
		//TODO if not erased
	}
*/

	/**************************************************/
	/* Methods so that we can draw the model */
	/**************************************************/

	/**
	 * Fill an ArrayList with the positions of all masses of a given type.
	 *
	 * @param mArray
	 *            the ArrayList (that will be cleared and refilled).
	 * @param m
	 *            the module type that we are looking for.
	 */
	public void getAllmodulesOfType(ArrayList<PVector> mArray, String m) {
		mArray.clear();
		Module mat;
		Vect3D pos = new Vect3D();
		for (int i = 0; i < modules.size(); i++) {
			mat = modules.get(i);
			if (mat.getType() == m) {
				pos.set(mat.getPos(0));
				mArray.add(new PVector((float) pos.x, (float) pos.y, (float) pos.z));
			}
		}
	}

	/**
	 * Create and fill two Array Lists with the positions and velocities (per
	 * sample) of all modules of a given type. DEPRECEATED FUNCTION, use
	 * createPosSpeedArraysForModType and update instead.
	 *
	 * @param pArray
	 *            the position ArrayList (that will be cleared and refilled).
	 * @param vArray
	 *            the velocity ArrayList (that will be cleared and refilled).
	 * @param m
	 *            the module type that we are looking for.
	 */
	public void getAllmodulespeedsOfType(ArrayList<PVector> pArray, ArrayList<PVector> vArray, String m) {
		pArray.clear();
		vArray.clear();
		Module mat;
		Vect3D pos = new Vect3D();
		for (int i = 0; i < modules.size(); i++) {
			mat = modules.get(i);
			if (mat.getType() == m) {
				pos.set(mat.getPos(0));
				pArray.add(new PVector((float) pos.x, (float) pos.y, (float) pos.z));
				pos.sub(mat.getPosR(0));
				vArray.add(new PVector((float) pos.x, (float) pos.y, (float) pos.z));
			}
		}
	}

	/**
	 * Create and fill two Array Lists with the positions and velocities (per
	 * sample) of all modules of a given type. Use this for creating the new arrays
	 * before the simulation is launched. Once the simulation is running, use the
	 * updatePosSpeedArraysForModType method to update the existing ArrayLists.
	 *
	 * Quite inefficient method (checks all Mat modules for a given type)
	 *
	 * @param pArray
	 *            the position ArrayList (that will be cleared and refilled).
	 * @param vArray
	 *            the velocity ArrayList (that will be cleared and refilled).
	 * @param m
	 *            the module type that we are looking for.
	 */
	public void createPosSpeedArraysForModType(ArrayList<PVector> pArray, ArrayList<PVector> vArray, String m) {
		pArray.clear();
		vArray.clear();
		Module mat;
		Vect3D pos = new Vect3D();
		for (int i = 0; i < modules.size(); i++) {
			mat = modules.get(i);
			if (mat.getType() == m) {
				pos.set(mat.getPos(0));
				pArray.add(new PVector((float) pos.x, (float) pos.y, (float) pos.z));
				pos.sub(mat.getPosR(0));
				vArray.add(new PVector((float) pos.x, (float) pos.y, (float) pos.z));
			}
		}
	}

	/**
	 * Update two Array Lists with the positions and velocities (per sample) of all
	 * modules of a given type. Use this after createPosSpeedArraysForModType, once
	 * the simulation is running to update existing arrays.
	 *
	 * Quite inefficient method (checks all Mat modules for a given type)
	 *
	 * @param pArray
	 *            the position ArrayList.
	 * @param vArray
	 *            the velocity ArrayList.
	 * @param m
	 *            m the module type that we are looking for.
	 */
	public void updatePosSpeedArraysForModType(ArrayList<PVector> pArray, ArrayList<PVector> vArray, String m) {
		Module mat;
		Vect3D pos = new Vect3D();
		int arrayIndex = 0;
		for (int i = 0; i < modules.size(); i++) {
			mat = modules.get(i);
			if (mat.getType() == m) {
				pos.set(mat.getPos(0));
				pArray.set(arrayIndex, new PVector((float) pos.x, (float) pos.y, (float) pos.z));
				pos.sub(mat.getPosR(0));
				vArray.set(arrayIndex, new PVector((float) pos.x, (float) pos.y, (float) pos.z));
				arrayIndex++;
			}
		}
	}

	/*************************************************/
	/* META Parameters: air friction and gravit */
	/*************************************************/

	/**
	 * Set the friction (globally) for the complete model.
	 *
	 * @param frZ
	 *            the friction value.
	 */
	public void setFriction(double frZ) {


		friction = frZ;


		//TODO if not erased !
		/*
		// Some shady typecasting going on here...
		Mass3D tmp;
		Osc3D tmp2;
		Mass1D tmp3;
		Osc1D tmp4;
		Mass2DPlane tmp5;
		for (int i = 0; i < modules.size(); i++) {
			if (modules.get(i).getType() == matModuleType.Mass3D) {
				tmp = (Mass3D) modules.get(i);
				tmp.updateFriction(frZ);
			} else if (modules.get(i).getType() == matModuleType.Osc3D) {
				tmp2 = (Osc3D) modules.get(i);
				tmp2.updateFriction(frZ);
			} else if (modules.get(i).getType() == matModuleType.Mass1D) {
				tmp3 = (Mass1D) modules.get(i);
				tmp3.updateFriction(frZ);
			} else if (modules.get(i).getType() == matModuleType.Osc1D) {
				tmp4 = (Osc1D) modules.get(i);
				tmp4.updateFriction(frZ);
			} else if (modules.get(i).getType() == matModuleType.Mass2DPlane) {
				tmp5 = (Mass2DPlane) modules.get(i);
				tmp5.updateFriction(frZ);
			}
		}
		*/

	}


	public double getFriction(){
		return friction;
	}

	/**
	 * Trigger a force impulse on a given Mat module (identified by index).
	 *
	 * @param index
	 *            index of the module to apply a force to.
	 * @param fx
	 *            force in the X dimension.
	 * @param fy
	 *            force in the Y dimension.
	 * @param fz
	 *            force in the Z dimension.
	 */
	public void triggerForceImpulse(int index, double fx, double fy, double fz) {
		Vect3D force = new Vect3D(fx, fy, fz);
		try {
			((Mat)modules.get(index)).applyExtForce(force);
		} catch (Exception e) {
			System.out.println("Issue during force impuse trigger");
			System.exit(1);
		}
	}

	/**
	 * Trigger a force impulse on a given Mat module.
	 *
	 * @param name
	 *            the name of the module to apply a force to.
	 * @param fx
	 *            force in the X dimension.
	 * @param fy
	 *            force in the Y dimension.
	 * @param fz
	 *            force in the Z dimension.
	 */
	public void triggerForceImpulse(String name, double fx, double fy, double fz) {
		int mat1_index = getMatIndex(name);
		this.triggerForceImpulse(mat1_index, fx, fy, fz);
	}

	/**
	 * Set the gravity direction for this model (using a 3D vector)
	 *
	 * @param grav
	 *            The PVector defining the orientation of the gravity.
	 */
	public void setGravityDirection(PVector grav) {
		Vect3D gravDir = new Vect3D(grav.x, grav.y, grav.z);
		g_vector.set(gravDir.div(gravDir.norm()));
	}

	/**
	 * Get the normalised gravity vector for this physical model
	 * @return the gravity direction vector
	 */
	public Vect3D getGravityDirection(){
		return g_vector;
	}

	/**
	 * Set the value of the gravity for the model (scalar value).
	 *
	 * @param grav
	 *            the scalar value of the gravity applied to Mat modules within the
	 *            model.
	 */
	public void setGravity(double grav) {

		g_magnitude = grav;

		//Vect3D g_scaled = new Vect3D();

		g_scaled.set(g_vector);
		g_scaled.mult(g_magnitude);
		System.out.println("G scaled: " + g_scaled);

		//TODO if not erased
/*		// Some shady typecasting going on here...
		Mass3D tmp;
		Osc3D tmp2;
		Mass1D tmp3;
		Osc1D tmp4;
		Mass2DPlane tmp5;
		for (int i = 0; i < modules.size(); i++) {
			if (modules.get(i).getType() == matModuleType.Mass3D) {
				tmp = (Mass3D) modules.get(i);
				tmp.updateGravity(g_scaled);
			} else if (modules.get(i).getType() == matModuleType.Osc3D) {
				tmp2 = (Osc3D) modules.get(i);
				tmp2.updateGravity(g_scaled);
			} else if (modules.get(i).getType() == matModuleType.Mass1D) {
				tmp3 = (Mass1D) modules.get(i);
				tmp3.updateGravity(g_scaled);
			} else if (modules.get(i).getType() == matModuleType.Osc1D) {
				tmp4 = (Osc1D) modules.get(i);
				tmp4.updateGravity(g_scaled);
			} else if (modules.get(i).getType() == matModuleType.Mass2DPlane) {
				tmp5 = (Mass2DPlane) modules.get(i);
				tmp5.updateGravity(g_scaled);
			}
		}

 */
	}

	/**
	 * Get the gravity magnitude for this model
	 * @return the magnitude
	 */
	public double getGravity(){
		return g_magnitude;
	}


	/**
	 * Get the differentiated position of an Osc module at a given index. (This is a
	 * prototype function, should ideally be removed or replaced).
	 *
	 * @param i
	 *            index of the Osc Module to observe.
	 * @return the speed (differentiated position) of the module.
	 */
	public double getOsc3DDeltaPos(int i) {
		Osc3D tmp;
		if (modules.get(i).getType() == "Osc3D") {
			tmp = (Osc3D) modules.get(i);
			return tmp.distRest();
		}
		return 0;
	}

	/**
	 * Get the current distance (length) of a Link type module
	 *
	 * @param i
	 *            index of the Link to observe.
	 * @return the current distance value.
	 */
	public double getLinkDistanceAt(int i) {

		if (getNumberOfLinks() > i)
			return ((Link)modules.get(i)).getDist();
		else
			return 0;
	}


	/**
	 * Get the current elongation (length minus resting length) of a Link type module
	 *
	 * @param i
	 *            index of the Link to observe.
	 * @return the current elongation value.
	 */
	public double getLinkElongationAt(int i) {

		if (getNumberOfLinks() > i)
			return ((Link)modules.get(i)).getElong();
		else
			return 0;
	}

	/**
	 * Get the resting distance of a Link type module
	 *
	 * @param i
	 *            index of the Link to observe.
	 * @return the resting distance value.
	 */
	public double getLinkDRestAt(int i) {

		if (getNumberOfLinks() > i)
			return ((Link)modules.get(i)).getDRest();
		else
			return 0;
	}


	/**
	 * Force a Mat module to a given position (with null velocity).
	 *
	 * @param matName
	 *            identifier of the module.
	 * @param newPos
	 *            target position.
	 */
	public void setMatPosition(String matName, Vect3D newPos) {
		int mat_index = getMatIndex(matName);
		if (mat_index > -1)
			((Mat)this.modules.get(mat_index)).setPos(newPos);
	}


	/**
	 * Force a Mat module to a given position (with null velocity).
	 *
	 * @param index
	 *            identifier of the mat module.
	 * @param newPos
	 *            target position.
	 */
	public void setMatPosAt(int index, Vect3D newPos) {
		if ((index > -1) && index < modules.size())
			((Mat)this.modules.get(index)).setPos(newPos);
	}


	/**
	 * Create an empty Mat module subset item. Module indexes will be associated to
	 * this specific key later.
	 *
	 * @param name
	 *            the identifier for this subset.
	 * @return 0 if success, -1 otherwise.
	 */
	public int createModulesubset(String name) {
		if (!this.mat_subsets.containsKey(name)) {
			this.mat_subsets.put(name, new ArrayList<Integer>());
			return 0;
		}
		return -1;
	}

	/**
	 * Create an empty Mat module subset item. Module indexes will be associated to
	 * this specific key later.
	 *
	 * @param name
	 *            the identifier for this subset.
	 * @return 0 if success, -1 otherwise.
	 */
	public int createMatSubset(String name) {
		if (!this.mat_subsets.containsKey(name)) {
			this.mat_subsets.put(name, new ArrayList<Integer>());
			return 0;
		}
		return -1;
	}


	/**
	 * Add a Mat module to a given subset.
	 *
	 * @param matIndex
	 *            index of the Mat module.
	 * @param subsetName
	 *            the subset to add the module to.
	 * @return 0 if success, -1 if fail.
	 */
	public int addMatToSubset(int matIndex, String subsetName) {
		if (matIndex != -1) {
			this.mat_subsets.get(subsetName).add(matIndex);
			return 0;
		}
		return -1;
	}

	/**
	 * Add a Mat module to a given subset.
	 *
	 * @param matName
	 *            identifier of the Mat module.
	 * @param subsetName
	 *            the subset to add the module to.
	 * @return 0 if success, -1 if fail.
	 */
	public int addMatToSubset(String matName, String subsetName) {
		int matIndex = this.getMatIndex(matName);
		return this.addMatToSubset(matIndex, subsetName);
	}

	/**
	 * Create an empty Link module subset item. Module indexes will be associated to
	 * this specific key later.
	 *
	 * @param name
	 *            the identifier for this subset.
	 * @return 0 if success, -1 otherwise.
	 */
	public int createLinkSubset(String name) {
		if (!this.link_subsets.containsKey(name)) {
			this.link_subsets.put(name, new ArrayList<Integer>());
			return 0;
		}
		return -1;
	}

	/**
	 * Add a Link module to a given subset.
	 *
	 * @param linkIndex
	 *            index of the Link module.
	 * @param subsetName
	 *            the subset to add the module to.
	 * @return 0 if success, -1 if fail.
	 */
	public int addLinkToSubset(int linkIndex, String subsetName) {
		if (linkIndex != -1) {
			this.link_subsets.get(subsetName).add(linkIndex);
			return 0;
		}
		return -1;
	}

	/**
	 * Add a Link module to a given subset.
	 *
	 * @param linkName
	 *            identifier of the Link module.
	 * @param subsetName
	 *            the subset to add the module to.
	 * @return 0 if success, -1 if fail.
	 */
	public int addLinkToSubset(String linkName, String subsetName) {
		int linkIndex = this.getLinkIndex(linkName);
		return this.addLinkToSubset(linkIndex, subsetName);
	}

	public boolean hasSubset(String name)
	{
		return link_subsets.containsKey(name) || mat_subsets.containsKey(name);
	}




	/**
	 * Change the distance regarding X  between a subset of Mass modules.
	 *
	 * @param center
	 *            the barycenter of the subset (pre computed for performance reasons
	 *
	 * @param newDist
	 *            the new distance between two neighbors in the subset
	 * @param subsetName
	 *            the name of the subset of modules to address.
	 */
	public void changeDistXBetweenSubset(Vect3D center,double newDist, String subsetName) {
		if(this.mat_subsets.containsKey(subsetName)) {
			int i=0;
			int nbmodules = this.mat_subsets.get(subsetName).size();
			Vect3D newPos = ((new Vect3D(0,0,0)).add(center)).add(new Vect3D(-newDist*(nbmodules-1)/2,0,0));
			for (int matIndex : this.mat_subsets.get(subsetName)) {
				if(i!=0) newPos.add(new Vect3D(newDist,0,0));
				//System.out.println("mat " +matIndex + " moved from " + modules.get(matIndex).getPos(0) + " to " + newPos);
				((Mat)modules.get(matIndex)).setPos(newPos);
				i++;
			}
		}
	}



	public void setParamForSubset(float newParam,String subsetName,  Method setter)
	{
		try {
			if (this.link_subsets.containsKey(subsetName)) {
				for (int linkIndex : this.link_subsets.get(subsetName)) {
					setter.invoke(modules.get(linkIndex), newParam);
				}
			}
			if (this.mat_subsets.containsKey(subsetName)) {

				for (int matIndex : this.mat_subsets.get(subsetName)) {
					setter.invoke(modules.get(matIndex), newParam);
				}
			}
			if (this.moduleIndexList.contains(subsetName)) setter.invoke(modules.get(getModuleIndex(subsetName)),newParam);
		}
		catch(Exception e)
		{
			System.out.println("could not set parameter " + e.getMessage());//TODO should rethrow exception
		}
	}
	/**
	 * Get any param for a subset of modules.
	 *
	 * @param subsetName
	 *            the name of the subset of modules to address.
	 * @param paramName
	 *            the name of the parameter to address.
	 * @return the param value
	 */

	public double getParamValueOfSubset(String subsetName,String paramName)
	{
		if(this.link_subsets.containsKey(subsetName) && !link_subsets.get(subsetName).isEmpty())
		{
			int li = link_subsets.get(subsetName).get(0);
			if(paramName.equals("stiffness")) return modules.get(li).getStiffness();
			else if(paramName.equals("damping")) return modules.get(li).getDamping();
			else if(paramName.equals("dist")) return ((Link)modules.get(li)).getDist();
		}
		else if (this.mat_subsets.containsKey(subsetName) )
		{
			int mi = mat_subsets.get(subsetName).get(0);
			if(paramName.equals("stiffness")) return modules.get(mi).getStiffness();
			else if(paramName.equals("damping")) return modules.get(mi).getDamping();
			//else if(paramName.equals("dist")) return modules.get(mi).getDist();
			else if(paramName.equals("mass")) return ((Mat)(modules.get(mi))).getMass();

		}
		return -1.;
	}


	/**
	 * Get any param for a subset of modules.
	 *
	 * @param subsetName
	 *            the name of the subset of modules to address.
	 * @param paramName
	 *            the name of the parameter to address.
	 * @return the param value
	 */

	public double getParam(String moduleName,String paramName)
	{

			if(paramName.equals("stiffness")) return getModule(moduleName).getStiffness();
			else if(paramName.equals("damping")) return getModule(moduleName).getDamping();
			else if(paramName.equals("dist")) return ((Link)getModule(moduleName)).getDist();
			else if(paramName.equals("mass")) return ((Mat)getModule(moduleName)).getMass();
			else return -1.; //TODO throw exception
	}

	public double getParam(int index,String paramName)
	{

		if(paramName.equals("stiffness")) return getModule(index).getStiffness();
		else if(paramName.equals("damping")) return getModule(index).getDamping();
		else if(paramName.equals("dist")) return ((Link)getModule(index)).getDist();
		else if(paramName.equals("mass")) return ((Mat)getModule(index)).getMass();
		else return -1.; //TODO throw exception
	}
	/**
	 * Get the barycenter for a subset of modules.
	 *
	 * @param subsetName
	 *            the name of the subset of modules to address.
	 * @return the barycenter vector
	 */

	public Vect3D getBarycenterOfSubset(String subsetName)
	{
		Vect3D res= new Vect3D(0,0,0);
		if (this.mat_subsets.containsKey(subsetName) )
		{
			for(int mi:mat_subsets.get(subsetName)) res.add(modules.get(mi).getPos(0));
		}
		res.div(this.mat_subsets.get(subsetName).size());
		return res;
	}



	/* HAPTIC INPUT ELEMENTS */

	/**
	 * Add a haptic input "avatar" module (or any position input module) to the physical model.
	 *
	 * @param name
	 *            the name of the module.
	 * @param initPos
	 *            the initial position of the module.
	 * @param smoothing
	 * 			  EWMA smoothing factor for incoming position data (1 = no smoothing)
	 *
	 */

	public HapticInput3D addHapticInput3D(String name, Vect3D initPos, int smoothing) {
		HapticInput3D inputMod;
		try {
			if (moduleIndexList.contains(name)) {

				System.out.println("The module name already exists!");
				throw new Exception("The module name already exists!");
			}
			inputMod = new HapticInput3D(initPos, smoothing);
			modules.add(inputMod);
			moduleIndexList.add(name);
			return inputMod;

		} catch (Exception e) {
			System.out.println("Error adding Module " + name + ": " + e);
			System.exit(1);
		}
		return null;
	}

	/**
	 * Set the position of a haptic input module ("avatar") from the outside world
	 *
	 * @param matName
	 * 			  the name of the haptic module
	 * @param newPos
	 * 			  the new position value.
	 */
	public void setHapticPosition(String matName, Vect3D newPos) {
		synchronized (m_lock) {
			int mat_index = getMatIndex(matName);
			HapticInput3D tmp;
			if (modules.get(mat_index).getType() == "HapticInput3D") {
				tmp = (HapticInput3D) modules.get(mat_index);
				tmp.applyInputPosition(newPos);
			} else {
				System.out.println("The module is not a haptic input!");
			}
		}
	}


	/**
	 * Get the force accumulated in a haptic "avatar" (to apply it to the haptic device)
	 *
	 * @param matName
	 * 			  the name of the haptic module
	 * @return the force vector.
	 */
	public Vect3D getHapticForce(String matName) {
		synchronized (m_lock) {
			int mat_index = getMatIndex(matName);
			HapticInput3D tmp;
			if (modules.get(mat_index).getType() == "HapticInput3D") {
				tmp = (HapticInput3D) modules.get(mat_index);
				return tmp.applyOutputForce();
			} else {
				System.out.println("The module is not a haptic input!");
				return new Vect3D(0, 0, 0);
			}
		}
	}

	/**
	 * Trigger a force impulse on a given Mat module (identified by index).
	 *
	 * @param index
	 *            index of the module to apply a force to.
	 * @param vx
	 *            force in the X dimension.
	 * @param vy
	 *            force in the Y dimension.
	 * @param vz
	 *            force in the Z dimension.
	 */
	public void triggerVelocityControl(int index, double vx, double vy, double vz) {
		Vect3D force = new Vect3D(vx/simRate, vy/simRate, vz/simRate);
		try {
			((Mat)modules.get(index)).triggerVelocityControl(force);
		} catch (Exception e) {
			System.out.println("Issue during velocity control trigger");
			System.exit(1);
		}
	}

	/**
	 * Trigger velocity control on a given Mat module.
	 *
	 * @param name
	 *            the name of the module to trigger.
	 * @param vx
	 *            velocity in the X dimension.
	 * @param vy
	 *            velocity in the Y dimension.
	 * @param vz
	 *            velocity in the Z dimension.
	 */
	public void triggerVelocityControl(String name, double vx, double vy, double vz) {
		int mat1_index = getMatIndex(name);
		this.triggerVelocityControl(mat1_index, vx, vy, vz);
	}

	/**
	 * Stop a force impulse on a given Mat module.
	 *
	 * @param name
	 *            the name of the module to stop velocity control to.
     */

	public void stopVelocityControl(String name)
	{
		this.stopVelocityControl(getMatIndex(name));
	}

	/**
	 * Stop a force impulse on a given Mat module.
	 *
	 * @param index
	 *            the index of the module to stop velocity control to.
	 */

	public void stopVelocityControl(int index)
	{
		try
		{
			((Mat)modules.get(index)).stopVelocityControl();
		}catch (Exception e) {
			System.out.println("Issue during stopping velocity control for mass at index " + index );
			System.exit(1);
		}
	}

	public void addParamController(String name,String subsetName,String paramName,float rampTime)
	{
		param_controllers.put(name,new ParamController(this,rampTime,subsetName,paramName));
	}

	public ParamController getParamController(String name) {return param_controllers.get(name);}

	public Module getFirstModuleOfSubset(String subsetName)
	{
		if(mat_subsets.containsKey(subsetName) ) return modules.get(mat_subsets.get(subsetName).get(0));
		else if(link_subsets.containsKey(subsetName))  return modules.get(link_subsets.get(subsetName).get(0));
		else return null;//TODO should throw exception
	}

	public Module getModule(String moduleName)
	{
		if(moduleIndexList.contains(moduleName) || matExists(moduleName) || linkExists(moduleName)) return modules.get(getModuleIndex(moduleName));
		else return null;//TODO throw exception or at least log sth
	}

	public Boolean hasModule(String moduleName)
	{
		return (moduleIndexList.contains(moduleName) || matExists(moduleName) || linkExists(moduleName));
	}

	public Module getModule(int i)
	{
		return modules.get(i);
	}


	public List<Mat> getMats()
	{
		List<Mat> ret = new ArrayList<Mat>();
		for(Module m:modules)
		{
			if (m instanceof Mat) ret.add((Mat)m);
		}
		return ret;
	}

	public List<Link> getLinks()
	{
		List<Link> ret = new ArrayList<Link>();
		for(Module m:modules)
		{
			if (m instanceof Link) ret.add((Link)m);
		}
		return ret;
	}


	public List<Module> getMultiPointModules()
	{
		List<Module> ret = new ArrayList<Module>();
		for(Module m:modules)
		{
			if (m instanceof String2D) ret.add(m);
		}
		return ret;
	}

	private void welcome() {
		System.out.println("##library.name## ##library.prettyVersion## by ##author##");
	}

	public Lock getLock(){
		return m_lock;
	}




	/**
	 * return the version of the Library.
	 *
	 * @return String
	 */
	public static String version() {
		return VERSION;
	}

}
