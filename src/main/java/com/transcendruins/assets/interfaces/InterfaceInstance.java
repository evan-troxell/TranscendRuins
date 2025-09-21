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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
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
import com.transcendruins.assets.interfaces.InterfaceAttributes.ComponentSchema.ButtonComponentSchema;
import com.transcendruins.assets.interfaces.InterfaceAttributes.ComponentSchema.ComponentActionSchema;
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
     * Creates a new instance of the <code>ComponentInstance</code> class.
     * 
     * @param schema <code>ComponentSchema</code>: The schema to create the new
     *               <code>ComponentInstance</code> instance from.
     * @return <code>ComponentInstance</code>: The resulting loot instance.
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
     * <code>ComponentInstance</code>: A class representing an instance of a visual
     * component.
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

        private final Style styleX; // TODO: Return to 'style'

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

        protected int x, y, width, height, borderLeft, borderRight, borderTop, borderBottom, marginLeft, marginRight,
                marginTop, marginBottom, rxTL, ryTL, rxTR, ryTR, rxBL, ryBL, rxBR, ryBR, paddingLeft, paddingRight,
                paddingTop, paddingBottom, fontSize, lineHeight;

        protected Font font;

        /**
         * Resizes this <code>ComponentInstance</code> to a new set of dimensions. This
         * should be used when resizing a component to account for the size of children.
         * 
         * @param contentWidth  <code>int</code>: The width of the content of this
         *                      <code>ComponentInstance</code>, which is equal to the
         *                      width minus the horizontal padding.
         * @param contentHeight <code>int</code>: The height of the content ofthis
         *                      <code>ComponentInstance</code>, which is equal to the
         *                      height minus the vertical padding.
         */
        protected final void resize(int contentWidth, int contentHeight) {

            this.width = contentWidth + paddingLeft + paddingRight;
            this.height = contentHeight + paddingTop + paddingBottom;
        }

        /**
         * Retrieves the width of the content of this <code>ComponentInstance</code>
         * instance.
         * 
         * @return <code>int</code>: The width of this <code>ComponentInstance</code>
         *         instance minus the horizontal padding and half of the horizontal
         *         border.
         */
        protected final int getContentWidth() {

            return width - paddingLeft - paddingRight - (borderLeft + borderRight) / 2;
        }

        /**
         * Retrieves the height of the content of this <code>ComponentInstance</code>
         * instance.
         * 
         * @return <code>int</code>: The height of this <code>ComponentInstance</code>
         *         instance minus the vertical padding and half of the vertical border.
         */
        protected final int getContentHeight() {

            return height - paddingTop - paddingBottom - (borderTop + borderBottom) / 2;
        }

        /**
         * Retrieves the total width of this <code>ComponentInstance</code> instance.
         * 
         * @return <code>int</code>: The width of this <code>ComponentInstance</code>
         *         instance plus the horizontal margins and the horizontal border.
         */
        protected final int getTotalWidth() {

            return width + marginLeft + marginRight + (borderLeft + borderRight);
        }

        /**
         * Retrieves the total height of this <code>ComponentInstance</code> instance.
         * 
         * @return <code>int</code>: The height of this <code>ComponentInstance</code>
         *         instance plus the vertical margins and the vertical border.
         */
        protected final int getTotalHeight() {

            return height + marginTop + marginBottom + (borderTop + borderBottom);
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
         * @param scrolled <code>int[2]</code>: The displacement which has been
         *                 scrolled.
         * @return <code>boolean</code>: Whether or not the event should continue to
         *         propogate.
         */
        public abstract boolean onScroll(int[] scrolled);

        public abstract TRScript getValue();

        public abstract ComponentInstance getHighestAt(int x, int y);

        public abstract List<ComponentInstance> getStackAt(int x, int y);

        public abstract List<ComponentInstance> getComponents();

        /**
         * Calculates the initial size of this component based on the measurements of
         * the parent. This is the first step in sizing this
         * <code>ComponentInstance</code> instance, and an additional step in resizing
         * may occur.
         * 
         * @param style               <code>Style</code>: The style to measure using.
         * @param parentContentWidth  <code>int</code>: The width of the internal space
         *                            in the parent of this
         *                            <code>InterfaceInstance</code> instance. This
         *                            value should be equal to the width of the parent
         *                            minus the horizontal padding of the parent.
         * @param parentContentHeight <code>int</code>: The height of the internal space
         *                            in the parent of this
         *                            <code>InterfaceInstance</code> instance. This
         *                            value should be equal to the height of the parent
         *                            minus the vertical padding of the parent.
         * @param parentFontSize      <code>int</code>: The font size of the parent of
         *                            this <code>ComponentInstance</code> instance.
         */
        private void measure(Style style, int parentContentWidth, int parentContentHeight, int parentFontSize) {

            // Update the coordinates.
            x = style.x().getSize(parentContentWidth, 0);
            y = style.y().getSize(parentContentHeight, 0);

            // Calculate the width.
            int minWidth = style.minWidth().getSize(parentContentWidth, 0);
            width = style.width().getSize(parentContentWidth, minWidth);

            // Calculate the horizontal margins.
            marginLeft = style.marginLeft().getSize(parentContentWidth, 0);
            marginRight = style.marginRight().getSize(parentContentWidth, 0);

            // If the horizontal margins extend beyond the parent, adjust them so they fit.
            if (width + marginLeft + marginRight > parentContentWidth) {

                double partial = (double) (parentContentWidth - width) / (marginLeft + marginRight);

                marginLeft *= partial;
                marginRight *= partial;
            }

            // Calculate the height.
            int minHeight = style.minHeight().getSize(parentContentHeight, 0);
            height = style.height().getSize(parentContentHeight, minHeight);

            // Calculate the vertical margins.
            marginTop = style.marginTop().getSize(parentContentHeight, 0);
            marginBottom = style.marginBottom().getSize(parentContentHeight, 0);

            // If the verical margins extend beyond the parent, adjust them so they fit.
            if (height + marginTop + marginBottom > parentContentHeight) {

                double partial = (double) (parentContentHeight - height) / (marginTop + marginBottom);

                marginTop *= partial;
                marginBottom *= partial;
            }

            // Adjust the full width and height.
            int marginWidth = width + marginLeft + marginRight;

            if (x + marginWidth > parentContentWidth) {

                x = parentContentWidth - marginWidth;
            }

            int marginHeight = height + marginTop + marginBottom;

            if (y + marginHeight > parentContentHeight) {

                y = parentContentHeight - marginHeight;
            }

            borderLeft = style.borderLeft().width().getSize(parentContentWidth, 0);
            borderRight = style.borderRight().width().getSize(parentContentWidth, 0);
            borderTop = style.borderTop().width().getSize(parentContentHeight, 0);
            borderBottom = style.borderBottom().width().getSize(parentContentHeight, 0);

            SizeDimensions rTL = style.rTL();
            rxTL = rTL.width().getSize(width, 0);
            ryTL = rTL.height().getSize(height, 0);

            SizeDimensions rTR = style.rTR();
            rxTR = rTR.width().getSize(width, 0);
            ryTR = rTR.height().getSize(height, 0);

            SizeDimensions rBL = style.rBL();
            rxBL = rBL.width().getSize(width, 0);
            ryBL = rBL.height().getSize(height, 0);

            SizeDimensions rBR = style.rBR();
            rxBR = rBR.width().getSize(width, 0);
            ryBR = rBR.height().getSize(height, 0);

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

            paddingLeft = style.paddingLeft().getSize(width, 0);
            paddingRight = style.paddingRight().getSize(width, 0);

            if (paddingLeft + paddingRight > width) {

                double partial = (double) width / (paddingLeft + paddingRight);

                paddingLeft *= partial;
                paddingRight *= partial;
            }

            paddingTop = style.paddingTop().getSize(height, 0);
            paddingBottom = style.paddingBottom().getSize(height, 0);

            if (paddingTop + paddingBottom > height) {

                double partial = (double) height / (paddingTop + paddingBottom);

                paddingTop *= partial;
                paddingBottom *= partial;
            }

            // The font size should be based on the parent font size and have a minimum of
            // 8px.
            fontSize = style.fontSize().getSize(parentFontSize, 8);

            // The line height should be based on the font size and be at least as large as
            // the font.
            lineHeight = style.lineHeight().getSize(fontSize, fontSize);

            // Create the font.
            font = new Font(style.fontFamily(), style.fontStyle() | style.fontWeight(), fontSize);
        }

        /**
         * <code>BufferedImage</code>: The image containing the current content of this
         * <code>ComponentInstance</code> instance.
         */
        private BufferedImage content;

        /**
         * Generates the content of this <code>ComponentInstance</code> instance.
         * 
         * @param style    <code>Style</code>: The style to draw using.
         * @param children <code>List&lt;ComponentInstance&gt;</code>: The drawn
         *                 children of this <code>ComponentInstance</code> instance.
         */
        protected abstract void createContent(Style style, List<ComponentInstance> children);

        /**
         * Generates the content box of this <code>ComponentInstance</code> instance.
         * This is the box that contains all images, text, and children element (also
         * known as the internal box).
         * 
         * @return <code>Graphics2D</code>: The graphics of the content image.
         */
        protected final Graphics2D createContentBox() {

            content = new BufferedImage(getContentWidth(), getContentHeight(), BufferedImage.TYPE_INT_ARGB);

            return content.createGraphics();
        }

        private BufferedImage render;

        public final BufferedImage getRender() {

            return render;
        }

        /**
         * Draws this <code>ComponentInstance</code> instance.
         * 
         * @param parentContentWidth  <code>int</code>: The content width of the parent
         *                            of this <code>ComponentInstance</code> instance.
         * @param parentContentHeight <code>int</code>: The content height of the parent
         *                            of this <code>ComponentInstance</code> instance.
         * @param parentFontSize      <code>int</code>: The font size of the parent of
         *                            this <code>ComponentInstance</code> instance.
         */
        public final void render(int parentContentWidth, int parentContentHeight, int parentFontSize) {

            // Nullify the current content and render so they cannot be reused by accident.
            content = null;
            render = null;

            // Generate the current style.
            Style s = getStyle();

            // Calculate the initial size.
            measure(s, parentContentWidth, parentContentHeight, parentFontSize);

            // This is the internal width of the component (i.e. the space the children will
            // use).
            int contentWidth = getContentWidth();
            int contentHeight = getContentHeight();

            // Draw the children.
            for (ComponentInstance child : children) {

                child.render(contentWidth, contentHeight, fontSize);
            }

            // Create all internal content and perform resizing.
            createContent(s, children);

            // Create the final component.
            BufferedImage component = new BufferedImage(getTotalWidth(), getTotalHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = component.createGraphics();

            // Adjust the origin to the top left corner of the component.
            // If the left or top border widths are odd numbers, this will consume the
            // larger integer half.
            g2d.translate(marginLeft + Math.ceil(borderLeft / 2.0), marginTop + Math.ceil(borderTop / 2.0));

            // Creates the content bounds.
            Shape contentBounds = createBounds(width, height, rxTR, ryTR, rxTL, ryTL, rxBL, ryBL, rxBR, ryBR);

            // Process the background color and image.
            BackgroundStyle background = s.background();
            drawBackground(g2d, background, contentBounds);

            // Start the border.
            Graphics2D g2 = (Graphics2D) g2d.create();

            // Find the exact center of the component.
            int centerX = width / 2;
            int centerY = height / 2;

            BorderStyle bR = s.borderRight();
            Color borderRightColor = bR.color();
            int borderRightWidth = bR.width().getSize(marginRight, 0);

            // Draw the right side.
            if (borderRightColor != null && borderRightWidth > 0) {

                g2.setColor(borderRightColor);
                g2.setStroke(new BasicStroke(borderRightWidth));
                Shape right = createSide(centerX, centerY, width, height, rxTR, ryTR, rxBR, ryBR);
                g2.draw(right);
            }

            g2.rotate(Math.PI / 2, centerX, centerY);

            BorderStyle bB = s.borderBottom();
            Color borderBottomColor = bB.color();
            int borderBottomWidth = bB.width().getSize(marginBottom, 0);

            // Draw the bottom side.
            if (borderBottomColor != null && borderBottomWidth > 0) {

                g2.setColor(borderBottomColor);
                g2.setStroke(new BasicStroke(borderBottomWidth));
                Shape bottom = createSide(centerX, centerY, height, width, ryBR, rxBR, ryBL, rxBL);
                g2.draw(bottom);
            }

            g2.rotate(Math.PI / 2, centerX, centerY);

            BorderStyle bL = s.borderLeft();
            Color borderLeftColor = bL.color();
            int borderLeftWidth = bL.width().getSize(marginLeft, 0);

            // Draw the left side.
            if (borderLeftColor != null && borderLeftWidth > 0) {

                g2.setColor(borderLeftColor);
                g2.setStroke(new BasicStroke(borderLeftWidth));
                Shape left = createSide(centerX, centerY, width, height, rxBL, ryBL, rxTL, ryTL);
                g2.draw(left);
            }

            g2.rotate(Math.PI / 2, centerX, centerY);

            BorderStyle bT = s.borderTop();
            Color borderTopColor = bT.color();
            int borderTopWidth = bT.width().getSize(marginTop, 0);

            // Draw the top side.
            if (borderTopColor != null && borderTopWidth > 0) {

                g2.setColor(borderTopColor);
                g2.setStroke(new BasicStroke(borderTopWidth));
                Shape top = createSide(centerX, centerY, height, width, ryTL, rxTL, ryTR, rxTR);
                g2.draw(top);
            }

            // End the border.
            g2.dispose();

            // Draw the content after the border and the remaining half of the padding.
            int contentX = borderLeft / 2 + paddingLeft;
            int contentY = borderTop / 2 + paddingTop;
            g2d.drawImage(content, contentX, contentY, null);

            g2d.dispose();
        }

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

        protected final void drawText(Graphics2D g2d, String text, Color color, int x, int y, int width, int height) {

            g2d.setFont(font);
            g2d.setColor(color);
            if (!textWrapping) {

                g2d.drawString(text, x, y);
                return;
            }

            FontMetrics fm = g2d.getFontMetrics();

            ArrayList<String> lines = wrapText(text, fm, width - x);

            for (int i = 0; i < lines.size(); i++) {

                g2d.drawString(lines.get(i), x, y + i * lineHeight);
            }
        }

        public ArrayList<String> wrapText(String text, FontMetrics fm, int maxWidth) {

            ArrayList<String> lines = new ArrayList<>();
            StringBuilder line = new StringBuilder();

            for (String word : text.split(" ")) {

                int lineWidth = fm.stringWidth(line + word + " ");
                if (lineWidth > maxWidth && line.length() > 0) {

                    lines.add(line.toString());
                    line = new StringBuilder(word + " ");
                } else {

                    line.append(word).append(" ");
                }
            }
            lines.add(line.toString());
            return lines;
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
