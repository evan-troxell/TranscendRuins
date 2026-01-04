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
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import javax.swing.ImageIcon;

import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.assets.AssetInstance;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.catalogue.locations.GlobalLocationInstance;
import com.transcendruins.assets.interfaces.InterfaceAttributes.ButtonComponentSchema;
import com.transcendruins.assets.interfaces.InterfaceAttributes.ComponentSchema;
import com.transcendruins.assets.interfaces.InterfaceAttributes.ContainerComponentSchema;
import com.transcendruins.assets.interfaces.InterfaceAttributes.GlobalMapComponentSchema;
import com.transcendruins.assets.interfaces.InterfaceAttributes.IconComponentSchema;
import com.transcendruins.assets.interfaces.InterfaceAttributes.IconType;
import com.transcendruins.assets.interfaces.InterfaceAttributes.InterfaceComponentSchema;
import com.transcendruins.assets.interfaces.InterfaceAttributes.InventoryComponentSchema;
import com.transcendruins.assets.interfaces.InterfaceAttributes.InventoryComponentSchema.GridDisplay;
import com.transcendruins.assets.interfaces.InterfaceAttributes.InventoryComponentSchema.NamedDisplay;
import com.transcendruins.assets.interfaces.InterfaceAttributes.InventoryDisplayComponentSchema;
import com.transcendruins.assets.interfaces.InterfaceAttributes.ListComponentSchema;
import com.transcendruins.assets.interfaces.InterfaceAttributes.LocationDisplayComponentSchema;
import com.transcendruins.assets.interfaces.InterfaceAttributes.RotateComponentSchema;
import com.transcendruins.assets.interfaces.InterfaceAttributes.TextComponentSchema;
import com.transcendruins.assets.interfaces.InterfaceInstance.GlobalMapComponentInstance.LocationDisplay;
import com.transcendruins.assets.interfaces.map.LocationRender;
import com.transcendruins.assets.interfaces.map.TerrainRender;
import com.transcendruins.assets.modelassets.items.ItemInstance;
import com.transcendruins.assets.modelassets.primaryassets.inventory.InventoryInstance;
import com.transcendruins.assets.modelassets.primaryassets.inventory.InventorySlotInstance;
import com.transcendruins.assets.scripts.TRScript;
import com.transcendruins.resources.styles.ComponentProperties;
import com.transcendruins.resources.styles.Style;
import com.transcendruins.resources.styles.Style.BorderStyle;
import com.transcendruins.resources.styles.Style.Display;
import com.transcendruins.resources.styles.Style.IconSize;
import com.transcendruins.resources.styles.Style.Overflow;
import com.transcendruins.resources.styles.Style.OverflowWrap;
import com.transcendruins.resources.styles.Style.Size;
import com.transcendruins.resources.styles.Style.SizeDimensions;
import com.transcendruins.resources.styles.Style.TextAlign;
import com.transcendruins.resources.styles.Style.TextOverflow;
import com.transcendruins.resources.styles.Style.WhiteSpace;
import com.transcendruins.resources.styles.StyleSet;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.files.DataConstants;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.immutable.ImmutableSet;
import com.transcendruins.utilities.random.DeterministicRandom;
import com.transcendruins.world.Player;
import com.transcendruins.world.World;

/**
 * <code>InterfaceInstance</code>: A class representing a generated interface
 * instance.
 */
public final class InterfaceInstance extends AssetInstance implements UIComponent {

    /**
     * <code>long</code>: The id of the player which this
     * <code>InterfaceInstance</code> instance is associated with.
     */
    private final long playerId;

    public final long getPlayerId() {

        return playerId;
    }

    /**
     * <code>ComponentInstance</code>: The parent component of this
     * <code>InterfaceInstance</code> instance. This is the parent which will be
     * used when generating the body content.
     */
    private final ComponentInstance componentParent;

    private final ImmutableList<Object> componentValues;

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

    private final HashMap<Attributes, ComponentInstance> bodyCache = new HashMap<>();

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

        playerId = context.getPlayerId();
        componentParent = context.getComponentParent();

        componentValues = context.getComponentValues();
    }

    @Override
    public void applyAttributes(Attributes attributeSet) {

        InterfaceAttributes attributes = (InterfaceAttributes) attributeSet;

        // If the attribute set is the base, empty the styles list.
        if (attributes.isBase()) {

            styles = StyleSet.EMPTY;
        }

        // Apply the new styles to the old.
        styles = calculateAttribute(attributes.getStyles(), set -> styles.extend(set), styles);

        DeterministicRandom random = new DeterministicRandom(getRandomId());

        body = calculateAttribute(attributes.getBody(),
                schema -> bodyCache.computeIfAbsent(attributes, _ -> createComponent(schema, componentParent, random)),
                body);
    }

    @Override
    protected void onUpdate(double time) {

        body.updateContent(time);
    }

    @Override
    public void updateContent(double time) {

        onUpdate(time);
    }

    /**
     * Creates a new instance of the <code>ComponentInstance</code> class.
     * 
     * @param schema <code>ComponentSchema</code>: The schema to create the new
     *               <code>ComponentInstance</code> instance from.
     * @param parent <code>ComponentInstance</code>: The parent component to the new
     *               <code>ComponentInstance</code> instance.
     * @param random <code>DeterministicRandom</code>: The random generator used to
     *               calculate random component ids.
     * @return <code>ComponentInstance</code>: The resulting loot instance.
     * @throws LoggedException Thrown if any exception is raised while creating the
     *                         new loot instance.
     */
    public ComponentInstance createComponent(ComponentSchema schema, ComponentInstance parent,
            DeterministicRandom random) {

        // TODO: Add rest of UI component types
        return switch (schema) {

        case TextComponentSchema textSchema -> new TextComponentInstance(textSchema, parent, random);

        case IconComponentSchema iconSchema -> new IconComponentInstance(iconSchema, parent, random);

        case ButtonComponentSchema buttonSchema -> new ButtonComponentInstance(buttonSchema, parent, random);

        // case InputComponentSchema inputSchema -> new
        // InputComponentInstance(inputSchema, parent);

        // case DropdownComponentSchema dropdownSchema -> new
        // DropdownComponentInstance(dropdownSchema, parent);

        // case SelectComponentSchema selectSchema -> new
        // SelectComponentInstance(selectSchema, parent);

        case ContainerComponentSchema containerSchema -> new ContainerComponentInstance(containerSchema, parent,
                random);

        case RotateComponentSchema rotateSchema -> new RotateComponentInstance(rotateSchema, parent, random);

        case ListComponentSchema listSchema -> new ListComponentInstance(listSchema, parent, random);

        case InterfaceComponentSchema interfaceSchema -> new InterfaceComponentInstance(interfaceSchema, parent,
                random);

        case GlobalMapComponentSchema globalMapSchema -> new GlobalMapComponentInstance(globalMapSchema, parent,
                random);

        case LocationDisplayComponentSchema locationDisplaySchema -> new LocationDisplayComponentInstance(
                locationDisplaySchema, parent, random);

        case InventoryDisplayComponentSchema inventoryDisplaySchema -> new InventoryDisplayComponentInstance(
                inventoryDisplaySchema, parent, random);

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

        protected final void addChild(ComponentSchema schema, DeterministicRandom random) {

            ComponentInstance child = createComponent(schema, this, random);
            children.add(child);
        }

        protected final void addChild(ComponentInstance child) {

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
                public final ComponentProperties getParent() {

                    return hasParent() ? parent.getProperties() : null;
                }

                @Override
                public final List<ComponentProperties> getChildren() {

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

        protected int x, y, minWidth, width, minHeight, height, borderLeft, borderRight, borderTop, borderBottom,
                marginLeft, marginRight, marginTop, marginBottom, rxTL, ryTL, rxTR, ryTR, rxBL, ryBL, rxBR, ryBR,
                paddingLeft, paddingRight, paddingTop, paddingBottom, fontSize, lineHeight, gapWidth, gapHeight;

        private SizeDimensions origin;

        protected Display displayMode;

        private String backgroundIconPath = null;
        private ImageIcon backgroundIcon = null;

        private Boolean propagateEvents;

        private final boolean defaultPropagateEvents;

        private Style.TriggerPhase triggerPhase;

        @Override
        public final int getX() {

            return x + marginLeft;
        }

        @Override
        public final int getY() {

            return y + marginTop;
        }

        @Override
        public final int getContentOffsetX() {

            return borderLeft + paddingLeft - scrollX;
        }

        @Override
        public final int getContentOffsetY() {

            return borderTop + paddingTop - scrollY;
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

        private TextAlign textAlign = null;
        private WhiteSpace whiteSpace = null;
        private OverflowWrap overflowWrap = null;
        private TextOverflow textOverflow = null;
        private Overflow overflowX = null;
        private Overflow overflowY = null;

        private final long randomComponentId;

        protected final long getRandomComponentId() {

            return randomComponentId;
        }

        public ComponentInstance(ComponentSchema schema, ComponentInstance parent, boolean defaultPropagateEvents,
                DeterministicRandom random) {

            this.parent = parent;

            type = schema.getType();
            id = schema.getId();
            classes = schema.getClasses();
            style = schema.getStyle();
            value = schema.getValue();

            this.defaultPropagateEvents = defaultPropagateEvents;
            randomComponentId = random.next();

            for (ComponentSchema child : schema.getChildren()) {

                addChild(child, random);
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
            minWidth = style.minWidth().getSize(parentWidth, 0);
            width = style.width().getSize(parentWidth, minWidth);

            // Calculate the horizontal margins.
            marginLeft = style.marginLeft().getSize(parentWidth, 0);
            marginRight = style.marginRight().getSize(parentWidth, 0);

            // Calculate the horizontal borders.
            if (style.borderLeftStyle() == BorderStyle.NONE) {

                borderLeft = 0;
            } else {

                borderLeft = style.borderLeftWidth().getSize(marginLeft, 0);
            }

            if (style.borderRightStyle() == BorderStyle.NONE) {

                borderRight = 0;
            } else {

                borderRight = style.borderRightWidth().getSize(marginRight, 0);
            }

            // Calculate the horizontal padding.
            paddingLeft = style.paddingLeft().getSize(parentWidth, 0);
            paddingRight = style.paddingRight().getSize(parentWidth, 0);

            // Calculate the height.
            minHeight = style.minHeight().getSize(parentHeight, 0);
            height = style.height().getSize(parentHeight, minHeight);

            // Calculate the vertical margins.
            marginTop = style.marginTop().getSize(parentHeight, 0);
            marginBottom = style.marginBottom().getSize(parentHeight, 0);

            // Calculate the vertical borders.
            if (style.borderTopStyle() == BorderStyle.NONE) {

                borderTop = 0;
            } else {

                borderTop = style.borderTopWidth().getSize(marginTop, 0);
            }

            if (style.borderBottomStyle() == BorderStyle.NONE) {

                borderBottom = 0;
            } else {

                borderBottom = style.borderBottomWidth().getSize(marginBottom, 0);
            }

            // Calculate the vertical padding.
            paddingTop = style.paddingTop().getSize(parentHeight, 0);
            paddingBottom = style.paddingBottom().getSize(parentHeight, 0);

            int boxWidth = width + paddingLeft + paddingRight;
            int boxHeight = height + paddingTop + paddingBottom;

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

            // The font size should be based on the parent font size and have a minimum of
            // 8px.
            fontSize = style.fontSize().getSize(parentFontSize, 8);

            // The line height should be based on the font size and be at least as large as
            // the font.
            lineHeight = style.lineHeight().getSize(fontSize, fontSize);

            textAlign = style.textAlign();

            origin = style.origin();

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

            gapWidth = style.gapWidth().getSize(width, 0);
            gapHeight = style.gapHeight().getSize(height, 0);

            propagateEvents = style.propagateEvents();
            displayMode = style.display();

            triggerPhase = style.triggerPhase();
        }

        public static final record ImageClip(int x, int y, BufferedImage image) {
        }

        public abstract Dimension calculateContentSize(Style style, List<Rectangle> children);

        public abstract Dimension rescaleContent(int targetWidth, int targetHeight, Style style,
                List<ComponentInstance> children);

        protected final void fitContent() {

            // If the content width/height is larger than the render width/height and it is
            // automatically sized, expand it.
            if (s.width() == Style.Size.FIT_CONTENT || contentSize.width > width && s.width() == Style.Size.AUTO) {

                width = contentSize.width;
            }

            if (s.height() == Style.Size.FIT_CONTENT || contentSize.height > height && s.height() == Style.Size.AUTO) {

                height = contentSize.height;
            }
        }

        /**
         * Generates the content of this <code>ComponentInstance</code> instance.
         * 
         * @param g2d      <code>Graphics2D</code>: The graphics to render using.
         * @param style    <code>Style</code>: The style to draw using.
         * @param children <code>List&lt;ImageClip&gt;</code>: The drawn children of
         *                 this <code>ComponentInstance</code> instance.
         */
        public abstract void createContent(Graphics2D g2d, Style style, List<ImageClip> children);

        private Style s;

        private final ArrayList<Rectangle> childBounds = new ArrayList<>();

        private Dimension contentSize;

        @Override
        public final Rectangle renderBounds(int parentWidth, int parentHeight, int parentFontSize, Style parentStyle) {

            // Generate the current style.
            s = getStyle(parentStyle);

            // Calculate the initial size.
            measure(s, parentWidth, parentHeight, parentFontSize);

            childBounds.clear();
            for (ComponentInstance child : children) {

                Rectangle childBound = child.renderBounds(width, height, fontSize, s);
                childBounds.add(childBound);
            }

            contentSize = calculateContentSize(s, childBounds);
            fitContent();

            x -= origin.width().getSize(getTotalWidth(), 0);
            y -= origin.height().getSize(getTotalHeight(), 0);

            return new Rectangle(x, y, getTotalWidth(), getTotalHeight());
        }

        @Override
        public final Rectangle rescale(int targetWidth, int targetHeight) {

            if (displayMode == Display.FLEX) {

                double widthFactor = 1;
                if (x + getTotalWidth() > targetWidth) {

                    widthFactor = (double) (targetWidth - borderLeft - borderRight)
                            / (x + marginLeft + paddingLeft + width + paddingRight + marginRight);
                    if (widthFactor < 0) {

                        widthFactor = 0;
                    }
                }

                double heightFactor = 1;
                if (y + getTotalHeight() > targetHeight) {

                    heightFactor = (double) (targetHeight - borderTop - borderBottom)
                            / (y + marginTop + paddingTop + height + paddingBottom + marginBottom);
                    if (heightFactor < 0) {

                        heightFactor = 0;
                    }
                }

                width *= widthFactor;
                height *= heightFactor;

                contentSize = rescaleContent(width, height, s, children);
                fitContent();

                if (borderLeft + borderRight + width >= targetWidth) {

                    x = 0;
                    marginLeft = 0;
                    paddingLeft = 0;
                    paddingRight = 0;
                    marginRight = 0;
                } else if (x + getTotalWidth() > targetWidth) {

                    widthFactor = (double) (targetWidth - borderLeft - borderRight - width)
                            / (x + marginLeft + paddingLeft + paddingRight + marginRight);
                    x *= widthFactor;
                    marginLeft *= widthFactor;
                    paddingLeft *= widthFactor;
                    paddingRight *= widthFactor;
                    marginRight *= widthFactor;
                }

                if (borderTop + borderBottom + height >= targetWidth) {

                    y = 0;
                    marginTop = 0;
                    paddingTop = 0;
                    paddingBottom = 0;
                    marginBottom = 0;
                } else if (y + getTotalHeight() > targetHeight) {

                    heightFactor = (double) (targetHeight - borderTop - borderBottom - height)
                            / (y + marginTop + paddingTop + paddingBottom + marginBottom);

                    y *= heightFactor;
                    marginTop *= heightFactor;
                    paddingTop *= heightFactor;
                    paddingBottom *= heightFactor;
                    marginBottom *= heightFactor;
                }

            } else {

                contentSize = rescaleContent(width, height, s, children);
                fitContent();
            }

            if (width < minWidth) {

                width = minWidth;
            }
            if (height < minHeight) {

                height = minHeight;
            }

            // The scroll X/Y cannot go beyond the content width/height.
            maxScrollX = Math.max(contentSize.width - width, 0);
            maxScrollY = Math.max(contentSize.height - height, 0);

            scrollX = Math.clamp(scrollX, 0, maxScrollX);
            scrollY = Math.clamp(scrollY, 0, maxScrollY);

            int boxWidth = width + paddingLeft + paddingRight;
            int boxHeight = height + paddingTop + paddingBottom;

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

            return new Rectangle(x, y, getTotalWidth(), getTotalHeight());
        }

        @Override
        public final BufferedImage render() {

            // Draw the children.
            ArrayList<ImageClip> childrenRenders = new ArrayList<>();
            for (ComponentInstance child : children) {

                BufferedImage childRender = child.render();
                childrenRenders.add(new ImageClip(child.x, child.y, childRender));
            }

            BufferedImage content = null;
            if (contentSize.width != 0 && contentSize.height != 0) {

                // Create all internal content and perform resizing.
                content = new BufferedImage(contentSize.width, contentSize.height, BufferedImage.TYPE_INT_ARGB);

                Graphics2D contentG2d = content.createGraphics();
                createContent(contentG2d, s, childrenRenders);
                contentG2d.dispose();
            }

            // Create the component border and background.
            int totalWidth = getTotalWidth();
            int totalHeight = getTotalHeight();
            if (totalWidth == 0 || totalHeight == 0) {

                return null;
            }

            BufferedImage component = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = component.createGraphics();

            // Adjust the origin to the top left corner of the component.
            g2d.translate(marginLeft, marginTop);

            Graphics2D g2 = (Graphics2D) g2d.create();

            // Find the exact center of the component.
            int internalWidth = width + paddingLeft + paddingRight;
            int internalHeight = height + paddingTop + paddingBottom;

            int centerX = internalWidth / 2 + borderLeft;
            int centerY = internalHeight / 2 + borderTop;

            // Start the border.
            BorderStyle borderRightStyle = s.borderRightStyle();
            Color borderRightColor = s.borderRightColor();

            // Draw the right side.
            if (borderRightStyle != BorderStyle.NONE && borderRight > 0) {

                g2.setColor(borderRightColor);
                g2.setStroke(new BasicStroke(borderRight * 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                Shape right = createSide(centerX, centerY, internalWidth, internalHeight, rxTR, ryTR, rxBR, ryBR);
                g2.draw(right);
            }

            g2.rotate(Math.PI / 2, centerX, centerY);

            BorderStyle borderBottomStyle = s.borderBottomStyle();
            Color borderBottomColor = s.borderBottomColor();

            // Draw the bottom side.
            if (borderBottomStyle != BorderStyle.NONE && borderBottom > 0) {

                g2.setColor(borderBottomColor);
                g2.setStroke(new BasicStroke(borderBottom * 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                Shape bottom = createSide(centerX, centerY, internalHeight, internalWidth, ryBR, rxBR, ryBL, rxBL);
                g2.draw(bottom);
            }

            g2.rotate(Math.PI / 2, centerX, centerY);

            BorderStyle borderLeftStyle = s.borderLeftStyle();
            Color borderLeftColor = s.borderLeftColor();

            // Draw the left side.
            if (borderLeftStyle != BorderStyle.NONE && borderLeft > 0) {

                g2.setColor(borderLeftColor);
                g2.setStroke(new BasicStroke(borderLeft * 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                Shape left = createSide(centerX, centerY, internalWidth, internalHeight, rxBL, ryBL, rxTL, ryTL);
                g2.draw(left);
            }

            g2.rotate(Math.PI / 2, centerX, centerY);

            BorderStyle borderTopStyle = s.borderTopStyle();
            Color borderTopColor = s.borderTopColor();

            // Draw the top side.
            if (borderTopStyle != BorderStyle.NONE && borderTop > 0) {

                g2.setColor(borderTopColor);
                g2.setStroke(new BasicStroke(borderTop * 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                Shape top = createSide(centerX, centerY, internalHeight, internalWidth, ryTL, rxTL, ryTR, rxTR);
                g2.draw(top);
            }

            // End the border.
            g2.dispose();

            g2d.translate(borderLeft, borderTop);

            // Draw the content after the border and the remaining half of the padding.
            int contentX = paddingLeft;
            int contentY = paddingTop;

            // Creates the content bounds.
            Shape contentBounds = createBounds(width + paddingLeft + paddingRight, height + paddingTop + paddingBottom,
                    rxTR, ryTR, rxTL, ryTL, rxBL, ryBL, rxBR, ryBR);

            // Process the background color and image.
            Color backgroundColor = s.backgroundColor();
            String newBackgroundIconPath = s.backgroundIcon();

            // If the background icon has changed, recompute it.
            if (newBackgroundIconPath != null
                    && (backgroundIconPath == null || !backgroundIconPath.equals(newBackgroundIconPath))) {

                backgroundIcon = getWorld().getTexture(newBackgroundIconPath, getRandomComponentId());
            } else if (newBackgroundIconPath == null) {

                backgroundIcon = null;
            }
            backgroundIconPath = newBackgroundIconPath;

            IconSize backgroundSize = s.backgroundSize();

            // Ensure the shape is self-contained, draw the content and background.
            g2d.clip(contentBounds);
            drawBackground(g2d, backgroundColor, backgroundIcon, backgroundSize, contentBounds);

            if (content != null) {

                g2d.drawImage(content, contentX - scrollX, contentY - scrollY, null);
            }

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
            int startY = centerY + (hT - hB) / 2;

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

        protected final void drawBackground(Graphics2D g2d, Color color, ImageIcon icon, IconSize size, Shape bounds) {

            if (color != Style.TRANSPARENT) {

                g2d.setColor(color);
                g2d.fill(bounds);
            }

            // Process the background icon, if it has one.
            if (icon != null) {

                drawIcon(g2d, icon, 0, 0, width + paddingLeft + paddingRight, height + paddingTop + paddingBottom,
                        size);
            }
        }

        protected final void drawImage(Graphics2D g2d, BufferedImage image, int x, int y) {

            g2d.drawImage(image, x, y, null);
        }

        protected final void drawImage(Graphics2D g2d, BufferedImage image, int x, int y, int centerX, int centerY,
                double angle) {

            AffineTransform old = g2d.getTransform();
            AffineTransform tx = new AffineTransform();

            tx.translate(x + centerX, y + centerY);
            tx.rotate(Math.toRadians(-angle));
            tx.translate(-centerX, -centerY);

            g2d.drawImage(image, tx, null);
            g2d.setTransform(old);
        }

        protected final void drawIcon(Graphics2D g2d, ImageIcon icon, int x, int y, int width, int height,
                IconSize size) {

            int iconWidth = icon.getIconWidth();
            int iconHeight = icon.getIconHeight();

            if (iconWidth == 0 || iconHeight == 0) {

                return;
            }

            int backgroundWidth = size.getWidth(iconWidth, iconHeight, width, height);
            int backgroundHeight = size.getHeight(iconWidth, iconHeight, width, height);

            g2d.drawImage(icon.getImage(), x, y, backgroundWidth, backgroundHeight, null);
        }

        protected final void drawText(Graphics2D g2d, String text, int x, int y, int width, Color color) {

            g2d.setColor(color);
            g2d.setFont(font);

            Stream<String> initLines = Stream.of(text.split("\n"));

            List<String> lines = (whiteSpace == WhiteSpace.NOWRAP ? initLines
                    : initLines.flatMap(line -> wrapText(line, fm, width - x).stream()))
                            .map(line -> textOverflow(fm, line, width - x)).toList();

            for (int i = 0; i < lines.size(); i++) {

                text = lines.get(i);
                int lineWidth = fm.stringWidth(text);

                // Handle the left align case.
                if (textAlign == TextAlign.LEFT || lineWidth > width) {

                    g2d.drawString(text, x, y + fontSize + lineHeight * i);
                    continue;
                }

                // Handle the right align case.
                if (textAlign == TextAlign.RIGHT) {

                    g2d.drawString(text, x + width - lineWidth, y + fontSize + lineHeight * i);
                    continue;
                }

                // Handle the center align case.
                if (textAlign == TextAlign.CENTER) {

                    g2d.drawString(text, x + (width - lineWidth) / 2, y + fontSize + lineHeight * i);
                    continue;
                }

                // Handle the justify align case.
                if (i == lines.size() - 1) {

                    g2d.drawString(text, x, y + fontSize + lineHeight * i);
                    continue;
                }

                String[] tokens = text.split(" ");
                if (tokens.length <= 1) {

                    g2d.drawString(text, x, y + fontSize + lineHeight * i);
                    continue;
                }

                double splitLength = Arrays.stream(tokens).mapToInt(fm::stringWidth).sum();
                double spaceWidth = (width - x - splitLength) / (tokens.length - 1);

                int offset = 0;
                for (String token : tokens) {

                    g2d.drawString(token, x + offset, y + fontSize + lineHeight * i);
                    offset += spaceWidth + fm.stringWidth(token);
                }
            }
        }

        protected final Dimension calculateIconSize(ImageIcon icon, int x, int y, IconSize size) {

            int iconWidth = icon.getIconWidth();
            int iconHeight = icon.getIconHeight();

            if (iconWidth == 0 || iconHeight == 0) {

                return new Dimension(0, 0);
            }

            int backgroundWidth = size.getWidth(iconWidth, iconHeight, width, height);
            int backgroundHeight = size.getHeight(iconWidth, iconHeight, width, height);

            return new Dimension(backgroundWidth, backgroundHeight);
        }

        protected final Dimension calculateTextSize(String text, int x, int y, int width) {

            String[] initLines = text.split("\n");
            List<String> lines = whiteSpace == WhiteSpace.NOWRAP ? List.of(initLines)
                    : Stream.of(initLines).flatMap(line -> wrapText(line, fm, width - x).stream()).toList();
            int lineWidth = 0;

            for (int i = 0; i < lines.size(); i++) {

                text = textOverflow(fm, lines.get(i), width - x);
                lineWidth = Math.max(lineWidth, fm.stringWidth(text));
            }

            if (textAlign != TextAlign.LEFT) {

                return new Dimension(width, lineHeight * lines.size());
            }

            return new Dimension(lineWidth, lineHeight * lines.size());
        }

        private ArrayList<String> wrapText(String text, FontMetrics fm, int maxWidth) {

            ArrayList<String> lines = new ArrayList<>();
            String line = "";

            int lineStart = 0;
            int prevBreak = -1;
            boolean whitespaceBreak = false;
            for (int i = 0; i < text.length(); i++) {

                line = text.substring(lineStart, i + 1);

                char c = text.charAt(i);
                if (c == '\n') {

                    String prevLine = text.substring(lineStart, i);
                    lines.add(prevLine);

                    // Start the new line.
                    lineStart = i + 1;
                    line = "";
                }

                if (c == ' ' || c == '-') {

                    prevBreak = i;
                    whitespaceBreak = c == ' ';
                }

                int lineLength = fm.stringWidth(line);

                // If the line is too long and can be broken, split into 2 new lines.
                if (lineLength > maxWidth) {

                    if (prevBreak != -1) {

                        // Cap the old line off with the space/hyphen.
                        String prevLine = text.substring(lineStart, prevBreak + (whitespaceBreak ? 0 : 1));
                        lines.add(prevLine);

                        // Start the new line.
                        lineStart = prevBreak + 1;
                        prevBreak = -1;
                        line = text.substring(lineStart, i + 1);
                    } else if (overflowWrap == OverflowWrap.BREAK_WORD) {

                        String prevLine = text.substring(lineStart, i);
                        lines.add(prevLine);

                        // Start the new line.
                        lineStart = i;
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

                String overflow = textOverflow.overflow();

                while (line.length() > 1) {

                    line = line.substring(0, line.length() - 1);
                    if (fm.stringWidth(line + overflow) <= maxWidth) {

                        line += overflow;
                        break;
                    }
                }
            }

            return line;
        }

        @Override
        public void onHover(int mouseX, int mouseY) {

            addState("hover");
        }

        @Override
        public void onExit(int mouseX, int mouseY) {

            removeState("hover");
        }

        protected boolean pressed = false;

        @Override
        public boolean onPress(int mouseX, int mouseY) {

            pressed = true;
            addState("active");

            if (propagateEvents == null) {

                return defaultPropagateEvents;
            }

            return propagateEvents;
        }

        @Override
        public void onRelease(int mouseX, int mouseY) {

            pressed = false;
            removeState("active");
        }

        @Override
        public boolean onTriggerPress(int mouseX, int mouseY, TRScript value, long time) {

            if (triggerPhase == Style.TriggerPhase.PRESS) {

                onComponentClick(mouseX, mouseY, value, time);
            }

            if (propagateEvents == null) {

                return defaultPropagateEvents;
            }

            return propagateEvents;
        }

        @Override
        public boolean onTriggerRelease(int mouseX, int mouseY, TRScript value, long time) {

            if (triggerPhase == Style.TriggerPhase.RELEASE) {

                onComponentClick(mouseX, mouseY, value, time);
            }

            if (propagateEvents == null) {

                return defaultPropagateEvents;
            }

            return propagateEvents;
        }

        public void onComponentClick(int mouseX, int mouseY, TRScript value, long time) {
        }

        @Override
        public void onScroll(int mouseX, int mouseY, Point displacement) {

            // Calculate the adjustment to scroll.
            int newScrollX = overflowX == Overflow.CLIP || contentSize.width <= width ? scrollX
                    : Math.clamp(scrollX + displacement.x, 0, maxScrollX);
            int newScrollY = overflowY == Overflow.CLIP || contentSize.height <= height ? scrollY
                    : Math.clamp(scrollY + displacement.y, 0, maxScrollY);

            // Remove the adjustment from the scroll
            displacement.translate(scrollX - newScrollX, scrollY - newScrollY);

            scrollX = newScrollX;
            scrollY = newScrollY;
        }

        @Override
        public final void updateContent(double time) {

            updateContent(time, children);
        }

        public final void updateContent(double time, List<ComponentInstance> children) {

            children.forEach(child -> child.updateContent(time));
        }
    }

    public final class TextComponentInstance extends ComponentInstance {

        private final TRScript text;

        private String stringVal;

        public TextComponentInstance(TextComponentSchema schema, ComponentInstance parent, DeterministicRandom random) {

            super(schema, parent, true, random);

            text = schema.getText();
        }

        public TextComponentInstance(TextComponentSchema schema, ComponentInstance parent, DeterministicRandom random,
                TRScript text) {

            super(schema, parent, true, random);

            this.text = text;
        }

        @Override
        public final Dimension calculateContentSize(Style style, List<Rectangle> children) {

            stringVal = getWorld().getText(text.evaluateString(InterfaceInstance.this));
            Dimension textSize = calculateTextSize(stringVal, 0, 0, width);
            return new Dimension(textSize.width, textSize.height);
        }

        @Override
        public Dimension rescaleContent(int targetWidth, int targetHeight, Style style,
                List<ComponentInstance> children) {

            Dimension textSize = calculateTextSize(stringVal, 0, 0, width);
            return new Dimension(textSize.width, textSize.height);
        }

        @Override
        public final void createContent(Graphics2D g2d, Style style, List<ImageClip> children) {

            drawText(g2d, stringVal, 0, 0, width, style.color());
        }
    }

    public final class IconComponentInstance extends ComponentInstance {

        private final IconType iconSchema;

        private ImageIcon normalIcon;

        private ImageIcon icon;

        private String iconPath;

        private final InventoryInstance inventory;

        public IconComponentInstance(InterfaceAttributes.IconComponentSchema schema, ComponentInstance parent,
                DeterministicRandom random) {

            super(schema, parent, true, random);

            iconSchema = schema.getIcon();

            if (iconSchema instanceof InterfaceAttributes.NamedSlotIconType
                    || iconSchema instanceof InterfaceAttributes.GridSlotIconType) {

                inventory = getWorld().playerFunction(playerId, player -> player.getEntity().getInventory());
            } else {

                inventory = null;
            }
        }

        @Override
        public final Dimension calculateContentSize(Style style, List<Rectangle> children) {

            String newIconPath = iconSchema.getIcon(InterfaceInstance.this);
            ItemInstance item = switch (iconSchema) {

            case InterfaceAttributes.GridSlotIconType gridSlotIconType -> inventory.getItem(gridSlotIconType.getSlot());

            case InterfaceAttributes.NamedSlotIconType namedSlotIconType -> inventory
                    .getItem(namedSlotIconType.getSlot());

            default -> null;
            };

            if (item != null) {

                icon = item.getIcon();
            } else {

                if (newIconPath != null && !newIconPath.equals(iconPath)) {

                    normalIcon = getWorld().getTexture(newIconPath, getRandomComponentId());
                    iconPath = newIconPath;
                }

                icon = normalIcon;
            }

            if (icon == null) {

                return new Dimension();
            }

            return calculateIconSize(icon, 0, 0, style.iconFit());
        }

        @Override
        public final Dimension rescaleContent(int targetWidth, int targetHeight, Style style,
                List<ComponentInstance> children) {

            if (icon == null) {

                return new Dimension();
            }

            return calculateIconSize(icon, 0, 0, style.iconFit());
        }

        @Override
        public final void createContent(Graphics2D g2d, Style style, List<ImageClip> children) {

            if (icon != null) {

                drawIcon(g2d, icon, 0, 0, width, height, style.iconFit());
            }
        }
    }

    public final class ButtonComponentInstance extends ComponentInstance {

        private final ComponentAction action;

        public ButtonComponentInstance(ButtonComponentSchema schema, ComponentInstance parent,
                DeterministicRandom random) {

            super(schema, parent, false, random);

            action = schema.getAction();
        }

        public ButtonComponentInstance(ButtonComponentSchema schema, ComponentInstance parent,
                DeterministicRandom random, ComponentAction.OnCall action) {

            super(schema, parent, false, random);

            this.action = ComponentAction.createComponentAction(action);
        }

        @Override
        public final Dimension calculateContentSize(Style style, List<Rectangle> children) {

            // Buttons are not required to have a child.
            if (children.isEmpty()) {

                return new Dimension();
            }

            Rectangle childSize = children.getFirst();
            return new Dimension(childSize.x + childSize.width, childSize.y + childSize.height);
        }

        @Override
        public final Dimension rescaleContent(int targetWidth, int targetHeight, Style style,
                List<ComponentInstance> children) {

            // Buttons are not required to have a child.
            if (children.isEmpty()) {

                return new Dimension();
            }

            ComponentInstance child = children.getFirst();

            Rectangle childSize = child.rescale(targetWidth, targetHeight);
            return new Dimension(childSize.x + childSize.width, childSize.y + childSize.height);
        }

        @Override
        public final void createContent(Graphics2D g2d, Style style, List<ImageClip> children) {

            // Buttons are not required to have a child.
            if (children.isEmpty()) {

                return;
            }

            ImageClip child = children.getFirst();
            BufferedImage childRender = child.image();
            drawImage(g2d, childRender, child.x(), child.y());
        }

        @Override
        public final void onComponentClick(int mouseX, int mouseY, TRScript value, long time) {

            action.call(InterfaceInstance.this, playerId, value);
        }
    }

    public final class RotateComponentInstance extends ComponentInstance {

        private final double angle;
        private double cos;
        private double sin;

        private final Size centerXSize;
        private int centerX;

        private final Size centerYSize;
        private int centerY;

        public RotateComponentInstance(RotateComponentSchema schema, ComponentInstance parent,
                DeterministicRandom random) {

            super(schema, parent, true, random);
            angle = schema.getAngle();
            centerXSize = schema.getCenterX();
            centerYSize = schema.getCenterY();
        }

        private Dimension getRotatedSize(Rectangle child) {

            // double childWidth = 0.5 * width * (Math.abs(cos) + cos) + 0.5 * height *
            // (Math.abs(sin) - sin);
            // double childHeight = 0.5 * height * (Math.abs(cos) + cos) + 0.5 * width *
            // (Math.abs(sin) + sin);

            double childWidth = centerX + cos * (child.x - centerX) - sin * (child.y - centerY);
            childWidth += DoubleStream
                    .of(0, cos * child.width, cos * child.width - sin * child.height, -sin * child.height).max()
                    .getAsDouble();

            if (childWidth < 0) {

                childWidth = 0;
            }

            double childHeight = centerY + cos * (child.y - centerY) + sin * (child.x - centerX);
            childHeight += DoubleStream
                    .of(0, cos * child.height, cos * child.height + sin * child.width, sin * child.width).max()
                    .getAsDouble();

            if (childHeight < 0) {

                childHeight = 0;
            }

            return new Dimension((int) childWidth, (int) childHeight);
        }

        @Override
        public final Dimension calculateContentSize(Style style, List<Rectangle> children) {

            cos = Math.cos(Math.toRadians(angle));
            sin = Math.sin(Math.toRadians(angle));

            centerX = centerXSize.getSize(width, 0);
            centerY = centerYSize.getSize(height, 0);

            Rectangle child = children.getFirst();
            return getRotatedSize(child);
        }

        @Override
        public final Dimension rescaleContent(int targetWidth, int targetHeight, Style style,
                List<ComponentInstance> children) {

            ComponentInstance child = children.getFirst();
            Rectangle childSize = new Rectangle(child.x, child.y, child.getTotalWidth(), child.getTotalHeight());

            // TODO finish rotation scaling
            return new Dimension(targetWidth, targetHeight);
        }

        @Override
        public final void createContent(Graphics2D g2d, Style style, List<ImageClip> children) {

            ImageClip child = children.getFirst();
            drawImage(g2d, child.image(), child.x(), child.y(), centerX, centerY, angle);
        }
    }

    public final class InterfaceComponentInstance extends ComponentInstance {

        private final InterfaceInstance asset;

        public InterfaceComponentInstance(InterfaceComponentSchema schema, ComponentInstance parent,
                DeterministicRandom random) {

            super(schema, parent, true, random);

            AssetPresets presets = schema.getPresets();
            InterfaceContext context = new InterfaceContext(presets, getWorld(), InterfaceInstance.this, playerId,
                    this);

            asset = context.instantiate();
        }

        @Override
        public final List<UIComponent> getChildren() {

            return List.of(asset);
        }

        @Override
        public final Dimension calculateContentSize(Style style, List<Rectangle> children) {

            Rectangle assetBounds = asset.renderBounds(width, height, fontSize, style);
            return new Dimension(assetBounds.x + assetBounds.width, assetBounds.y + assetBounds.height);
        }

        @Override
        public Dimension rescaleContent(int targetWidth, int targetHeight, Style style,
                List<ComponentInstance> children) {

            Rectangle assetSize = asset.rescale(targetWidth, targetHeight);
            return new Dimension(assetSize.x + assetSize.width, assetSize.y + assetSize.height);
        }

        @Override
        public final void createContent(Graphics2D g2d, Style style, List<ImageClip> children) {

            BufferedImage body = asset.render();

            drawImage(g2d, body, 0, 0);
        }
    }

    public final class ContainerComponentInstance extends ComponentInstance {

        public ContainerComponentInstance(ContainerComponentSchema schema, ComponentInstance parent,
                DeterministicRandom random) {

            super(schema, parent, true, random);
        }

        @Override
        public Dimension calculateContentSize(Style style, List<Rectangle> children) {

            int maxWidth = 0;
            int maxHeight = 0;

            for (Rectangle childSize : children) {

                maxWidth = Math.max(maxWidth, childSize.x + childSize.width);
                maxHeight = Math.max(maxHeight, childSize.y + childSize.height);
            }

            return new Dimension(maxWidth, maxHeight);
        }

        @Override
        public Dimension rescaleContent(int targetWidth, int targetHeight, Style style,
                List<ComponentInstance> children) {

            int maxWidth = 0;
            int maxHeight = 0;

            for (ComponentInstance child : children) {

                Rectangle childSize = child.rescale(targetWidth, targetHeight);

                maxWidth = Math.max(maxWidth, childSize.x + childSize.width);
                maxHeight = Math.max(maxHeight, childSize.y + childSize.height);
            }

            return new Dimension(maxWidth, maxHeight);
        }

        @Override
        public void createContent(Graphics2D g2d, Style style, List<ImageClip> children) {

            for (ImageClip child : children) {

                BufferedImage childRender = child.image();
                g2d.drawImage(childRender, child.x(), child.y(), null);
            }
        }
    }

    public final class ListComponentInstance extends ComponentInstance {

        public ListComponentInstance(ListComponentSchema schema, ComponentInstance parent, DeterministicRandom random) {

            super(schema, parent, true, random);
        }

        @Override
        public final Dimension calculateContentSize(Style style, List<Rectangle> children) {

            int maxWidth = 0;
            int maxHeight = 0;

            if (style.listDirection() == Style.Direction.VERTICAL) {

                for (Rectangle childSize : children) {

                    maxWidth = Math.max(maxWidth, childSize.x + childSize.width);
                    maxHeight += childSize.y + childSize.height;
                }

                maxHeight += gapHeight * (children.size() - 1);
            } else {

                for (Rectangle childSize : children) {

                    maxHeight = Math.max(maxHeight, childSize.y + childSize.height);
                    maxWidth += childSize.x + childSize.width;
                }

                maxWidth += gapWidth * (children.size() - 1);
            }

            return new Dimension(maxWidth, maxHeight);
        }

        @Override
        public final Dimension rescaleContent(int targetWidth, int targetHeight, Style style,
                List<ComponentInstance> children) {

            int maxWidth = 0;
            int maxHeight = 0;

            if (style.listDirection() == Style.Direction.VERTICAL) {

                double partial = 1;
                if (displayMode == Display.FLEX) {

                    double sum = children.stream().mapToDouble(child -> child.y + child.getTotalHeight()).sum()
                            + gapHeight * (children.size() - 1);
                    partial = targetHeight / sum;
                }

                for (ComponentInstance child : children) {

                    Rectangle childSize = child.rescale(targetWidth,
                            (int) ((child.y + child.getTotalHeight()) * partial));

                    maxWidth = Math.max(maxWidth, childSize.x + childSize.width);

                    child.y += maxHeight;
                    maxHeight += childSize.y + childSize.height;
                }

                if (displayMode == Display.FLEX) {

                    if (children.size() > 1) {

                        gapHeight = (targetHeight - maxHeight) / (children.size() - 1);

                        if (gapHeight < 0) {

                            gapHeight = 0;
                        }
                    } else {

                        gapHeight = 0;
                    }
                }

                for (int i = 1; i < children.size(); i++) {

                    children.get(i).y += i * gapHeight;
                }
                maxHeight += gapHeight * (children.size() - 1);
            } else {

                double partial = 1;
                if (displayMode == Display.FLEX) {

                    double sum = children.stream().mapToDouble(child -> child.x + child.getTotalWidth()).sum()
                            + gapWidth * (children.size() - 1);
                    partial = targetWidth / sum;
                }

                for (ComponentInstance child : children) {

                    Rectangle childSize = child.rescale((int) ((child.x + child.getTotalWidth()) * partial),
                            targetHeight);

                    maxHeight = Math.max(maxHeight, childSize.y + childSize.height);

                    child.x += maxWidth;
                    maxWidth += childSize.x + childSize.width;
                }

                if (displayMode == Display.FLEX) {

                    if (children.size() > 1) {

                        gapWidth = (targetWidth - maxWidth) / (children.size() - 1);

                        if (gapWidth < 0) {

                            gapWidth = 0;
                        }
                    } else {

                        gapWidth = 0;
                    }
                }

                for (int i = 1; i < children.size(); i++) {

                    children.get(i).x += i * gapWidth;
                }
                maxWidth += gapWidth * (children.size() - 1);
            }

            return new Dimension(maxWidth, maxHeight);
        }

        @Override
        public void createContent(Graphics2D g2d, Style style, List<ImageClip> children) {

            for (ImageClip child : children) {

                BufferedImage childRender = child.image();
                g2d.drawImage(childRender, child.x(), child.y(), null);
            }
        }
    }

    public final class GlobalMapComponentInstance extends ComponentInstance {

        private double centerX;
        private double centerY;

        private int prevMouseX;
        private int prevMouseY;

        private double zoom = 0;

        private double H;

        private double height() {

            return 1.0 / (1.7 + Math.atan(zoom / 80));
        }

        private LinkedHashMap<ImageIcon, Rectangle> terrainRenders;

        private final HashMap<String, ImageIcon> renderIcons = new HashMap<>();

        private LinkedHashMap<String, LocationDisplay> locationRenders;

        public final record LocationDisplay(TRScript name, TRScript description, ImageIcon icon, Rectangle bounds,
                ImageIcon pin, Rectangle pinBounds) {
        }

        private String pressedLocation;

        private String currentLocation;

        private long prevPress;

        ImageIcon playerPin = getWorld().getTexture(DataConstants.GLOBAL_MAP_PLAYER_PIN, getRandomComponentId());

        public GlobalMapComponentInstance(GlobalMapComponentSchema schema, ComponentInstance parent,
                DeterministicRandom random) {

            super(schema, parent, false, random);

            if (componentValues.size() != 2) {

                throw new Error("Global map could not be displayed");
            }

            centerX = (double) componentValues.get(0);
            centerY = (double) componentValues.get(1);

            ButtonComponentSchema enterButtonSchema = schema.getEnterButton();
            addChild(new ButtonComponentInstance(enterButtonSchema, this, random,
                    (_, playerId, _) -> getWorld().enterLocation(playerId)));

            ButtonComponentSchema travelButtonSchema = schema.getTravelButton();
            addChild(new ButtonComponentInstance(travelButtonSchema, this, random,
                    (_, playerId, _) -> getWorld().travel(playerId, pressedLocation)));
        }

        @Override
        public final Dimension calculateContentSize(Style style, List<Rectangle> children) {

            World world = getWorld();

            DeterministicRandom random = new DeterministicRandom(getRandomComponentId());
            H = height();

            terrainRenders = new LinkedHashMap<>();
            for (TerrainRender render : world.getTerrainRenders()) {

                double h = render.height();
                if (h >= H) {

                    continue;
                }

                String iconPath = render.icon().evaluateString(InterfaceInstance.this);
                ImageIcon icon = renderIcons.computeIfAbsent(iconPath,
                        path -> world.getTexture(iconPath, random.next()));

                double cX = render.x();
                double cY = render.y();
                double scale = 1 / (H - h);

                int left = (int) ((cX - centerX) * scale + centerX);
                int top = (int) ((cY - centerY) * scale + centerY);
                int iconW = (int) (icon.getIconWidth() * scale);
                int iconH = (int) (icon.getIconHeight() * scale);
                Rectangle bounds = new Rectangle(left, top, iconW, iconH);

                terrainRenders.put(icon, bounds);
            }

            locationRenders = new LinkedHashMap<>();
            for (Map.Entry<String, LocationRender> renderEntry : world.getLocationRenders().entrySet()) {

                String locationId = renderEntry.getKey();
                LocationRender render = renderEntry.getValue();

                double h = render.height();
                if (h >= H) {

                    continue;
                }

                double cX = render.x();
                double cY = render.y();
                double scale = 1 / (H - h);

                ImageIcon icon = render.icon();
                int iconW = icon.getIconWidth();
                int iconH = icon.getIconHeight();

                int iconLeft = (int) ((cX - centerX - iconW / 2.0) * scale + centerX);
                int iconTop = (int) ((cY - centerY - iconH / 2.0) * scale + centerY);
                int iconBoundsW = (int) (iconW * scale);
                int iconBoundsH = (int) (iconH * scale);
                Rectangle bounds = new Rectangle(iconLeft, iconTop, iconBoundsW, iconBoundsH);

                ImageIcon pin = render.pin();
                IconSize pinSize = render.pinSize();

                Rectangle pinBounds = null;
                if (pin != null) {

                    scale = 1 / (H + 0.35 - h);

                    Dimension pinDim = calculateIconSize(pin, 0, 0, pinSize);
                    int pinW = pinDim.width;
                    int pinH = pinDim.height;

                    int pinBoundsW = (int) (pinW * scale);
                    int pinBoundsH = (int) (pinH * scale);

                    int pinLeft = iconLeft + iconBoundsW / 2 - pinBoundsW / 2;
                    int pinTop = iconTop - pinBoundsW - (int) (8 * scale);
                    pinBounds = new Rectangle(pinLeft, pinTop, pinBoundsW, pinBoundsH);
                }

                locationRenders.put(locationId, new LocationDisplay(render.name(), render.description(), icon, bounds,
                        render.pin(), pinBounds));
            }

            currentLocation = world.playerFunction(playerId, Player::getLocation);

            return new Dimension(width, height);
        }

        @Override
        public final Dimension rescaleContent(int targetWidth, int targetHeight, Style style,
                List<ComponentInstance> children) {

            ComponentInstance enterButton = children.get(0);
            enterButton.rescale(width, height);

            if (locationRenders.containsKey(currentLocation)) {

                LocationDisplay location = locationRenders.get(currentLocation);
                Rectangle locationBounds = location.bounds();
                enterButton.x += locationBounds.getCenterX() - enterButton.getTotalWidth() / 2 + width / 2 - centerX;
                enterButton.y += locationBounds.y + locationBounds.height + gapHeight + height / 2 - centerY;
            } else {

                enterButton.x = -1 - enterButton.getTotalWidth();
                enterButton.y = -1 - enterButton.getTotalHeight();
            }

            ComponentInstance travelButton = children.get(1);
            travelButton.rescale(width, height);

            if (pressedLocation != null && locationRenders.containsKey(pressedLocation)
                    && !pressedLocation.equals(currentLocation)) {

                LocationDisplay location = locationRenders.get(pressedLocation);
                Rectangle locationBounds = location.bounds();
                travelButton.x += locationBounds.getCenterX() - travelButton.getTotalWidth() / 2 + width / 2 - centerX;
                travelButton.y += locationBounds.y + locationBounds.height + gapHeight + height / 2 - centerY;
            } else {

                travelButton.x = -1 - travelButton.getTotalWidth();
                travelButton.y = -1 - travelButton.getTotalHeight();
            }

            return new Dimension(width, height);
        }

        @Override
        public final void createContent(Graphics2D g2d, Style style, List<ImageClip> children) {

            Graphics2D init = (Graphics2D) g2d.create();

            if (pressedLocation != null && System.currentTimeMillis() < prevPress + 2500) {

                Rectangle target = locationRenders.get(pressedLocation).bounds();

                double adjust = 0.98;
                zoom = Math.pow(adjust, 2) * (zoom - 50) + 50;

                double targetX = width / 4.0 + target.getCenterX();
                double targetY = target.getCenterY();
                centerX = adjust * (centerX - targetX) + targetX;
                centerY = adjust * (centerY - targetY) + targetY;
            }

            g2d.translate(width / 2 - centerX, height / 2 - centerY);

            IconSize size = style.iconFit();

            for (Map.Entry<ImageIcon, Rectangle> terrainRender : terrainRenders.sequencedEntrySet()) {

                Rectangle renderBounds = terrainRender.getValue();
                drawIcon(g2d, terrainRender.getKey(), renderBounds.x, renderBounds.y, renderBounds.width,
                        renderBounds.height, size);
            }

            ZonedDateTime now = ZonedDateTime.now();

            for (Map.Entry<String, LocationDisplay> locationRender : locationRenders.sequencedEntrySet()) {

                String locationKey = locationRender.getKey();

                LocationDisplay locationDisplay = locationRender.getValue();

                ImageIcon icon = locationDisplay.icon();
                Rectangle bounds = locationDisplay.bounds();
                drawIcon(g2d, icon, bounds.x, bounds.y, bounds.width, bounds.height, size);

                int boundsCenterX = (int) bounds.getCenterX();
                int top = bounds.y;

                ImageIcon pin = locationDisplay.pin();
                if (pin != null) {

                    Rectangle pinBounds = locationDisplay.pinBounds();
                    drawIcon(g2d, pin, pinBounds.x, pinBounds.y, pinBounds.width, pinBounds.height, size);

                    top = pinBounds.y;
                }

                GlobalLocationInstance location = getWorld().getLocation(locationKey);
                if (location == null) {

                    continue;
                }

                String endCounter = location.getEndCounter(now);
                if (endCounter != null) {

                    Dimension endCounterSize = calculateTextSize(endCounter, 0, 0, width);

                    int endCounterX = boundsCenterX - endCounterSize.width / 2;
                    int endCounterY = top - endCounterSize.height - gapHeight;

                    g2d.setColor(new Color(128, 128, 128, 128));
                    g2d.fillRect(endCounterX - 5, endCounterY - 2, endCounterSize.width + 10,
                            endCounterSize.height + 4);

                    drawText(g2d, endCounter, endCounterX, endCounterY, width, style.color());

                    top = endCounterY - 2;
                }

                String resetCounter = location.getResetCounter(now);
                if (resetCounter != null) {

                    Dimension resetCounterSize = calculateTextSize(resetCounter, 0, 0, width);

                    int resetCounterX = boundsCenterX - resetCounterSize.width / 2;
                    int resetCounterY = top - resetCounterSize.height - gapHeight;
                    top = resetCounterY;

                    String resetLabel = getWorld().getText("global.resetLabel");
                    Dimension resetLabelSize = calculateTextSize(resetLabel, 0, 0, width);

                    int resetLabelX = boundsCenterX - resetLabelSize.width / 2;
                    int resetLabelY = top - resetLabelSize.height;

                    g2d.setColor(new Color(80, 200, 220));
                    g2d.fillRect(Math.min(resetCounterX, resetLabelX) - 35, resetLabelY - 2,
                            Math.max(resetCounterSize.width, resetLabelSize.width) + 70,
                            resetCounterSize.height + resetLabelSize.height + 4);

                    drawText(g2d, resetCounter, resetCounterX, resetCounterY, width, style.color());
                    drawText(g2d, resetLabel, resetLabelX, resetLabelY, width, style.color());
                }
            }

            g2d.dispose();
            g2d = init;

            if (locationRenders.containsKey(currentLocation)) {

                Rectangle locationBounds = locationRenders.get(currentLocation).bounds();

                double scale = 0.1 / (H + 0.35);
                int playerPinWidth = (int) (playerPin.getIconWidth() * scale);
                int playerPinHeight = (int) (playerPin.getIconHeight() * scale);
                int playerPinX = (int) (locationBounds.getCenterX() + width / 2.0 - centerX) - playerPinWidth / 2;
                int playerPinY = (int) (locationBounds.getCenterY() + locationBounds.height / 3.25 + height / 2.0
                        - centerY) - playerPinHeight;

                drawIcon(g2d, playerPin, playerPinX, playerPinY, playerPinWidth, playerPinHeight, IconSize.COVER);

                ImageClip enterButton = children.get(0);
                BufferedImage enterButtonImage = enterButton.image();

                drawImage(g2d, enterButtonImage, enterButton.x(), enterButton.y());
            }

            if (pressedLocation != null && locationRenders.containsKey(pressedLocation)
                    && !pressedLocation.equals(currentLocation)) {

                ImageClip travelButton = children.get(1);
                BufferedImage travelButtonImage = travelButton.image();

                drawImage(g2d, travelButtonImage, travelButton.x(), travelButton.y());
            }
        }

        @Override
        public final boolean onPress(int mouseX, int mouseY) {

            if (pressedLocation == null) {

                // The map should only move if the mouse is already pressed.
                if (pressed) {

                    double scale = 1 * height();
                    int dx = mouseX - prevMouseX;
                    int dy = mouseY - prevMouseY;

                    centerX -= dx * scale;
                    centerY -= dy * scale;
                }
            }

            prevMouseX = mouseX;
            prevMouseY = mouseY;

            return super.onPress(mouseX, mouseY);
        }

        @Override
        public final void onScroll(int mouseX, int mouseY, Point displacement) {

            if (pressedLocation != null) {

                unselectLocation();
            }

            double newZoom = Math.clamp(zoom + displacement.y, -200, 200);

            // Remove the adjustment from the scroll
            displacement.translate(0, (int) (zoom - newZoom));

            zoom = newZoom;
        }

        @Override
        public final void onComponentClick(int mouseX, int mouseY, TRScript value, long time) {

            prevPress = time;

            if (pressedLocation != null) {

                unselectLocation();
            }
            mouseX -= width / 2;
            mouseY -= height / 2;

            for (Map.Entry<String, LocationDisplay> locationRender : locationRenders.sequencedEntrySet().reversed()) {

                String location = locationRender.getKey();
                LocationDisplay locationDisplay = locationRender.getValue();
                Rectangle bounds = locationDisplay.bounds();
                if (bounds.contains(mouseX + centerX, mouseY + centerY)) {

                    pressedLocation = location;
                    break;
                }
            }

            if (pressedLocation != null) {

                selectLocation();
            }
        }

        private void selectLocation() {

            LocationDisplay location = locationRenders.get(pressedLocation);
            getWorld().playerConsumer(playerId, player -> {

                player.displayLocation(location);
            });
        }

        private void unselectLocation() {

            pressedLocation = null;
            getWorld().closeMenu(playerId);
        }
    }

    public final class LocationDisplayComponentInstance extends ComponentInstance {

        public LocationDisplayComponentInstance(LocationDisplayComponentSchema schema,
                ComponentInstance componentParent, DeterministicRandom random) {

            super(schema, componentParent, false, random);

            if (componentValues.size() != 1) {

                throw new Error("Location could not be displayed");
            }

            LocationDisplay locationDisplay = (LocationDisplay) componentValues.get(0);

            addChild(new TextComponentInstance(schema.getNameText(), this, random, locationDisplay.name()));
            addChild(new TextComponentInstance(schema.getDescriptionText(), this, random,
                    locationDisplay.description()));
        }

        @Override
        public Dimension calculateContentSize(Style style, List<Rectangle> children) {

            return new Dimension(width, height);
        }

        @Override
        public Dimension rescaleContent(int targetWidth, int targetHeight, Style style,
                List<ComponentInstance> children) {

            ComponentInstance nameText = children.get(0);
            int offset = nameText.getTotalHeight() + nameText.getY();

            ComponentInstance descriptionText = children.get(1);
            descriptionText.y += offset;
            offset = descriptionText.getTotalHeight() + descriptionText.getY();

            int leftover = height - offset;
            int pad = Math.clamp(leftover, 0, gapHeight);
            descriptionText.y += pad;

            return new Dimension(width, height);
        }

        @Override
        public void createContent(Graphics2D g2d, Style style, List<ImageClip> children) {

            for (ImageClip child : children) {

                drawImage(g2d, child.image(), child.x(), child.y());
            }
        }
    }

    public final class InventoryDisplayComponentInstance extends ComponentInstance {

        private final InventoryInstance primaryInventory;
        private final InventoryComponentInstance primaryUi;

        private final InventoryInstance secondaryInventory;
        private final InventoryComponentInstance secondaryUi;

        private InventorySlotInstance selectedSlot;
        private long prevClick = -1;

        private final ArrayList<ItemTransfer> transfers = new ArrayList<>();

        private void addTransfer(long time, InventorySlotInstance start, boolean startIsPrimary,
                InventorySlotInstance end, boolean endIsPrimary, ImageIcon item) {

            transfers.add(new ItemTransfer(time, start, startIsPrimary, end, endIsPrimary, item));
        }

        private final record ItemTransfer(long time, InventorySlotInstance start, boolean startIsPrimary,
                InventorySlotInstance end, boolean endIsPrimary, ImageIcon item) {

        }

        private boolean displayTransfer(ItemTransfer transfer, Graphics2D g2d, long time) {

            InventoryComponentInstance firstUi = transfer.startIsPrimary() ? primaryUi : secondaryUi;
            InventoryComponentInstance.SlotDisplay first = firstUi.slots.get(transfer.start());

            InventoryComponentInstance secondUi = transfer.endIsPrimary() ? primaryUi : secondaryUi;
            InventoryComponentInstance.SlotDisplay second = secondUi.slots.get(transfer.end());

            if (first == null || second == null) {

                return false;
            }

            Point p1 = new Point(first.x, first.y);
            Point p2 = new Point(second.x, second.y);

            p1.x += firstUi.getX() + firstUi.getContentOffsetX();
            p1.y += firstUi.getY() + firstUi.getContentOffsetY();

            p2.x += secondUi.getX() + secondUi.getContentOffsetX();
            p2.y += secondUi.getY() + secondUi.getContentOffsetY();

            int slotWidth = firstUi.slotWidth;
            ImageIcon item = transfer.item();

            long dt = time - transfer.time();
            double distance = Math.hypot(p2.x - p1.x, p2.y - p1.y);

            double speed = Math.sqrt(distance) / 5;

            int slotX;
            int slotY;

            boolean end = false;

            if (dt * speed > distance) {

                slotX = p2.x;
                slotY = p2.y;
                end = true;
            } else {

                slotX = p1.x + (int) ((p2.x - p1.x) * dt * speed / distance);
                slotY = p1.y + (int) ((p2.y - p1.y) * dt * speed / distance);
            }

            drawIcon(g2d, item, slotX, slotY, slotWidth, slotWidth, Style.IconSize.CONTAIN);

            return !end;
        }

        public InventoryDisplayComponentInstance(InventoryDisplayComponentSchema schema,
                ComponentInstance componentParent, DeterministicRandom random) {

            super(schema, componentParent, false, random);

            if (componentValues.size() != 4) {

                throw new Error("Inventory could not be displayed");
            }

            primaryInventory = (InventoryInstance) componentValues.get(0);
            primaryUi = new InventoryComponentInstance((InventoryComponentSchema) componentValues.get(1), this, random,
                    primaryInventory);

            secondaryInventory = (InventoryInstance) componentValues.get(2);
            secondaryUi = new InventoryComponentInstance((InventoryComponentSchema) componentValues.get(3), this,
                    random, secondaryInventory);

            addChild(primaryUi);
            addChild(secondaryUi);
        }

        @Override
        public Dimension calculateContentSize(Style style, List<Rectangle> children) {

            Rectangle primary = children.get(0);
            Rectangle secondary = children.get(1);

            return new Dimension(primary.x + primary.width + gapWidth + secondary.x + secondary.width,
                    Math.max(primary.y + primary.height, secondary.y + secondary.height));
        }

        @Override
        public Dimension rescaleContent(int targetWidth, int targetHeight, Style style,
                List<ComponentInstance> children) {

            ComponentInstance primary = children.get(0);
            ComponentInstance secondary = children.get(1);

            double contentWidth = primary.x + primary.getTotalWidth() + gapWidth + secondary.x
                    + secondary.getTotalWidth();
            double partial = 1;

            // If the content expands too far horizontally, try to shrink.
            // Do not expand content, if there is more room the button will move.
            if (contentWidth > targetWidth) {

                partial = targetWidth / contentWidth;
            }

            int newContentWidth = 0;
            int newContentHeight = 0;
            for (ComponentInstance child : children) {

                Rectangle expanded = child.rescale((int) ((child.x + child.getTotalWidth()) * partial), targetHeight);
                newContentWidth += expanded.x + expanded.width;
                newContentHeight = Math.max(newContentHeight, expanded.y + expanded.height);
            }

            gapWidth = Math.clamp(targetWidth - newContentWidth, 0, gapWidth);

            secondary.x += primary.x + primary.getTotalWidth() + gapWidth;

            return new Dimension(newContentWidth + gapWidth, newContentHeight);
        }

        @Override
        public void createContent(Graphics2D g2d, Style style, List<ImageClip> children) {

            for (ImageClip child : children) {

                drawImage(g2d, child.image(), child.x(), child.y());
            }

            long time = System.currentTimeMillis();

            for (int i = 0; i < transfers.size(); i++) {

                ItemTransfer transfer = transfers.get(i);
                boolean keep = displayTransfer(transfer, g2d, time);
                if (!keep) {

                    transfers.remove(i);
                    i--;
                }
            }
        }

        private boolean primaryInventorySelected = true;

        @Override
        public void onComponentClick(int mouseX, int mouseY, TRScript value, long time) {

            InventorySlotInstance newSlot = null;
            boolean newPrimaryInventorySelected = true;

            if (primaryUi.contains(mouseX, mouseY)) {

                newSlot = primaryUi.getSlotAt(mouseX - primaryUi.getContentOffsetX() - primaryUi.getX(),
                        mouseY - primaryUi.getContentOffsetY() - primaryUi.getY());

                if (newSlot != null) {

                    newPrimaryInventorySelected = true;
                }
            } else if (secondaryUi.contains(mouseX, mouseY)) {

                newSlot = secondaryUi.getSlotAt(mouseX - secondaryUi.getContentOffsetX() - secondaryUi.getX(),
                        mouseY - secondaryUi.getContentOffsetY() - secondaryUi.getY());

                if (newSlot != null) {

                    newPrimaryInventorySelected = false;
                }
            }

            // If the player didn't click on a slot, do nothing.
            if (newSlot == null) {

                return;
            }

            // Update the timestamp.
            long dt = time - prevClick;
            prevClick = time;

            // If the player did not select a slot before, set the selection.
            if (selectedSlot == null || selectedSlot.isEmpty() && selectedSlot != newSlot) {

                selectedSlot = newSlot;
                primaryInventorySelected = newPrimaryInventorySelected;

                if (newPrimaryInventorySelected) {

                    primaryUi.setSelectedSlot(selectedSlot);
                    secondaryUi.setSelectedSlot(selectedSlot);
                } else {

                    primaryUi.setSelectedSlot(selectedSlot);
                    secondaryUi.setSelectedSlot(selectedSlot);
                }

                return;
            }

            if (!selectedSlot.containsItem()) {

                selectedSlot = null;
                primaryUi.setSelectedSlot(selectedSlot);
                secondaryUi.setSelectedSlot(selectedSlot);
                return;
            }

            // If the new slot is not the same as the old slot, attempt to swap.
            if (newSlot != selectedSlot) {

                ItemInstance firstItem = selectedSlot.getItem();
                ItemInstance secondItem = newSlot.getItem();

                // If the items cannot be swapped, do nothing.
                if (!newSlot.isAcceptedType(firstItem)
                        || secondItem != null && !selectedSlot.isAcceptedType(secondItem)) {

                    selectedSlot = null;
                } else {

                    // If the first item can be swapped, register the transfer.
                    if (secondItem == null || secondItem.isLikeAsset(firstItem)
                            && secondItem.getStackSize() < secondItem.getMaxStackSize()) {

                        ImageIcon firstItemIcon = firstItem.getIcon();
                        addTransfer(time, selectedSlot, primaryInventorySelected, newSlot, newPrimaryInventorySelected,
                                firstItemIcon);
                    }

                    // If the second item is different, register the transfer.
                    if (secondItem != null && !secondItem.isLikeAsset(firstItem)) {

                        ImageIcon secondItemIcon = secondItem.getIcon();
                        addTransfer(time, newSlot, newPrimaryInventorySelected, selectedSlot, primaryInventorySelected,
                                secondItemIcon);
                    }

                    selectedSlot.transfer(newSlot);

                    selectedSlot = null;
                }

                primaryUi.setSelectedSlot(selectedSlot);
                secondaryUi.setSelectedSlot(selectedSlot);

                return;
            }

            // Attempt to add the inventory slot to the other inventory.
            if (dt < 500) {

                ItemInstance item = selectedSlot.getItem();
                ImageIcon itemIcon = item.getIcon();

                InventoryComponentInstance transferInventory = newPrimaryInventorySelected ? secondaryUi : primaryUi;

                List<InventorySlotInstance> matches = transferInventory.slots.sequencedKeySet().stream()
                        .filter(slot -> slot.canAddLike(item)).toList();

                ItemInstance leftover = item;
                for (InventorySlotInstance slot : matches) {

                    addTransfer(time, selectedSlot, newPrimaryInventorySelected, slot, !newPrimaryInventorySelected,
                            itemIcon);
                    leftover = slot.putItem(item);
                    if (leftover == null) {

                        break;
                    }
                }

                if (leftover != null) {

                    matches = transferInventory.slots.sequencedKeySet().stream().filter(slot -> slot.canAddEmpty(item))
                            .toList();
                    for (InventorySlotInstance slot : matches) {

                        addTransfer(time, selectedSlot, newPrimaryInventorySelected, slot, !newPrimaryInventorySelected,
                                itemIcon);
                        leftover = slot.putItem(item);
                        if (leftover == null) {
                            break;
                        }
                    }
                }

                selectedSlot.setItem(leftover);
            }

            selectedSlot = null;

            primaryUi.setSelectedSlot(selectedSlot);
            secondaryUi.setSelectedSlot(selectedSlot);

            // Remove the selection.
        }
    }

    public final class InventoryComponentInstance extends ComponentInstance {

        private final InventoryInstance inventory;

        private final ImmutableList<GridDisplay> grid;

        private final ImmutableMap<String, NamedDisplay> named;

        private final Size slotSize;
        private int slotWidth;

        private int gridSize;

        private final HashSet<String> namedOverlap = new HashSet<>();

        private final LinkedHashMap<InventorySlotInstance, SlotDisplay> slots = new LinkedHashMap<>();

        public final class SlotDisplay {

            private int x, y;
            private final ImageIcon slotIcon, selectedSlotIcon;

            private SlotDisplay(int x, int y, ImageIcon slotIcon, ImageIcon selectedSlotIcon) {

                this.x = x;
                this.y = y;
                this.slotIcon = slotIcon;
                this.selectedSlotIcon = selectedSlotIcon;
            }
        }

        private int maxSlotWidth;
        private int maxSlotHeight;

        private InventorySlotInstance selectedSlot;

        public final void setSelectedSlot(InventorySlotInstance selectedSlot) {

            this.selectedSlot = selectedSlot;
        }

        public final InventorySlotInstance getSlotAt(int mouseX, int mouseY) {

            for (InventorySlotInstance slot : slots.reversed().sequencedKeySet()) {

                SlotDisplay p = slots.get(slot);

                if (p.x <= mouseX && mouseX < p.x + slotWidth && p.y <= mouseY && mouseY < p.y + slotWidth) {

                    return slot;
                }
            }

            return null;
        }

        private final HashMap<String, ImageIcon> slotIcons = new HashMap<>();

        private ImageIcon getSlotIcon(World world, String icon, long random) {

            if (icon == null) {

                return null;
            }

            return slotIcons.computeIfAbsent(icon, path -> world.getTexture(path, random));
        }

        public InventoryComponentInstance(InventoryComponentSchema schema, ComponentInstance componentParent,
                DeterministicRandom random, InventoryInstance inventory) {

            super(schema, componentParent, true, random);

            this.inventory = inventory;
            slotSize = schema.getSlotSize();

            grid = schema.getGrid();
            named = schema.getNamed();
        }

        @Override
        public final Dimension calculateContentSize(Style style, List<Rectangle> children) {

            int headerWidth = 0;
            int headerHeight = 0;
            if (!children.isEmpty()) {

                Rectangle header = children.getFirst();
                headerWidth = header.x + header.width;
                headerHeight = header.y + header.height + gapHeight;
            }

            gridSize = inventory.getGridSize();
            slots.clear();

            slotWidth = slotSize.getSize(width, 0);

            DeterministicRandom random = new DeterministicRandom(getRandomComponentId());
            World world = getWorld();

            for (GridDisplay gridSlots : grid) {

                int gridX = gridSlots.x();
                int gridY = gridSlots.y();
                int gridWidth = gridSlots.width();
                int gridHeight = gridSlots.height();
                int start = gridSlots.start();

                String slotIconPath = gridSlots.slotIcon();
                ImageIcon slotIcon = getSlotIcon(world, slotIconPath, random.next());

                String selectedSlotIconPath = gridSlots.selectedSlotIcon();
                ImageIcon selectedSlotIcon = getSlotIcon(world, selectedSlotIconPath, random.next());

                int displayGridWidth = ((gridSize - start) < gridWidth ? gridSize : gridWidth);
                int displayGridHeight = Math.min(gridHeight, (int) Math.ceil((double) (gridSize - start) / gridWidth));

                maxSlotWidth = Math.max(maxSlotWidth,
                        gridX + slotWidth * displayGridWidth + gapWidth * (displayGridWidth - 1));
                maxSlotHeight = Math.max(maxSlotHeight,
                        gridY + slotWidth * displayGridHeight + gapHeight * (displayGridHeight - 1));

                for (int i = 0; i < gridSize - start && i < gridWidth * gridHeight; i++) {

                    int slotX = (i % gridWidth);
                    int slotY = (i / gridWidth);

                    slots.put(inventory.getSlot(i + start), new SlotDisplay(gridX + slotX * (slotWidth + gapWidth),
                            gridY + slotY * (slotWidth + gapHeight), slotIcon, selectedSlotIcon));
                }
            }

            namedOverlap.clear();
            namedOverlap.addAll(named.keySet());
            namedOverlap.retainAll(inventory.getNamedSlots());

            for (String namedSlot : namedOverlap) {

                NamedDisplay p = named.get(namedSlot);
                int namedX = p.x();
                int namedY = p.y();

                String slotIconPath = p.slotIcon();
                ImageIcon slotIcon = getSlotIcon(world, slotIconPath, random.next());

                String selectedSlotIconPath = p.selectedSlotIcon();
                ImageIcon selectedSlotIcon = getSlotIcon(world, selectedSlotIconPath, random.next());

                maxSlotWidth = Math.max(maxSlotWidth, namedX + slotWidth);
                maxSlotHeight = Math.max(maxSlotHeight, namedY + slotWidth);

                slots.put(inventory.getSlot(namedSlot), new SlotDisplay(namedX, namedY, slotIcon, selectedSlotIcon));
            }

            return new Dimension(Math.max(headerWidth, maxSlotWidth), headerHeight + maxSlotHeight);
        }

        @Override
        public final Dimension rescaleContent(int targetWidth, int targetHeight, Style style,
                List<ComponentInstance> children) {

            int headerWidth = 0;
            int headerHeight = 0;
            if (!children.isEmpty()) {

                ComponentInstance header = children.getFirst();
                Rectangle headerBounds = header.rescale(targetWidth, targetHeight - gapHeight - maxSlotHeight);
                headerWidth = headerBounds.x + headerBounds.width;
                headerHeight = headerBounds.y + headerBounds.height;

                // The gap height should be resized between 0 and the current gap height.
                gapHeight = Math.clamp(targetHeight - maxSlotHeight - headerHeight, 0, gapHeight);
                headerHeight += gapHeight;
            }

            if (headerHeight > 0) {

                for (SlotDisplay slot : slots.values()) {

                    slot.y += headerHeight;
                }
            }

            return new Dimension(Math.max(headerWidth, maxSlotWidth), headerHeight + maxSlotHeight);
        }

        @Override
        public void createContent(Graphics2D g2d, Style style, List<ImageClip> children) {

            if (!children.isEmpty()) {

                ImageClip header = children.getFirst();
                BufferedImage headerImage = header.image();
                drawImage(g2d, headerImage, header.x(), header.y());
            }

            IconSize slotFit = Style.IconSize.CONTAIN;

            World world = getWorld();
            ImageIcon defaultSlotIcon = getSlotIcon(world, DataConstants.INVENTORY_SLOT_ICON, getRandomComponentId());
            ImageIcon defaultSelectedSlotIcon = getSlotIcon(world, DataConstants.INVENTORY_SLOT_SELECTED_ICON,
                    getRandomComponentId());

            for (InventorySlotInstance slot : slots.sequencedKeySet()) {

                SlotDisplay p = slots.get(slot);
                ItemInstance item = slot.getItem();

                boolean slotSelected = slot == selectedSlot;
                ImageIcon slotIcon = slotSelected ? p.selectedSlotIcon : p.slotIcon;
                if (slotIcon == null || item != null) {

                    slotIcon = slotSelected ? defaultSelectedSlotIcon : defaultSlotIcon;
                }

                drawIcon(g2d, slotIcon, p.x, p.y, slotWidth, slotWidth, slotFit);

                if (item == null) {

                    continue;
                }

                drawIcon(g2d, item.getIcon(), p.x, p.y, slotWidth, slotWidth, slotFit);

                int stackSize = item.getStackSize();
                if (stackSize > 1) {

                    drawText(g2d, String.valueOf(stackSize), p.x, p.y + slotWidth - lineHeight,
                            slotWidth + fontSize - lineHeight, style.color());
                }
            }
        }
    }

    @Override
    public final void onExit(int mouseX, int mouseY) {
    }

    @Override
    public final void onHover(int mouseX, int mouseY) {
    }

    @Override
    public final void onScroll(int mouseX, int mouseY, Point displacement) {
    }

    @Override
    public final void onRelease(int mouseX, int mouseY) {
    }

    @Override
    public final boolean onPress(int mouseX, int mouseY) {

        return true;
    }

    @Override
    public final boolean onTriggerPress(int mouseX, int mouseY, TRScript value, long time) {

        return true;
    }

    @Override
    public final boolean onTriggerRelease(int mouseX, int mouseY, TRScript value, long time) {

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
    public final int getContentOffsetX() {

        return 0;
    }

    @Override
    public final int getContentOffsetY() {

        return 0;
    }

    @Override
    public final Dimension getSize() {

        // Create the size such that it starts in the top left corner of the parent
        // element and ends at the boundaries of the body.
        return new Dimension(body.x + body.getTotalWidth(), body.y + body.getTotalHeight());
    }

    @Override
    public final Rectangle renderBounds(int parentWidth, int parentHeight, int parentFontSize, Style parentStyle) {

        return body.renderBounds(parentWidth, parentHeight, parentFontSize, parentStyle);
    }

    @Override
    public final Rectangle rescale(int targetWidth, int targetHeight) {

        return body.rescale(targetWidth, targetHeight);
    }

    @Override
    public final BufferedImage render() {

        BufferedImage bodyRender = body.render();
        int bodyOffsetX = body.getX();
        int bodyOffsetY = body.getY();

        BufferedImage render = new BufferedImage(bodyOffsetX + bodyRender.getWidth(),
                bodyOffsetY + bodyRender.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = render.createGraphics();
        g2d.drawImage(bodyRender, bodyOffsetX, bodyOffsetY, null);

        return render;
    }
}
