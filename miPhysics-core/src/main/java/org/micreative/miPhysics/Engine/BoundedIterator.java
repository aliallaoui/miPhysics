package org.micreative.miPhysics.Engine;

public class BoundedIterator extends AbstractIterator{


    private int[] bounds;
    public BoundedIterator(int[] dimensions, String boundsDescr) throws Exception {
        super(dimensions,boundsDescr);
        if(dimensions.length==1) {
            bounds = new int[2];
            for (String bound : boundsDescr.split("|")) {
                if (bound.contains("LEFT")) bounds[0]=  Integer.parseInt(bound.substring(4));
                if (bound.contains("RIGHT")) bounds[1]=  Integer.parseInt(bound.substring(5));
            }
            nextMethod = this.getClass().getMethod("LeftNRightN");
        }
    }



    public boolean LeftNRightN()
    {
        while(coordinates[0]<bounds[1]) coordinates[0]++;
        if(coordinates[0]< dimensions[0]-bounds[1]) coordinates[0]++;
        else
        {
            coordinates[0]=0;// todo reset method
            return false;
        }
        return true;
    }
    public boolean LeftUpNMRightNM()
    {
        if(coordinates[0]< dimensions[0]) coordinates[0]++;
        if(coordinates[0] == dimensions[0]-1 && coordinates[1] < dimensions[1]-1)
        {
            coordinates[0]=0;
            coordinates[1]++;
        }
        return true;
    }

}
