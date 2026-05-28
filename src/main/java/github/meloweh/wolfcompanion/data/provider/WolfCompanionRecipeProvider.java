package github.meloweh.wolfcompanion.data.provider;

import github.meloweh.wolfcompanion.init.InitItem;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import java.util.concurrent.CompletableFuture;

public class WolfCompanionRecipeProvider extends FabricRecipeProvider {
    public WolfCompanionRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider wrapperLookup, RecipeOutput recipeExporter) {
        return new RecipeProvider(wrapperLookup, recipeExporter) {
            @Override
            public void buildRecipes() {
                shaped(RecipeCategory.TOOLS, InitItem.ITEM_SINGLE_WOLF_BAG)
                        .define('A', Items.ARMADILLO_SCUTE)
                        .define('C', Items.CHEST)
                        .pattern(" A ")
                        .pattern("ACA")
                        .pattern("AAA")
                        .unlockedBy(getHasName(Items.CHEST), has(Items.CHEST))
                        .unlockedBy(getHasName(Items.ARMADILLO_SCUTE), has(Items.ARMADILLO_SCUTE))
                        .save(recipeExporter);

                shaped(RecipeCategory.TOOLS, InitItem.ITEM_WOLF_BAG)
                        .define('A', Items.ARMADILLO_SCUTE)
                        .define('C', InitItem.ITEM_SINGLE_WOLF_BAG)
                        .pattern(" A ")
                        .pattern("C C")
                        .pattern(" A ")
                        .unlockedBy(getHasName(InitItem.ITEM_SINGLE_WOLF_BAG), has(InitItem.ITEM_SINGLE_WOLF_BAG))
                        .unlockedBy(getHasName(Items.ARMADILLO_SCUTE), has(Items.ARMADILLO_SCUTE))
                        .save(recipeExporter);

                shaped(RecipeCategory.TOOLS, InitItem.DOG_WHISTLE_ITEM)
                        .define('I', Items.IRON_INGOT)
                        .pattern("   ")
                        .pattern("III")
                        .pattern(" II")
                        .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                        .save(recipeExporter);

            }
        };
    }

    @Override
    public String getName() {
        return "Better Wolf Companion Recipes";
    }
}
