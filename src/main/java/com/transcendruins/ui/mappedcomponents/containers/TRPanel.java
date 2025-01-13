package com.transcendruins.ui.mappedcomponents.containers;

import java.awt.Component;
import java.awt.LayoutManager;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.transcendruins.ui.mappedcomponents.TRComponent;
import com.transcendruins.ui.mappedcomponents.settings.ComponentSettings;

/**
 * <code>TRPanel</code>: A class representing a <code>JPanel</code> whose
 * components have been mapped, allowing for easy retrieval.
 */
public class TRPanel extends JPanel implements TRContainer {

    /**
     * <code>String</code>: The name of this <code>TRPanel</code> instance.
     */
    private final String name;

    @Override
    public final String getComponentName() {

        return name;
    }

    /**
     * <code>LinkedHashMap&lt;String, TRComponent&gt;</code>: The map of components
     * of this <code>TRPanel</code> instance.
     */
    private final LinkedHashMap<String, TRComponent> componentMap = new LinkedHashMap<>();

    /**
     * Creates a new instance of the <code>TRPanel</code> class.
     * 
     * @param name     <code>String</code>: The name of this <code>TRPanel</code>
     *                 instance.
     * @param settings <code>ComponentSettings</code>: The settings to apply to this
     *                 <code>TRPanel</code> instance.
     */
    public TRPanel(String name, ComponentSettings settings) {

        super();
        this.name = name;

        applySettings(settings);
        getComponentName();
    }

    /**
     * Creates a new instance of the <code>TRPanel</code> class.
     * 
     * @param name     <code>String</code>: The name of this <code>TRPanel</code>
     *                 instance.
     * @param layout   <code>LayoutManager</code>: The layout to apply to this
     *                 <code>TRPanel</code> instance.
     * @param settings <code>ComponentSettings</code>: The settings to apply to this
     *                 <code>TRPanel</code> instance.
     */
    public TRPanel(String name, LayoutManager layout, ComponentSettings settings) {

        this(name, settings);
        setLayout(layout);
    }

    /**
     * Creates a panel using the <code>BoxLayout</code> layout manager with a set
     * spacing between each element.
     * 
     * @param name               <code>String</code>: The name of the new
     *                           <code>TRPanel</code> instance.
     *
     * @param components         <code>List&lt;TRComponent&gt;</code>: The
     *                           components to add to the new <code>TRPanel</code>
     *                           instance.
     * @param isVertical         <code>boolean</code>: Whether or not the layout
     *                           should be stacked vertically or not.
     * @param componentAlignment <code>Float</code>: The alignment to apply to each
     *                           component.
     *                           A <code>null</code> value represents not applying
     *                           any alignment.
     * @param pixelSpacing       <code>int</code>: The spacing, in pixels, to apply
     *                           between each element.
     * @param settings           <code>ComponentSettings</code>: The settings to
     *                           apply to the new <code>TRPanel</code> instance.
     * @param createBuffers      <code>boolean</code>: Whether or not buffers should
     *                           be created around the edge components.
     * @return <code>TRPanel</code>: The generated mapped panel.
     */
    public static final TRPanel createBoxPanel(String name, List<TRComponent> components, boolean isVertical,
            Float componentAlignment, int pixelSpacing, ComponentSettings settings, boolean createBuffers) {

        TRPanel returnPanel = new TRPanel(name, settings);
        returnPanel.setLayout(new BoxLayout(returnPanel, isVertical ? BoxLayout.Y_AXIS : BoxLayout.X_AXIS));

        Iterator<TRComponent> iterator = components.iterator();

        if (createBuffers) {

            returnPanel.add((Component) (isVertical ? Box.createVerticalGlue() : Box.createHorizontalGlue()));
        }

        while (iterator.hasNext()) {

            TRComponent trComponent = iterator.next();
            JComponent component = (JComponent) trComponent;

            if (isVertical) {

                component.setAlignmentX(componentAlignment);
            } else {

                component.setAlignmentY(componentAlignment);
            }

            returnPanel.addComponent(trComponent);
            if (pixelSpacing != 0 && iterator.hasNext()) {

                returnPanel.add(
                        isVertical ? Box.createVerticalStrut(pixelSpacing) : Box.createHorizontalStrut(pixelSpacing));
            }
        }

        if (createBuffers) {

            returnPanel.add((Component) (isVertical ? Box.createVerticalGlue() : Box.createHorizontalGlue()));
        }

        return returnPanel;
    }

    @Override
    public final void addComponent(TRComponent component) {

        componentMap.put(component.getComponentName(), component);
        add((Component) component);
    }

    @Override
    public final void addComponent(TRComponent component, int index) {

        componentMap.put(component.getComponentName(), component);
        add((Component) component, index);
    }

    @Override
    public final void addComponent(TRComponent component, Object constraints) {

        componentMap.put(component.getComponentName(), component);
        add((Component) component, constraints);
    }

    @Override
    public final void addComponent(TRComponent component, Object constraints, int index) {

        componentMap.put(component.getComponentName(), component);
        add((Component) component, constraints, index);
    }

    @Override
    public final TRComponent getComponent(String key) {

        return componentMap.get(key);
    }

    @Override
    public final Set<Map.Entry<String, TRComponent>> getComponentSet() {

        return componentMap.entrySet();
    }

    @Override
    public final void applySettings(ComponentSettings settings) {

        settings.apply(this);
    }

    @Override
    public final void setEnabled(boolean enabled) {

        super.setEnabled(enabled);
        for (Map.Entry<String, TRComponent> componentEntry : getComponentSet()) {

            componentEntry.getValue().setEnabled(enabled);
        }
    }

    @Override
    public final void setComponentEnabled(String name, boolean enabled) {

        TRComponent component = getComponent(name);
        component.setEnabled(enabled);
    }
}
