package com.transcendruins.world.calls;

import com.transcendruins.assets.modelassets.primaryassets.PrimaryAssetInstance;
import com.transcendruins.assets.modelassets.primaryassets.interaction.AssetInteractionInstance;
import com.transcendruins.world.Player;

public final record InteractionCall(AssetInteractionInstance interaction, Player player, PrimaryAssetInstance target) {

    public final long call(long time) {

        return interaction.call(target, time, player);
    }
}