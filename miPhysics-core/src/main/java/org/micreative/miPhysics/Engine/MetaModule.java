package org.micreative.miPhysics.Engine;

import org.apache.commons.chain.web.MapEntry;
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
    public MetaModule(String name_)
    {
        name=name_;
        modules = new HashMap<>();
        dataProviders = new HashMap<>();
        moduleControllers = new HashMap<>();
        isInit = false;
    }

    public String getType(){return name;} // not an error ! That's the essence of meta modules
    public Module getModule(String name){return modules.get(name);}
    public DataProvider getDataProvider(String name){return dataProviders.get(name);}
    public void computeForces() throws Exception
    {
        for(Map.Entry<String,Module> module:modules.entrySet()) module.getValue().computeForces();
    }

    public void computeMoves() throws Exception
    {
        for(Map.Entry<String,Module> module:modules.entrySet()) module.getValue().computeMoves();
    }

    public void init()throws Exception
    {
        for(Map.Entry<String,Module> module :modules.entrySet())
        {
            module.getValue().init();
        }
        for(Map.Entry<String,ModuleController> module :moduleControllers.entrySet())
        {
            module.getValue().init();
        }
    }

    public Vect3D getPoint(String name,Index index)
    {
        return modules.get(name).getPoint(name,index);// something to do with split(".") to handle recursive tree structure
    }

    public Vect3D getPointR(String name,Index index)
    {
        return modules.get(name).getPointR(name,index);// something to do with split(".") to handle recursive tree structure
    }
    public void setPoint(String name,Index index,Vect3D point)
    {
        modules.get(name).setPoint(index,point);
    }
    public void setPointR(String name,Index index,Vect3D point)
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

    public void addMacroInteraction(String type, String name,String module1,String module2,
                                    String iterator1Type,String iterator1Description,
                                    String iterator2Type,String iterator2Description) throws Exception {
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
            int [] dimensions1 = getModule(module1).getDimensions();
            int [] dimensions2 = getModule(module2).getDimensions();
            AbstractIterator iterator1 = (AbstractIterator) Class.forName("org.micreative.miPhysics.Engine." + iterator1Type)
                    .getDeclaredConstructor(dimensions1.getClass(),String.class)
                    .newInstance(dimensions1,iterator1Description);
            iterator1.init();
            AbstractIterator iterator2 = (AbstractIterator) Class.forName("org.micreative.miPhysics.Engine." + iterator2Type)
                    .getDeclaredConstructor(dimensions2.getClass(),String.class)
                    .newInstance(dimensions2,iterator2Description);
            iterator2.init();
            modules.put(name,(Module)Class.forName("org.micreative.miPhysics.Engine.Modules.M" + type)
                    .getDeclaredConstructor(Module.class,Module.class,
                            AbstractIterator.class,AbstractIterator.class)
                    .newInstance(getModule(module1),getModule(module2),iterator1,iterator2));
            modules.get(name).loadParameters(defaultParams);
        }
    }

    public void addMacroMass(String name,String iteratorType,String iteratorDescription,
                             String containerType, int[]dimensions) throws Exception {
//        try
 //       {
            AbstractIterator iterator = (AbstractIterator) Class.forName("org.micreative.miPhysics.Engine." + iteratorType)
                    .getDeclaredConstructor(dimensions.getClass(),String.class)
                    .newInstance(dimensions,iteratorDescription);
            iterator.init();
            modules.put(name,new MacroMass(dimensions,iterator,containerType));

            InputStream input = PhysicalModel.class.getClassLoader().getResourceAsStream("defaultParams.properties");
        Properties p = new Properties();
        if (input == null) {
            System.out.println("defaultParams.properties not found");
            return;
        }
        p.load(input);
        Map defaultParams = getPropertySubsetAsMap(p, "MacroMass.");
        defaultParams.putAll(getPropertySubsetAsMap(p, "Global."));
        defaultParams.putAll(getPropertySubsetAsMap(p, containerType+"."));
        modules.get(name).loadParameters(defaultParams);
   //     }


    }

    public void addModuleController(String name,
                                    String moduleName,
                                    String dataProviderName,
                                    String controlledData) throws Exception {
        if(moduleControllers.containsKey(name)) throw new Exception("ModuleController named " +name + "already exists");
        Module mod=modules.get(moduleName);
        if (mod.hasParam(controlledData))
            moduleControllers.put(name,new ModuleController(mod,
                dataProviders.get(dataProviderName),
                controlledData));
        else
            moduleControllers.put(name,new ContainerController(mod,
                    dataProviders.get(dataProviderName),
                    controlledData));


    }

    public void addPositionScalarController(String name,
                                    String moduleName,
                                    String dataProviderName,
                                    String controlledData,
                                            Index index) throws Exception {
        if(moduleControllers.containsKey(name)) throw new Exception("ModuleController named " +name + "already exists");
        moduleControllers.put(name, new PositionScalarController(modules.get(moduleName),
                dataProviders.get(dataProviderName),
                controlledData,index));
    }

    public void addDataProvider(String type,String name) throws Exception {
        if(dataProviders.containsKey(name)) throw new Exception("DataProvider named " +name + "already exists");
        dataProviders.put(name,(DataProvider) Class.forName(type).newInstance());
    }

    public void addPositionScalarObserver(String name,String module,Index index,Vect3D projDir) throws Exception
    {
        if(dataProviders.containsKey(name)) throw new Exception("DataProvider named " +name + "already exists");
        dataProviders.put(name,(DataProvider) new PositionScalarObserver(modules.get(module),index,projDir));
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

    public int getNbPoints()
    {
        int nbPoints=0;
        for(Module m:modules.values()) nbPoints=nbPoints+m.getNbPoints();
        return nbPoints;
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

