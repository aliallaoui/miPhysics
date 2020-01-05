package org.micreative.miPhysics.Processing.Utility.ModelRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.micreative.miPhysics.Engine.Link;
import org.micreative.miPhysics.Engine.Mat;
import org.micreative.miPhysics.Engine.Module;
import processing.core.PVector;

import processing.core.*;
import processing.core.PApplet;
import processing.core.PShape;

import org.micreative.miPhysics.Engine.PhysicalModel;

import org.micreative.miPhysics.Vect3D;

public class ModelRenderer implements PConstants{

    protected PApplet app;

    HashMap <String, MatRenderProps> matStyles;
    HashMap <String, LinkRenderProps> linkStyles;

    MatRenderProps fallbackMat = new MatRenderProps(125, 125, 125, 5);
    LinkRenderProps fallbackLink = new LinkRenderProps(125, 125, 125, 0);

    ArrayList<MatDataHolder> matHolders = new ArrayList<MatDataHolder>();
    ArrayList<LinkDataHolder> linkHolders = new ArrayList<LinkDataHolder>();

    int nbMats;
    int nbLinks;

    float m_scale;

    PVector m_zoomRatio;

    boolean m_matDisplay;

    public ModelRenderer(PApplet parent){

        this.app = parent;

        m_scale = 1;
        m_matDisplay = true;

        matStyles = new HashMap <String, MatRenderProps> ();
        linkStyles = new HashMap <String, LinkRenderProps> ();

        m_zoomRatio = new PVector(1,1,1);

        // Default renderer settings for modules
        matStyles.put("Mass3D", new MatRenderProps(180, 100, 0, 10));
        matStyles.put("Ground3D", new MatRenderProps(0, 220, 130, 10));
        matStyles.put("Mass2DPlane", new MatRenderProps(0, 220, 130, 10));
        matStyles.put("Osc3D", new MatRenderProps(0, 220, 130, 10));
        matStyles.put("HapticInput3D", new MatRenderProps(255, 50, 50, 10));

        linkStyles.put("Damper3D", new LinkRenderProps(30, 100, 100, 255));
        linkStyles.put("Spring3D", new LinkRenderProps(30, 100, 100, 255));
        linkStyles.put("SpringDamper3D", new LinkRenderProps(30, 250, 250, 255));
        linkStyles.put("SpringDamper1D", new LinkRenderProps(50, 255, 250, 255));
        linkStyles.put("Contact3D", new LinkRenderProps(255, 100, 100, 0));
        linkStyles.put("Bubble3D", new LinkRenderProps(30, 100, 100, 0));
        linkStyles.put("Rope3D", new LinkRenderProps(0, 255, 100, 255));

    }

    public boolean setColor(String m, int r, int g, int b){
        if(matStyles.containsKey(m)) {
            matStyles.get(m).setColor(r, g, b);
            return true;
        }
        else return false;
    }

    public boolean setSize(String m, float size){
        if(matStyles.containsKey(m)) {
            matStyles.get(m).setBaseSize(size);
            return true;
        }
        else return false;
    }

    public void setZoomVector(float x, float y, float z){
        m_zoomRatio.x = x;
        m_zoomRatio.y = y;
        m_zoomRatio.z = z;
    }


    public boolean setSize(String m, int size){
        if(linkStyles.containsKey(m)) {
            linkStyles.get(m).setSize(size);
            return true;
        }
        else return false;
    }

    public boolean setColor(String m, int r, int g, int b, int alpha){
        if(linkStyles.containsKey(m)) {
            linkStyles.get(m).setColor(r, g, b, alpha);
            return true;
        }
        else return false;
    }


    public boolean setStrainGradient(String m, boolean cond, float val){
        if(linkStyles.containsKey(m)) {
            linkStyles.get(m).setStrainGradient(cond, val);
            return true;
        }
        else return false;
    }

    public boolean setStrainColor(String m, int r, int g, int b, int alpha){
        if(linkStyles.containsKey(m)) {
            linkStyles.get(m).setStrainColor(r, g, b, alpha);
            return true;
        }
        else return false;
    }


    public boolean setScaling(String m, float scale){
        if(matStyles.containsKey(m)) {
            matStyles.get(m).setInertiaScaling(scale);
            return true;
        }
        else return false;
    }


    public void setScale(float scale){
        m_scale = scale;
    }

    public float getScale(){
        return(m_scale);
    }

    public void displayMats(boolean val){
        m_matDisplay = val;
    }


    public void init(PhysicalModel mdl)
    {
        nbMats = mdl.getNumberOfMats();
        nbLinks = mdl.getNumberOfLinks();

        if(m_matDisplay) {
            for (Mat m :mdl.getMats())
                matHolders.add(new MatDataHolder(m.getPos(),
                        m.getMass(),
                        m.getType()));
            for(Module m: mdl.getMultiPointModules())
            {
                for(int j=0;j<m.getNbMats();j++)
                {
                    matHolders.add(new MatDataHolder(m.getPos(j),1,"Mass3D"));
                }
            }
        }
        for (Link l:mdl.getLinks())
            linkHolders.add(new LinkDataHolder(l.getMat1().getPos(),
                    l.getMat2().getPos(),
                    l.getElong() / l.getDRest(),
                    l.getType()));
    }

    public void updateMatHolders(PhysicalModel mdl)
    {
        List<Vect3D> positions = mdl.getAllPositions();
        int i=0;
        for(MatDataHolder mh:matHolders)
        {
            mh.setPos(positions.get(i++));
        }
    }
    public void updateLinkHolders(PhysicalModel mdl)
    {
        List<Vect3D> positions = mdl.getAllLinkPositions();
        int i=0;
        for(LinkDataHolder lh:linkHolders)
        {
            lh.setP1(positions.get(i++));
            lh.setP2(positions.get(i++));
        }
    }

    public void renderModel(PhysicalModel mdl) {
        PVector v;
        MatRenderProps tmp;
        LinkRenderProps tmp2;

        MatDataHolder mH;
        LinkDataHolder lH;

        // Limit the synchronized section to a copy of the model state
        synchronized (mdl.getLock()) {

            updateMatHolders(mdl);
            updateLinkHolders(mdl);
        }

        if(m_matDisplay){

            // Scaling the detail of the spheres depending on size of the model
            if (nbMats < 100)
                app.sphereDetail(30);
            else if (nbMats < 1000)
                app.sphereDetail(15);
            else if (nbMats < 10000)
                app.sphereDetail(5);

            // All the drawing can then run concurrently to the model calculation
            // Should really structure several lists according to module type
            for (int i = 0; i < nbMats; i++) {

                mH = matHolders.get(i);

                if (matStyles.containsKey(mH.getType()))
                    tmp = matStyles.get(mH.getType());
                else tmp = fallbackMat;

                v = mH.getPos().toPVector().mult(1);
                app.pushMatrix();
                app.translate(m_zoomRatio.x * v.x, m_zoomRatio.y * v.y, m_zoomRatio.z * v.z);
                app.fill(tmp.red(), tmp.green(), tmp.blue());
                app.noStroke();
                app.sphere(tmp.getScaledSize(mH.getMass()));
                app.popMatrix();
            }
        }


        for ( int i = 0; i < nbLinks; i++) {

            lH = linkHolders.get(i);

            app.strokeWeight(1);

            if (linkStyles.containsKey(lH.getType()))
                tmp2 = linkStyles.get(lH.getType());
            else tmp2 = fallbackLink;

            if(tmp2.strainGradient()){
                if ((tmp2.getAlpha() > 0) || (tmp2.getStrainAlpha() > 0))
                {
                    float stretching = (float)lH.getElongation();

                    app.strokeWeight(tmp2.getSize());
                    app.stroke(tmp2.redStretch(stretching),
                            tmp2.greenStretch(stretching),
                            tmp2.blueStretch(stretching),
                            tmp2.alphaStretch(stretching));

                    drawLine(lH.getP1(), lH.getP2());
                }
            }

            else if (tmp2.getAlpha() > 0) {
                app.stroke(tmp2.red(), tmp2.green(), tmp2.blue(), tmp2.getAlpha());
                app.strokeWeight(tmp2.getSize());

                drawLine(lH.getP1(), lH.getP2());
            }
            else
            {
                drawLine(lH.getP1(), lH.getP2());
            }
        }

/*            for (int i = 0; i < mdl.getNumberOfMats(); i++) {
                matHolders.add(new MatDataHolder(mdl.getMatPosAt(i),
                                                 mdl.getMatMassAt(i),
                                                 mdl.getMatTypeAt(i)));
            }

            if(m_matDisplay) {

                int nbMats = mdl.getNumberOfMats();

                // Scaling the detail of the spheres depending on size of the model
                if (nbMats < 100)
                    app.sphereDetail(30);
                else if (nbMats < 1000)
                    app.sphereDetail(15);
                else if (nbMats < 10000)
                    app.sphereDetail(5);

                for (int i = 0; i < nbMats; i++) {
                    if (matStyles.containsKey(mdl.getMatTypeAt(i)))
                        tmp = matStyles.get(mdl.getMatTypeAt(i));
                    else tmp = fallbackMat;

                    v = mdl.getMatPosAt(i).toPVector().mult(1);
                    app.pushMatrix();
                    app.translate(m_zoomRatio.x * v.x, m_zoomRatio.y * v.y, m_zoomRatio.z * v.z);
                    app.fill(tmp.red(), tmp.green(), tmp.blue());
                    app.noStroke();
                    app.sphere(tmp.getScaledSize(mdl.getMatMassAt(i)));
                    app.popMatrix();
                }
            }*/

          /*  synchronized(mdl.getLock()) {


            for ( int i = 0; i < mdl.getNumberOfLinks(); i++) {
                app.strokeWeight(1);

                if (linkStyles.containsKey(mdl.getLinkTypeAt(i)))
                    tmp2 = linkStyles.get(mdl.getLinkTypeAt(i));
                else tmp2 = fallbackLink;

                if(tmp2.strainGradient() == true){
                    if ((tmp2.getAlpha() > 0) || (tmp2.getStrainAlpha() > 0))
                    {
                        float stretching = (float)(mdl.getLinkElongationAt(i) / mdl.getLinkDRestAt(i));

                        app.strokeWeight(tmp2.getSize());
                        app.stroke(tmp2.redStretch(stretching),
                                   tmp2.greenStretch(stretching),
                                   tmp2.blueStretch(stretching),
                                   tmp2.alphaStretch(stretching));

                        drawLine(mdl.getLinkPos1At(i), mdl.getLinkPos2At(i));
                    }

                }

                else if (tmp2.getAlpha() > 0) {
                    app.stroke(tmp2.red(), tmp2.green(), tmp2.blue(), tmp2.getAlpha());
                    app.strokeWeight(tmp2.getSize());

                    drawLine(mdl.getLinkPos1At(i), mdl.getLinkPos2At(i));

                }
            }
        }*/
    }


    private void drawLine(Vect3D pos1, Vect3D pos2) {
        app.line(m_zoomRatio.x * (float)pos1.x,
                m_zoomRatio.y * (float)pos1.y,
                m_zoomRatio.z * (float)pos1.z,
                m_zoomRatio.x * (float)pos2.x,
                m_zoomRatio.y * (float)pos2.y,
                m_zoomRatio.z * (float)pos2.z);
    }
}