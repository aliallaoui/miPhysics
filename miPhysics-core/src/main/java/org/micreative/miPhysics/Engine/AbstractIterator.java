package org.micreative.miPhysics.Engine;

import java.lang.reflect.Method;

public abstract class AbstractIterator extends Index{

    protected int[] dimensions;
    protected Method nextMethod;
    protected Method beginMethod;
    protected Method endMethod;

    public AbstractIterator(int[] dimensions,String definition) {
        super(dimensions);
        this.dimensions = dimensions;
    }
// todo could be abstract
    public void begin() throws Exception
    {
        beginMethod.invoke(this);
//        for (int i=0;i<dimensions.length;i++) coordinates[i]=0;
    }

    public void next() throws Exception
    {
        nextMethod.invoke(this);
    }

    public boolean end() throws Exception
    {
        return (boolean) endMethod.invoke(this);
    }



}
