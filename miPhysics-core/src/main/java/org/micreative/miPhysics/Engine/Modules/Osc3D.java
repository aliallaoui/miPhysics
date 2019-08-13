package org.micreative.miPhysics.Engine.Modules;


import org.micreative.miPhysics.Engine.Mat;
import org.micreative.miPhysics.Vect3D;

/**
 * A 3D Mass Spring Oscillator with a given inertia, stiffness and damping, subject to potential gravity.
 * @author James Leonard / james.leonard@gipsa-lab.fr
 *
 */
public class Osc3D extends Mat {

  public Osc3D(double M, double K_param, double Z_param, Vect3D initPos, Vect3D initPosR, double friction, Vect3D grav) {
    super(M, initPos, initPosR);
    m_pRest = new Vect3D();
    m_pRest.set(initPos);

    stiffness = K_param;
    damping = Z_param;

    m_A = 2. - m_invMass * stiffness - m_invMass * (damping+friction) ;
    m_B = 1. -m_invMass * (friction + damping) ;

    this.friction = friction;
    gravity = new Vect3D();
    gravity.set(grav);
  }

  public void compute() { 
    tmp.set(m_pos);
    
    // Remove the position offset of the module (calculate the oscillator around zero)
    m_pos.x -= m_pRest.x;
    m_pos.y -= m_pRest.y;
    m_pos.z -= m_pRest.z;
    m_posR.x -= m_pRest.x;
    m_posR.y -= m_pRest.y;
    m_posR.z -= m_pRest.z;

    // Calculate the oscillator algorithm, centered around zero.
    m_frc.mult(m_invMass);
    m_pos.mult(m_A);
    m_posR.mult(m_B);
    m_pos.sub(m_posR);
    m_pos.add(m_frc);

    // Add gravitational force.
    m_pos.sub(gravity);
    
    // Restore the offset of the module
    m_pos.x += m_pRest.x;
    m_pos.y += m_pRest.y;
    m_pos.z += m_pRest.z;

    // Bring old position to delayed position and reset force buffer
    m_posR.set(tmp);
    m_frc.set(0., 0., 0.);
  }

  public double getStiffness(){return stiffness;}
  public double getDamping(){return damping;}

  public void setStiffness(double K){
    stiffness = K;
  }

  public void setDamping(double Z){
    damping = Z;
  }


  public void setFriction(double fric) { 
    friction= fric;

    m_A = 2. - m_invMass * stiffness - m_invMass * (damping+friction) ;
    m_B = 1. -m_invMass * (friction + damping) ;
  }
  
  public double distRest() {  // AJOUT JV Permet de sortir le DeltaX relatif entre mas et sol d'une cel
	    return m_pos.dist(m_pRest); 
	  }

  /* Class attributes */
  
  private double m_A;
  private double m_B;
  
  private double stiffness;
  private double damping;
  
  private Vect3D m_pRest;
}
