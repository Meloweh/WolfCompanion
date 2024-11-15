package github.meloweh.wolfcompanion.screen;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.network.SampleC2SPayload;
import github.meloweh.wolfcompanion.screenhandler.ExampleInventoryScreenHandler2;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ExampleInventoryBlockScreen2 extends HandledScreen<ExampleInventoryScreenHandler2> {
    //private static final Identifier TEXTURE = WolfCompanion.id("textures/gui/container/example_inventory_block.png");
    private static final Identifier CHEST_SLOTS_TEXTURE = Identifier.ofVanilla("container/horse/chest_slots");
    //private static final Identifier SADDLE_SLOT_TEXTURE = Identifier.ofVanilla("container/horse/saddle_slot");

    private static final Identifier LLAMA_ARMOR_SLOT_TEXTURE = Identifier.ofVanilla("container/horse/llama_armor_slot");
    private static final Identifier ARMOR_SLOT_TEXTURE = Identifier.ofVanilla("container/horse/armor_slot");
    private static final Identifier TEXTURE = WolfCompanion.id("textures/gui/container/horse.png"); //Identifier.ofVanilla("textures/gui/container/horse.png");
    //private static final Identifier SADDLE_SLOT_TEXTURE = WolfCompanion.id("textures/gui/container/armor_slot.png");
    private final WolfEntity wolf;
    private final int slotColumnCount;
    private float mouseX;
    private float mouseY;
    private SimpleInventory wolfInventory;
    private PlayerEntity player;
    private ExampleInventoryScreenHandler2 handler;

    public ExampleInventoryBlockScreen2(ExampleInventoryScreenHandler2 handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 184;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
        //this.entity = inventory.player;
        this.slotColumnCount = 5;
        this.wolf = handler.getWolf();
        this.wolfInventory = handler.getWolfInventory();
        this.player = inventory.player;
        this.handler = handler;
    }



    /*@Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }*/

    /*@Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }*/

    @Override
    public void close() {
        super.close();
        /*System.out.println("BlockScreen2 onClosed");
        System.out.println("BlockScreen2: " + player.getWorld().isClient + "   " + handler.getWolf().getWorld().isClient);
        for (int k = 0; k < handler.slots.size(); k++) {
                Slot slot = getScreenHandler().getSlot(k);
                ItemStack stack = slot.getStack();
                if (stack.isEmpty()) continue;
                System.out.println("BlockScreen2 has item: " + player.getWorld().isClient + "   " + handler.getWolf().getWorld().isClient + " " + stack.getItem().toString());
                //System.out.println(stack.getItem().toString());
        }*/
        //ClientPlayNetworking.send(new UuidPayload(wolf.getUuid(), NBTHelper.getWolfNBT(wolf)));
        if (player.getWorld().isClient)
            ClientPlayNetworking.send(new SampleC2SPayload("It says we are on " + player.getWorld().isClient, 69));

    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        if (this.slotColumnCount > 0) {
            context.drawGuiTexture(CHEST_SLOTS_TEXTURE, 90, 54, 0, 0, i + 79, j + 17, this.slotColumnCount * 18, 54);
        }

        //if (this.entity.canBeSaddled()) {
        //context.drawGuiTexture(SADDLE_SLOT_TEXTURE, i + 7, j + 35 - 18, 18, 18);
        //}

        /*
        if (this.entity.canUseSlot(EquipmentSlot.BODY)) {
            if (this.entity instanceof LlamaEntity) {
                context.drawGuiTexture(LLAMA_ARMOR_SLOT_TEXTURE, i + 7, j + 35, 18, 18);
            } else {
                context.drawGuiTexture(ARMOR_SLOT_TEXTURE, i + 7, j + 35, 18, 18);
            }
        }*/

        InventoryScreen.drawEntity(context, i + 26, j + 18, i + 78, j + 70, 33, 0.25F, this.mouseX, this.mouseY, this.wolf);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.mouseX = (float)mouseX;
        this.mouseY = (float)mouseY;
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}