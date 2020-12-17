package org.micreative.miPhysics.Engine;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.locks.*;
import java.lang.Math;

import org.apache.commons.beanutils.PropertyUtils;
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

public class PhysicalModel extends MetaModule{

	// dummy comment

	// myParent is a reference to the parent sketch
	PApplet myParent;

	private Lock m_lock;

	/* The simulation rate (mono rate only) */
	private int simRate;

	/* The processing sketch display rate */
	private int displayRate;

	private double simDisplayFactor;
	private int nbStepsToSim;
	private double residue;
	private long nbStepsSimulated=0;
	public static int loopFrame = 0;
	private Timestamp timestamp ;

	protected List<OutputBuffer> outputBuffers;
	protected List<InputBuffer> inputBuffers;
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
	public PhysicalModel(String name,int sRate, int displayRate)
	{
		super(name);
		Vect3D tmp = new Vect3D(0., 0., 0.);

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

		System.out.println("Physical Model Class Initialised");
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
	 * Get number of Mat modules in current model.
	 *
	 * @return the number of Mat modules in this model.
	 */
	@Override
	public int getNbPoints() {
		int m = 0;
		for(Map.Entry<String,Module> mod:modules.entrySet())
		{
			m+=mod.getValue().getNbPoints();
		}
		return m;
	}

	public int getNumberOfModules() {
		return modules.size();
	}

	/**
	 * Check if a Module module with a given identifier exists in the current model.
	 *
	 * @param lName
	 *            the identifier of the Module module.
	 * @return True of the module exists, False otherwise.
	 */
	public boolean moduleExists(String lName) {
		return modules.containsKey(lName);
	}



	/**
	 * Check the type (mass, ground, osc) of Mat module at index i
	 *
	 * @param name
	 *            the name of the  module.
	 * @return the type of the module.
	 */
	public String getModuleType(String name) {
		return modules.get(name).getType();
	}




/* should be put on top in MetaModule or interface
	public List<Vect3D> getAllPositions()
	{
		List<Vect3D> ret = new ArrayList<>();
		for(Mat m:getMats()) ret.add(m.getPoint());
		for(Module m:getMultiPointModules()){
			for(int i=0;i<m.getNbPoints();i++) ret.add(m.getPoint(i));
		}
		return ret;
	}

	public List<Vect3D> getAllLinkPositions()
	{
		List<Vect3D> ret = new ArrayList<>();
		for(Link l:getLinks()){
			ret.add(l.getMat1().getPoint());
			ret.add(l.getMat2().getPoint());
		}

		return ret;
	}
*/


	/**
	 * Explicitly compute N steps of the physical simulation. Should be called once
	 * the model creation is finished and the init() method has been called.
	 *
	 * @param N
	 *            number of steps to compute.
	 */
	public void computeNSteps(int N,boolean init) throws Exception {
			for (int frame = 0; frame < N; frame++) {
//				if(nbStepsSimulated== 0) timestamp = new Timestamp(System.currentTimeMillis());
				loopFrame = frame;
				gatherData();
				controlModules();
				if(!init) computeMoves();
				//controlModulePositions() ?
				if(!init) computeForces(); //TODO those if should be put outside the loop
				System.out.println(getPoint("m",0));
				}
		nbStepsSimulated+=N;
	/*	if(nbStepsSimulated%(simRate*5) == 0)
		{
			Timestamp current = new Timestamp(System.currentTimeMillis());
			double secs = (double)(current.getTime() - timestamp.getTime())/1000.;
			double sim_secs = (double)nbStepsSimulated/(double)simRate;
			if(Math.abs(secs-sim_secs) > 0.05) System.out.println(secs + " seconds ellapsed " +  sim_secs + " simulated");
		}
		*/

	}

	/**
	 * Compute a single step of the physical simulation. Should be called once the
	 * model creation is finished and the init() method has been called.
	 */
	public void computeStep(boolean init)throws Exception
	{
		computeNSteps(1,init);
	}


	public void setVelocity(String name,int index,Vect3D velocity)
	{
		modules.get(name).setPointR(index,Vect3D.constructDelayedPos(getPoint(name,index),
		velocity,simRate));
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
		int mat1_index = -1; //getMatIndex(name);
		this.triggerForceImpulse(mat1_index, fx, fy, fz);
	}

	public Object getParam(String moduleName,String param)
	{
		try{
			if (this.modules.containsKey(moduleName))
			{
				Module m = modules.get(moduleName);
				return PropertyUtils.getPropertyDescriptor(m, param).getReadMethod().invoke(m);
			}
			/*
			else if(this.param_controllers.containsKey(moduleName))
			{
				ScalarController m = param_controllers.get(moduleName);
				return PropertyUtils.getPropertyDescriptor(m, param).getReadMethod().invoke(m);
			}
			else if(this.position_controllers.containsKey(moduleName))
			{
				PositionController m = position_controllers.get(moduleName);
				return PropertyUtils.getPropertyDescriptor(m, param).getReadMethod().invoke(m);
			}
			*/

		}
		catch(Exception e)
		{
			System.out.println("could not set parameter " + e.getMessage());//TODO should rethrow exception
			return null;
		}
		return null;
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
