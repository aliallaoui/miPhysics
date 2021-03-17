package org.micreative.miPhysics.Engine;

import java.util.List;

public class Index {

    protected int[] coordinates;

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

    public Index(Index index)
    {
        coordinates = new int[index.coordinates.length];
        for(int i=0;i<index.coordinates.length;i++) coordinates[i]=index.coordinates[i];
    }

    //maybe should be a static method called buildBegin()?
    public Index(int[] dimensions)
    {
        coordinates = new int[dimensions.length];
        for(int i=0;i<dimensions.length;i++) coordinates[i]=0;
    }

    public Index()
    {

    }

    public int x(){return coordinates[0];}
    public int y(){return coordinates[1];}
    public int z(){return coordinates[2];}


}
