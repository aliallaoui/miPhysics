package org.micreative.miPhysics.Processing.Utility.ModelRenderer;

import org.micreative.miPhysics.Engine.Mat;
import org.micreative.miPhysics.Vect3D;

public class MatDataHolder{

    public MatDataHolder(){
        this.m_pos = new Vect3D();
        this.m_mass = 1.;
        this.m_type ="";
    }

    public MatDataHolder(Mat element){
        this.setPos(element.getPos());
        this.m_mass = element.getMass();
        this.m_type = element.getType();
    }

    public MatDataHolder(Vect3D p, double m, String t){
        this.m_pos = new Vect3D();
        this.setPos(p);
        this.setMass(m);
        this.setType(t);
    }


    public void setPos(Vect3D p){
        this.m_pos.set(p);
    }

    public void setMass(double m){
        this.m_mass = m;
    }

    public void setType(String t){
        this.m_type = t;
    }

    public Vect3D getPos(){return this.m_pos;}
    public double getMass(){return this.m_mass;}
    public String getType(){return this.m_type;}

    private Vect3D m_pos;
    private double m_mass;
    private String m_type;

}