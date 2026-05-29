package github.meloweh.wolfcompanion.data.provider;

import github.meloweh.wolfcompanion.registry.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import java.util.concurrent.CompletableFuture;

public class WolfCompanionRecipeProvider extends FabricRecipeProvider {
    public WolfCompanionRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider wrapperLookup, RecipeOutput recipeExporter) {
        return new RecipeProvider(wrapperLookup, recipeExporter) {
            @Override
            public void buildRecipes() {
                shaped(RecipeCategory.TOOLS, ModItems.SINGLE_WOLF_BAG)
                        .define('A', Items.ARMADILLO_SCUTE)
                        .define('C', Items.CHEST)
                        .pattern(" A ")
                        .pattern("ACA")
                        .pattern("AAA")
                        .unlockedBy(getHasName(Items.CHEST), has(Items.CHEST))
                        .unlockedBy(getHasName(Items.ARMADILLO_SCUTE), has(Items.ARMADILLO_SCUTE))
                        .save(recipeExporter);

                shaped(RecipeCategory.TOOLS, ModItems.WOLF_BAG)
                        .define('A', Items.ARMADILLO_SCUTE)
                        .define('C', ModItems.SINGLE_WOLF_BAG)
                        .pattern(" A ")
                        .pattern("C C")
                        .pattern(" A ")
                        .unlockedBy(getHasName(ModItems.SINGLE_WOLF_BAG), has(ModItems.SINGLE_WOLF_BAG))
                        .unlockedBy(getHasName(Items.ARMADILLO_SCUTE), has(Items.ARMADILLO_SCUTE))
                        .save(recipeExporter);

                shaped(RecipeCategory.TOOLS, ModItems.DOG_WHISTLE)
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
