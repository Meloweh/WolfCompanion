package github.meloweh.wolfcompanion.data.provider;

import github.meloweh.wolfcompanion.registry.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import java.util.function.Consumer;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;

public class WolfCompanionRecipeProvider extends FabricRecipeProvider {
    public WolfCompanionRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> recipeExporter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SINGLE_WOLF_BAG)
                .define('A', ModItems.ARMADILLO_SCUTE)
                .define('C', Items.CHEST)
                .pattern(" A ")
                .pattern("ACA")
                .pattern("AAA")
                .unlockedBy(getHasName(Items.CHEST), has(Items.CHEST))
                .unlockedBy(getHasName(ModItems.ARMADILLO_SCUTE), has(ModItems.ARMADILLO_SCUTE))
                .save(recipeExporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.WOLF_BAG)
                .define('A', ModItems.ARMADILLO_SCUTE)
                .define('C', ModItems.SINGLE_WOLF_BAG)
                .pattern(" A ")
                .pattern("C C")
                .pattern(" A ")
                .unlockedBy(getHasName(ModItems.SINGLE_WOLF_BAG), has(ModItems.SINGLE_WOLF_BAG))
                .unlockedBy(getHasName(ModItems.ARMADILLO_SCUTE), has(ModItems.ARMADILLO_SCUTE))
                .save(recipeExporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.WOLF_ARMOR)
                .define('A', ModItems.ARMADILLO_SCUTE)
                .pattern(" A ")
                .pattern("AAA")
                .pattern("A A")
                .unlockedBy(getHasName(ModItems.ARMADILLO_SCUTE), has(ModItems.ARMADILLO_SCUTE))
                .save(recipeExporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.DOG_WHISTLE)
                .define('I', Items.IRON_INGOT)
                .pattern("   ")
                .pattern("III")
                .pattern(" II")
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(recipeExporter);
    }
}
