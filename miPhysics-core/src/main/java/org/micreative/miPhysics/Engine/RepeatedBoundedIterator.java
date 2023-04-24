package org.micreative.miPhysics.Engine;

public class RepeatedBoundedIterator extends BoundedIterator{


    private String repeatType;
    private int repeatNumber;
    private int currentLoop;
    public RepeatedBoundedIterator(int[] dimensions, String boundsDescr)  {
        super(dimensions,boundsDescr);
	repeatNumber = 0;
    currentLoop=0;
    }

    public AbstractIterator getInverseIterator()
    {
        BoundedIterator inv = new BoundedIterator(dimensions,definition);
        inv.inv = true;
        return inv;
    }

    public void init() throws Exception
    {
	// syntax : [S|L][0-9]+\|LEFT
	repeatType = this.definition.split("\\|")[0].substring(0,1);
	repeatNumber = Integer.parseInt(this.definition.split("\\|")[0].substring(1));
        if (dimensions.length == 1) {
            bounds = new int[2];
	    
            for (String bound : this.definition.split("\\|")) {
                if (bound.contains("LEFT")) bounds[0] = Integer.parseInt(bound.substring(4));
                if (bound.contains("RIGHT")) bounds[1] = Integer.parseInt(bound.substring(5));
            }
            if(!inv) {
		if(repeatType.equals("S"))
		    {
			nextMethod = this.getClass().getMethod("loopRight");
			beginMethod = this.getClass().getMethod("beginLeftN");
			endMethod = this.getClass().getMethod("endRightMLoop");
		    }
		else
		    {
			nextMethod = this.getClass().getMethod("rightLoop");
			beginMethod = this.getClass().getMethod("beginLeftN");
			endMethod = this.getClass().getMethod("endRightM");
	
		    }
            }
            else
            {
                nextMethod = this.getClass().getMethod("rightOrJumpToBoundM");
                beginMethod = this.getClass().getMethod("begin0");
                endMethod = this.getClass().getMethod("end0");
            }
        }
    }
    
    public void loopRight()
    {
	if(currentLoop < repeatNumber -1)
	    currentLoop++;
	else
	    {
		coordinates[0]++;
		currentLoop =0;
	    }
    }

    /*    public void rightLoop()
    {
	coordinates[0]++;
	if ( coordinates[0] >= dimensions[0]-bounds[1])
	    {
		currentLoop++;
		coordinates[0]=0;
	    }
    }
    */
    public boolean endRightMLoop()
    {
        return  (coordinates[0] >= dimensions[0]-bounds[1] && currentLoop == 0);
    }



    public void rightLoop() throws Exception
    {
	coordinates[0]++;
	
	if(currentLoop < repeatNumber && endRightM())
	    {
	   currentLoop++;

	   beginMethod.invoke(this);
	    }
    }

}
