package me.pajic.tiered_chests;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TieredChests {

	public static final String MOD_ID = "tiered_chests";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static void onInitialize() {
		LOGGER.info("Initializing Tiered Chests...");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
