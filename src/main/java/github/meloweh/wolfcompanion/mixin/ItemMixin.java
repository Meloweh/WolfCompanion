package github.meloweh.wolfcompanion.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.Properties.class)
public class ItemMixin {
    @Inject(method = "wolfArmor", at = @At("TAIL"), cancellable = true)
    private void makeEnchantable(ArmorMaterial material, CallbackInfoReturnable<Item.Properties> cir) {
        final Item.Properties settings = ((Item.Properties) (Object) this).durability(ArmorType.BODY.getDurability(material.durability())).attributes(material.createAttributes(ArmorType.BODY)).repairable(material.repairIngredient()).component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.BODY).setEquipSound(material.equipSound()).setAsset(material.assetId()).setAllowedEntities(HolderSet.direct(new Holder[]{EntityType.WOLF.builtInRegistryHolder()})).build()).component(DataComponents.BREAK_SOUND, SoundEvents.WOLF_ARMOR_BREAK).stacksTo(1);

        settings.enchantable(15);
        cir.setReturnValue(settings);
        cir.cancel();
    }
}
