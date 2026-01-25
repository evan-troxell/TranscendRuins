/* Copyright 2026 Evan Troxell
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

package com.transcendruins.assets.modelassets.primaryassets;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.transcendruins.assets.animations.boneactors.BoneActor;
import com.transcendruins.assets.animations.boneactors.BoneActorSet;
import com.transcendruins.assets.assets.AssetContext;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.assets.assets.schema.AssetAttributes;
import com.transcendruins.assets.catalogue.locations.GlobalLocationInstance;
import com.transcendruins.assets.interfaces.InterfaceAttributes.InventoryComponentSchema;
import com.transcendruins.assets.modelassets.ModelAssetAttributes;
import com.transcendruins.assets.modelassets.ModelAssetInstance;
import com.transcendruins.assets.modelassets.items.ItemContext;
import com.transcendruins.assets.modelassets.items.ItemInstance;
import com.transcendruins.assets.modelassets.primaryassets.crafting.CraftingSchema;
import com.transcendruins.assets.modelassets.primaryassets.crafting.CraftingSchema.SlotReference;
import com.transcendruins.assets.modelassets.primaryassets.interaction.AssetInteractionInstance;
import com.transcendruins.assets.modelassets.primaryassets.inventory.InventoryContents;
import com.transcendruins.assets.modelassets.primaryassets.inventory.InventoryInstance;
import com.transcendruins.assets.modelassets.primaryassets.inventory.InventorySchema;
import com.transcendruins.assets.modelassets.primaryassets.inventory.InventorySlotInstance;
import com.transcendruins.assets.models.ModelAttributes.Bone;
import com.transcendruins.assets.models.ModelInstance;
import com.transcendruins.assets.recipes.RecipeInstance;
import com.transcendruins.rendering.renderbuffer.RenderBuffer;
import com.transcendruins.utilities.immutable.ImmutableList;
import com.transcendruins.utilities.immutable.ImmutableMap;
import com.transcendruins.utilities.metadata.Identifier;
import com.transcendruins.world.AreaGrid;
import com.transcendruins.world.World;

/**
 * <code>PrimaryAsset</code>: A class representing an <code>AssetInstance</code>
 * instance which has the capability of being rendered using the standard
 * <code>RenderInstance</code> method.
 */
public abstract class PrimaryAssetInstance extends ModelAssetInstance {

    private GlobalLocationInstance location;

    public final GlobalLocationInstance getLocation() {

        return location;
    }

    public final void setLocation(GlobalLocationInstance location) {

        this.location = location;
        if (modelParent == null) {

            setParent(location);
        }
    }

    private PrimaryAssetInstance modelParent;

    protected final void setModelParent(PrimaryAssetInstance modelParent) {

        this.modelParent = modelParent;
        if (hasModelParent()) {

            setParent(modelParent);
        } else {

            setParent(location);
        }
    }

    public final boolean hasModelParent() {

        return modelParent != null;
    }

    /**
     * <code>InventoryInstance</code>: The inventory of this
     * <code>PrimaryAssetInstance</code> instance.
     */
    private final InventoryInstance inventory = new InventoryInstance(this);

    /**
     * Retrieves the inventory of this <code>PrimaryAssetInstance</code> instance.
     * 
     * @return <code>InventoryInstance</code>: The <code>inventory</code> field of
     *         this <code>PrimaryAssetInstance</code> instance.
     */
    public final InventoryInstance getInventory() {

        return inventory;
    }

    public abstract void updateSlot(String name, ItemInstance item);

    private InventoryComponentSchema inventoryUi;

    public final InventoryComponentSchema getInventoryUi() {

        return inventoryUi;
    }

    private InventoryComponentSchema privateInventoryUi;

    public final InventoryComponentSchema getPrivateInventoryUi() {

        return privateInventoryUi;
    }

    private InventoryContents inventoryContents;

    private CraftingSchema crafting;

    public final CraftingSchema getCrafting() {

        return crafting;
    }

    private RecipeInstance currentRecipe;

    public final ItemInstance getRecipeOutput(World world) {

        if (currentRecipe == null) {

            return null;
        }

        AssetPresets outputItem = currentRecipe.getOutput();
        int outputCount = currentRecipe.getOutputCount();

        return new ItemContext(outputItem, world, outputCount).instantiate();
    }

    private long currentRecipeStart = -1;

    private boolean currentRecipeActive = false;

    public final long getRecipeRemaining(long time) {

        if (!currentRecipeActive) {

            return -1;
        }

        // System.out.println(fuelEnd - time);

        if (fuelDuration != -1) {

            time = Math.min(time, fuelEnd);
        }

        long dt = time - currentRecipeStart;
        long duration = currentRecipe.getDuration();

        return duration - dt;
    }

    private long fuelDuration = -1;

    private long fuelEnd = -1;

    public final double getFuelRemaining(long time) {

        if (fuelEnd == -1 || fuelDuration == -1 || fuelDuration == 0) {

            return 0.0;
        }

        long dt = fuelEnd - time;

        if (dt < 0) {

            return 0.0;
        }

        if (dt >= fuelDuration) {

            return 0.0;
        }

        return dt / (double) fuelDuration;
    }

    private ImmutableList<AssetInteractionInstance> interaction;

    private final HashMap<AssetAttributes, ImmutableList<AssetInteractionInstance>> interactionCache = new HashMap<>();

    public final ImmutableList<AssetInteractionInstance> getInteraction() {

        return interaction;
    }

    private boolean hasTileUpdate;

    public final boolean hasTileUpdate() {

        return hasTileUpdate;
    }

    private final HashSet<PrimaryAssetInstance> primaryModelChildren = new HashSet<>();

    private final HashMap<String, HashSet<PrimaryAssetInstance>> primaryModelConnections = new HashMap<>();

    public final boolean containsPrimaryAssetChild(String connection) {

        if (!primaryModelConnections.containsKey(connection)) {

            return false;
        }

        return !primaryModelConnections.get(connection).isEmpty();
    }

    protected final void queueAreaUpdate() {

        hasTileUpdate = true;
        if (!primaryModelChildren.isEmpty()) {

            primaryModelChildren.forEach(PrimaryAssetInstance::queueAreaUpdate);
        }
    }

    protected final void removeAreaUpdate() {

        hasTileUpdate = false;
    }

    public abstract void updateArea(AreaGrid area);

    public abstract void setPosition(int tileX, int tileZ);

    public abstract void translate(int dx, int dz);

    public abstract void rotate(int direction, int areaWidth, int areaLength);

    public final Rectangle getTileBounds() {

        if (hasModelParent()) {

            return modelParent.getTileBounds();
        }

        return getInternalTileBounds();
    }

    protected abstract Rectangle getInternalTileBounds();

    public final Rectangle getTileBoundsTranslated(int dx, int dz) {

        if (hasModelParent()) {

            return modelParent.getTileBoundsTranslated(dx, dz);
        }

        return getInternalTileBoundsAt(dx, dz);
    }

    protected abstract Rectangle getInternalTileBoundsTranslated(int dx, int dz);

    public final Rectangle getTileBoundsAt(int tileX, int tileZ) {

        if (hasModelParent()) {

            return modelParent.getTileBoundsAt(tileX, tileZ);
        }

        return getInternalTileBoundsAt(tileX, tileZ);
    }

    protected abstract Rectangle getInternalTileBoundsAt(int tileX, int tileZ);

    private String modelParentAttachment;

    protected final void setModelParent(PrimaryAssetInstance parent, String modelParentAttachment) {

        setModelParent(parent);
        this.modelParentAttachment = modelParentAttachment;

        queueAreaUpdate();
    }

    public final void removeModelParent() {

        setModelParent(null);
        modelParentAttachment = null;

        queueAreaUpdate();
    }

    public final String getModelParentAttachment() {

        return modelParentAttachment;
    }

    /**
     * Adds a child model to this <code>PrimaryAssetInstance</code> instance. The
     * child model will be attached to the bone with the specified name.
     * 
     * @param modelChild <code>PrimaryAssetInstance</code>: The child model to add
     *                   to this <code>PrimaryAssetInstance</code> instance.
     * @param connection <code>String</code>: The name of the bone to which the
     *                   child model will be attached.
     * @return <code>boolean</code>: Whether or not the child was successfully
     *         added.
     */
    protected final boolean addModelChild(PrimaryAssetInstance modelChild, String connection) {

        if (!containsBone(connection)) {

            return false;
        }

        if (this != modelChild.modelParent) {

            modelChild.removeModelParent();
        } else {

            String prevConnection = modelChild.getModelParentAttachment();
            primaryModelConnections.get(prevConnection).remove(modelChild);
        }

        modelChild.setModelParent(this, connection);

        primaryModelChildren.add(modelChild);
        primaryModelConnections.computeIfAbsent(connection, _ -> new HashSet<>()).add(modelChild);

        return true;
    }

    /**
     * Removes a child model from this <code>PrimaryAssetInstance</code> instance.
     * 
     * @param modelChild <code>PrimaryAssetInstance</code>: The child model to
     *                   remove from this <code>PrimaryAssetInstance</code>
     *                   instance.
     */
    public final void removeModelChild(PrimaryAssetInstance modelChild) {

        // If the child is a child of this model, remove it.
        if (modelChild != null && primaryModelChildren.remove(modelChild)) {

            String prevConnection = modelChild.getModelParentAttachment();
            primaryModelConnections.get(prevConnection).remove(modelChild);

            modelChild.removeModelParent();
        }
    }

    @Override
    protected final RenderBuffer getParentPolygons(ModelInstance model, BoneActorSet boneActors, Vector3f position,
            Quaternion rotation) {

        Bone root = model.getRoot();

        HashMap<String, ArrayList<ModelAssetInstance>> boneMappings = new HashMap<>();

        for (Map.Entry<String, HashSet<PrimaryAssetInstance>> boneMapping : primaryModelConnections.entrySet()) {

            String attachment = boneMapping.getKey();
            HashSet<PrimaryAssetInstance> bones = boneMapping.getValue();

            boneMappings.put(attachment, new ArrayList<>(bones));
        }

        for (Map.Entry<String, HashSet<ItemInstance>> boneMapping : inventory.getAttachments().entrySet()) {

            String attachment = boneMapping.getKey();
            HashSet<ItemInstance> bones = boneMapping.getValue();

            if (boneMappings.containsKey(attachment)) {

                boneMappings.get(attachment).addAll(bones);
                continue;
            }

            boneMappings.put(attachment, new ArrayList<>(bones));
        }

        RenderBuffer renders = new RenderBuffer();
        HashMap<Integer, HashMap<Vector3f, Float>> vertices = root.computeVertexWeights(boneActors, BoneActor.DEFAULT,
                model.getVertices(), (bone, actor, pivotPoint) -> {

                    if (boneMappings.containsKey(bone)) {

                        List<RenderBuffer> newRenders = boneMappings.get(bone).stream()
                                .map(asset -> asset.getPolygons(this, bone)).toList();
                        renders.append(newRenders);
                    }

                    renders.transform(actor, pivotPoint);
                });

        RenderBuffer render = generatePolygons(vertices, position, rotation, root.pivotPoint());
        render.append(renders);

        return render;
    }

    @Override
    protected final RenderBuffer getChildPolygons(ModelInstance model, BoneActorSet boneActors,
            ModelAssetInstance parent, String attachment) {

        Vector3f position = model.getPivotPoint(attachment);
        Quaternion rotation = Quaternion.IDENTITY;

        return getParentPolygons(model, boneActors, position, rotation);
    }

    public abstract boolean alive();

    /**
     * Creates a new instance of the <code>PrimaryAssetInstance</code> class.
     * 
     * @param assetContext <code>AssetContext</code>: The context used to generate
     *                     this <code>PrimaryAssetInstance</code> instance.
     * @param key          <code>Object</code>: The instantiation key, which is
     *                     required to match <code>AssetType.KEY</code>.
     */
    public PrimaryAssetInstance(AssetContext assetContext, Object key) {

        super(assetContext, key);

        PrimaryAssetContext context = (PrimaryAssetContext) assetContext;

        setLocation(context.getLocation());
    }

    @Override
    public final void onInitialized() {

        if (inventoryContents != null) {

            inventoryContents.generate(getRandom(), this);
        }

        onPrimaryInitialized();
    }

    public void onPrimaryInitialized() {

        // Assets may choose to override
    }

    @Override
    public void applyModelAssetAttributes(ModelAssetAttributes attributeSet) {

        PrimaryAssetAttributes attributes = (PrimaryAssetAttributes) attributeSet;

        // Updates the inventory field.
        computeAttribute(attributes.getInventory(), inventory::applyAttributes, attributes, InventorySchema.DEFAULT);

        // Updates the inventoryUi field.
        inventoryUi = calculateAttribute(attributes.getInventoryUi(), inventoryUi, attributes, null);

        privateInventoryUi = calculateAttribute(attributes.getPrivateInventoryUi(), privateInventoryUi, attributes,
                null);

        inventoryContents = calculateAttribute(attributes.getInventoryContents(), inventoryContents, attributes, null);

        crafting = calculateAttribute(attributes.getCrafting(), crafting, attributes, null);

        // Update the interaction.
        interaction = calculateAttribute(attributes.getInteraction(),
                schemas -> interactionCache.computeIfAbsent(attributes,
                        _ -> new ImmutableList<>(
                                schemas.stream().map(AssetInteractionInstance::createInteraction).toList())),
                interaction, attributes, new ImmutableList<>());

        applyPrimaryAssetAttributes(attributes);
    }

    /**
     * Applies an attribute set to this <code>PrimaryAssetInstance</code> instance.
     * 
     * @param attributeSet <code>ModelAssetAttributes</code>: The attributes to
     *                     apply.
     */
    public abstract void applyPrimaryAssetAttributes(PrimaryAssetAttributes attributeSet);

    @Override
    protected void onModelAssetUpdate(double time) {

        // Update the inventory items.
        inventory.getItems().forEach(item -> item.update(time));

        if (crafting != null) {

            updateCrafting(time);
        }

        onPrimaryAssetUpdate(time);
    }

    private void updateCrafting(double time) {

        if (currentRecipe != null) {

            currentRecipe.update(time);
        }

        long timestamp = System.currentTimeMillis();

        ArrayList<InventorySlotInstance> inputList = crafting.getInput().stream()
                .map(reference -> reference.apply(inventory)).filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));

        Map<Identifier, ArrayList<InventorySlotInstance>> inputSlots = inputList.stream()
                .filter(InventorySlotInstance::containsItem)
                .collect(Collectors.groupingBy(slot -> slot.getItem().getIdentifier(),
                        Collectors.collectingAndThen(Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparingInt(slot -> slot.getItem().getStackSize()))
                                        .collect(Collectors.toCollection(ArrayList::new)))));

        Map<Identifier, List<ItemInstance>> inputs = inputSlots.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue().stream().map(InventorySlotInstance::getItem).toList(), (a, _) -> a,
                        HashMap::new));

        // Check if the existing recipe needs to be changed.
        updateRecipe(inputs, timestamp);

        CraftingSchema.SlotReference fuelReference = crafting.getFuel();

        if (currentRecipe != null) {

            CraftingSchema.SlotReference outputReference = crafting.getOutput();
            InventorySlotInstance outputSlot = outputReference.apply(inventory);

            ItemInstance output = recipeActive(outputSlot, fuelReference, timestamp);

            // If an output can be generated.
            if (output != null) {

                if (fuelReference != null && (fuelEnd == -1 || fuelEnd < timestamp)) {

                    InventorySlotInstance fuelSlot = fuelReference.apply(inventory);
                    ItemInstance fuelItem = fuelSlot == null ? null : fuelSlot.getItem();

                    if (fuelItem != null && fuelItem.getFuelDuration() > 0) {

                        if (fuelEnd == -1) {

                            currentRecipeStart = timestamp;
                        } else {

                            long fuelBufferTime = timestamp - fuelEnd;
                            currentRecipeStart += fuelBufferTime;
                        }

                        fuelDuration = fuelItem.getFuelDuration();
                        fuelEnd = timestamp + fuelDuration;

                        int fuelCount = fuelItem.getStackSize();
                        if (fuelCount <= 1) {

                            fuelSlot.setItem(null);
                        } else {

                            fuelItem.setStackSize(fuelCount - 1);
                        }
                    }
                } else {

                    if (!currentRecipeActive) {

                        currentRecipeActive = true;
                        currentRecipeStart = timestamp;
                    }

                    int slotMax = output.getMaxStackSize();
                    if (outputSlot.containsItem()) {

                        ItemInstance outputStack = outputSlot.getItem();
                        slotMax = outputStack.getMaxStackSize() - outputStack.getStackSize();
                    }

                    slotMax /= currentRecipe.getOutputCount();

                    // Craft the existing items if permitted.
                    output = createRecipe(inputs, inputSlots, fuelReference, timestamp, output, slotMax);

                    if (output != null) {

                        outputSlot.putItem(output);
                    }
                }
            }

        }

        if (fuelReference == null)

        {

            fuelDuration = -1;
            fuelEnd = -1;
        }
    }

    private void updateRecipe(Map<Identifier, List<ItemInstance>> inputs, long timestamp) {

        if (currentRecipe != null) {

            ImmutableMap<Identifier, ImmutableList<Integer>> required = currentRecipe.getInputCounts();
            boolean failed = false;

            for (Map.Entry<Identifier, ImmutableList<Integer>> requiredEntry : required.entrySet()) {

                Identifier id = requiredEntry.getKey();
                if (!inputs.containsKey(id)) {

                    failed = true;
                    break;
                }

                List<ItemInstance> items = inputs.get(id);
                List<Integer> requiredCount = requiredEntry.getValue();
                if (items.size() < requiredCount.size()) {

                    failed = true;
                    break;
                }

                for (int i = 0; i < requiredCount.size(); i++) {

                    if (items.get(i).getStackSize() < requiredCount.get(i)) {

                        failed = true;
                        break;
                    }
                }

                if (failed) {

                    break;
                }
            }

            if (failed) {

                currentRecipe = null;
                currentRecipeActive = false;
            }
        }

        if (currentRecipe == null) {

            HashMap<String, RecipeInstance> recipes = getWorld().getRecipes(crafting.getCategory());

            for (RecipeInstance recipe : recipes.values()) {

                ImmutableMap<Identifier, ImmutableList<Integer>> required = recipe.getInputCounts();
                boolean failed = false;

                for (Map.Entry<Identifier, ImmutableList<Integer>> requiredEntry : required.entrySet()) {

                    Identifier id = requiredEntry.getKey();
                    if (!inputs.containsKey(id)) {

                        failed = true;
                        break;
                    }

                    List<ItemInstance> items = inputs.get(id);
                    List<Integer> requiredCount = requiredEntry.getValue();
                    if (items.size() < requiredCount.size()) {

                        failed = true;
                        break;
                    }

                    for (int i = 0; i < requiredCount.size(); i++) {

                        if (items.get(i).getStackSize() < requiredCount.get(i)) {

                            failed = true;
                            break;
                        }
                    }

                    if (failed) {

                        break;
                    }
                }

                if (!failed) {

                    currentRecipe = recipe;
                    if (fuelEnd < timestamp) {

                        fuelEnd = -1;
                    }
                    break;
                }
            }
        }
    }

    private ItemInstance recipeActive(InventorySlotInstance outputSlot, SlotReference fuelReference, long timestamp) {

        // If there is no output slot, no item can be created.
        if (outputSlot == null) {

            currentRecipeActive = false;
            return null;
        }

        AssetPresets outputPresets = currentRecipe.getOutput();
        ItemInstance output = new ItemContext(outputPresets, getWorld(), 1).instantiate();

        // If the output slot is filled by another item, at capacity, or will not accept
        // the output, no item can be created.
        if (!outputSlot.isEmpty()
                && (!outputSlot.containsItem(output) || outputSlot.getItem().getMaxStackSize()
                        - outputSlot.getItem().getStackSize() < currentRecipe.getOutputCount())
                || !outputSlot.isAcceptedType(output)) {

            currentRecipeActive = false;
            return null;
        }

        return output;
    }

    private ItemInstance createRecipe(Map<Identifier, List<ItemInstance>> inputs,
            Map<Identifier, ArrayList<InventorySlotInstance>> slots, CraftingSchema.SlotReference fuelReference,
            long timestamp, ItemInstance output, int max) {

        int timeCount = (int) ((fuelReference == null ? timestamp : Math.min(timestamp, fuelEnd)) - currentRecipeStart);

        long duration = currentRecipe.getDuration();
        if (duration > timeCount) {

            return null;
        }

        if (duration > 0) {

            timeCount /= duration;
        } else {

            timeCount = -1;
        }

        ImmutableMap<Identifier, ImmutableList<Integer>> required = currentRecipe.getInputCounts();

        // If not all items are present, return.
        if (!inputs.keySet().containsAll(required.keySet())) {

            return null;
        }

        HashMap<Identifier, ArrayList<Integer>> modMap = new HashMap<>();

        int maxItemCount = -1;

        for (Map.Entry<Identifier, ImmutableList<Integer>> requiredEntry : required.entrySet()) {

            Identifier id = requiredEntry.getKey();
            ArrayList<Integer> itemCounts = inputs.get(id).stream().map(item -> item.getStackSize())
                    .collect(Collectors.toCollection(ArrayList::new));
            List<Integer> requiredCounts = requiredEntry.getValue();

            ArrayList<Integer> mods = new ArrayList<>();

            int mitemCount = 0;

            while (itemCounts.size() >= requiredCounts.size() && !itemCounts.isEmpty()) {

                int m = -1;

                for (int i = 0; i < requiredCounts.size(); i++) {

                    int requiredCount = requiredCounts.get(i);
                    int itemCount = itemCounts.get(i);

                    // If another addition cannot be made, return from the loop.
                    if (requiredCount > itemCount) {

                        m = 0;
                        break;
                    }

                    int newM = itemCount / requiredCount;
                    if (m == -1 || newM < m) {

                        m = newM;
                    }
                }

                // The items list is guaranteed to be sorted so if m == 0, it means one of the
                // requirements cannot be fulfilled regardless of list order.
                if (m <= 0) {

                    break;
                }

                mods.add(m);

                // Decrement the item counts.
                for (int i = 0; i < requiredCounts.size() && i < itemCounts.size(); i++) {

                    int requiredCount = requiredCounts.get(i);
                    int remainder = itemCounts.get(i) - requiredCount * m;
                    if (remainder < requiredCount) {

                        itemCounts.remove(i);
                        i--;
                    } else {

                        itemCounts.set(i, remainder);
                    }
                }

                mitemCount += m;
                itemCounts.sort(Comparator.naturalOrder());
            }

            modMap.put(id, mods);

            if (maxItemCount == -1 || mitemCount < maxItemCount) {

                maxItemCount = mitemCount;
            }
        }

        // Return null if an item cannot be created.
        if (maxItemCount <= 0) {

            return null;
        }

        if (timeCount != -1 && timeCount < maxItemCount) {

            maxItemCount = timeCount;
        }

        if (max < maxItemCount) {

            maxItemCount = max;
        }

        currentRecipeStart += maxItemCount * duration;

        for (Map.Entry<Identifier, ImmutableList<Integer>> requiredEntry : required.entrySet()) {

            Identifier id = requiredEntry.getKey();
            ArrayList<InventorySlotInstance> itemSlots = slots.get(id);

            List<Integer> requiredCounts = requiredEntry.getValue();

            ArrayList<Integer> subtractCounts = modMap.get(id);

            int remainder = maxItemCount;
            while (remainder > 0 && !subtractCounts.isEmpty() && itemSlots.size() >= requiredCounts.size()
                    && !itemSlots.isEmpty()) {

                int subtract = subtractCounts.removeFirst();
                remainder -= subtract;

                // If more items cannot be removed, set subtract to the previous remainder and
                // end after this iteration.
                if (remainder < 0) {

                    subtract += remainder;
                    remainder = 0;
                }

                for (int i = 0; i < requiredCounts.size() && i < itemSlots.size(); i++) {

                    int requiredCount = requiredCounts.get(i);

                    InventorySlotInstance slot = itemSlots.get(i);
                    if (slot.isEmpty()) {

                        itemSlots.remove(i);
                        i--;
                    }

                    ItemInstance item = slot.getItem();
                    int itemCount = item.getStackSize() - subtract * requiredCount;

                    if (itemCount <= 0) {

                        slot.setItem(null);
                        itemSlots.remove(i);
                        i--;
                    }

                    item.setStackSize(itemCount);
                }

                itemSlots.sort(Comparator.comparingInt(slot -> slot.getItem().getStackSize()));
            }
        }

        output.setStackSize(maxItemCount * currentRecipe.getOutputCount());
        return output;
    }

    /**
     * Performs the instance update of the class of this
     * <code>PrimaryAssetInstance</code> instance.
     * 
     * @param time <code>double</code>: The time since the world was created.
     */
    protected abstract void onPrimaryAssetUpdate(double time);

    @Override
    public PrimaryAssetInstance clone(Function<AssetPresets, ? extends AssetContext> contextualize, World world) {

        PrimaryAssetInstance asset = (PrimaryAssetInstance) super.clone(contextualize, world);

        asset.location = location;

        HashSet<String> namedSlots = inventory.getNamedSlots();
        asset.inventory.resize(namedSlots);
        for (String named : namedSlots) {

            InventorySlotInstance newSlot = asset.inventory.getSlot(named);
            InventorySlotInstance oldSlot = inventory.getSlot(named);

            if (newSlot == null || oldSlot == null) {

                continue;
            }

            newSlot.setItem(oldSlot.getItem().clone(presets -> new ItemContext(presets, world, 1), world));
        }

        int gridSlots = inventory.getGridSize();
        asset.inventory.resize(gridSlots);
        for (int i = 0; i < gridSlots; i++) {

            InventorySlotInstance newSlot = asset.inventory.getSlot(i);
            InventorySlotInstance oldSlot = inventory.getSlot(i);

            if (newSlot == null || oldSlot == null) {

                continue;
            }

            newSlot.setItem(oldSlot.getItem().clone(presets -> new ItemContext(presets, world, 1), world));
        }

        asset.inventoryUi = inventoryUi;
        asset.privateInventoryUi = privateInventoryUi;
        asset.inventoryContents = inventoryContents;
        asset.crafting = crafting;
        asset.currentRecipe = currentRecipe;
        asset.currentRecipeStart = currentRecipeStart;
        asset.fuelEnd = fuelEnd;

        asset.interaction = interaction;

        asset.interactionCache.clear();
        asset.interactionCache.putAll(interactionCache);

        return asset;
    }
}
