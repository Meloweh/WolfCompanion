package github.meloweh.wolfcompanion.util;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

public class WolfInventoryHelper {
    private static boolean hasLifesavingEffects(final LivingEntity entity) {
        return getLifeSavingEffects(true).stream().anyMatch(entity::hasEffect);
    }

    public static boolean hasFittingLifesavingEffect(final LivingEntity entity) {
        final boolean isLow = entity.getHealth() <= 7;
        final boolean isHot = entity.isOnFire() || entity.isInLava();
        final boolean hasAnyEffect = hasLifesavingEffects(entity);
        final boolean hasFireResistance = entity.hasEffect(MobEffects.FIRE_RESISTANCE);

        if (isHot && !hasFireResistance) return false;
        if (isLow) {
            if (!hasAnyEffect || hasFireResistance) {
                return false;
            } else return true;
        }
        return true;
    }

    public static boolean hasFittingLifesavingEffect(final LivingEntity entity, final List<ItemStack> inventoryContents) {
        final boolean hasFireResistance = entity.hasEffect(MobEffects.FIRE_RESISTANCE);
        final boolean isHot = entity.isOnFire() || entity.isInLava();
        final boolean isLow = entity.getHealth() <= 7;
        final boolean hasAnyEffect = hasLifesavingEffects(entity);
        final boolean hasAnyPotion = !findLifesavingPotions(inventoryContents, entity).first.isEmpty();
        final boolean hasFirePotion = !findPotion(inventoryContents, MobEffects.FIRE_RESISTANCE).first.isEmpty();

        boolean hasArmor = false;

        if (entity instanceof Wolf wolf) {
            hasArmor = WolfArmorHelper.hasArmor(wolf);
        }

        if (entity instanceof Player) {
            Player p = (Player) entity;
            boolean isOk = true;

            for(EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR) {
                    ItemStack e = entity.getItemBySlot(equipmentSlot);

                    if (!e.is(Items.NETHERITE_BOOTS) && !e.is(Items.NETHERITE_LEGGINGS) && !e.is(Items.NETHERITE_CHESTPLATE) && !e.is(Items.NETHERITE_HELMET)) {
                        isOk = false;
                        break;
                    }
                }
            }

            hasArmor = isOk;
        }

        if (entity.isInLava()) {
            if (!hasFireResistance) {
                if (hasAnyPotion && !hasArmor || hasFirePotion) return false;
            }
        }
        if (isLow) {
            if (hasAnyPotion && (!hasAnyEffect || hasFireResistance)) {

                return false;
            } else return true;
        }

        return true;
    }

    private static List<MobEffect> getLifeSavingEffects(final boolean withFire) {
        final List<MobEffect> acceptableStatusEffects = new ArrayList<>();
        if (withFire)
            acceptableStatusEffects.add(MobEffects.FIRE_RESISTANCE);
        acceptableStatusEffects.add(MobEffects.HEAL);
        acceptableStatusEffects.add(MobEffects.REGENERATION);
        return acceptableStatusEffects;
    }

    private static Potion getPotion(final MobEffect effect, final int ampl, final int duration) {
        if (Potions.LONG_FIRE_RESISTANCE.getEffects().stream()
                .anyMatch(a -> a.getAmplifier() == ampl && a.getDuration() == duration && a.getEffect() == effect)) {
            return Potions.LONG_FIRE_RESISTANCE;
        }

        if (Potions.FIRE_RESISTANCE.getEffects().stream()
                .anyMatch(a -> a.getAmplifier() == ampl && a.getDuration() == duration && a.getEffect() == effect)) {
            return Potions.FIRE_RESISTANCE;
        }

        if (Potions.HEALING.getEffects().stream()
                .anyMatch(a -> a.getAmplifier() == ampl && a.getDuration() == duration && a.getEffect() == effect)) {
            return Potions.HEALING;
        }

        if (Potions.STRONG_HEALING.getEffects().stream()
                .anyMatch(a -> a.getAmplifier() == ampl && a.getDuration() == duration && a.getEffect() == effect)) {
            return Potions.STRONG_HEALING;
        }

        if (Potions.REGENERATION.getEffects().stream()
                .anyMatch(a -> a.getAmplifier() == ampl && a.getDuration() == duration && a.getEffect() == effect)) {
            return Potions.REGENERATION;
        }

        if (Potions.STRONG_REGENERATION.getEffects().stream()
                .anyMatch(a -> a.getAmplifier() == ampl && a.getDuration() == duration && a.getEffect() == effect)) {
            return Potions.STRONG_REGENERATION;
        }

        if (Potions.LONG_REGENERATION.getEffects().stream()
                .anyMatch(a -> a.getAmplifier() == ampl && a.getDuration() == duration && a.getEffect() == effect)) {
            return Potions.LONG_REGENERATION;
        }

        return Potions.AWKWARD;
    }

    public static Pair<ItemStack, Potion> findLifesavingPotions(final List<ItemStack> inventoryContents, final LivingEntity entity) {
        final boolean withFire = (entity.isOnFire() || entity.isInLava()) && !entity.hasEffect(MobEffects.FIRE_RESISTANCE);
        final List<MobEffect> acceptableEffects =
                getLifeSavingEffects(withFire);

        final Set<MobEffect> actives = entity.getActiveEffects().stream().map(MobEffectInstance::getEffect).collect(Collectors.toSet());

        final List<MobEffect> filtered = acceptableEffects.stream()
                .filter(e -> !actives.contains(e)).toList();

        return findPotions(inventoryContents, filtered);

    }
    private static Pair<ItemStack, Potion> findPotions(final List<ItemStack> inventoryContents, final List<MobEffect> statusEffects) {
        for (final MobEffect statusEffect : statusEffects) {
            final Pair<ItemStack, Potion> potion = findPotion(inventoryContents, statusEffect);
            if (!potion.first.isEmpty()) return potion;
        }
        return Pair.of(ItemStack.EMPTY, Potions.AWKWARD);
    }

    public static Potion getPotionOfStack(final ItemStack stack, final MobEffect statusEffect) {

        if (stack.isEmpty()) return Potions.AWKWARD;

        int ampl = 0;
        int duration = 0;

        for (final Iterator<MobEffectInstance> it = PotionUtils.getMobEffects(stack).iterator(); it.hasNext(); ) {
            final MobEffectInstance instance = it.next();
            ampl = instance.getAmplifier();
            duration = instance.getDuration();
        }

        return getPotion(statusEffect, ampl, duration);
    }
    private static Pair<ItemStack, Potion> findPotion(final List<ItemStack> inventoryContents, final MobEffect statusEffect) {
        final ItemStack stack = inventoryContents.stream()
                .filter(WolfInventoryHelper::hasPotionContents)
                .filter(itemStack -> {
                    List<MobEffectInstance> potion = PotionUtils.getMobEffects(itemStack);
                    return StreamSupport.stream(potion.spliterator(), false)
                            .anyMatch(effect -> effect.getEffect() == statusEffect);
                })
                .max(Comparator.comparing(itemStack -> {
                    final List<MobEffectInstance> potion = PotionUtils.getMobEffects(itemStack);
                    return StreamSupport.stream(potion.spliterator(), false)
                            .filter(effect -> effect.getEffect() == statusEffect)
                            .mapToInt(MobEffectInstance::getAmplifier)
                            .max()
                            .orElse(0);
                }))
                .orElse(ItemStack.EMPTY);

        final Potion actualPotion = getPotionOfStack(stack, statusEffect);

        return Pair.of(stack, actualPotion);
    }

    public static boolean hasPotionContents(ItemStack stack) {
        return !stack.isEmpty()
                && (stack.is(Items.POTION) || stack.is(Items.SPLASH_POTION) || stack.is(Items.LINGERING_POTION));
    }
}
