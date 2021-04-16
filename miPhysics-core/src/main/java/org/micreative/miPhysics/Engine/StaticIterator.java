package org.micreative.miPhysics.Engine;

public class StaticIterator extends AbstractIterator{

    boolean first=false;
    public StaticIterator(int[] dimensions, String definition) {
        super(dimensions, definition);

    }

    public void init() throws Exception
    {
        String[] stringCoordinates = definition.split(",");
        for(int i=0;i<stringCoordinates.length;i++)
        {
            coordinates[i]=Integer.parseInt(stringCoordinates[i]);
        }
    }

    @Override
    public AbstractIterator getInverseIterator() {
        return null;
    }

     public void begin() throws Exception
    {
        first=true;

    }

     public void next() throws Exception {
        first=false;
    }

    public boolean end() throws Exception
    {
        return !first;
    }


}
