package org.micreative.miPhysics.Engine;

public class BoundedIterator extends AbstractIterator{

    public BoundedIterator(int[] dimensions, Index begin) {
        super(dimensions, begin);
    }

    @Override
    public boolean next() {
        //TODO lambda functions here
        if (dimensions.length ==1 )
        {
            if(coordinates[0]< dimensions[0]-1) coordinates[0]++;
            else return false;
        }
        return true;
    }
}
