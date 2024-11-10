package github.meloweh.wolfcompanion.screen;

import github.meloweh.wolfcompanion.network.UuidPayload;
import github.meloweh.wolfcompanion.screenhandler.WolfScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class WolfScreen extends HandledScreen<WolfScreenHandler> {
    private static final Identifier CHEST_SLOTS_TEXTURE = Identifier.ofVanilla("container/horse/chest_slots");
    private static final Identifier SADDLE_SLOT_TEXTURE = Identifier.ofVanilla("container/horse/saddle_slot");
    private static final Identifier LLAMA_ARMOR_SLOT_TEXTURE = Identifier.ofVanilla("container/horse/llama_armor_slot");
    private static final Identifier ARMOR_SLOT_TEXTURE = Identifier.ofVanilla("container/horse/armor_slot");
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/horse.png");
    private final WolfEntity entity;
    private final int slotColumnCount;
    private float mouseX;
    private float mouseY;

    public WolfScreen(WolfScreenHandler handler, PlayerInventory inventory, WolfEntity entity, int slotColumnCount) {
        super(handler, inventory, entity.getDisplayName());
        this.entity = entity;
        this.slotColumnCount = slotColumnCount;
    }

    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        if (this.slotColumnCount > 0) {
            context.drawGuiTexture(CHEST_SLOTS_TEXTURE, 90, 54, 0, 0, i + 79, j + 17, this.slotColumnCount * 18, 54);
        }

        //if (this.entity.canBeSaddled()) {
        context.drawGuiTexture(SADDLE_SLOT_TEXTURE, i + 7, j + 35 - 18, 18, 18);
        //}

        /*
        if (this.entity.canUseSlot(EquipmentSlot.BODY)) {
            if (this.entity instanceof LlamaEntity) {
                context.drawGuiTexture(LLAMA_ARMOR_SLOT_TEXTURE, i + 7, j + 35, 18, 18);
            } else {
                context.drawGuiTexture(ARMOR_SLOT_TEXTURE, i + 7, j + 35, 18, 18);
            }
        }*/

        InventoryScreen.drawEntity(context, i + 26, j + 18, i + 78, j + 70, 17, 0.25F, this.mouseX, this.mouseY, this.entity);
    }

    /*public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) return super.use(world, user, hand);

        ClientPlayNetworking.send(new UuidPayload(uuid, nbt));

        return TypedActionResult.success(user.getHandStack(hand));
    }*/


    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.mouseX = (float)mouseX;
        this.mouseY = (float)mouseY;
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
