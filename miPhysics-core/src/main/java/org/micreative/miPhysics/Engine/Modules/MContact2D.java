package org.micreative.miPhysics.Engine.Modules;

import org.micreative.miPhysics.Engine.AbstractIterator;
import org.micreative.miPhysics.Engine.Index;
import org.micreative.miPhysics.Engine.MacroLink;
import org.micreative.miPhysics.Engine.Module;

public class MContact2D extends MacroLink {



    protected double restLength;


    /**
     * @param m1
     * @param m2
     * @param dimensions
     * @param iterator1
     * @param iterator2
     */
    public MContact2D(Module m1, Module m2, int[] dimensions,
                      AbstractIterator iterator1, AbstractIterator iterator2)
    {
        super(m1,m2,dimensions,iterator1,iterator2);
    }

    public double computeForce(Index i1,Index i2)
    {
        if(distance < restLength)
            return (distance - restLength) * stiffness +  (distance - distanceR )* damping;
        else
            return 0.;
    }


}