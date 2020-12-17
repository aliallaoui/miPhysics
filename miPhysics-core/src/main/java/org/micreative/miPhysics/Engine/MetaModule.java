package org.micreative.miPhysics.Engine;

import org.micreative.miPhysics.Engine.Modules.String2D;
import org.micreative.miPhysics.Vect3D;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.io.FileReader;
import java.io.InputStream;

public abstract class MetaModule implements AbstractModule{

    protected Map<String,Module> modules;
    protected Map<String,DataProvider> dataProviders;
    protected Map<String,ModuleController> moduleControllers;

    protected boolean isInit; // in AbstractModule ?
    protected String name;
    MetaModule(String name_)
    {
        name=name_;
        modules = new HashMap<>();
        dataProviders = new HashMap<>();
        moduleControllers = new HashMap<>();
        isInit = false;
    }
    public String getType(){return name;} // not an error ! That's the essence of meta modules
    public Module getModule(String name){return modules.get(name);}
    public void computeForces()
    {
        modules.forEach((name,module)->module.computeForces());
    }

    public void computeMoves()
    {
        modules.forEach((name,module)->module.computeMoves());
    }
    public void init()
    {
        modules.forEach((name,module)->module.init());
    }

    public Vect3D getPoint(String name,int index)
    {
        return modules.get(name).getPoint(name,index);// something to do with split(".") to handle recursive tree structure
    }

    public Vect3D getPointR(String name,int index)
    {
        return modules.get(name).getPointR(name,index);// something to do with split(".") to handle recursive tree structure
    }
    public void setPoint(String name,int index,Vect3D point)
    {
        modules.get(name).setPoint(index,point);
    }
    public void setPointR(String name,int index,Vect3D point)
    {
        modules.get(name).setPointR(index,point);
    }

    protected Properties getPropertySubset(Properties prop, String key)
    {
        final Properties p = new Properties();

        for (String s : prop.stringPropertyNames()) {
            if (s.startsWith(key) && s.length() > key.length())
            {
                p.put(s.substring(key.length()), p.getProperty(s));
            }
        }
        return p;
    }

    protected Map<String,String> getPropertySubsetAsMap(Properties prop, String key)
    {
        final Map<String,String> p = new HashMap<>();

        for (String s : prop.stringPropertyNames()) {
            if (s.startsWith(key) && s.length() > key.length())
            {
                p.put(s.substring(key.length()), prop.getProperty(s));
            }
        }
        return p;
    }

    public void addModule(String type, String name) throws Exception {
        if(modules.containsKey(name)) throw new Exception("Module named " +name + "already exists");

        try(InputStream input = PhysicalModel.class.getClassLoader().getResourceAsStream("defaultParams.properties")) {
            Properties p = new Properties();
            if (input == null) {
                System.out.println("defaultParams.properties not found");
                return;
            }
            //p.load(new FileReader(defaultParamsPropertiesPath));
            p.load(input);
            Map defaultParams = getPropertySubsetAsMap(p, type + ".");
            defaultParams.putAll(getPropertySubsetAsMap(p, "Global."));
            modules.put(name,(Module)Class.forName("org.micreative.miPhysics.Engine.Modules." + type).newInstance());
            modules.get(name).loadParameters(defaultParams);
        }
    }

    public void addInteraction(String type, String name,String moduleA,String moduleB) throws Exception {
        if(modules.containsKey(name)) throw new Exception("Module named " +name + "already exists");

        try(InputStream input = PhysicalModel.class.getClassLoader().getResourceAsStream("defaultParams.properties")) {
            Properties p = new Properties();
            if (input == null) {
                System.out.println("defaultParams.properties not found");
                return;
            }
            //p.load(new FileReader(defaultParamsPropertiesPath));
            p.load(input);
            Map defaultParams = getPropertySubsetAsMap(p, type + ".");
            defaultParams.putAll(getPropertySubsetAsMap(p, "Global."));
            modules.put(name,(Module)Class.forName("org.micreative.miPhysics.Engine.Modules." + type)
                    .getDeclaredConstructor(Module.class,Module.class)
                    .newInstance(getModule(moduleA),getModule(moduleB)));
            modules.get(name).loadParameters(defaultParams);
        }
    }

    public void addMassModule(String type, String name) throws Exception {

    }

    public void addModuleController(String name,
                                    String moduleName,
                                    String dataProviderName,
                                    String controlledData) throws Exception {
        if(moduleControllers.containsKey(name)) throw new Exception("ModuleController named " +name + "already exists");
        moduleControllers.put(name,new ModuleController(modules.get(moduleName),
                dataProviders.get(dataProviderName),
                controlledData)
        );
    }

    public void addPositionScalarController(String name,
                                    String moduleName,
                                    String dataProviderName,
                                    String controlledData,
                                            int index) throws Exception {
        if(moduleControllers.containsKey(name)) throw new Exception("ModuleController named " +name + "already exists");
        moduleControllers.put(name, new PositionScalarController(modules.get(moduleName),
                dataProviders.get(dataProviderName),
                controlledData,index));
    }

    public void addDataProvider(String type,String name) throws Exception {
        if(dataProviders.containsKey(name)) throw new Exception("DataProvider named " +name + "already exists");
        dataProviders.put(name,(DataProvider) Class.forName(type).newInstance());
    }


    public void gatherData() throws Exception
    {
        for(Map.Entry<String,DataProvider> dataProvider:dataProviders.entrySet()) {
            dataProvider.getValue().gatherData();
        }
    }

    public void controlModules() throws Exception
    {
        for(Map.Entry<String,ModuleController> moduleController:moduleControllers.entrySet()) {
            moduleController.getValue().setData();
        }
    }

    /**
     * Delete all modules in the model and start from scratch.
     */
    public void clearModel() {

    }
/*
    public List<Mat> getMats()
    {
        List<Mat> ret = new ArrayList<Mat>();
        for(Module m:modules)
        {
            if (m instanceof Mat) ret.add((Mat)m);
        }
        return ret;
    }

    public List<Link> getLinks()
    {
        List<Link> ret = new ArrayList<Link>();
        for(Module m:modules)
        {
            if (m instanceof Link) ret.add((Link)m);
        }
        return ret;
    }


    public List<Module> getMultiPointModules()
    {
        List<Module> ret = new ArrayList<Module>();
        for(Module m:modules)
        {
            if (m instanceof String2D) ret.add(m);
        }
        return ret;
    }
*/
}

