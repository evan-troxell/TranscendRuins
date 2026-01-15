package com.transcendruins.assets.modelassets.attack;

import com.transcendruins.assets.Attributes;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

public final class AttackSchema extends Attributes {

    public static final AttackSchema DEFAULT = new AttackSchema(null, null, null, null, true);

    private final Integer damage;

    public final Integer getDamage() {

        return damage;
    }

    private final Float range;

    public final Float getRange() {

        return range;
    }

    private final Double speed;

    public final Double getSpeed() {

        return speed;
    }

    private final Double cooldown;

    public final Double getCooldown() {

        return cooldown;
    }

    public AttackSchema(Integer damage, Float range, Double speed, Double cooldown, boolean isBase) {

        super(isBase);
        this.damage = damage;
        this.range = range;
        this.speed = speed;
        this.cooldown = cooldown;
    }

    public static final AttackSchema createAttack(TracedDictionary json, boolean isBase) throws LoggedException {

        TracedEntry<Integer> damageEntry = json.getAsInteger("damage", true, null, num -> num >= 0);
        Integer damage = damageEntry.getValue();

        TracedEntry<Float> attackRangeEntry = json.getAsFloat("attackRange", true, null, num -> num >= 0);
        Float attackRange = attackRangeEntry.getValue();

        TracedEntry<Double> attackSpeedEntry = json.getAsDouble("attackSpeed", true, null, num -> num > 0 || num == -1);
        Double attackSpeed = attackSpeedEntry.getValue();

        TracedEntry<Double> attackCooldownEntry = json.getAsDouble("attackCooldown", true, null,
                num -> 0 <= num && num <= 1);
        Double attackCooldown = attackCooldownEntry.getValue();

        if (attackRange == null && damage == null && attackSpeed == null && attackCooldown == null) {

            return null;
        }

        return new AttackSchema(damage, attackRange, attackSpeed, attackCooldown, isBase);
    }
}