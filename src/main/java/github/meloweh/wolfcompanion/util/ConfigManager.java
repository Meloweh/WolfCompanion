package github.meloweh.wolfcompanion.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import github.meloweh.wolfcompanion.WolfCompanion;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.io.*;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "wolf_config.json");

    private static final Set<EntityType<?>> TYPES = new HashSet<>();
    private static final Set<TagKey<EntityType<?>>> TAGS = new HashSet<>();


    public static WolfConfig config = new WolfConfig();

    public static void loadConfig() {
        if (!CONFIG_FILE.exists()) {
            try (InputStream input = WolfCompanion.class.getResourceAsStream("/wolf_config.json")) {
                Files.copy(input, CONFIG_FILE.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(reader, WolfConfig.class);
                System.out.println("Config loaded: " + config);
            } catch (IOException e) {
                e.printStackTrace();
                saveConfig(); // Save default if loading fails
            }
        } else {
            saveConfig(); // Save default if file doesn't exist
        }

        reload(ConfigManager.config.doNotAttackMobs);
    }

    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
            System.out.println("Config saved: " + config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reload(List<String> entries) {
        TYPES.clear(); TAGS.clear();
        for (String s : entries) {
            if (s.startsWith("#")) {
                Identifier id = Identifier.tryParse(s.substring(1));
                if (id != null) TAGS.add(TagKey.of(Registries.ENTITY_TYPE.getKey(), id));
                continue;
            }
            Identifier id = Identifier.tryParse(s);
            if (id == null) continue;

            TYPES.add(Registries.ENTITY_TYPE.get(id));
        }
    }

    public static boolean isBlacklisted(Entity attacker) {
        EntityType<?> t = attacker.getType();
        if (TYPES.contains(t)) return true;
        for (var tag : TAGS) if (t.isIn(tag)) return true;
        return false;
    }
}