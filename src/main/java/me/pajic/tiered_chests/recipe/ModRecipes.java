package me.pajic.tiered_chests.recipe;

import me.pajic.tiered_chests.TieredChests;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ModRecipes {
    public static final RecipeSerializer<UpgradeRecipe> UPGRADE_SERIALIZER = new RecipeSerializer<>(UpgradeRecipe.MAP_CODEC, UpgradeRecipe.STREAM_CODEC);

    public static void init() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, TieredChests.id("upgrade"), UPGRADE_SERIALIZER);
    }
}
