package org.micreative.miPhysics.Engine.Modules;

import org.micreative.miPhysics.Engine.Link;
import org.micreative.miPhysics.Engine.Mat;

/**
 * Damper interaction: viscous force between two Mat elements.
 * 
 * @author James Leonard / james.leonard@gipsa-lab.fr
 *
 */
public class Damper3D extends Link {

  public Damper3D(double Z_param, Mat m1, Mat m2) {
    super(0., m1, m2);
  }

  public void compute() {
    updateEuclidDist();
    applyForces( - getVel() *  damping );
  }
  

}
