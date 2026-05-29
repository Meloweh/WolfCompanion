package github.meloweh.wolfcompanion.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

public class WolfInventoryHelper {

//    private static ItemStack findLavaSituationPotion(final List<ItemStack> inventoryContents, final LivingEntity entity) {
//        final ItemStack potion = WolfInventoryHelper.findPotion(inventoryContents, StatusEffects.FIRE_RESISTANCE);
//
//        if (!potion.isEmpty()) return potion;
//
//        return WolfInventoryHelper.findLifesavingPotions(inventoryContents, entity);
//    }

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

        if (entity instanceof Wolf) {
            Wolf w = (Wolf) entity;
            hasArmor = w.isWearingBodyArmor();
        }

        if (entity instanceof Player) {
            Player p = (Player) entity;
            boolean isOk = true;

            for(EquipmentSlot equipmentSlot : EquipmentSlotGroup.ARMOR) {
                if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                    ItemStack e = entity.getItemBySlot(equipmentSlot);

                    if (!e.is(Items.NETHERITE_BOOTS) && !e.is(Items.NETHERITE_LEGGINGS) && !e.is(Items.NETHERITE_CHESTPLATE) && !e.is(Items.NETHERITE_HELMET)) {
                        isOk = false;
                        break;
                    }
                }
            }

            /*for (ItemStack e : p.getArmorItems()) {
                if (!e.isOf(Items.NETHERITE_BOOTS) && !e.isOf(Items.NETHERITE_LEGGINGS) && !e.isOf(Items.NETHERITE_CHESTPLATE) && !e.isOf(Items.NETHERITE_HELMET)) {
                    isOk = false;
                    break;
                }
            }*/

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

    private static List<Holder<MobEffect>> getLifeSavingEffects(final boolean withFire) {
        final List<Holder<MobEffect>> acceptableStatusEffects = new ArrayList<>();
        if (withFire)
            acceptableStatusEffects.add(MobEffects.FIRE_RESISTANCE);
        acceptableStatusEffects.add(MobEffects.INSTANT_HEALTH);
        acceptableStatusEffects.add(MobEffects.REGENERATION);
        return acceptableStatusEffects;
    }

    private static Holder<Potion> getPotion(final Holder<MobEffect> effect, final int ampl, final int duration) {
        if (Potions.LONG_FIRE_RESISTANCE.value().getEffects().stream()
                .anyMatch(a -> a.getAmplifier() == ampl && a.getDuration() == duration && a.getEffect() == effect)) {
            return Potions.LONG_FIRE_RESISTANCE;
        }

        if (Potions.FIRE_RESISTANCE.value().getEffects().stream()
                .anyMatch(a -> a.getAmplifier() == ampl && a.getDuration() == duration && a.getEffect() == effect)) {
            return Potions.FIRE_RESISTANCE;
        }

        if (Potions.HEALING.value().getEffects().stream()
                .anyMatch(a -> a.getAmplifier() == ampl && a.getDuration() == duration && a.getEffect() == effect)) {
            return Potions.HEALING;
        }

        if (Potions.STRONG_HEALING.value().getEffects().stream()
                .anyMatch(a -> a.getAmplifier() == ampl && a.getDuration() == duration && a.getEffect() == effect)) {
            return Potions.STRONG_HEALING;
        }

        if (Potions.REGENERATION.value().getEffects().stream()
                .anyMatch(a -> a.getAmplifier() == ampl && a.getDuration() == duration && a.getEffect() == effect)) {
            return Potions.REGENERATION;
        }

        if (Potions.STRONG_REGENERATION.value().getEffects().stream()
                .anyMatch(a -> a.getAmplifier() == ampl && a.getDuration() == duration && a.getEffect() == effect)) {
            return Potions.STRONG_REGENERATION;
        }

        if (Potions.LONG_REGENERATION.value().getEffects().stream()
                .anyMatch(a -> a.getAmplifier() == ampl && a.getDuration() == duration && a.getEffect() == effect)) {
            return Potions.LONG_REGENERATION;
        }

        return Potions.AWKWARD;
    }

    public static Pair<ItemStack, Holder<Potion>> findLifesavingPotions(final List<ItemStack> inventoryContents, final LivingEntity entity) {
        final boolean withFire = (entity.isOnFire() || entity.isInLava()) && !entity.hasEffect(MobEffects.FIRE_RESISTANCE);
        final List<Holder<MobEffect>> acceptableEffects =
                getLifeSavingEffects(withFire);

        final Set<Holder<MobEffect>> actives = entity.getActiveEffects().stream().map(MobEffectInstance::getEffect).collect(Collectors.toSet());

        final List<Holder<MobEffect>> filtered = acceptableEffects.stream()
                .filter(e -> actives.stream().noneMatch(a -> a.is(e.unwrapKey().get()))).toList();

        return findPotions(inventoryContents, filtered);

    }
    private static Pair<ItemStack, Holder<Potion>> findPotions(final List<ItemStack> inventoryContents, final List<Holder<MobEffect>> statusEffects) {
        for (final Holder<MobEffect> statusEffect : statusEffects) {
            final Pair<ItemStack, Holder<Potion>> potion = findPotion(inventoryContents, statusEffect);
            if (!potion.first.isEmpty()) return potion;
        }
        return Pair.of(ItemStack.EMPTY, Potions.AWKWARD);
    }

    public static Holder<Potion> getPotionOfStack(final ItemStack stack, final Holder<MobEffect> statusEffect) {

        if (stack.isEmpty()) return Potions.AWKWARD;

        final PotionContents potion_ = stack.get(DataComponents.POTION_CONTENTS);

        int ampl = 0;
        int duration = 0;
        //RegistryEntry<StatusEffect> effectType;

        for (final Iterator<MobEffectInstance> it = potion_.getAllEffects().iterator(); it.hasNext(); ) {
            final MobEffectInstance instance = it.next();
            ampl = instance.getAmplifier();
            duration = instance.getDuration();
            //effectType = instance.getEffectType();
        }

        return getPotion(statusEffect, ampl, duration);
    }
    private static Pair<ItemStack, Holder<Potion>> findPotion(final List<ItemStack> inventoryContents, final Holder<MobEffect> statusEffect) {
        final ItemStack stack = inventoryContents.stream()
                .filter(itemStack -> {
                    return !itemStack.isEmpty() && itemStack.has(DataComponents.POTION_CONTENTS);
                })
                .filter(itemStack -> {
                    PotionContents potion = itemStack.get(DataComponents.POTION_CONTENTS);
                    return StreamSupport.stream(potion.getAllEffects().spliterator(), false)
                            .anyMatch(effect -> effect.getEffect() == statusEffect);
                })
                .max(Comparator.comparing(itemStack -> {
                    final PotionContents potion = itemStack.get(DataComponents.POTION_CONTENTS);
                    return StreamSupport.stream(potion.getAllEffects().spliterator(), false)
                            .filter(effect -> effect.getEffect() == statusEffect)
                            .mapToInt(MobEffectInstance::getAmplifier)
                            .max()
                            .orElse(0);
                }))
                .orElse(ItemStack.EMPTY);

        final Holder<Potion> actualPotion = getPotionOfStack(stack, statusEffect);

        return Pair.of(stack, actualPotion);
    }
}
