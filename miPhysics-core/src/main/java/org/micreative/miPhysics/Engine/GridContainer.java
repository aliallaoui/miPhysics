package org.micreative.miPhysics.Engine;

import org.micreative.miPhysics.Vect3D;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.lang.reflect.ParameterizedType;
/*
public abstract class Container {

 //   Vect3D[] vectors;

    abstract public Vect3D getVector(Index index);
    abstract public Vect3D addVector(Index index,Vect3D vector);
    abstract public Vect3D setVector(Index index,Vect3D vector);
}
*/

public class GridContainer extends AbstractContainer{

    protected float length;
    protected float height;
    protected float width;
    protected Vect3D center;
    protected Vect3D direction;



    protected float stretchFactor;

    public GridContainer(int[] dimensions)
    {
        super(dimensions);
        size = 1;
        for(int i=0;i<dim;i++)
        {
            size = size*dimensions[i];
        }
        data = new ArrayList<Vect3D>(size);
    }

    //public void init(E value) //throws Exception
    public  void init(String initType) throws Exception
    //public <Vect3D> void init(Class<Vect3D> tclass) throws Exception
    {
        /*
        Field dataField = GridContainer.class.getSuperclass().getDeclaredField("data");
        ParameterizedType dataType = (ParameterizedType) dataField.getGenericType();
        Class<?> dataClass = (Class<?>) dataType.getActualTypeArguments()[0];
        */
        if(initType == "gridVector")
        {
            if (dim == 1) {
                for (int i = 0; i < dimensions[0]; i++) {
                    data.add(computePosition(new Index(i)));
                }
            }
        }
        else if(initType == "zeroVector")
        {
            for (int i = 0; i < size; i++) {
                data.add(new Vect3D(0,0,0));
            }
        }
        init = true;

    }

    protected Vect3D computePosition(Index i)
    {
        if (dim==1)
        {
            return Vect3D.add(center,Vect3D.mult(direction,
                    i.x()*length*stretchFactor/(dimensions[0]-1)-length*stretchFactor/2.));
        }
        else return new Vect3D(0,0,0);
    }

    protected int offset(Index index)
    {
        if (dim == 1) return index.x();
        else if(dim == 2) return index.x()*dimensions[1]+index.y();
        else return 0;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public Vect3D getCenter() {
        return center;
    }

    public void setCenter(Vect3D center) {
        this.center = center;
    }

    public Vect3D getDirection() {
        return direction;
    }

    public void setDirection(Vect3D direction) {
        this.direction = direction;
    }

    public float getStretchFactor() {
        return stretchFactor;
    }

    public void setStretchFactor(float stretchFactor) throws Exception{
         this.stretchFactor = stretchFactor;
        if(init) {
            for (fixedPointIterator.begin(); !fixedPointIterator.end(); fixedPointIterator.next()) {
                set(fixedPointIterator, computePosition(fixedPointIterator));
            }
        }
    }

        /*
        if (dim == 1) {
            Vect3D left = Vect3D.add(center, Vect3D.mult(direction, -length*stretchFactor / 2.));
            for (int i = 0; i < dimensions[0]; i++) {
                data.add(Vect3D.add(left, Vect3D.mult(direction, i * length*stretchFactor / (dimensions[0] -
                        1))));
            }
        }
        this.stretchFactor = stretchFactor;
        Vect3D dir = new Vect3D(direction);
        dir.mult(restDistance* stretchFactor);
        Vect3D left = Vect3D.add(center,Vect3D.mult(dir,-(size+1.)/2.));
        Vect3D right = Vect3D.add(center,Vect3D.mult(dir,(size+1.)/2.));
        m_pos.set(0,left);
        m_pos.set(size+1,right);
        */


}
