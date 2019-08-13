package org.micreative.miPhysics.Engine.Modules;


import org.micreative.miPhysics.Engine.Link;
import org.micreative.miPhysics.Engine.Mat;

import org.micreative.miPhysics.Vect3D;
/**
 * Plane Contact: contact interaction between a Mat module and a 2D plane.
 * This module's implementation is a little weird, the physics seem OK though
 *
 * @author James Leonard / james.leonard@gipsa-lab.fr
 *
 */
public class PlaneContact extends Link {


  public PlaneContact(double distance, double K_param, double Z_param, Mat m1, Mat m2, int or, double pos) {
    super(0., m1, m2);

    damping = Z_param;
    stiffness = K_param;
    m_dRest = distance;
    m_position = pos;
    m_orientation = or;
  }

  public void compute() {

    m_dlyPosForPlane = m_posForPlane;

    //TODO Review this code before making anything public in class Mat to make it work...
    /*
    if (m_orientation == 0)
      m_posForPlane = getMat1().getPos().x;
    else if (m_orientation == 1)
      m_posForPlane = getMat1().getPos().y;
    else if (m_orientation == 2)
      m_posForPlane = getMat1().getPos().z;

    double thresholdPos = m_posForPlane - m_position - m_dRest ;
    double lnkFrc = - thresholdPos *(stiffness) - (m_posForPlane - m_dlyPosForPlane) *  damping;

    if (thresholdPos < 0) {
      if (m_orientation == 0)
        getMat1().m_frc.x += lnkFrc;
      else if (m_orientation == 1)
        getMat1().m_frc.y += lnkFrc;
      else if (m_orientation == 2)
        getMat1().m_frc.z += lnkFrc;
    }
    */

  }

  public int getOrientation(){return m_orientation;}
  public double getPosition(){return m_position;}

  private double m_position;
  private int m_orientation;

  double m_posForPlane;
  double m_dlyPosForPlane;
}