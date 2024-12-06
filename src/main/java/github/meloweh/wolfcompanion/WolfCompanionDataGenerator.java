package github.meloweh.wolfcompanion;

import github.meloweh.wolfcompanion.data.provider.*;
import github.meloweh.wolfcompanion.init.InitItem;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import org.jetbrains.annotations.Nullable;

public class WolfCompanionDataGenerator implements DataGeneratorEntrypoint {
//	@Override
//	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
//
//		ModelLoadingPlugin.register(pluginContext -> {
//			pluginContext.addModels(InitItem.DOG_WHISTLE_ITEM_ID);
//		// Replace the brown glazed terracotta model with a missing model without affecting child models.
//		// Since 1.21.4, the item model is not a child model, so it is also affected.
//		pluginContext.modifyModelOnLoad().register(ModelModifier.WRAP_PHASE, (model, context) -> {
//				if (context.id().equals(InitItem.DOG_WHISTLE_ITEM_ID)) {
//					return new WrapperUnbakedModel(model) {
//						@Override
//						public void resolve(Resolver resolver) {
//							super.resolve(resolver);
//							resolver.resolve(MissingModel.ID);
//						}
//
//						@Override
//						public BakedModel bake(ModelTextures textures, Baker baker, ModelBakeSettings settings, boolean ambientOcclusion, boolean isSideLit, ModelTransformation transformation) {
//							return baker.bake(MissingModel.ID, settings);
//						}
//					};
//				}
//				return model;
//			});
//		});
//
//		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
//		//pack.addProvider(WolfCompanionModelProvider::new);
//		pack.addProvider(WolfCompanionEnglishLanguageProvider::new);
//		pack.addProvider(WolfCompanionLootTableProvider::new);
//		pack.addProvider(WolfCompanionBlockTagProvider::new);
//		pack.addProvider(WolfCompanionRecipeProvider::new);
//		WolfCompanion.LOGGER.warn("LOADED onInitializeDataGenerator");
//	}

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		//pack.addProvider(WolfCompanionModelProvider::new);
		pack.addProvider(WolfCompanionEnglishLanguageProvider::new);
		pack.addProvider(WolfCompanionLootTableProvider::new);
		pack.addProvider(WolfCompanionBlockTagProvider::new);
		pack.addProvider(WolfCompanionRecipeProvider::new);
		WolfCompanion.LOGGER.warn("LOADED onInitializeDataGenerator");


	}

	@Override
	public @Nullable String getEffectiveModId() {
		return DataGeneratorEntrypoint.super.getEffectiveModId();
	}

	//	@Override
//	public void buildRegistry(RegistryBuilder registryBuilder) {
//		registryBuilder.addRegistry(
//				TEST_DATAGEN_DYNAMIC_REGISTRY_KEY,
//				this::bootstrapTestDatagenRegistry
//		);
//		// do NOT add TEST_DATAGEN_DYNAMIC_EMPTY_REGISTRY_KEY, should still work without it
//	}
}
