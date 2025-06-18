/* Copyright 2025 Evan Troxell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.transcendruins.assets.interfaces;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.swing.JComponent;

import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.interfaces.InterfaceAttributes.ComponentSchema;
import com.transcendruins.assets.interfaces.InterfaceAttributes.ComponentSchema.StylePromise;
import com.transcendruins.assets.interfaces.InterfaceAttributes.ComponentType;
import com.transcendruins.resources.styles.Style;
import com.transcendruins.resources.styles.StyleSet;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.json.TracedDictionary;

public final class InterfaceInstance extends AssetInstance {

    public enum MouseState {

        NONE,

        HOVER,

        PRESS;
    }

    private StyleSet styles;

    private ComponentInstance component;

    public InterfaceInstance(AssetContext assetContext, Object key) {

        super(assetContext, key);
        InterfaceContext context = (InterfaceContext) assetContext;
    }

    @Override
    public void applyAttributes(Attributes attributeSet) {

        InterfaceAttributes attributes = (InterfaceAttributes) attributeSet;

        // If the attribute set is the base, a new style layer should be applied.
        if (attributes.isBase()) {

            styles = new StyleSet();
        }

        // Apply the new styles to the old.
        styles = calculateAttribute(attributes.getStyles(), set -> styles.apply(set), styles);

        component = calculateAttribute(attributes.getComponent(), InterfaceInstance::createComponent, component);
    }

    @Override
    protected void onUpdate(double time) {

    }

    public ComponentInstance createComponent(ComponentSchema schema) {

        return switch (schema) {

        case LabelSchema label -> new LabelComponentInstance(label);

        case ContainerSchema container -> new ContainerComponentInstance(container);

        default -> null;
        };
    }

    /**
     * <code>InterfaceAttributes.ComponentInstance</code>: A class representing an
     * instance of a visual component.
     */
    public abstract class ComponentInstance {

        private final JComponent component;

        private final String id;

        private final ComponentSize width;

        private final ComponentSize height;

        private final ComponentSize x;

        private final ComponentSize y;

        private final ImmutableList<StylePromise> style;

        private final ImmutableList<StylePromise> onHoverStyle;

        private final ImmutableList<StylePromise> onPressStyle;

        private MouseState mouseState = MouseState.NONE;

        public final Style evaluateStyle(StyleSet... styles) {

            ArrayList<Style> evaluated = new ArrayList<>();

            evaluated.addAll(evaluateStyle(style, styles));

            evaluated.addAll(switch (mouseState) {

            case HOVER -> evaluateStyle(onHoverStyle, styles);

            case PRESS -> evaluateStyle(onPressStyle, styles);

            default -> new ArrayList<>();
            });

            return new Style(evaluated);
        }

        private List<Style> evaluateStyle(List<StylePromise> promises, StyleSet[] styles) {

            return promises.stream().map(promise -> promise.getStyle(styles)).filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        public ComponentInstance(ComponentSchema schema, JComponent component) throws LoggedException {

            id = schema.getId();

            width = schema.getWidth();
            height = schema.getHeight();
            x = schema.getX();
            y = schema.getY();

            style = schema.getStyle();
            onHoverStyle = schema.getOnHoverStyle();
            onPressStyle = schema.getOnPressStyle();

            this.component = component;
            component.addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent e) { // Mouse pressed and released at same time.
                }

                @Override
                public void mousePressed(MouseEvent e) { // Mouse pressed.

                    press();
                }

                @Override
                public void mouseReleased(MouseEvent e) { // Mouse released.

                    // Only go back to hovering if the mouse is still over the element.
                    if (mouseDownOver()) {

                        onPress();
                        hover();
                    } else {

                        exit();
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) { // Mouse entered the component.

                    hover();
                }

                @Override
                public void mouseExited(MouseEvent e) { // Mouse exited the component.

                    exit();
                }
            });
        }

        public final void exit() {

            mouseState = MouseState.NONE;
        }

        public final void hover() {

            mouseState = MouseState.HOVER;
        }

        public final void press() {

            mouseState = MouseState.PRESS;
        }

        public final boolean mouseDownOver() {

            return mouseState == MouseState.PRESS;
        }

        public abstract void onPress();

        public Object getValue();
    }
}
