package me.pajic.tiered_chests;
 
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.pajic.tiered_chests.config.ModConfig;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TieredChests {

	public static final String MOD_ID = "tiered_chests";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static ModConfig CONFIG;

	public static void onInitialize() {
		CONFIG = ConfigApiJava.registerAndLoadConfig(ModConfig::new);
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
