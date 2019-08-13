package org.micreative.miPhysics.Engine.Modules;


/* 3D SPRING OBJECT */

import org.micreative.miPhysics.Engine.Link;
import org.micreative.miPhysics.Engine.Mat;

/**
 * Spring interaction: elastic interaction between two Mat elements.
 * @author James Leonard / james.leonard@gipsa-lab.fr
 *
 */
public class Spring3D extends Link {

  public Spring3D(double distance, double K_param, Mat m1, Mat m2)
  {
    super(distance, m1, m2);
    stiffness = K_param;
  }

  public void compute()
  {
    updateEuclidDist();
    applyForces( -(m_dist - m_dRest) * stiffness );
  }

}