package github.meloweh.wolfcompanion.registry;

import github.meloweh.wolfcompanion.WolfCompanion;
import github.meloweh.wolfcompanion.entity.ArmadilloEntity;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;

public final class ModEntities {
    public static final EntityType<ArmadilloEntity> ARMADILLO = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            WolfCompanion.id("armadillo"),
            FabricEntityTypeBuilder.<ArmadilloEntity>createMob()
                    .spawnGroup(MobCategory.CREATURE)
                    .entityFactory(ArmadilloEntity::new)
                    .dimensions(EntityDimensions.fixed(0.7F, 0.65F))
                    .spawnRestriction(SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ArmadilloEntity::checkArmadilloSpawnRules)
                    .trackRangeBlocks(8)
                    .build()
    );

    private ModEntities() {
    }

    public static void register() {
        FabricDefaultAttributeRegistry.register(ARMADILLO, ArmadilloEntity.createAttributes());
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA),
                MobCategory.CREATURE,
                ARMADILLO,
                8,
                2,
                3
        );
    }
}
