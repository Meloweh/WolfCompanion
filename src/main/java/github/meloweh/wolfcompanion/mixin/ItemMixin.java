package github.meloweh.wolfcompanion.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.Settings.class)
public class ItemMixin {
    @Inject(method = "wolfArmor", at = @At("TAIL"), cancellable = true)
    private void makeEnchantable(ArmorMaterial material, CallbackInfoReturnable<Item.Settings> cir) {
        final Item.Settings settings = ((Item.Settings) (Object) this).maxDamage(EquipmentType.BODY.getMaxDamage(material.durability())).attributeModifiers(material.createAttributeModifiers(EquipmentType.BODY)).repairable(material.repairIngredient()).component(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(EquipmentSlot.BODY).equipSound(material.equipSound()).model(material.assetId()).allowedEntities(RegistryEntryList.of(new RegistryEntry[]{EntityType.WOLF.getRegistryEntry()})).build()).component(DataComponentTypes.BREAK_SOUND, SoundEvents.ITEM_WOLF_ARMOR_BREAK).maxCount(1);

        settings.enchantable(15);
        cir.setReturnValue(settings);
        cir.cancel();
    }
}
