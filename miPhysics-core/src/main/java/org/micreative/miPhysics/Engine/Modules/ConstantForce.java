package org.micreative.miPhysics.Engine.Modules;
import org.micreative.miPhysics.Engine.AbstractIterator;
import org.micreative.miPhysics.Engine.Index;
import org.micreative.miPhysics.Engine.Link;
import org.micreative.miPhysics.Engine.Module;
import org.micreative.miPhysics.Vect3D;

public class ConstantForce extends Link{
    public double getForceX() {
        return forceX;
    }

    public void setForceX(double forceX) {
        this.forceX = forceX;
        this.forceVector.x = forceX;
    }

    public double getForceY() {
        return forceY;
    }

    public void setForceY(double forceY) {
        this.forceY = forceY;
        this.forceVector.y = forceY;
    }

    public double getForceZ() {
        return forceZ;
    }

    public void setForceZ(double forceZ) {
        this.forceZ = forceZ;
        this.forceVector.z = forceZ;
    }

    protected double forceX;
    protected double forceY;
    protected double forceZ;
    protected Vect3D forceVector;

    public ConstantForce(Module m1, Module m2, AbstractIterator i1, AbstractIterator i2)
    {
        super(m1,m2,i1,i2);
        this.forceVector = new Vect3D(0.,0.,0.);
    }

    @Override
    public void init()
    {
        //    initDistances();
    }

    @Override
    public void computeForces() throws Exception {
        getMat1().addFrc(forceVector,index1);
        getMat2().addFrc(forceVector,index2);
    }
}
