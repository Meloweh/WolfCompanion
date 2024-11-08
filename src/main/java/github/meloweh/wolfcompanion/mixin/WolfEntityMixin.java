package github.meloweh.wolfcompanion.mixin;

import github.meloweh.wolfcompanion.accessor.ServerPlayerAccessor;
import github.meloweh.wolfcompanion.entity.AbstractInventoryWolf;
import github.meloweh.wolfcompanion.entity.WolfInventoryEntity;
import github.meloweh.wolfcompanion.network.WolfInventoryPayload;
import github.meloweh.wolfcompanion.screenhandler.WolfInventoryScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.OpenHorseScreenS2CPacket;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WolfEntity.class)
public abstract class WolfEntityMixin implements InventoryChangedListener, RideableInventory, Tameable, JumpingMount, Saddleable {
    private boolean customRenderFlag;
    private final SimpleInventory inventory = new SimpleInventory(9);

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTickEnd(CallbackInfo ci) {
        // Set the customRenderFlag based on logic
        this.customRenderFlag = true; // example condition
    }

    public void openWolfInventory(final ServerPlayerEntity player, AbstractInventoryWolf horse, Inventory inventory) {
        if (player.currentScreenHandler != player.playerScreenHandler) {
            player.closeHandledScreen();
        }

        ((ServerPlayerAccessor) player).execIncrementScreenHandlerSyncId();
        int i = horse.getInventoryColumns();
        player.networkHandler.sendPacket(new OpenHorseScreenS2CPacket(((ServerPlayerAccessor) player).getScreenHandlerSyncId(), i, horse.getId()));
        player.currentScreenHandler = new HorseScreenHandler(((ServerPlayerAccessor) player).getScreenHandlerSyncId(), player.getInventory(), inventory, horse, i);
        ((ServerPlayerAccessor) player).execOnScreenHandlerOpened(player.currentScreenHandler);
    }

    @Inject(method = "interactMob", at = @At(value = "HEAD"), cancellable = true)
    private void onRightClick(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!player.getWorld().isClient() && hand == Hand.MAIN_HAND) {
            player.openHandledScreen(new WolfInventoryEntity(player, this));
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

    public boolean getCustomRenderFlag() {
        return customRenderFlag;
    }

    /*@Override
    public SimpleInventory getInventory() {
        return inventory;
    }*/
}

