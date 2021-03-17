package org.micreative.miPhysics;

import processing.core.PVector;

/* A simple vector class based on double precision floats
 * as opposed to Processing's PVector class
 */
//TODO rename to Vector, make dimension free by replacing double x,y,z by double vector[]
// keep x,y,z setters/getters, add three constructors for 1D, 2D and 3D
// make that member private
public class Vect3D {
	public double x, y, z;

	// constructors
	public Vect3D( ) {
	}

	public Vect3D(double x, double y, double z) {
		this.x = x; this.y = y; this.z = z;
	}

	public Vect3D(Vect3D v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}

	public double x() {return x;}
	public double y() {return y;}
	public double z() {return z;}

	public double norm() {
		return Math.sqrt(Math.pow(this.x,2)+ Math.pow(this.y,2) + Math.pow(this.z,2));
	}

	public float scalarProduct(Vect3D v2)
	{
		return (float)(x*v2.x + y*v2.y + z*v2.z);
	}


	public double dist(Vect3D v2) {
		// Avoid NaN problems !
		if(this.equals(v2)) {
			return 0.00000001;
		}
		else
		{
			return Math.sqrt(Math.pow(v2.x-this.x,2)+ Math.pow(v2.y-this.y,2) + Math.pow(v2.z-this.z,2));
			//return Math.sqrt((v2.x-this.x)*(v2.x-this.x)
			//+ (v2.y-this.y)*(v2.y-this.y) + (v2.z-this.z)*(v2.z-this.z));
		}

	}

	public double distZ(Vect3D v2) { return this.z - v2.z;}

	public double sqDist(Vect3D v2) {
		// Avoid NaN problems !
		if(this.equals(v2)) {
			return 0.00000001;
		}
		else
			return Math.pow(v2.x-this.x,2)+ Math.pow(v2.y-this.y,2) + Math.pow(v2.z-this.z,2);

	}

	public boolean equals(Vect3D v2) {
		return (this.x == v2.x) && (this.y == v2.y) && (this.z == v2.z) ;
	}

	public Vect3D add(Vect3D v2) {
		this.x += v2.x;
		this.y += v2.y;
		this.z += v2.z;
		return this;
	}

	public static Vect3D add(Vect3D v1,Vect3D v2)
	{
		Vect3D res = new Vect3D(v1);
		res.add(v2);
		return res;
	}

	public Vect3D sub(Vect3D v2) {
		this.x -= v2.x;
		this.y -= v2.y;
		this.z -= v2.z;
		return this;
	}

	public static Vect3D sub(Vect3D v1,Vect3D v2)
	{
		Vect3D res = new Vect3D(v2);
		res.sub(v1);
		return res;
	}
	public Vect3D mult(double factor) {
		this.x = this.x * factor;
		this.y = this.y * factor;
		this.z = this.z * factor;
		return this;
	}

	public static Vect3D mult(Vect3D v,double factor)
	{
		Vect3D res = new Vect3D(v);
		res.mult(factor);
		return res;
	}

	public Vect3D div(double factor) {
		double invFact = 1./factor;
		this.x = this.x * invFact;
		this.y = this.y * invFact;
		this.z = this.z * invFact;
		return this;
	}

	public Vect3D set(Vect3D v2) {
		this.x = v2.x;
		this.y = v2.y;
		this.z = v2.z;
		return this;
	}

	public void reset() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public Vect3D set(double x_val, double y_val, double z_val) {
		this.x = x_val;
		this.y = y_val;
		this.z = z_val;
		return this;
	}

	public Vect3D getUnitTo(Vect3D other)
	{
		double invDist = 1/this.dist(other);
		return new Vect3D((x-other.x)*invDist,(y-other.y)*invDist,(z-other.z)*invDist);
	}

	public PVector toPVector() {
		return new PVector((float)x, (float)y, (float)z);
	}


	// methods
	public String toString() {
		return "["+x+", "+y+", "+z+"]";
	}

	static public Vect3D fromString(String s)
	{
		String [] v = s.substring(1,s.length()-1).split(",");
		return new Vect3D(Double.parseDouble( v[0]),Double.parseDouble( v[1]),Double.parseDouble( v[2]));
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
	static public Vect3D constructDelayedPos(Vect3D pos, Vect3D vel_mps,int simRate) {
		Vect3D velPerSample = new Vect3D();
		Vect3D initPosR = new Vect3D();

		velPerSample.set(vel_mps);
		velPerSample.div(simRate);

		initPosR.set(pos);
		initPosR.sub(velPerSample);

		return initPosR;
	}
}

