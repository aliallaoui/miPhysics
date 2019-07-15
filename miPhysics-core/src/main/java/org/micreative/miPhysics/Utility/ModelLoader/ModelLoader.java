package org.micreative.miPhysics.Utility.ModelLoader;
import org.micreative.miPhysics.Engine.PhysicalModel;
import org.micreative.miPhysics.Vect3D;
import processing.data.XML;
import processing.core.PApplet;
import processing.core.*;
import java.io.IOException;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JFileChooser;


public class ModelLoader {

    private PApplet app;
    boolean IS_LOADED;


    /**
     * Constructor method. Call this in setup with "this" as argument
     * @param parent
     */
    public ModelLoader(PApplet parent) {

        this.app = parent;
        IS_LOADED=false;

    }

    /**
     * Get the boolean IS_LOADED
     * @return true if the file is loaded, false if not
     */
    public boolean getIsLoaded(){
        return IS_LOADED;
    }

    /**
     * Set the boolean IS_LOADED
     * @param bool the boolean to set
     */
    public void setIsLoaded(boolean bool){
        this.IS_LOADED=bool;
    }

    /**
     * Save a physical model in a XML file
     * @param xmlFileName the name of the file
     * @param mdl the physical model to save
     * @return true if it worked
     */
    public boolean saveModel(String xmlFileName, PhysicalModel mdl) {

        XML xmlFile = new XML(xmlFileName);

        generateXML(xmlFile, mdl);
        app.saveXML(xmlFile, xmlFileName);

        return true;
    }

    /**
     * Load a model from a XML file
     * @param xmlFileName The absolute path of the file to load
     * @param mdl The physical model
     * @return true if it worked
     */
    public boolean loadModel(String xmlFileName, PhysicalModel mdl) {

        File myXmlFile = new File(xmlFileName);

        try {
            XML xml = new XML(myXmlFile);
            //Création des 3 premières branches : Global Link et Mat
            XML li = xml.getChild("Link");
            XML ma = xml.getChild("MAT");
            XML glo = xml.getChild("global");

            createGlobal(glo, mdl);
            createMat(ma, mdl);
            createLink(li, mdl);
            mdl.init();
            setIsLoaded(true);

            return true;
        } catch (Exception e) {
            System.out.println("Couldn't load model");
            return false;
        }
    }


    /**
     * Create the XML file with all the branches
     * @param xml The XML file
     * @param mdl The physical model
     */
    private static void generateXML(XML xml, PhysicalModel mdl) {


        /*------------ GLOBAL VARIABLES -------------*/

        XML global = xml.addChild("global");

        XML gravity = global.addChild("gravity");
        gravity.setDouble("gravity", mdl.getGravity());

        XML friction = global.addChild("friction");
        friction.setDouble("friction", mdl.getFriction());

        XML vector = global.addChild("vector");
        vector.setFloat("pos.x", (float) mdl.getGravityDirection().x);
        vector.setFloat("pos.y", (float) mdl.getGravityDirection().y);
        vector.setFloat("pos.z", (float) mdl.getGravityDirection().z);


        /*------------------ MAT --------------------*/

        XML mat = xml.addChild("MAT");
        XML Ground1D = mat.addChild("Ground1D");
        XML Ground3D = mat.addChild("Ground3D");
        XML HapticInput3D = mat.addChild("HapticInput3D");
        XML Mass1D = mat.addChild("Mass1D");
        XML Mass2DPlane = mat.addChild("Mass2DPlane");
        XML Mass3D = mat.addChild("Mass3D");
        XML Mass3DSimple = mat.addChild("Mass3DSimple");
        XML Osc1D = mat.addChild("Osc1D");
        XML Osc3D = mat.addChild("Osc3D");

        /*------------------ LINK --------------------*/

        XML link = xml.addChild("Link");
        XML Damper3D = link.addChild("Damper3D");
        XML Bubble3D = link.addChild("Bubble3D");
        XML Contact3D = link.addChild("Contact3D");
        XML PlaneContact = link.addChild("PlaneContact3D");
        XML Rope3D = link.addChild("Rope3D");
        XML Spring3D = link.addChild("Spring3D");
        XML SpringDamper1D = link.addChild("SpringDamper1D");
        XML SpringDamper3D = link.addChild("SpringDamper3D");

        setupLink(link, mdl);
        setupMat(mat, mdl);
    }

    /**
     * Write the parameters of the links in the XML file
     * @param link The XML branch of the links
     * @param mdl The physical model
     */
    static void setupLink(XML link, PhysicalModel mdl) {
        for (int i = 0; i < mdl.getNumberOfLinks(); i++) {
            String a = mdl.getLinkTypeAt(i).toString();
            XML interaction = link.getChild(a).addChild("interaction_" + i);

            interaction.setInt("index", i);
            interaction.setString("name", mdl.getLinkNameAt(i));
            interaction.setString("m1", mdl.getLinkMat1NameAt(i));
            interaction.setString("m2", mdl.getLinkMat2NameAt(i));

            if (a == "Damper3D") {
                interaction.setDouble("damping", mdl.getLinkDampingAt(i));
            }

            if (a == "Spring3D") {
                interaction.setDouble("stiffness", mdl.getLinkStiffnessAt(i));
                interaction.setDouble("drest", mdl.getLinkDRestAt(i));
            } else {
                interaction.setDouble("drest", mdl.getLinkDRestAt(i));
                interaction.setDouble("stiffness", mdl.getLinkStiffnessAt(i));
                interaction.setDouble("damping", mdl.getLinkDampingAt(i));

                if (a == "PlaneContact3D") {
                    interaction.setInt("orientation", mdl.getPlaneOrientationAt(i));
                    interaction.setDouble("position", mdl.getPlanePositionAt(i));
                }

            }

    /*if (a=="Attractor3D"){
     interaction.setString("limitDist", mdl.getLimitDist());
     interaction.setString("attrFactor", mdl.getAttrFactor());
     }*/

        }
    }

    /**
     * Write the parameters of the mats in the XML file
     * @param mat The XML branch of mats
     * @param mdl The physical model
     */
    static void setupMat(XML mat, PhysicalModel mdl) {
        for (int i = 0; i < mdl.getNumberOfMats(); i++) {
            String a = mdl.getMatTypeAt(i).toString();

            XML mass = mat.getChild(a).addChild("mass_" + i);

            mass.setInt("index", i);
            mass.setString("name", mdl.getMatNameAt(i));
            mass.setDouble("initPos.x", mdl.getMatPosAt(i).x);
            mass.setDouble("initPos.y", mdl.getMatPosAt(i).y);
            mass.setDouble("initPos.z", mdl.getMatPosAt(i).z);

    /* if (a=="HapticInput3D"){
     mass.setString("SmoothingFactor", smoothingFactor);
     }*/

            if (a == "Mass3DSimple" || a == "Mass1D" || a == "Mass3D" || a == "Mass2DPlane" || a == "Osc1D" || a == "Osc3D") {

                mass.setDouble("mass", mdl.getMatMassAt(i));
                mass.setDouble("initVel.x", mdl.getMatVelAt(i).x);
                mass.setDouble("initVel.y", mdl.getMatVelAt(i).y);
                mass.setDouble("initVel.z", mdl.getMatVelAt(i).z);

                if (a == "Osc3D" || a == "Osc1D") {

                    mass.setDouble("damping", mdl.getMatDampingAt(i));
                    mass.setDouble("stiffness", mdl.getMatStiffnessAt(i));
                }
            }
        }
    }

    /**
     * Set the global variables of the model from the XML file
     * @param glo The XML branch for the global variables
     * @param mdl The physical model
     */

    static void createGlobal(XML glo, PhysicalModel mdl) {
        mdl.setGravity(glo.getChild("gravity").getDouble("gravity"));
        mdl.setFriction(glo.getChild("friction").getDouble("friction"));
        mdl.setGravityDirection(new PVector(glo.getChild("vector").getFloat("pos.x"), glo.getChild("vector").getFloat("pos.y"), glo.getChild("vector").getFloat("pos.z")));
    }

    /**
     * Check if there are modules of each type and initiate switchMat if there are
     * @param ma The XML branch for the mats
     * @param mdl The physical model
     */

    static void createMat(XML ma, PhysicalModel mdl) {
        XML type, mass;

        for (int i = 1; i < ma.listChildren().length; i += 2) { // de 2 en 2 pour sauter les #text
            type = ma.getChild(i);

            if (type.hasChildren()) { // --> il faut fouiller tout l'élément = descendre de 2

                for (int p = 1; p < type.listChildren().length; p += 2) {
                    mass = type.getChild(p); // on récupère les child = niveau "mass"

                    //Check du nom pour savoir quoi construire
                    switchMat(mass, mass.getParent().getName(), mdl);
                }
            }
        }
    }


    /**
     * Check if there are modules of each type and initiate switchLink if there are
     * @param li The XML branch for the links
     * @param mdl The physical model
     */

    static void createLink(XML li, PhysicalModel mdl) {
        XML type, link;

        for (int i = 1; i < li.listChildren().length; i += 2) {
            type = li.getChild(i);

            if (type.hasChildren()) {

                for (int p = 1; p < type.listChildren().length; p += 2) {
                    link = type.getChild(p);
                    switchLink(link, link.getParent().getName(), mdl);
                }
            }
        }
    }


    /**
     * Check the type of module and add it to the physical model with its parametres
     * @param mass The XML object for the mass to create
     * @param typeMat The type of mass used for the switch
     * @param mdl The physical model
     */

    static void switchMat(XML mass, String typeMat, PhysicalModel mdl) {

        switch (typeMat) {

            case "Ground3D":
                mdl.addGround3D(mass.getString("name"), new Vect3D(mass.getDouble("initPos.x"), mass.getDouble("initPos.y"), mass.getDouble("initPos.z")));
                break;

            case "Ground1D":
                mdl.addGround1D(mass.getString("name"), new Vect3D(mass.getDouble("initPos.x"), mass.getDouble("initPos.y"), mass.getDouble("initPos.z")));
                break;

            case "Mass1D":
                mdl.addMass1D(mass.getString("name"), mass.getDouble("mass"), new Vect3D(mass.getDouble("initPos.x"), mass.getDouble("initPos.y"), mass.getDouble("initPos.z")),
                        new Vect3D(mass.getDouble("initVel.x"), mass.getDouble("initVel.y"), mass.getDouble("initVel.z")));
                break;

            case "Mass2DPlane":
                mdl.addMass2DPlane(mass.getString("name"), mass.getDouble("mass"), new Vect3D(mass.getDouble("initPos.x"), mass.getDouble("initPos.y"), mass.getDouble("initPos.z")),
                        new Vect3D(mass.getDouble("initVel.x"), mass.getDouble("initVel.y"), mass.getDouble("initVel.z")));
                break;

            case "Mass3D":
                mdl.addMass3D(mass.getString("name"), mass.getDouble("mass"), new Vect3D(mass.getDouble("initPos.x"), mass.getDouble("initPos.y"), mass.getDouble("initPos.z")),
                        new Vect3D(mass.getDouble("initVel.x"), mass.getDouble("initVel.y"), mass.getDouble("initVel.z")));
                break;

            case "Mass3DSimple":
                mdl.addMass3DSimple(mass.getString("name"), mass.getDouble("mass"), new Vect3D(mass.getDouble("initPos.x"), mass.getDouble("initPos.y"), mass.getDouble("initPos.z")),
                        new Vect3D(mass.getDouble("initVel.x"), mass.getDouble("initVel.y"), mass.getDouble("initVel.z")));
                break;

            case "Osc3D":
                mdl.addOsc3D(mass.getString("name"), mass.getDouble("mass"), mass.getDouble("stiffness"), mass.getDouble("damping"), new Vect3D(mass.getDouble("initPos.x"), mass.getDouble("initPos.y"), mass.getDouble("initPos.z")),
                        new Vect3D(mass.getDouble("initVel.x"), mass.getDouble("initVel.y"), mass.getDouble("initVel.z")));
                break;

            case "Osc1D":
                mdl.addOsc3D(mass.getString("name"), mass.getDouble("mass"), mass.getDouble("stiffness"), mass.getDouble("damping"), new Vect3D(mass.getDouble("initPos.x"), mass.getDouble("initPos.y"), mass.getDouble("initPos.z")),
                        new Vect3D(mass.getDouble("initVel.x"), mass.getDouble("initVel.y"), mass.getDouble("initVel.z")));
                break;
        }
    }

    /**
     * Check the type of module and add it to the physical model with its parametres
     * @param link The XML object for the link to create
     * @param typeLink The type of link used for the switch
     * @param mdl The physical model
     */

    static void switchLink(XML link, String typeLink, PhysicalModel mdl) {
        switch (typeLink) {
            case "Bubble3D":
                mdl.addBubble3D(link.getString("name"), link.getDouble("drest"), link.getDouble("stiffness"), link.getDouble("damping"), link.getString("m1"), link.getString("m2"));
                break;

            case "Contact3D":
                mdl.addContact3D(link.getString("name"), link.getDouble("drest"), link.getDouble("stiffness"), link.getDouble("damping"), link.getString("m1"), link.getString("m2"));
                break;

            case "Rope3D":
                mdl.addRope3D(link.getString("name"), link.getDouble("drest"), link.getDouble("stiffness"), link.getDouble("damping"), link.getString("m1"), link.getString("m2"));
                break;

            case "SpringDamper1D":
                mdl.addSpringDamper1D(link.getString("name"), link.getDouble("drest"), link.getDouble("stiffness"), link.getDouble("damping"), link.getString("m1"), link.getString("m2"));
                break;

            case "SpringDamper3D":
                mdl.addSpringDamper3D(link.getString("name"), link.getDouble("drest"), link.getDouble("stiffness"), link.getDouble("damping"), link.getString("m1"), link.getString("m2"));
                break;

            case "Spring3D":
                mdl.addSpring3D(link.getString("name"), link.getDouble("drest"), link.getDouble("stiffness"), link.getString("m1"), link.getString("m2"));
                break;

            case "Damper3D":
                mdl.addDamper3D(link.getString("name"), link.getDouble("damping"), link.getString("m1"), link.getString("m2"));
                break;

            case "PlaneContact3D":
                mdl.addPlaneContact(link.getString("name"), link.getDouble("drest"), link.getDouble("stiffness"), link.getDouble("damping"),
                        link.getInt("orientation"), link.getDouble("position"), link.getString("m1"));
                break;
        }
    }

    /**
     * Load a phyiscal model from a file using a UI Dialog
     * @param mdl the model to load
     */
    public void  loadModelFromFile(PhysicalModel mdl){
        app.selectInput("Select the model to load","load");
    }

    /**
     * Initiate the loadModel method with the selected file
     * @param selection The selected file
     * @param mdl The physical model
     */

    void load(File selection, PhysicalModel mdl){
        if (selection==null){
            System.out.println("No file was selected");
        }
        else{
            String fileName=selection.getAbsolutePath();
            synchronized(mdl.getLock()){
                loadModel(fileName, mdl);
            }
        }
    }

}