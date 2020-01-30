package org.micreative.miPhysics.Engine.Control;

import org.micreative.miPhysics.Engine.PhysicalModel;
import org.micreative.miPhysics.Vect3D;

public class PositionController {

    protected PhysicalModel pm;

    protected  int inputIndex;

    protected float value;

    protected float value_min;

    protected float value_max;

    protected float a;

    protected float b;

    protected String moduleName;

    public int getInputIndex() {
        return inputIndex;
    }

    public void setInputIndex(int inputIndex) {
        this.inputIndex = inputIndex;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getValue_min() {
        return value_min;
    }

    public void setValue_min(float value_min) {
        this.value_min = value_min;
    }

    public float getValue_max() {
        return value_max;
    }

    public void setValue_max(float value_max) {
        this.value_max = value_max;
    }

    public float getA() {
        return a;
    }

    public void setA(float a) {
        this.a = a;
    }

    public float getB() {
        return b;
    }

    public void setB(float b) {
        this.b = b;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Vect3D getPointA() {
        return pointA;
    }

    public void setPointA(Vect3D pointA) {
        this.pointA = pointA;
    }

    public Vect3D getPointB() {
        return pointB;
    }

    public void setPointB(Vect3D pointB) {
        this.pointB = pointB;
    }

    public Vect3D getUnitVector() {
        return unitVector;
    }

    public void setUnitVector(Vect3D unitVector) {
        this.unitVector = unitVector;
    }

    public int getMatIndex() {
        return matIndex;
    }

    public void setMatIndex(int matIndex) {
        this.matIndex = matIndex;
    }

    protected Vect3D pointA;

    protected Vect3D pointB;

    protected Vect3D unitVector;

    protected int matIndex;


    public PositionController(PhysicalModel pm_, int inputIndex,String moduleName,int matIndex,Vect3D pointA, Vect3D pointB){
        this.pm = pm_;
        this.moduleName = moduleName;
        this.matIndex = matIndex;
        this.pointA = pointA;
        this.pointB = pointB;
        this.inputIndex = inputIndex;

        unitVector = Vect3D.sub(pointB,pointA);
       // unitVector = unitVector.div(unitVector.norm());

        a=1;
        b=0;
    }

    public void updatePosition()
    {
        pm.getModule(moduleName).setPosition(matIndex,linearScale(value));
    }

    protected Vect3D linearScale(float val)
    {
        Vect3D ret = new Vect3D(pointA);
        ret.add(Vect3D.mult(unitVector,a*val+b));
        return ret;
    }


}
