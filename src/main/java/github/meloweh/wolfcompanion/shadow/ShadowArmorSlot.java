package github.meloweh.wolfcompanion.shadow;

import com.mojang.datafixers.util.Pair;
import github.meloweh.wolfcompanion.util.WolfArmorHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.Nullable;

public class ShadowArmorSlot extends Slot {
    @Nullable
    private final ResourceLocation backgroundSprite;

    public ShadowArmorSlot(Container inventory, LivingEntity entity, int index, int x, int y, @Nullable ResourceLocation backgroundSprite) {
        super(inventory, index, x, y);
        this.backgroundSprite = backgroundSprite;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return WolfArmorHelper.isWolfArmor(stack);
    }

    @Override
    public boolean mayPickup(Player playerEntity) {
        ItemStack itemStack = this.getItem();
        return (itemStack.isEmpty()
                || playerEntity.isCreative()
                || !EnchantmentHelper.hasBindingCurse(itemStack)) && super.mayPickup(playerEntity);
    }


    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return this.backgroundSprite != null
                ? Pair.of(InventoryMenu.BLOCK_ATLAS, this.backgroundSprite)
                : super.getNoItemIcon();
    }
}
