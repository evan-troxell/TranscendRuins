package com.transcendruins.world.calls;

import com.transcendruins.assets.modelassets.attack.AttackInstance;
import com.transcendruins.assets.modelassets.entities.EntityInstance;

public final record AttackCall(AttackInstance attack, EntityInstance attacker, EntityInstance target) {

    public final void call(long time) {

        attacker.attack(time, attack, target);
    }
}