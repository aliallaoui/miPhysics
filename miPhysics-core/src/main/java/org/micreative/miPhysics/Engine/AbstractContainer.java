package org.micreative.miPhysics.Engine;

import java.util.List;

public abstract class AbstractContainer<E> {
    protected List<E> data;
    protected int size;
    protected int dim;
    protected int[] dimensions;
    public AbstractContainer(int[] dimensions)
    {
        this.dimensions = dimensions;
        dim = dimensions.length;
    }
//Class<E> tclass
    public abstract void init(String initType) throws Exception;

    public E get(Index index)
    {
        return (E) data.get(offset(index));
    }

    public void add(Index index,E vector)
    {
    }

    public void set(Index index,E vector)
    {
    }

    abstract protected int offset(Index index);
}
