package org.micreative.miPhysics.Engine;

import org.micreative.miPhysics.Vect3D;

import java.util.List;

public abstract class AbstractContainer {
    protected List<Vect3D> data;
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

    public Vect3D get(Index index)
    {
        return data.get(offset(index));
    }

    public void add(Index index,Vect3D vector)
    {
      data.get(offset(index)).add(vector);
    }

    public void set(Index index,Vect3D vector)
    {
        data.set(offset(index),vector);
    }

    abstract protected int offset(Index index);
}
