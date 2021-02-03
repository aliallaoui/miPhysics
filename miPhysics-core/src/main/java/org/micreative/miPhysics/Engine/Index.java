package org.micreative.miPhysics.Engine;

import java.util.List;

public class Index {

    private int[] coordinates;
    private int[] dimensions;
    private List<Index> ignoredIndexes;
    public Index(int l)
    {
        coordinates= new int[1];
        coordinates[0]=l;
    }

    public Index(int l,int w)
    {
        coordinates= new int[2];
        coordinates[0]=l;
        coordinates[1]=w;
    }

    public Index(int l,int w,int h)
    {
        coordinates= new int[3];
        coordinates[0]=l;
        coordinates[1]=w;
        coordinates[2]=h;
    }

    public Index(int[] dimensions)
    {
        this.dimensions = dimensions;
        for (int i=0;i<dimensions.length;i++) coordinates[i]=0;
    }

    public Index(int[] dimensions, List<Index> ignoredIndexes_)
    {
        this.dimensions = dimensions;
        for (int i=0;i<dimensions.length;i++) coordinates[i]=0;
        ignoredIndexes=ignoredIndexes_;
    }

    public int x(){return coordinates[0];}
    public int y(){return coordinates[1];}
    public int z(){return coordinates[2];}

    public void begin()
    {
        this.dimensions = dimensions;
        for (int i=0;i<dimensions.length;i++) coordinates[i]=0;
    }

    public boolean next()
    {
        //TODO lambda functions here
        if (dimensions.length ==1 )
        {
            if(coordinates[0]< dimensions[0]) coordinates[0]++;
            while(isIgnored()) coordinates[0]++;
            if(coordinates[0]>= dimensions[0]) return false;
        }
        return false;
    }

    public boolean isIgnored()
    {
        for(Index i :ignoredIndexes)
        {
            if (i.x() ==x()) return true;
        }
        return false;
    }
}
