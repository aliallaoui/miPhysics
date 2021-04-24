package org.micreative.miPhysics.Engine.Modules;

import org.micreative.miPhysics.Engine.AbstractIterator;
import org.micreative.miPhysics.Engine.Index;
import org.micreative.miPhysics.Engine.MacroLink;
import org.micreative.miPhysics.Engine.Module;

public class MSpringDamper extends MacroLink {


    public double getRestLength() {
        return restLength;
    }

    public void setRestLength(double restLength) {
        this.restLength = restLength;
    }

    protected double restLength;


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
        return (distance - restLength) * stiffness +  (distance - distanceR )* damping;
    }


}
