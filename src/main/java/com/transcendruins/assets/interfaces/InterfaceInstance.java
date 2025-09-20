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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.ImageIcon;

import static com.transcendruins.assets.AssetType.INTERFACE;
import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.interfaces.InterfaceAttributes.ComponentSchema;
import com.transcendruins.assets.interfaces.InterfaceAttributes.ComponentSchema.InterfaceComponentSchema;
import com.transcendruins.assets.interfaces.InterfaceAttributes.ComponentSchema.LabelComponentSchema;
import com.transcendruins.assets.scripts.TRScript;
import com.transcendruins.resources.styles.ComponentProperties;
import com.transcendruins.resources.styles.Style;
import com.transcendruins.resources.styles.Style.BackgroundStyle;
import com.transcendruins.resources.styles.Style.BorderStyle;
import com.transcendruins.resources.styles.Style.SizeDimensions;
import com.transcendruins.resources.styles.Style.TextureSize;
import com.transcendruins.resources.styles.StyleSet;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.immutable.ImmutableList;

public final class InterfaceInstance extends AssetInstance {

    private StyleSet styles;

    private ComponentInstance body;

    public ComponentInstance getBody() {

        return body;
    }

    public InterfaceInstance(AssetContext assetContext, Object key) {

        super(assetContext, key);
        InterfaceContext context = (InterfaceContext) assetContext;
    }

    @Override
    public void applyAttributes(Attributes attributeSet) {

        InterfaceAttributes attributes = (InterfaceAttributes) attributeSet;

        // If the attribute set is the base, empty the styles list.
        if (attributes.isBase()) {

            styles = new StyleSet();
        }

        // Apply the new styles to the old.
        styles = calculateAttribute(attributes.getStyles(), set -> styles.extend(set), styles);

        body = calculateAttribute(attributes.getBody(), schema -> createComponent(schema, null), body);
    }

    @Override
    protected void onUpdate(double time) {

    }

    /**
     * Creates a new instance of the
     * <code>InterfaceInstance.ComponentInstance</code> class.
     * 
     * @param schema <code>InterfaceAttributes.ComponentSchema</code>: The schema to
     *               create the new <code>InterfaceInstance.ComponentInstance</code>
     *               instance from.
     * @return <code>InterfaceInstance.ComponentInstance</code>: The resulting loot
     *         instance.
     * @throws LoggedException Thrown if any exception is raised while creating the
     *                         new loot instance.
     */
    public ComponentInstance createComponent(ComponentSchema schema, ComponentInstance parent) {

        return switch (schema) {

        case LabelComponentSchema labelSchema -> new LabelComponentInstance(labelSchema, parent);

        case ButtonComponentSchema buttonSchema -> new ButtonComponentInstance(buttonSchema, parent);

        case InputComponentSchema inputSchema -> new InputComponentInstance(inputSchema, parent);

        case InterfaceComponentSchema interfaceSchema -> new InterfaceComponentInstance(interfaceSchema, parent);

        case DropdownComponentSchema dropdownSchema -> new DropdownComponentInstance(dropdownSchema, parent);

        case SelectComponentSchema selectSchema -> new SelectComponentInstance(selectSchema, parent);

        case ListComponentSchema listSchema -> new ListComponentInstance(listSchema, parent);

        case PanelComponentSchema panelSchema -> new PanelComponentInstance(panelSchema, parent);

        default -> null;
        };
    }

    /**
     * <code>InterfaceAttributes.ComponentInstance</code>: A class representing an
     * instance of a visual component.
     */
    public abstract class ComponentInstance {

        private final ComponentInstance parent;

        public final boolean hasParent() {

            return parent != null;
        }

        private final ArrayList<ComponentInstance> children = new ArrayList<>();

        protected final void addChild(ComponentInstance child) {

            children.add(child);
        }

        private final String type;

        private final String id;

        private final Style style;

        private final HashSet<String> classes = new HashSet<>();

        public final void addClass(String c) {

            classes.add(c);
        }

        public final void removeClass(String c) {

            classes.remove(c);
        }

        private final HashSet<String> states = new HashSet<>();

        public final void addState(String state) {

            states.add(state);
        }

        public final void removeState(String state) {

            states.remove(state);
        }

        public final ComponentProperties getProperties() {

            return new ComponentProperties(type, id, classes, states) {

                @Override
                public ComponentProperties getParent() {

                    return hasParent() ? parent.getProperties() : null;
                }

                @Override
                public List<ComponentProperties> getChildren() {

                    return children.stream().map(ComponentInstance::getProperties).toList();
                }
            };
        }

        public final Style getStyle() {

            ComponentProperties properties = getProperties();

            // The base styles should be supplemented by packs.
            ArrayList<Style> evaluated = new ArrayList<>(getWorld().getStyle().getStyle(properties));

            // The next level should be file-specific styles.
            evaluated.addAll(styles.getStyle(properties));

            // The instance styles should be the top.
            evaluated.add(style);

            return Style.createStyle(evaluated);
        }

        public ComponentInstance(ComponentSchema schema, ComponentInstance parent) {

            this.parent = parent;

            type = schema.getType();
            id = schema.getId();
            style = schema.getStyle();
        }

        public final void exit() {

            removeState("hover");
            removeState("active");
        }

        public final void hover() {

            addState("hover");
            removeState("active");
        }

        public final void press() {

            addState("active");
        }

        /**
         * Evaluates the event which should be run when this
         * <code>ComponentInstance</code> instance is pressed.
         * 
         * @param value <code>TRScript</code>: The value of this
         *              <code>ComponentInstance</code> instance.
         * @return <code>boolean</code>: Whether or not the event should continue to
         *         propogate.
         */
        public abstract boolean onPress(TRScript value);

        /**
         * Evaluates the event which should be run when this
         * <code>ComponentInstance</code> instance is scrolled.
         * 
         * @param scrolled <code>int[2]</code>: The displacement of the
         * @return <code>boolean</code>: Whether or not the event should continue to
         *         propogate.
         */
        public abstract boolean onScroll(int[] scrolled);

        public abstract TRScript getValue();

        public abstract ComponentInstance getHighestAt(int x, int y);

        public abstract List<ComponentInstance> getStackAt(int x, int y);

        public abstract List<ComponentInstance> getComponents();

        public final class ComponentDimensions {
            private int x, y, width, height, marginLeft, marginRight, marginTop, marginBottom, rxTL, ryTL, rxTR, ryTR,
                    rxBL, ryBL, rxBR, ryBR, paddingLeft, paddingRight, paddingTop, paddingBottom;

            public ComponentDimensions(int x, int y, int width, int height, int marginLeft, int marginRight,
                    int marginTop, int marginBottom, int rxTL, int ryTL, int rxTR, int ryTR, int rxBL, int ryBL,
                    int rxBR, int ryBR, int paddingLeft, int paddingRight, int paddingTop, int paddingBottom) {

                this.x = x;
                this.y = y;
                this.width = width;
                this.height = height;
                this.marginLeft = marginLeft;
                this.marginRight = marginRight;
                this.marginTop = marginTop;
                this.marginBottom = marginBottom;
                this.rxTL = rxTL;
                this.ryTL = ryTL;
                this.rxTR = rxTR;
                this.ryTR = ryTR;
                this.rxBL = rxBL;
                this.ryBL = ryBL;
                this.rxBR = rxBR;
                this.ryBR = ryBR;
                this.paddingLeft = paddingLeft;
                this.paddingRight = paddingRight;
                this.paddingTop = paddingTop;
                this.paddingBottom = paddingBottom;
            }

        }

        private ComponentDimensions getDimensions(int parentWidth, int parentHeight) {

            // Update the coordinates.
            int x = style.x().getSize(parentWidth, 0);
            int y = style.y().getSize(parentHeight, 0);

            // Calculate the width.
            int minWidth = style.minWidth().getSize(parentWidth, 0);
            int width = style.width().getSize(parentWidth, minWidth);

            // Calculate the horizontal margins.
            int marginLeft = style.marginLeft().getSize(parentWidth, 0);
            int marginRight = style.marginRight().getSize(parentWidth, 0);

            // If the horizontal margins extend beyond the parent, adjust them so they fit.
            if (width + marginLeft + marginRight > parentWidth) {

                double partial = (double) (parentWidth - width) / (marginLeft + marginRight);

                marginLeft *= partial;
                marginRight *= partial;
            }

            // Calculate the height.
            int minHeight = style.minHeight().getSize(parentHeight, 0);
            int height = style.height().getSize(parentHeight, minHeight);

            // Calculate the vertical margins.
            int marginTop = style.marginTop().getSize(parentHeight, 0);
            int marginBottom = style.marginBottom().getSize(parentHeight, 0);

            // If the verical margins extend beyond the parent, adjust them so they fit.
            if (height + marginTop + marginBottom > parentHeight) {

                double partial = (double) (parentHeight - height) / (marginTop + marginBottom);

                marginTop *= partial;
                marginBottom *= partial;
            }

            // Adjust the full width and height.
            int marginWidth = width + marginLeft + marginRight;

            if (x + marginWidth > parentWidth) {

                x = parentWidth - marginWidth;
            }

            int marginHeight = height + marginTop + marginBottom;

            if (y + marginHeight > parentHeight) {

                y = parentHeight - marginHeight;
            }

            SizeDimensions rTL = style.rTL();
            int rxTL = rTL.width().getSize(width, 0);
            int ryTL = rTL.height().getSize(height, 0);

            SizeDimensions rTR = style.rTR();
            int rxTR = rTR.width().getSize(width, 0);
            int ryTR = rTR.height().getSize(height, 0);

            SizeDimensions rBL = style.rBL();
            int rxBL = rBL.width().getSize(width, 0);
            int ryBL = rBL.height().getSize(height, 0);

            SizeDimensions rBR = style.rBR();
            int rxBR = rBR.width().getSize(width, 0);
            int ryBR = rBR.height().getSize(height, 0);

            // If the top radii exceed the width, adjust them so they fit.
            if (rxTR + rxTL > width) {

                double partial = (double) width / (rxTR + rxTL);

                rxTR *= partial;
                rxTL *= partial;
            }

            // If the bottom radii exceed the width, adjust them so they fit.
            if (rxBR + rxBL > width) {

                double partial = (double) width / (rxBR + rxBL);

                rxBR *= partial;
                rxBL *= partial;
            }

            // If the left radii exceed the height, adjust them so they fit.
            if (ryBL + ryTL > height) {

                double partial = (double) height / (ryBL + ryTL);

                ryBL *= partial;
                ryTL *= partial;
            }

            // If the right radii exceed the height, adjust them so they fit.
            if (ryBR + ryTR > height) {

                double partial = (double) height / (ryBR + ryTR);

                ryBR *= partial;
                ryTR *= partial;
            }

            int paddingLeft = style.paddingLeft().getSize(width, 0);
            int paddingRight = style.paddingRight().getSize(width, 0);

            if (paddingLeft + paddingRight > width) {

                double partial = (double) width / (paddingLeft + paddingRight);

                paddingLeft *= partial;
                paddingRight *= partial;
            }

            int paddingTop = style.paddingTop().getSize(height, 0);
            int paddingBottom = style.paddingBottom().getSize(height, 0);

            if (paddingTop + paddingBottom > height) {

                double partial = (double) height / (paddingTop + paddingBottom);

                paddingTop *= partial;
                paddingBottom *= partial;
            }

            return new ComponentDimensions(x, y, width, height, marginLeft, marginRight, marginTop, marginBottom, rxTL,
                    ryTL, rxTR, ryTR, rxBL, ryBL, rxBR, ryBR, paddingLeft, paddingRight, paddingTop, paddingBottom);
        }

        public final void draw(ComponentDimensions dimensions) {

            // Copy the graphics.
            g2d = (Graphics2D) g2d.create();

            // Adjust the origin to the top left corner of the component.
            g2d.translate(x + marginLeft, y + marginTop);

            // Creates the content bounds.
            Shape contentBounds = createBounds(width, height, rxTR, ryTR, rxTL, ryTL, rxBL, ryBL, rxBR, ryBR);

            // Process the background color and image.
            BackgroundStyle background = s.background();
            drawBackground(g2d, background, contentBounds);

            //

            //

            // Find the exact center of the component.
            int centerX = width / 2;
            int centerY = height / 2;

            // Draw the borders of the component.
            Graphics2D g2 = (Graphics2D) g2d.create();

            BorderStyle borderRight = s.borderRight();
            Color borderRightColor = borderRight.color();
            int borderRightWidth = borderRight.width().getSize(marginRight, 0);

            // Draw the right side.
            if (borderRightColor != null && borderRightWidth > 0) {

                g2.setColor(borderRightColor);
                g2.setStroke(new BasicStroke(borderRightWidth));
                Shape right = createSide(centerX, centerY, width, height, rxTR, ryTR, rxBR, ryBR);
                g2.draw(right);
            }

            g2.rotate(Math.PI / 2, centerX, centerY);

            BorderStyle borderBottom = s.borderBottom();
            Color borderBottomColor = borderBottom.color();
            int borderBottomWidth = borderBottom.width().getSize(marginBottom, 0);

            // Draw the bottom side.
            if (borderBottomColor != null && borderBottomWidth > 0) {

                g2.setColor(borderBottomColor);
                g2.setStroke(new BasicStroke(borderBottomWidth));
                Shape bottom = createSide(centerX, centerY, height, width, ryBR, rxBR, ryBL, rxBL);
                g2.draw(bottom);
            }

            g2.rotate(Math.PI / 2, centerX, centerY);

            BorderStyle borderLeft = s.borderLeft();
            Color borderLeftColor = borderLeft.color();
            int borderLeftWidth = borderLeft.width().getSize(marginLeft, 0);

            // Draw the left side.
            if (borderLeftColor != null && borderLeftWidth > 0) {

                g2.setColor(borderLeftColor);
                g2.setStroke(new BasicStroke(borderLeftWidth));
                Shape left = createSide(centerX, centerY, width, height, rxBL, ryBL, rxTL, ryTL);
                g2.draw(left);
            }

            g2.rotate(Math.PI / 2, centerX, centerY);

            BorderStyle borderTop = s.borderTop();
            Color borderTopColor = borderLeft.color();
            int borderTopWidth = borderTop.width().getSize(marginTop, 0);

            // Draw the top side.
            if (borderTopColor != null && borderTopWidth > 0) {

                g2.setColor(borderTopColor);
                g2.setStroke(new BasicStroke(borderTopWidth));
                Shape top = createSide(centerX, centerY, height, width, ryTL, rxTL, ryTR, rxTR);
                g2.draw(top);
            }

            g2.dispose();

            //

            //

            // Clip the content bounds to render children.
            g2d.clip(contentBounds);

            // Adjust to the interior of the component.
            g2d.translate(paddingLeft, paddingTop);

            // Draw the children of the component.
            paint(s, g2d);

            // Revert to the previous position.
            g2d.translate(-x - marginLeft - paddingLeft, -y - marginTop - paddingTop);
        }

        protected abstract void paint(Style s, Graphics2D g2d, int width, int height);

        private Shape createBounds(int width, int height, int rxTR, int ryTR, int rxTL, int ryTL, int rxBL, int ryBL,
                int rxBR, int ryBR) {

            Path2D path = new Path2D.Double();

            // Start at top-left corner.
            path.moveTo(rxTL, 0);

            // Top edge.
            path.lineTo(width - rxTR, 0);
            path.append(new Arc2D.Double(width - 2 * rxTR, 0, 2 * rxTR, 2 * ryTR, 90, -90, Arc2D.OPEN), true);

            // Right edge.
            path.lineTo(width, height - ryBR);
            path.append(new Arc2D.Double(width - 2 * rxBR, height - 2 * ryBR, 2 * rxBR, 2 * ryBR, 360, -90, Arc2D.OPEN),
                    true);

            // Bottom edge.
            path.lineTo(rxBL, height);
            path.append(new Arc2D.Double(0, height - 2 * ryBL, 2 * rxBL, 2 * ryBL, 270, -90, Arc2D.OPEN), true);

            // Left edge.
            path.lineTo(0, ryTL);
            path.append(new Arc2D.Double(0, 0, 2 * rxTL, 2 * ryTL, 180, -90, Arc2D.OPEN), true);

            path.closePath();
            return path;
        }

        private Shape createSide(int centerX, int centerY, int width, int height, int wT, int hT, int wB, int hB) {

            int startX = centerX + width / 2;
            int startY = centerY;

            Path2D.Double path = new Path2D.Double();

            Arc2D.Double arc1 = new Arc2D.Double(centerX + width / 2 - 2 * wB, centerY + height / 2 - 2 * hB, wB * 2,
                    hB * 2, 0, -45, Arc2D.OPEN);
            Point2D start1 = arc1.getStartPoint();

            path.moveTo(startX, startY);
            path.lineTo(start1.getX(), start1.getY());
            path.append(arc1, true);

            Arc2D.Double arc2 = new Arc2D.Double(centerX + width / 2 - 2 * wT, centerY - height / 2, wT * 2, hT * 2, 0,
                    45, Arc2D.OPEN);
            Point2D start2 = arc2.getStartPoint();

            path.moveTo(startX, startY);
            path.lineTo(start2.getX(), start2.getY());
            path.append(arc2, true);

            return path;
        }

        protected final void drawBackground(Graphics2D g2d, BackgroundStyle background, Shape bounds) {

            // Clip out the content bounds just for the background.
            g2d = (Graphics2D) g2d.create();
            g2d.clip(bounds);

            Color backgroundColor = background.color();
            if (backgroundColor != null) {

                g2d.setColor(backgroundColor);
                g2d.fill(bounds);
            }

            // Process the background texture, if it has one.
            String backgroundTexture = background.texture();
            if (backgroundTexture != null) {

                drawImage(g2d, backgroundTexture, 0, 0, background.size());
                g2d.dispose();
            }
        }

        protected final void drawImage(Graphics2D g2d, String texture, int x, int y, TextureSize size) {

            ImageIcon icon = getTexture(texture);
            int textureWidth = icon.getIconWidth();
            int textureHeight = icon.getIconHeight();

            if (textureWidth == 0 || textureHeight == 0) {

                return;
            }

            int backgroundWidth = size.getWidth(textureWidth, textureHeight, width, height);
            int backgroundHeight = size.getHeight(textureWidth, textureHeight, width, height);

            g2d.drawImage(icon.getImage(), x, y, backgroundWidth, backgroundHeight, null);
        }

        protected final void drawText(Graphics2D g2d, String text, int x, int y, TextFormat format) {

        }
    }

    public abstract class LeafComponentInstance extends ComponentInstance {

        public LeafComponentInstance(ComponentSchema schema, ComponentInstance parent) {

            super(schema, parent);
        }

        @Override
        public final ComponentInstance getHighestAt(int x, int y) {

            return this;
        }

        @Override
        public final List<ComponentInstance> getStackAt(int x, int y) {

            return List.of(this);
        }

        @Override
        public final List<ComponentInstance> getComponents() {

            return new ArrayList<>();
        }
    }

    public final class InterfaceComponentInstance extends ComponentInstance {

        private final InterfaceInstance asset;

        public InterfaceComponentInstance(InterfaceComponentSchema schema, ComponentInstance parent) {

            super(schema, parent);

            AssetPresets presets = schema.getPresets();
            InterfaceContext context = new InterfaceContext(presets, getWorld(), InterfaceInstance.this);

            asset = (InterfaceInstance) INTERFACE.createAsset(context);
        }

        @Override
        public void onPress(TRScript value) {

        }

        @Override
        public TRScript getValue() {

            ComponentInstance body = asset.getBody();
            return body.getValue();
        }

        @Override
        protected void paint(Style s, Graphics2D g2d, int width, int height) {

            asset.draw(g2d, width, height);
        }
    }

    public abstract class ComponentActionInstance {

        private final ImmutableList<TRScript> conditions;

        /**
         * Determines whether or not this action passes for a given asset.
         * 
         * @param asset <code>AssetInstance</code>: The asset to evaluate the conditions
         *              against.
         * @return <code>boolean</code>: Whether or not the conditions pass for the
         *         given asset.
         */
        public boolean passes(AssetInstance asset) {

            for (TRScript condition : conditions) {

                if (!condition.evaluateBoolean(asset)) {

                    return false;
                }
            }

            return true;
        }

        public ComponentActionInstance(ComponentActionSchema schema) {

            conditions = schema.getConditions();
        }
    }

    public void draw(Graphics2D g2d, int parentWidth, int parentHeight) {

        body.draw(g2d, parentWidth, parentHeight);
    }
}
