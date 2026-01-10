package com.transcendruins.assets.modelassets.attack;

import com.transcendruins.assets.Attributes;
import com.transcendruins.assets.Instance;
import com.transcendruins.assets.modelassets.entities.EntityInstance;

public final class AttackInstance extends Instance {

    private int damage;

    public final int getDamage() {

        return damage;
    }

    private double range;

    public final double getRange() {

        return range;
    }

    private double duration;

    public final double getDuration() {

        return duration;
    }

    private double cooldown;

    public final double getCooldown() {

        return cooldown;
    }

    private long prevTime = -1;

    public final boolean canCall(long time) {

        return range >= 0 && (prevTime == -1 || cooldown > -1 && 1000 * cooldown <= time - prevTime);
    }

    public final long call(long time, EntityInstance target) {

        if (!canCall(time)) {

            return -1;
        }

        prevTime = time;

        target.inflict(this);

        return time + (long) (duration * 1000);
    }

    @Override
    public void applyAttributes(Attributes attributeSet) {

        AttackSchema attributes = (AttackSchema) attributeSet;

        damage = calculateAttribute(attributes.getDamage(), damage, attributes, 0);
        range = calculateAttribute(attributes.getRange(), range, attributes, -1.0);
        duration = calculateAttribute(attributes.getDuration(), duration, attributes, 0.0);
        cooldown = calculateAttribute(attributes.getCooldown(), cooldown, attributes, 0.0);
    }
}