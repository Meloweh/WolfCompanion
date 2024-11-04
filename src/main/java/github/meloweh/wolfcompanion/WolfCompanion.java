package github.meloweh.wolfcompanion;

import github.meloweh.wolfcompanion.init.InitBlock;
import github.meloweh.wolfcompanion.init.InitItem;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WolfCompanion implements ModInitializer {
	public static final String MOD_ID = "wolfcompanion";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Loading...");
		InitItem.load();
		InitBlock.load();
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}