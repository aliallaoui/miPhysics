package org.micreative.miPhysics.Renderer;

import org.micreative.miPhysics.Engine.AbstractContainer;
import org.micreative.miPhysics.Engine.AbstractIterator;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

public abstract class AbstractRenderer {

    protected List<AbstractContainer> containers;
    protected List<AbstractIterator> iterators;
    protected PApplet app;
    public AbstractRenderer(PApplet parent)
    {
        this.app =parent;
        containers=new ArrayList<>();
        iterators = new ArrayList<>();
    }

    public void addContainer(AbstractContainer container)
    {
        containers.add(container);
    }
    public void addIterator(AbstractIterator iterator)
    {
        iterators.add(iterator);
    }
    public abstract void render() throws Exception;
}
