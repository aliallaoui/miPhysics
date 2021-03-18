package org.micreative.miPhysics.Engine.Modules;

import org.micreative.miPhysics.Engine.AbstractIterator;
import org.micreative.miPhysics.Engine.Index;
import org.micreative.miPhysics.Engine.MacroLink;
import org.micreative.miPhysics.Engine.Module;

public class MSpringDamper extends MacroLink {


    public double getRestDistance() {
        return restDistance;
    }

    public void setRestDistance(double restDistance) {
        this.restDistance = restDistance;
    }

    protected double restDistance;


    /**
     * @param m1
     * @param m2
     * @param iterator1
     * @param iterator2
     */
    public MSpringDamper(Module m1, Module m2,
                      AbstractIterator iterator1, AbstractIterator iterator2)
    {
        super(m1,m2,iterator1,iterator2);
    }

    public double computeForce(Index i1,Index i2)
    {
        return (distance - restDistance) * stiffness +  (distance - distanceR )* damping;
    }


}
