package github.meloweh.wolfcompanion.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import github.meloweh.wolfcompanion.WolfCompanion;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;

public class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "wolf_config.json");

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
    }

    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
            System.out.println("Config saved: " + config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
