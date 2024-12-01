package github.meloweh.wolfcompanion.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class WolfInventoryHelper {

//    private static ItemStack findLavaSituationPotion(final List<ItemStack> inventoryContents, final LivingEntity entity) {
//        final ItemStack potion = WolfInventoryHelper.findPotion(inventoryContents, StatusEffects.FIRE_RESISTANCE);
//
//        if (!potion.isEmpty()) return potion;
//
//        return WolfInventoryHelper.findLifesavingPotions(inventoryContents, entity);
//    }

    private static boolean hasLifesavingEffects(final LivingEntity entity) {
        return getLifeSavingEffects(true).stream().anyMatch(entity::hasStatusEffect);
    }

    public static boolean hasFittingLifesavingEffect(final LivingEntity entity) {
        final boolean isLow = entity.getHealth() <= 7;
        final boolean isHot = entity.isOnFire() || entity.isInLava();
        final boolean hasAnyEffect = hasLifesavingEffects(entity);
        final boolean hasFireResistance = entity.hasStatusEffect(StatusEffects.FIRE_RESISTANCE);

        if (isHot && !hasFireResistance) return false;
        if (isLow) {
            if (!hasAnyEffect || hasFireResistance) {
                return false;
            } else return true;
        }
        return true;
    }

    public static boolean hasFittingLifesavingEffect(final LivingEntity entity, final List<ItemStack> inventoryContents) {
        final boolean hasFireResistance = entity.hasStatusEffect(StatusEffects.FIRE_RESISTANCE);
        final boolean isHot = entity.isOnFire() || entity.isInLava();
        final boolean isLow = entity.getHealth() <= 7;
        final boolean hasAnyEffect = hasLifesavingEffects(entity);
        final boolean hasAnyPotion = !findLifesavingPotions(inventoryContents, entity).first.isEmpty();
        final boolean hasFirePotion = !findPotion(inventoryContents, StatusEffects.FIRE_RESISTANCE).first.isEmpty();

        if (entity.isInLava()) {
            if (!hasFireResistance) {
                if (hasAnyPotion) return false;
            }
        }
        if (isLow) {
            if (hasAnyPotion && (!hasAnyEffect || hasFireResistance)) {

                return false;
            } else return true;
        }

        return true;


//        if (entity.getHealth() / entity.getHealth() <= 0.2f) {
//            if (!hasLifesavingEffects(entity))
//        }
//
//
//        if (entity.isOnFire()) {
//            final boolean hasFireResistance = entity.hasStatusEffect(StatusEffects.FIRE_RESISTANCE);
//            if (entity.getHealth() / entity.getHealth() > 0.2f && hasFireResistance) {
//                return true;
//            }
//            if (findPotion(inventoryContents, StatusEffects.FIRE_RESISTANCE).first.isEmpty()) return true;
//        }
//
//        if (entity.getHealth() / entity.getHealth() <= 0.2f) {
//            return hasLifesavingEffects(entity) || findLifesavingPotions(inventoryContents, entity).first.isEmpty();
//        }
//
//        return true;

        //return getLifeSavingEffects().stream().anyMatch(entity::hasStatusEffect);
    }

    private static List<RegistryEntry<StatusEffect>> getLifeSavingEffects(final boolean withFire) {
        //System.out.println(withFire);
        final List<RegistryEntry<StatusEffect>> acceptableStatusEffects = new ArrayList<>();
        if (withFire)
            acceptableStatusEffects.add(StatusEffects.FIRE_RESISTANCE);
        acceptableStatusEffects.add(StatusEffects.INSTANT_HEALTH);
        acceptableStatusEffects.add(StatusEffects.REGENERATION);
        return acceptableStatusEffects;
    }

    private static RegistryEntry<Potion> getPotion(final RegistryEntry<StatusEffect> effect, final int ampl, final int duration) {
        if (Potions.LONG_FIRE_RESISTANCE.value().getEffects().stream()
                .anyMatch(a -> a.getAmplifier() == ampl && a.getDuration() == duration && a.getEffectType() == effect)) {
            return Potions.LONG_FIRE_RESISTANCE;
        }

        if (Potions.FIRE_RESISTANCE.value().getEffects().stream()
                .anyMatch(a -> a.getAmplifier() == ampl && a.getDuration() == duration && a.getEffectType() == effect)) {
            return Potions.FIRE_RESISTANCE;
        }

        if (Potions.HEALING.value().getEffects().stream()
                .anyMatch(a -> a.getAmplifier() == ampl && a.getDuration() == duration && a.getEffectType() == effect)) {
            return Potions.HEALING;
        }

        if (Potions.STRONG_HEALING.value().getEffects().stream()
                .anyMatch(a -> a.getAmplifier() == ampl && a.getDuration() == duration && a.getEffectType() == effect)) {
            return Potions.STRONG_HEALING;
        }

        if (Potions.REGENERATION.value().getEffects().stream()
                .anyMatch(a -> a.getAmplifier() == ampl && a.getDuration() == duration && a.getEffectType() == effect)) {
            return Potions.REGENERATION;
        }

        if (Potions.STRONG_REGENERATION.value().getEffects().stream()
                .anyMatch(a -> a.getAmplifier() == ampl && a.getDuration() == duration && a.getEffectType() == effect)) {
            return Potions.STRONG_REGENERATION;
        }

        if (Potions.LONG_REGENERATION.value().getEffects().stream()
                .anyMatch(a -> a.getAmplifier() == ampl && a.getDuration() == duration && a.getEffectType() == effect)) {
            return Potions.LONG_REGENERATION;
        }

        return Potions.AWKWARD;
    }

    public static Pair<ItemStack, RegistryEntry<Potion>> findLifesavingPotions(final List<ItemStack> inventoryContents, final LivingEntity entity) {
        final boolean withFire = (entity.isOnFire() || entity.isInLava()) && !entity.hasStatusEffect(StatusEffects.FIRE_RESISTANCE);
        final List<RegistryEntry<StatusEffect>> acceptableEffects =
                getLifeSavingEffects(withFire);

        final Set<RegistryEntry<StatusEffect>> actives = entity.getStatusEffects().stream().map(StatusEffectInstance::getEffectType).collect(Collectors.toSet());

        final List<RegistryEntry<StatusEffect>> filtered = acceptableEffects.stream()
                .filter(e -> actives.stream().noneMatch(a -> a.matchesKey(e.getKey().get()))).toList();

        return findPotions(inventoryContents, filtered);

    }
    private static Pair<ItemStack, RegistryEntry<Potion>> findPotions(final List<ItemStack> inventoryContents, final List<RegistryEntry<StatusEffect>> statusEffects) {
        for (final RegistryEntry<StatusEffect> statusEffect : statusEffects) {
            final Pair<ItemStack, RegistryEntry<Potion>> potion = findPotion(inventoryContents, statusEffect);
            if (!potion.first.isEmpty()) return potion;
        }
        return Pair.of(ItemStack.EMPTY, Potions.AWKWARD);
    }

    public static RegistryEntry<Potion> getPotionOfStack(final ItemStack stack, final RegistryEntry<StatusEffect> statusEffect) {

        if (stack.isEmpty()) return Potions.AWKWARD;

        final PotionContentsComponent potion_ = stack.get(DataComponentTypes.POTION_CONTENTS);

        int ampl = 0;
        int duration = 0;
        //RegistryEntry<StatusEffect> effectType;

        for (final Iterator<StatusEffectInstance> it = potion_.getEffects().iterator(); it.hasNext(); ) {
            final StatusEffectInstance instance = it.next();
            ampl = instance.getAmplifier();
            duration = instance.getDuration();
            //effectType = instance.getEffectType();
        }

        return getPotion(statusEffect, ampl, duration);
    }
    private static Pair<ItemStack, RegistryEntry<Potion>> findPotion(final List<ItemStack> inventoryContents, final RegistryEntry<StatusEffect> statusEffect) {
        //System.out.println(inventoryContents.size());
        final ItemStack stack = inventoryContents.stream()
                .filter(itemStack -> {
                    //itemStack.getComponents().forEach(e -> System.out.println(e.toString()));
                    //System.out.println(itemStack.getComponents());
                    return !itemStack.isEmpty() && itemStack.contains(DataComponentTypes.POTION_CONTENTS);
                })
                .filter(itemStack -> {
                    PotionContentsComponent potion = itemStack.get(DataComponentTypes.POTION_CONTENTS);
                    return StreamSupport.stream(potion.getEffects().spliterator(), false)
                            .anyMatch(effect -> effect.getEffectType() == statusEffect);
                })
                .max(Comparator.comparing(itemStack -> {
                    final PotionContentsComponent potion = itemStack.get(DataComponentTypes.POTION_CONTENTS);
                    return StreamSupport.stream(potion.getEffects().spliterator(), false)
                            .filter(effect -> effect.getEffectType() == statusEffect)
                            .mapToInt(StatusEffectInstance::getAmplifier)
                            .max()
                            .orElse(0);
                }))
                .orElse(ItemStack.EMPTY);

        final RegistryEntry<Potion> actualPotion = getPotionOfStack(stack, statusEffect);

        return Pair.of(stack, actualPotion);
    }
}
