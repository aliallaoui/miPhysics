package org.micreative.miPhysics.Engine;

import java.lang.reflect.Method;

public abstract class AbstractIterator extends Index{

    protected int[] dimensions;
    protected Method nextMethod;

    public AbstractIterator(int[] dimensions,String definition) {
        super(dimensions);
        this.dimensions = dimensions;
    }
// todo could be abstract
    public void begin()
    {
        for (int i=0;i<dimensions.length;i++) coordinates[i]=0;
    }

    public boolean next() throws Exception
    {
        return (boolean) nextMethod.invoke(this);
    }



}
