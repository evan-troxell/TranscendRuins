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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
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
import com.transcendruins.assets.interfaces.InterfaceAttributes.ButtonComponentSchema;
import com.transcendruins.assets.interfaces.InterfaceAttributes.ComponentActionSchema;
import com.transcendruins.assets.interfaces.InterfaceAttributes.ComponentSchema;
import com.transcendruins.assets.interfaces.InterfaceAttributes.InterfaceComponentSchema;
import com.transcendruins.assets.interfaces.InterfaceAttributes.StringComponentSchema;
import com.transcendruins.assets.interfaces.InterfaceAttributes.TextComponentSchema;
import com.transcendruins.assets.interfaces.InterfaceAttributes.TextureComponentSchema;
import com.transcendruins.assets.scripts.TRScript;
import com.transcendruins.resources.styles.ComponentProperties;
import com.transcendruins.resources.styles.Style;
import com.transcendruins.resources.styles.Style.BorderStyle;
import com.transcendruins.resources.styles.Style.Overflow;
import com.transcendruins.resources.styles.Style.OverflowWrap;
import com.transcendruins.resources.styles.Style.SizeDimensions;
import com.transcendruins.resources.styles.Style.TextOverflow;
import com.transcendruins.resources.styles.Style.TextureSize;
import com.transcendruins.resources.styles.Style.WhiteSpace;
import com.transcendruins.resources.styles.StyleSet;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableSet;

/**
 * <code>InterfaceInstance</code>: A class representing a generated interface
 * instance.
 */
public final class InterfaceInstance extends AssetInstance implements UIComponent {

    /**
     * <code>ComponentInstance</code>: The parent component of this
     * <code>InterfaceInstance</code> instance. This is the parent which will be
     * used when generating the body content.
     */
    private final ComponentInstance componentParent;

    /**
     * <code>StyleSet</code>: The style set of this <code>InterfaceInstance</code>
     * instance.
     */
    private StyleSet styles;

    /**
     * <code>ComponentInstance</code>: The content body of this
     * <code>InterfaceInstance</code> instance.
     */
    private ComponentInstance body;

    /**
     * Creates a new instance of the <code>InterfaceInstance</code> class.
     * 
     * @param assetContext <code>AssetContext</code>: The context used to generate
     *                     this <code>InterfaceInstance</code> instance.
     * @param key          <code>Object</code>: The instantiation key, which is
     *                     required to match <code>AssetType.KEY</code>.
     */
    public InterfaceInstance(AssetContext assetContext, Object key) {

        super(assetContext, key);
        InterfaceContext context = (InterfaceContext) assetContext;

        componentParent = context.getComponentParent();
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

        body = calculateAttribute(attributes.getBody(), schema -> createComponent(schema, componentParent), body);
    }

    @Override
    protected void onUpdate(double time) {

    }

    /**
     * Creates a new instance of the <code>ComponentInstance</code> class.
     * 
     * @param schema <code>ComponentSchema</code>: The schema to create the new
     *               <code>ComponentInstance</code> instance from.
     * @param parent <code>ComponentInstance</code>: The parent component to the new
     *               <code>ComponentInstance</code> instance.
     * @return <code>ComponentInstance</code>: The resulting loot instance.
     * @throws LoggedException Thrown if any exception is raised while creating the
     *                         new loot instance.
     */
    public ComponentInstance createComponent(ComponentSchema schema, ComponentInstance parent) {

        // TODO: Add rest of UI component types
        return switch (schema) {

        case TextComponentSchema labelSchema -> new TextComponentInstance(labelSchema, parent);

        case TextureComponentSchema labelSchema -> new TextureComponentInstance(labelSchema, parent);

        case ButtonComponentSchema buttonSchema -> new ButtonComponentInstance(buttonSchema, parent);

        // case InputComponentSchema inputSchema -> new
        // InputComponentInstance(inputSchema, parent);

        // case DropdownComponentSchema dropdownSchema -> new
        // DropdownComponentInstance(dropdownSchema, parent);

        // case SelectComponentSchema selectSchema -> new
        // SelectComponentInstance(selectSchema, parent);

        // case ListComponentSchema listSchema -> new ListComponentInstance(listSchema,
        // parent);

        // case ContainerComponentSchema panelSchema -> new
        // ContainerComponentInstance(panelSchema, parent);

        case InterfaceComponentSchema interfaceSchema -> new InterfaceComponentInstance(interfaceSchema, parent);

        default -> null;
        };
    }

    /**
     * <code>ComponentInstance</code>: A class representing an instance of a visual
     * component.
     */
    public abstract class ComponentInstance implements UIComponent {

        private final ComponentInstance parent;

        public final boolean hasParent() {

            return parent != null;
        }

        private final ArrayList<ComponentInstance> children = new ArrayList<>();

        protected final void addChild(ComponentSchema schema) {

            ComponentInstance child = createComponent(schema, this);
            children.add(child);
        }

        @Override
        public List<UIComponent> getChildren() {

            return new ArrayList<>(children);
        }

        private final String type;

        private final String id;

        private final Style style;

        public final Style getStyle(Style parentStyle) {

            ComponentProperties properties = getProperties();

            // The base styles should be supplemented by packs.
            ArrayList<Style> evaluated = new ArrayList<>(getWorld().getStyle().getStyle(properties));

            // The next level should be file-specific styles.
            evaluated.addAll(styles.getStyle(properties));

            // The instance styles should be the top.
            evaluated.add(style);

            return Style.createStyle(evaluated, parentStyle);
        }

        private final ImmutableSet<String> classes;

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

        private TRScript value;

        protected final void setValue(TRScript value) {

            this.value = value;
        }

        @Override
        public final TRScript getValue() {

            return value;
        }

        private int x, y, width, height, borderLeft, borderRight, borderTop, borderBottom, marginLeft, marginRight,
                marginTop, marginBottom, rxTL, ryTL, rxTR, ryTR, rxBL, ryBL, rxBR, ryBR, paddingLeft, paddingRight,
                paddingTop, paddingBottom, fontSize, lineHeight;

        private Boolean eventPropagation;

        private final boolean defaultEventPropagation;

        public final int getWidth() {

            return width;
        }

        public final int getHeight() {

            return height;
        }

        public final int getFontSize() {

            return fontSize;
        }

        @Override
        public final int getX() {

            return x + marginLeft;
        }

        @Override
        public final int getY() {

            return y + marginTop;
        }

        @Override
        public final Dimension getSize() {

            return new Dimension(borderLeft + paddingLeft + width + paddingRight + borderRight,
                    borderTop + paddingTop + height + paddingBottom + borderBottom);
        }

        private int scrollX = 0;
        private int maxScrollX = 0;
        private int scrollY = 0;
        private int maxScrollY = 0;

        /**
         * Resizes this <code>ComponentInstance</code> to a new set of dimensions. This
         * should be used when resizing a component to account for the size of children.
         * 
         * @param width  <code>int</code>: The width of the content of this
         *               <code>ComponentInstance</code>, which is equal to the width
         *               minus the horizontal padding.
         * @param height <code>int</code>: The height of the content ofthis
         *               <code>ComponentInstance</code>, which is equal to the height
         *               minus the vertical padding.
         */
        protected final void resize(int width, int height) {

            this.width = width;
            this.height = height;
        }

        /**
         * Retrieves the total width of this <code>ComponentInstance</code> instance.
         * 
         * @return <code>int</code>: The width of this <code>ComponentInstance</code>
         *         instance plus the horizontal margins and the horizontal border.
         */
        protected final int getTotalWidth() {

            return width + paddingLeft + paddingRight + marginLeft + marginRight + (borderLeft + borderRight);
        }

        /**
         * Retrieves the total height of this <code>ComponentInstance</code> instance.
         * 
         * @return <code>int</code>: The height of this <code>ComponentInstance</code>
         *         instance plus the vertical margins and the vertical border.
         */
        protected final int getTotalHeight() {

            return height + paddingTop + paddingBottom + marginTop + marginBottom + (borderTop + borderBottom);
        }

        private Font font = null;
        private FontMetrics fm = null;

        private WhiteSpace whiteSpace = null;
        private OverflowWrap overflowWrap = null;
        private TextOverflow textOverflow = null;
        private Overflow overflowX = null;
        private Overflow overflowY = null;

        public ComponentInstance(ComponentSchema schema, ComponentInstance parent, boolean defaultEventPropagation) {

            this.parent = parent;

            type = schema.getType();
            id = schema.getId();
            classes = schema.getClasses();
            style = schema.getStyle();
            value = schema.getValue();

            this.defaultEventPropagation = defaultEventPropagation;

            for (ComponentSchema child : schema.getChildren()) {

                addChild(child);
            }
        }

        /**
         * Calculates the initial size of this component based on the measurements of
         * the parent. This is the first step in sizing this
         * <code>ComponentInstance</code> instance, and an additional step in resizing
         * may occur.
         * 
         * @param style          <code>Style</code>: The style to measure using.
         * @param parentWidth    <code>int</code>: The width of the internal space in
         *                       the parent of this <code>InterfaceInstance</code>
         *                       instance. This value should be equal to the width of
         *                       the parent minus the horizontal padding of the parent.
         * @param parentHeight   <code>int</code>: The height of the internal space in
         *                       the parent of this <code>InterfaceInstance</code>
         *                       instance. This value should be equal to the height of
         *                       the parent minus the vertical padding of the parent.
         * @param parentFontSize <code>int</code>: The font size of the parent of this
         *                       <code>ComponentInstance</code> instance.
         */
        private void measure(Style style, int parentWidth, int parentHeight, int parentFontSize) {

            // Update the coordinates.
            x = style.x().getSize(parentWidth, 0);
            y = style.y().getSize(parentHeight, 0);

            // Calculate the width.
            int minWidth = style.minWidth().getSize(parentWidth, 0);
            width = style.width().getSize(parentHeight, minWidth);

            // Calculate the horizontal margins.
            marginLeft = style.marginLeft().getSize(parentWidth, 0);
            marginRight = style.marginRight().getSize(parentWidth, 0);

            // Calculate the horizontal borders.
            borderLeft = style.borderLeftWidth().getSize(marginLeft, 0);
            borderRight = style.borderRightWidth().getSize(marginRight, 0);

            // Calculate the horizontal padding.
            paddingLeft = style.paddingLeft().getSize(parentWidth, 0);
            paddingRight = style.paddingRight().getSize(parentWidth, 0);

            // Calculate the height.
            int minHeight = style.minHeight().getSize(parentHeight, 0);
            height = style.height().getSize(parentHeight, minHeight);

            // Calculate the vertical margins.
            marginTop = style.marginTop().getSize(parentHeight, 0);
            marginBottom = style.marginBottom().getSize(parentHeight, 0);

            // Calculate the vertical borders.
            borderTop = style.borderTopWidth().getSize(marginTop, 0);
            borderBottom = style.borderBottomWidth().getSize(marginBottom, 0);

            // Calculate the vertical padding.
            paddingTop = style.paddingTop().getSize(parentHeight, 0);
            paddingBottom = style.paddingBottom().getSize(parentHeight, 0);

            int boxWidth = width + paddingLeft + paddingRight + (borderLeft + borderRight) / 2;
            int boxHeight = height + paddingTop + paddingBottom + (borderTop + borderBottom) / 2;

            SizeDimensions rTL = style.rTL();
            rxTL = rTL.width().getSize(boxWidth, 0);
            ryTL = rTL.height().getSize(boxHeight, 0);

            SizeDimensions rTR = style.rTR();
            rxTR = rTR.width().getSize(boxWidth, 0);
            ryTR = rTR.height().getSize(boxHeight, 0);

            SizeDimensions rBL = style.rBL();
            rxBL = rBL.width().getSize(boxWidth, 0);
            ryBL = rBL.height().getSize(boxHeight, 0);

            SizeDimensions rBR = style.rBR();
            rxBR = rBR.width().getSize(boxWidth, 0);
            ryBR = rBR.height().getSize(boxHeight, 0);

            // If the top radii exceed the width, adjust them so they fit.
            if (rxTR + rxTL > boxWidth) {

                double partial = (double) boxWidth / (rxTR + rxTL);

                rxTR *= partial;
                rxTL *= partial;
            }

            // If the bottom radii exceed the width, adjust them so they fit.
            if (rxBR + rxBL > boxWidth) {

                double partial = (double) boxWidth / (rxBR + rxBL);

                rxBR *= partial;
                rxBL *= partial;
            }

            // If the left radii exceed the height, adjust them so they fit.
            if (ryBL + ryTL > boxHeight) {

                double partial = (double) boxHeight / (ryBL + ryTL);

                ryBL *= partial;
                ryTL *= partial;
            }

            // If the right radii exceed the height, adjust them so they fit.
            if (ryBR + ryTR > boxHeight) {

                double partial = (double) boxHeight / (ryBR + ryTR);

                ryBR *= partial;
                ryTR *= partial;
            }

            // The font size should be based on the parent font size and have a minimum of
            // 8px.
            fontSize = style.fontSize().getSize(parentFontSize, 8);

            // The line height should be based on the font size and be at least as large as
            // the font.
            lineHeight = style.lineHeight().getSize(fontSize, fontSize);

            // Create the font.
            font = new Font(style.fontFamily(), style.fontStyle() | style.fontWeight(), fontSize);
            BufferedImage fontImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = fontImage.createGraphics();
            fm = g2d.getFontMetrics(font);

            whiteSpace = style.whiteSpace();
            overflowWrap = style.overflowWrap();
            textOverflow = style.textOverflow();
            overflowX = style.overflowX();
            overflowY = style.overflowY();

            eventPropagation = style.eventPropagation();
        }

        private BufferedImage render;

        public final BufferedImage getRender() {

            return render;
        }

        public static final record ImageClip(int x, int y, BufferedImage image) {
        }

        public abstract Dimension calculateContentSize(Style style, List<ImageClip> children);

        /**
         * Generates the content of this <code>ComponentInstance</code> instance.
         * 
         * @param g2d      <code>Graphics2D</code>: The graphics to render using.
         * @param style    <code>Style</code>: The style to draw using.
         * @param children <code>List&lt;ImageClip&gt;</code>: The drawn children of
         *                 this <code>ComponentInstance</code> instance.
         */
        public abstract void createContent(Graphics2D g2d, Style style, List<ImageClip> children);

        @Override
        public final BufferedImage render(int parentWidth, int parentHeight, int parentFontSize, Style parentStyle) {

            // Nullify the render so it cannot be reused by accident.
            render = null;

            // Generate the current style.
            Style s = getStyle(parentStyle);

            // Calculate the initial size.
            measure(s, parentWidth, parentHeight, parentFontSize);

            // Draw the children.
            ArrayList<ImageClip> childrenRenders = new ArrayList<>();
            for (ComponentInstance child : children) {

                BufferedImage childRender = child.render(width, height, fontSize, s);
                childrenRenders.add(new ImageClip(child.x, child.y, childRender));
            }

            // Calculate the content size.
            Dimension contentSize = calculateContentSize(s, childrenRenders);

            // If the content width/height is larger than the render width/height and it is
            // automatically sized, expand it.
            if (contentSize.width > width && s.width() == Style.Size.AUTO) {

                width = contentSize.width;
            }

            if (contentSize.height > height && s.height() == Style.Size.AUTO) {

                height = contentSize.height;
            }

            // The scroll X/Y cannot go beyond the content width/height.
            maxScrollX = Math.max(contentSize.width - width, 0);
            maxScrollY = Math.max(contentSize.height - height, 0);

            scrollX = Math.clamp(scrollX, 0, maxScrollX);
            scrollY = Math.clamp(scrollY, 0, maxScrollY);

            BufferedImage content = new BufferedImage(contentSize.width, contentSize.height,
                    BufferedImage.TYPE_INT_ARGB);

            // Create all internal content and perform resizing.
            Graphics2D contentG2d = content.createGraphics();
            createContent(contentG2d, s, childrenRenders);
            contentG2d.dispose();

            // Create the component border and background.
            BufferedImage component = new BufferedImage(getTotalWidth(), getTotalHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = component.createGraphics();

            // Adjust the origin to the top left corner of the component.
            // If the left or top border widths are odd numbers, this will consume the
            // larger integer half.
            g2d.translate(marginLeft + Math.ceil(borderLeft / 2.0), marginTop + Math.ceil(borderTop / 2.0));

            // Creates the content bounds.
            Shape contentBounds = createBounds(width + paddingLeft + paddingRight + (borderLeft + borderRight) / 2,
                    height + paddingTop + paddingBottom + (borderTop + borderBottom) / 2, rxTR, ryTR, rxTL, ryTL, rxBL,
                    ryBL, rxBR, ryBR);

            // Process the background color and image.
            Color backgroundColor = s.backgroundColor();
            String backgroundTexture = s.backgroundTexture();
            TextureSize backgroundSize = s.backgroundSize();
            drawBackground(g2d, backgroundColor, backgroundTexture, backgroundSize, contentBounds);

            // Start the border.
            Graphics2D g2 = (Graphics2D) g2d.create();

            // Find the exact center of the component.
            int centerX = (width + borderLeft) / 2 + paddingLeft;
            int centerY = (height + borderTop) / 2 + paddingTop;
            int internalWidth = width + paddingLeft + paddingRight + (borderLeft + borderRight) / 2;
            int internalHeight = height + paddingTop + paddingBottom + (borderTop + borderBottom) / 2;

            BorderStyle borderRightStyle = s.borderRightStyle();
            Color borderRightColor = s.borderRightColor();

            // Draw the right side.
            if (borderRightStyle != BorderStyle.NONE && borderRight > 0) {

                g2.setColor(borderRightColor);
                g2.setStroke(new BasicStroke(borderRight));
                Shape right = createSide(centerX, centerY, internalWidth, internalHeight, rxTR, ryTR, rxBR, ryBR);
                g2.draw(right);
            }

            g2.rotate(Math.PI / 2, centerX, centerY);

            BorderStyle borderBottomStyle = s.borderBottomStyle();
            Color borderBottomColor = s.borderBottomColor();

            // Draw the bottom side.
            if (borderBottomStyle != BorderStyle.NONE && borderBottom > 0) {

                g2.setColor(borderBottomColor);
                g2.setStroke(new BasicStroke(borderBottom));
                Shape bottom = createSide(centerX, centerY, internalHeight, internalWidth, ryBR, rxBR, ryBL, rxBL);
                g2.draw(bottom);
            }

            g2.rotate(Math.PI / 2, centerX, centerY);

            BorderStyle borderLeftStyle = s.borderLeftStyle();
            Color borderLeftColor = s.borderLeftColor();

            // Draw the left side.
            if (borderLeftStyle != BorderStyle.NONE && borderLeft > 0) {

                g2.setColor(borderLeftColor);
                g2.setStroke(new BasicStroke(borderLeft));
                Shape left = createSide(centerX, centerY, internalWidth, internalHeight, rxBL, ryBL, rxTL, ryTL);
                g2.draw(left);
            }

            g2.rotate(Math.PI / 2, centerX, centerY);

            BorderStyle borderTopStyle = s.borderTopStyle();
            Color borderTopColor = s.borderTopColor();

            // Draw the top side.
            if (borderTopStyle != BorderStyle.NONE && borderTop > 0) {

                g2.setColor(borderTopColor);
                g2.setStroke(new BasicStroke(borderTop));
                Shape top = createSide(centerX, centerY, internalHeight, internalWidth, ryTL, rxTL, ryTR, rxTR);
                g2.draw(top);
            }

            // End the border.
            g2.dispose();

            // Draw the content after the border and the remaining half of the padding.
            int contentX = borderLeft / 2 + paddingLeft;
            int contentY = borderTop / 2 + paddingTop;

            // Ensure the shape is self-contained
            g2d.clip(contentBounds);
            g2d.drawImage(content, contentX - scrollX, contentY - scrollY, null);

            g2d.dispose();

            return component;
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

        protected final void drawBackground(Graphics2D g2d, Color color, String texture, TextureSize size,
                Shape bounds) {

            // Clip out the content bounds just for the background.
            g2d = (Graphics2D) g2d.create();
            g2d.clip(bounds);

            if (color != Style.TRANSPARENT) {

                g2d.setColor(color);
                g2d.fill(bounds);
            }

            // Process the background texture, if it has one.
            if (texture != null) {

                drawTexture(g2d, texture, 0, 0, size);
                g2d.dispose();
            }
        }

        protected final void drawImage(Graphics2D g2d, BufferedImage image, int x, int y) {

            g2d.drawImage(image, x, y, null);
        }

        protected final void drawTexture(Graphics2D g2d, String texture, int x, int y, TextureSize size) {

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

        protected final void drawText(Graphics2D g2d, String text, int x, int y, Color color) {

            g2d.setColor(color);
            g2d.setFont(font);

            if (whiteSpace == WhiteSpace.NOWRAP) {

                text = textOverflow(fm, text, width - x);
                g2d.drawString(text, x, y);
                return;
            }

            ArrayList<String> lines = wrapText(text, fm, width - x);

            for (int i = 0; i < lines.size(); i++) {

                text = textOverflow(fm, lines.get(i), width - x);
                g2d.drawString(text, x, y + i * lineHeight);
            }
        }

        protected final Dimension calculateTextureSize(String texture, int x, int y, TextureSize size) {

            ImageIcon icon = getTexture(texture);
            int textureWidth = icon.getIconWidth();
            int textureHeight = icon.getIconHeight();

            if (textureWidth == 0 || textureHeight == 0) {

                return new Dimension(0, 0);
            }

            int backgroundWidth = size.getWidth(textureWidth, textureHeight, width, height);
            int backgroundHeight = size.getHeight(textureWidth, textureHeight, width, height);

            return new Dimension(backgroundWidth, backgroundHeight);
        }

        protected final Dimension calculateTextSize(String text, int x, int y) {

            if (whiteSpace == WhiteSpace.NOWRAP) {

                text = textOverflow(fm, text, width - x);
                int lineWidth = fm.stringWidth(text);
                return new Dimension(lineWidth, lineHeight);
            }

            ArrayList<String> lines = wrapText(text, fm, width - x);
            int lineWidth = 0;

            for (int i = 0; i < lines.size(); i++) {

                text = textOverflow(fm, lines.get(i), width - x);
                lineWidth = Math.max(lineWidth, fm.stringWidth(text));
            }
            return new Dimension(lineWidth, lineHeight * lines.size());
        }

        private ArrayList<String> wrapText(String text, FontMetrics fm, int maxWidth) {

            ArrayList<String> lines = new ArrayList<>();
            String line = "";

            // Handle the case when the overflow wrap breaks between words and hyphens.
            if (overflowWrap == OverflowWrap.BREAK_WORD) {

                int lineStart = 0;

                for (int i = 0; i < text.length(); i++) {

                    line = text.substring(lineStart, i + 1);

                    int lineLength = fm.stringWidth(line);

                    // If the line is too long and can be broken, split into 2 new lines.
                    if (lineLength > maxWidth) {

                        // Cap the old line off with the space/hyphen.
                        String prevLine = text.substring(lineStart, i);
                        lines.add(prevLine);

                        // Start the new line.
                        lineStart = i;
                        line = text.substring(lineStart, i + 1);
                    }
                }
            } else {

                int lineStart = 0;
                int prevBreak = -1;
                for (int i = 0; i < text.length(); i++) {

                    line = text.substring(lineStart, i + 1);

                    char c = text.charAt(i);
                    if (c == ' ' || c == '-') {

                        prevBreak = i;
                    }

                    // The only time a line can break is when a breaking character has been found.
                    if (prevBreak == -1) {

                        continue;
                    }

                    int lineLength = fm.stringWidth(line);

                    // If the line is too long and can be broken, split into 2 new lines.
                    if (lineLength > maxWidth) {

                        // Cap the old line off with the space/hyphen.
                        String prevLine = text.substring(lineStart, prevBreak + 1);
                        lines.add(prevLine);

                        // Start the new line.
                        lineStart = prevBreak + 1;
                        prevBreak = -1;
                        line = text.substring(lineStart, i + 1);
                    }
                }
            }

            // If the last line is not empty, add it as well.
            if (!line.isEmpty()) {

                lines.add(line);
            }

            return lines;
        }

        private String textOverflow(FontMetrics fm, String line, int maxWidth) {

            if (textOverflow != TextOverflow.CLIP && fm.stringWidth(line) > maxWidth) {

                while (line.length() > 1) {

                    line = line.substring(0, line.length() - 1);
                    if (fm.stringWidth(line) <= maxWidth) {

                        line += textOverflow.overflow();
                        break;
                    }
                }
            }

            return line;
        }

        @Override
        public final void exit(int mouseX, int mouseY) {

            removeState("hover");
        }

        @Override
        public final void hover(int mouseX, int mouseY) {

            addState("hover");
        }

        @Override
        public final void release(int mouseX, int mouseY) {

            removeState("active");
        }

        @Override
        public final boolean press(int mouseX, int mouseY) {

            addState("active");

            if (eventPropagation == null) {

                return defaultEventPropagation;
            }

            return eventPropagation;
        }

        @Override
        public final boolean onClick(int mouseX, int mouseY, TRScript value) {

            onComponentClick(mouseX, mouseY, value);

            if (eventPropagation == null) {

                return defaultEventPropagation;
            }

            return eventPropagation;
        }

        public abstract void onComponentClick(int mouseX, int mouesY, TRScript value);

        @Override
        public final void scroll(int mouseX, int mouseY, Point displacement) {

            // Calculate the adjustment to scroll.
            int newScrollX = overflowX == Overflow.CLIP ? scrollX : Math.clamp(scrollX + displacement.x, 0, maxScrollX);
            int newScrollY = overflowY == Overflow.CLIP ? scrollY : Math.clamp(scrollY + displacement.y, 0, maxScrollX);

            // Remove the adjustment from the scroll
            displacement.translate(scrollX - newScrollX, scrollY - newScrollY);

            scrollX = newScrollX;
            scrollY = newScrollY;
        }
    }

    public final class StringComponentInstance extends ComponentInstance {

        private final String string;

        public StringComponentInstance(StringComponentSchema schema, ComponentInstance parent) {

            super(schema, parent, true);
            string = schema.getString();
        }

        @Override
        public final Dimension calculateContentSize(Style style, List<ImageClip> children) {

            Dimension textSize = calculateTextSize(string, 0, 0);

            return new Dimension(textSize.width, textSize.height);
        }

        @Override
        public void createContent(Graphics2D g2d, Style style, List<ImageClip> children) {

            drawText(g2d, string, 0, 0, style.color());
        }

        @Override
        public final void onComponentClick(int mouseX, int mouseY, TRScript value) {

        }
    }

    public final class TextComponentInstance extends ComponentInstance {

        public TextComponentInstance(TextComponentSchema schema, ComponentInstance parent) {

            super(schema, parent, true);
        }

        @Override
        public final Dimension calculateContentSize(Style style, List<ImageClip> children) {

            ImageClip textSize = children.getFirst();
            BufferedImage image = textSize.image();

            return new Dimension(textSize.x() + image.getWidth(), textSize.y() + image.getHeight());
        }

        @Override
        public final void createContent(Graphics2D g2d, Style style, List<ImageClip> children) {

            ImageClip textRender = children.getFirst();
            BufferedImage text = textRender.image();

            drawImage(g2d, text, textRender.x(), textRender.y());
        }

        @Override
        public final void onComponentClick(int mouseX, int mouseY, TRScript value) {

        }
    }

    public final class TextureComponentInstance extends ComponentInstance {

        private final String texture;

        public TextureComponentInstance(TextureComponentSchema schema, ComponentInstance parent) {

            super(schema, parent, true);

            texture = schema.getTexture();
        }

        @Override
        public final Dimension calculateContentSize(Style style, List<ImageClip> children) {

            Dimension textureSize = calculateTextureSize(texture, 0, 0, style.textureFit());

            return new Dimension(textureSize.width, textureSize.height);
        }

        @Override
        public final void createContent(Graphics2D g2d, Style style, List<ImageClip> children) {

            drawTexture(g2d, texture, 0, 0, style.textureFit());
        }

        @Override
        public final void onComponentClick(int mouseX, int mouseY, TRScript value) {

        }
    }

    public final class ButtonComponentInstance extends ComponentInstance {

        public ButtonComponentInstance(ButtonComponentSchema schema, ComponentInstance parent) {

            super(schema, parent, false);
        }

        @Override
        public Dimension calculateContentSize(Style style, List<ImageClip> children) {

            // Buttons are not required to have a child.
            if (children.isEmpty()) {

                return new Dimension();
            }

            ImageClip child = children.getFirst();
            BufferedImage childRender = child.image();
            return new Dimension(child.x() + childRender.getWidth(), child.y() + childRender.getHeight());
        }

        @Override
        public void createContent(Graphics2D g2d, Style style, List<ImageClip> children) {

            // Buttons are not required to have a child.
            if (children.isEmpty()) {

                return;
            }

            ImageClip child = children.getFirst();
            BufferedImage childRender = child.image();
            drawImage(g2d, childRender, child.x(), child.y());
        }

        @Override
        public void onComponentClick(int mouseX, int mouesY, TRScript value) {

            // TODO add button actions
        }
    }

    public final class InterfaceComponentInstance extends ComponentInstance {

        private final InterfaceInstance asset;

        private ImageClip bodyRender;

        public InterfaceComponentInstance(InterfaceComponentSchema schema, ComponentInstance parent) {

            super(schema, parent, true);

            AssetPresets presets = schema.getPresets();
            InterfaceContext context = new InterfaceContext(presets, getWorld(), InterfaceInstance.this, this);

            asset = (InterfaceInstance) INTERFACE.createAsset(context);
        }

        @Override
        public final List<UIComponent> getChildren() {

            return List.of(asset);
        }

        @Override
        public final Dimension calculateContentSize(Style style, List<ImageClip> children) {

            int width = getTotalWidth();
            int height = getTotalHeight();
            int fontSize = getFontSize();
            BufferedImage image = asset.render(width, height, fontSize, style);

            int x = body.x;
            int y = body.y;
            bodyRender = new ImageClip(x, y, image);

            return new Dimension(x + image.getWidth(), y + image.getHeight());
        }

        @Override
        public final void createContent(Graphics2D g2d, Style style, List<ImageClip> children) {

            BufferedImage body = bodyRender.image();

            drawImage(g2d, body, bodyRender.x(), bodyRender.y());
        }

        @Override
        public final void onComponentClick(int mouseX, int mouseY, TRScript value) {

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
        public final boolean passes(AssetInstance asset) {

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

    @Override
    public final void exit(int mouseX, int mouseY) {
    }

    @Override
    public final void hover(int mouseX, int mouseY) {

        // Nothing should happen when the mouse hovers the interface wrapper.
    }

    @Override
    public final void scroll(int mouseX, int mouseY, Point displacement) {

        // Nothing should happen when the mouse scrolls the interface wrapper.
    }

    @Override
    public final void release(int mouseX, int mouseY) {
    }

    @Override
    public final boolean press(int mouseX, int mouseY) {

        return true;
    }

    @Override
    public final boolean onClick(int mouseX, int mouseY, TRScript value) {

        return true;
    }

    @Override
    public final List<UIComponent> getChildren() {

        return List.of(body);
    }

    @Override
    public final TRScript getValue() {

        return TRScript.EMPTY;
    }

    @Override
    public final int getX() {

        return 0;
    }

    @Override
    public final int getY() {

        return 0;
    }

    @Override
    public final Dimension getSize() {

        // Create the size such that it starts in the top left corner of the parent
        // element and ends at the boundaries of the body.
        return new Dimension(body.getX() + body.getTotalWidth(), body.getY() + body.getTotalHeight());
    }

    @Override
    public final BufferedImage render(int parentWidth, int parentHeight, int parentFontSize, Style parentStyle) {

        // This interface should be treated as essentially covering the entire parent.
        return body.render(parentWidth, parentHeight, parentFontSize, parentStyle);
    }
}
