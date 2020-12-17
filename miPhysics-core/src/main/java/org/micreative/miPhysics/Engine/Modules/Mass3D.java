package org.micreative.miPhysics.Engine.Modules;


import org.micreative.miPhysics.Engine.Mat;
import org.micreative.miPhysics.Vect3D;

/**
 * A regular 3D Mat module, with a given inertia, subject to potential gravity.
 * @author James Leonard / james.leonard@gipsa-lab.fr
 *
 */
public class Mass3D extends Mat {

  public Mass3D( Vect3D initPos, Vect3D initPosR) {
    super(initPos, initPosR);

  }
  public Mass3D( ) {
    super();
  }
  public void computeMoves() {
    tmp.set(m_pos);
    if (m_controlled) {
      m_pos.add(m_controlVelocity);
    }
    else {
    // Calculate the update of the mass's position
    m_frc.mult(m_invMass);
      m_pos.mult(2 - m_invMass * friction);
      m_posR.mult(1 - m_invMass * friction);
    m_pos.sub(m_posR);
    m_pos.add(m_frc);

    // Add gravitational force.
      m_pos.sub(gravity);
    }
    // Bring old position to delayed position and reset force buffer
    m_posR.set(tmp);
    m_frc.set(0., 0., 0.);
  }

}
