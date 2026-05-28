package github.meloweh.wolfcompanion.data.provider;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.init.InitBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import java.util.concurrent.CompletableFuture;

public class WolfCompanionBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
    public WolfCompanionBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    private static final TagKey<Block> EXAMPLE_TAG = TagKey.create(Registries.BLOCK, WolfCompanion.id("example"));

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
//        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
//                .add(InitBlock.EXAMPLE_BLOCK);
//        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
//                .add(InitBlock.EXAMPLE_BLOCK)
//                .add(Blocks.BLUE_ORCHID);
    }
}
