package org.micreative.miPhysics.Engine.Modules;


import org.micreative.miPhysics.Engine.Mat;
import org.micreative.miPhysics.Vect3D;

/**
 * A regular 2D Mat module (constrained to the XY plane), with a given inertia, subject to potential gravity.
 * @author James Leonard / james.leonard@gipsa-lab.fr
 *
 */
public class Mass2DPlane extends Mat {

  public Mass2DPlane(double M, Vect3D initPos, Vect3D initPosR, double friction, Vect3D grav) {
    super(initPos, initPosR);

    // Make sure there is no initial velocity on the Z axis
    m_posR.z = m_pos.z;

    this.friction = friction;
    gravity = new Vect3D();
    gravity.set(grav);
  }

  public void computeMoves() {
    tmp.set(m_pos);

    if (m_controlled) {
      m_pos.add(m_controlVelocity);
    }
    else
    {
    // Calculate the update of the mass's position
    m_frc.mult(m_invMass);
    m_pos.mult(2 - m_invMass * friction);
    m_posR.mult(1 -m_invMass * friction);
    m_pos.sub(m_posR);
    m_pos.add(m_frc);
    m_pos.sub(gravity);

    // Constrain to 2D Plane : keep Z axis value constant
    m_pos.z = tmp.z;
    }
    m_posR.set(tmp);
    m_frc.set(0., 0., 0.);
  }


}
