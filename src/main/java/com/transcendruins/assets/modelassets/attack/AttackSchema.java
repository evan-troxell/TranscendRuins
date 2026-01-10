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

    private final Double range;

    public final Double getRange() {

        return range;
    }

    private final Double duration;

    public final Double getDuration() {

        return duration;
    }

    private final Double cooldown;

    public final Double getCooldown() {

        return cooldown;
    }

    public AttackSchema(Integer damage, Double range, Double duration, Double cooldown, boolean isBase) {

        super(isBase);
        this.damage = damage;
        this.range = range;
        this.duration = duration;
        this.cooldown = cooldown;
    }

    public static final AttackSchema createAttack(TracedDictionary json, boolean isBase) throws LoggedException {

        TracedEntry<Integer> damageEntry = json.getAsInteger("damage", true, null, num -> num >= 0);
        Integer damage = damageEntry.getValue();

        TracedEntry<Double> attackRangeEntry = json.getAsDouble("attackRange", true, null, num -> num >= 0);
        Double attackRange = attackRangeEntry.getValue();

        TracedEntry<Double> attackDurationEntry = json.getAsDouble("attackDuration", true, null, num -> num >= 0);
        Double attackDuration = attackDurationEntry.getValue();

        TracedEntry<Double> attackCooldownEntry = json.getAsDouble("attackCooldown", true, null,
                num -> num >= 0 || num == -1);
        Double attackCooldown = attackCooldownEntry.getValue();

        if (attackRange == null && damage == null && attackDuration == null && attackCooldown == null) {

            return null;
        }

        return new AttackSchema(damage, attackRange, attackDuration, attackCooldown, isBase);
    }
}