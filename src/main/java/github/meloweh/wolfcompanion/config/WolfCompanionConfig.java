package github.meloweh.wolfcompanion.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import github.meloweh.wolfcompanion.WolfCompanion;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.Entity;
import java.nio.file.Files;

public final class WolfCompanionConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "wolf_config.json");
    private static final EntityAttackBlocklist ATTACK_BLOCKLIST = new EntityAttackBlocklist();

    private static WolfConfig current = new WolfConfig();

    private WolfCompanionConfig() {
    }

    public static void load() {
        ensureConfigFileExists();

        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                current = GSON.fromJson(reader, WolfConfig.class);
                if (current == null) {
                    current = new WolfConfig();
                }
                current.normalize();
                WolfCompanion.LOGGER.info("Config loaded: {}", current);
            } catch (IOException e) {
                WolfCompanion.LOGGER.warn("Failed to load config; saving defaults.", e);
                save();
            }
        } else {
            save();
        }

        ATTACK_BLOCKLIST.reload(current.doNotAttackMobs);
    }

    public static WolfConfig current() {
        return current;
    }

    public static WolfConfig editableCopy() {
        return current.copy();
    }

    public static void save() {
        current.normalize();
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(current, writer);
            WolfCompanion.LOGGER.info("Config saved: {}", current);
        } catch (IOException e) {
            WolfCompanion.LOGGER.warn("Failed to save config.", e);
        }
    }

    public static void apply(WolfConfig nextConfig) {
        current = nextConfig.copy();
        current.normalize();
        save();
        ATTACK_BLOCKLIST.reload(current.doNotAttackMobs);
    }

    public static boolean isEntityAttackBlocked(Entity attacker) {
        return ATTACK_BLOCKLIST.contains(attacker);
    }

    private static void ensureConfigFileExists() {
        if (CONFIG_FILE.exists()) {
            return;
        }

        try (InputStream input = WolfCompanion.class.getResourceAsStream("/wolf_config.json")) {
            if (input == null) {
                save();
                return;
            }
            Files.copy(input, CONFIG_FILE.toPath());
        } catch (IOException e) {
            WolfCompanion.LOGGER.warn("Failed to copy default config; saving generated defaults.", e);
            save();
        }
    }
}
