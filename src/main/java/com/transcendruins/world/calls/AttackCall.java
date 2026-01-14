package com.transcendruins.world.calls;

import com.transcendruins.assets.modelassets.attack.AttackInstance;
import com.transcendruins.assets.modelassets.entities.EntityInstance;
import com.transcendruins.assets.modelassets.items.ItemInstance;

public final record AttackCall(AttackInstance attack, EntityInstance target, ItemInstance weapon) {

    public final boolean isValid() {

        return target != null && target.alive() && (weapon == null || weapon.intact());
    }
}