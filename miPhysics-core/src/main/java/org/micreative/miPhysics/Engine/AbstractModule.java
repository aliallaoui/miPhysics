package org.micreative.miPhysics.Engine;

import org.micreative.miPhysics.Vect3D;

public interface AbstractModule {
    void computeForces();
    void computeMoves();
    void init() throws Exception;
    int getNbPoints();
    String getType();
}
