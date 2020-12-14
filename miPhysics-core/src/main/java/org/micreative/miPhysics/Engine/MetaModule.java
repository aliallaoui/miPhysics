package org.micreative.miPhysics.Engine;

import org.micreative.miPhysics.Engine.Modules.String2D;
import org.micreative.miPhysics.Vect3D;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void computeForces()
    {
        modules.forEach((name,module)->module.computeForces());
    }

    public void computeMoves()
    {
        modules.forEach((name,module)->module.computeMoves());
    }

    public Vect3D getPoint(String name,int index)
    {
        return modules.get(name).getPoint(name,index);// something to do with split(".") to handle recursive tree structure
    }

    public Vect3D getPointR(String name,int index)
    {
        return modules.get(name).getPointR(name,index);// something to do with split(".") to handle recursive tree structure
    }

    public void addModule(String type, String name) throws Exception {
        if(modules.containsKey(name)) throw new Exception("Module named " +name + "already exists");
        modules.put(name,(Module)Class.forName(type).newInstance());
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

