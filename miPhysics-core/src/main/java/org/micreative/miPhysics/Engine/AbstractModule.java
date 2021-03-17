package org.micreative.miPhysics.Engine;

import org.micreative.miPhysics.Vect3D;

public interface AbstractModule {
    void computeForces() throws Exception;
    void computeMoves() throws Exception;
    void init() throws Exception;
    int getNbPoints();
    String getType();
}
