package github.meloweh.wolfcompanion.networking;

import github.meloweh.wolfcompanion.network.WolfEatS2CPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.particle.CrackParticle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RegisterPayloads {
//    public static void register() {
//        ClientPlayNetworking.registerGlobalReceiver(WolfEatS2CPayload.ID, (payload, context) -> {
//            context.client().execute(() -> {
//                final int wolfId = payload.id();
//                final ItemStack foodItem = payload.stack();
//                final PlayerEntity player = context.client().player;
//                World world = player.getEntityWorld();
//                Entity entity = world.getEntityById(wolfId);
//                if(entity != null) {
//                    Vec3d velocity = new Vec3d((world.random.nextFloat() - 0.5D) * 0.1D, world.random.nextFloat() * 0.1D + 0.1D, 0.0D);
//                    velocity = velocity.rotatePitch(-entity.getPitch() * (float)(Math.PI / 180));
//                    velocity = velocity.rotateYaw(-entity.getYaw() * (float)(Math.PI / 180));
//                    Vec3d position = new Vec3d(world.random.nextFloat() - 0.5D, -world.random.nextFloat() * 0.6D - 0.3D, 0.6D);
//                    position = position.rotatePitch(-entity.getPitch() * (float)(Math.PI / 180));
//                    position = position.rotateYaw(-entity.getYaw() * (float)(Math.PI / 180));
//                    position = position.add(entity.getX(), entity.getY() + entity.getEyeHeight(entity.getPose()), entity.getZ());
//
//                    new CrackParticle.ItemFactory().createParticle(foodItem.)
//
//                    if(foodItem.getHasSubtypes()) {
//                        ParticleEffect pe =
//                        world.addParticle(EnumParticleTypes.ITEM_CRACK,
//                                position.x,
//                                position.y,
//                                position.z,
//                                velocity.x,
//                                velocity.y + 0.05D,
//                                velocity.z,
//                                Item.getRawId(foodItem.getItem()), foodItem.getComponents());
//                    }
//                    else {
//                        world.spawnParticle(EnumParticleTypes.ITEM_CRACK,
//                                position.x,
//                                position.y,
//                                position.z,
//                                velocity.x,
//                                velocity.y + 0.05D,
//                                velocity.z,
//                                Item.getIdFromItem(foodItem.getItem()));
//                    }
//                }
//
//                //ClientBlockHighlighting.highlightBlock(client, payload.blockPos());
//            });
//        });
//
//    }
}
