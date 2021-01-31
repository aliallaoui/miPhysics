package org.micreative.miPhysics.Engine;

public class Index {

    private int[] coordinates;

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

    public int x(){return coordinates[0];}
    public int y(){return coordinates[1];}
    public int z(){return coordinates[2];}
}
