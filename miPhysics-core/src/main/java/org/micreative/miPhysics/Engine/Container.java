package org.micreative.miPhysics.Engine;

import org.micreative.miPhysics.Vect3D;

/*
public abstract class Container {

 //   Vect3D[] vectors;

    abstract public Vect3D getVector(Index index);
    abstract public Vect3D addVector(Index index,Vect3D vector);
    abstract public Vect3D setVector(Index index,Vect3D vector);
}
*/

public class Container {

    protected Vect3D[] vectors;
    protected int size;
    protected int dim;
    protected int[] dimensions;
    public Container(int[] dimensions)
    {
        size = 1;
        dim = dimensions.length;
        this.dimensions = dimensions;
        for(int i=0;i<dim;i++)
        {
            size = size*dimensions[i];
        }
        vectors = new Vect3D[size];
    }

    public Vect3D getVector(Index index)
    {
        return vectors[offset(index)];
    }

    public void addVector(Index index,Vect3D vector)
    {

    }
    public void setVector(Index index,Vect3D vector)
    {

    }

    protected int offset(Index index)
    {
        if (dim == 1) return index.x();
        else if(dim == 2) return index.x()*dimensions[1]+index.y();
        else return 0;
    }
}
