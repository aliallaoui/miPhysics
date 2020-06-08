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

    protected float pointAx;
    protected float pointAy;
    protected float pointAz;

    public float getPointAx() {
        return pointAx;
    }

    public void setPointAx(float pAx) {
        this.pointAx = pAx;
        this.pointA.x = pAx;
        unitVector = Vect3D.sub(pointB,pointA);
    }

    public float getPointAy() {
        return pointAy;
    }

    public void setPointAy(float pAy) {
        this.pointAy = pAy;
        this.pointA.y = pAy;
        unitVector = Vect3D.sub(pointB,pointA);
    }

    public float getPointAz() {
        return pointAz;
    }

    public void setPointAz(float pAz) {
        this.pointAz = pAz;
        this.pointA.z = pAz;
        unitVector = Vect3D.sub(pointB,pointA);
    }

    public float getPointBx() {
        return pointBx;
    }

    public void setPointBx(float pBx) {
        this.pointBx = pBx;
        this.pointB.x = pBx;
        unitVector = Vect3D.sub(pointB,pointA);
    }

    public float getPointBy() {
        return pointBy;
    }

    public void setPointBy(float pBy) {
        this.pointBy = pBy;
        this.pointB.y = pBy;
        unitVector = Vect3D.sub(pointB,pointA);
    }

    public float getPointBz() {
        return pointBz;
    }

    public void setPointBz(float pBz) {
        this.pointBz = pBz;
        this.pointB.z = pBz;
        unitVector = Vect3D.sub(pointB,pointA);
    }

    protected float pointBx;
    protected float pointBy;
    protected float pointBz;

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
