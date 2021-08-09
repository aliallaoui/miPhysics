package org.micreative.miPhysics.Engine;

public class BoundedIterator extends AbstractIterator{

    private int[] bounds;

    public BoundedIterator(int[] dimensions, String boundsDescr)  {
        super(dimensions,boundsDescr);

    }

    public AbstractIterator getInverseIterator()
    {
        BoundedIterator inv = new BoundedIterator(dimensions,definition);
        inv.inv = true;
        return inv;
    }

    public void init() throws Exception
    {
        if (dimensions.length == 1) {
            bounds = new int[2];
            for (String bound : this.definition.split("\\|")) {
                if (bound.contains("LEFT")) bounds[0] = Integer.parseInt(bound.substring(4));
                if (bound.contains("RIGHT")) bounds[1] = Integer.parseInt(bound.substring(5));
            }
            if(!inv) {
                nextMethod = this.getClass().getMethod("right");
                beginMethod = this.getClass().getMethod("beginLeftN");
                endMethod = this.getClass().getMethod("endRightM");
            }
            else
            {
                nextMethod = this.getClass().getMethod("rightOrJumpToBoundM");
                beginMethod = this.getClass().getMethod("begin0");
                endMethod = this.getClass().getMethod("end0");
            }
        }
    }

    public void beginLeftN()
    {
        coordinates[0]=0;
        while(coordinates[0]<bounds[0]) coordinates[0]++;
    }

    public void begin0()
    {
        for (int i=0;i<dimensions.length;i++) coordinates[i]=0;
    }

    public boolean endRightM()
    {
        return  (coordinates[0] >= dimensions[0]-bounds[1]);
    }

    public void right()
    {
        coordinates[0]++;
    }

    public void rightOrJumpToBoundM()
    {
        coordinates[0]++;
        if(coordinates[0]>=bounds[0] && coordinates[0]<dimensions[0]-bounds[1])
            coordinates[0]= dimensions[0]-bounds[1];
    }

    public boolean end0()
    {
        boolean ret=true;
        for (int i=0;i<dimensions.length;i++) ret = ret && coordinates[i]>=dimensions[i];
        return ret;
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
