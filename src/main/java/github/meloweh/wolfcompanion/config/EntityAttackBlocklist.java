package github.meloweh.wolfcompanion.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

final class EntityAttackBlocklist {
    private final Set<EntityType<?>> entityTypes = new HashSet<>();
    private final Set<TagKey<EntityType<?>>> tags = new HashSet<>();

    void reload(List<String> entries) {
        entityTypes.clear();
        tags.clear();

        for (String entry : entries) {
            add(entry);
        }
    }

    boolean contains(Entity entity) {
        EntityType<?> type = entity.getType();
        if (entityTypes.contains(type)) {
            return true;
        }

        for (TagKey<EntityType<?>> tag : tags) {
            if (type.builtInRegistryHolder().is(tag)) {
                return true;
            }
        }
        return false;
    }

    private void add(String rawEntry) {
        if (rawEntry == null || rawEntry.isBlank()) {
            return;
        }

        String entry = rawEntry.trim();
        if (entry.startsWith("#")) {
            addTag(entry.substring(1));
        } else {
            addEntityType(entry);
        }
    }

    private void addTag(String rawId) {
        Identifier id = Identifier.tryParse(rawId);
        if (id != null) {
            tags.add(TagKey.create(BuiltInRegistries.ENTITY_TYPE.key(), id));
        }
    }

    private void addEntityType(String rawId) {
        Identifier id = Identifier.tryParse(rawId);
        if (id != null) {
            entityTypes.add(BuiltInRegistries.ENTITY_TYPE.getValue(id));
        }
    }
}
