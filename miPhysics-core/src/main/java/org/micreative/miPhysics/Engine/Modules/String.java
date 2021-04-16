package org.micreative.miPhysics.Engine.Modules;

import org.apache.commons.beanutils.PropertyUtils;
import org.micreative.miPhysics.Engine.Index;
import org.micreative.miPhysics.Engine.MacroModule;
import org.micreative.miPhysics.Engine.MetaModule;
import org.micreative.miPhysics.Vect3D;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class String extends MetaModule
{
    protected int size;

    public String()
    {
        super("String");

    }

    public void init() throws Exception
    {
        int[] dim = new int[1];
        dim[0] = size;
        addMacroMass("masses",
                "BoundedIterator","LEFT1|RIGHT1",
                "GridContainer",dim);
        addMacroInteraction("SpringDamper","string",
                "masses","macro",
                "BoundedIterator","LEFT0|RIGHT1",
                "BoundedIterator","LEFT1|RIGHT0"
        );
        super.init();
        isInit = true;
    }

}
