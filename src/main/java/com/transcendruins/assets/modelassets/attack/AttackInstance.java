package com.transcendruins.assets.modelassets.attack;

import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.Instance;

public final class AttackInstance extends Instance {

    private int damage;

    public final int getDamage() {

        return damage;
    }

    private float range;

    public final float getRange() {

        return range;
    }

    private double speed = -1;

    public final double getSpeed() {

        return speed;
    }

    private double cooldown;

    public final double getCooldown() {

        return cooldown;
    }

    @Override
    public void applyAttributes(Attributes attributeSet) {

        AttackSchema attributes = (AttackSchema) attributeSet;

        damage = calculateAttribute(attributes.getDamage(), damage, attributes, 0);
        range = calculateAttribute(attributes.getRange(), range, attributes, 0.0f);
        speed = calculateAttribute(attributes.getSpeed(), speed, attributes, -1.0);
        cooldown = calculateAttribute(attributes.getCooldown(), cooldown, attributes, 0.0);
    }
}