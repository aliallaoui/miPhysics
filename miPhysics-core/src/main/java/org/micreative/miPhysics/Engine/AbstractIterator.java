package org.micreative.miPhysics.Engine;

public abstract class AbstractIterator extends Index{

    protected int[] dimensions;

    public AbstractIterator(int[] dimensions,Index begin) {
        super(begin);
        this.dimensions = dimensions;
    }

    public void begin()
    {
        this.dimensions = dimensions;
        for (int i=0;i<dimensions.length;i++) coordinates[i]=0;
    }

    abstract public boolean next();

}
