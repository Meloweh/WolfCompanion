package github.meloweh.wolfcompanion.data.provider;

import github.meloweh.wolfcompanion.init.InitItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class WolfCompanionRecipeProvider extends FabricRecipeProvider {
    public WolfCompanionRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter recipeExporter) {
//        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, InitItem.ITEM_SINGLE_WOLF_BAG)
//                .input('C', Items.CHEST).input('S', Items.ARMADILLO_SCUTE)
//                .pattern(" S ")
//                .pattern("SCS")
//                .pattern("SSS")
//                .criterion(hasItem(Items.CHEST), conditionsFromItem(Items.CHEST))
//                .criterion(hasItem(Items.ARMADILLO_SCUTE), conditionsFromItem(Items.ARMADILLO_SCUTE))
//                .offerTo(recipeExporter);

//        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, InitItem.ITEM_WOLF_BAG)
//                .input('S', Items.ARMADILLO_SCUTE).input('B', InitItem.ITEM_SINGLE_WOLF_BAG)
//                .pattern(" S ")
//                .pattern("B B")
//                .pattern(" S ")
//                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, InitItem.ITEM_SINGLE_WOLF_BAG)
                .input('A', Items.ARMADILLO_SCUTE)
                .input('C', Items.CHEST)
                .pattern(" A ")
                .pattern("ACA")
                .pattern("AAA")
                .criterion(hasItem(Items.CHEST), conditionsFromItem(Items.CHEST))
                .criterion(hasItem(Items.ARMADILLO_SCUTE), conditionsFromItem(Items.ARMADILLO_SCUTE))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, InitItem.ITEM_WOLF_BAG)
                .input('A', Items.ARMADILLO_SCUTE)
                .input('C', InitItem.ITEM_SINGLE_WOLF_BAG)
                .pattern(" A ")
                .pattern("C C")
                .pattern(" A ")
                .criterion(hasItem(InitItem.ITEM_SINGLE_WOLF_BAG), conditionsFromItem(InitItem.ITEM_SINGLE_WOLF_BAG))
                .criterion(hasItem(Items.ARMADILLO_SCUTE), conditionsFromItem(Items.ARMADILLO_SCUTE))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, InitItem.DOG_WHISTLE_ITEM)
                .input('I', Items.IRON_INGOT)
                .pattern("   ")
                .pattern("III")
                .pattern(" II")
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .offerTo(recipeExporter);
    }
}
