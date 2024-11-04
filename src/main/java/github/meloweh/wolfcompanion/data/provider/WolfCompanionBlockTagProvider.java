package github.meloweh.wolfcompanion.data.provider;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.init.InitBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class WolfCompanionBlockTagProvider extends FabricTagProvider.BlockTagProvider{
    public WolfCompanionBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    private static final TagKey<Block> EXAMPLE_TAG = TagKey.of(RegistryKeys.BLOCK, WolfCompanion.id("example"));

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(InitBlock.EXAMPLE_BLOCK);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(InitBlock.EXAMPLE_BLOCK)
                .add(Blocks.BLUE_ORCHID);
    }
}
