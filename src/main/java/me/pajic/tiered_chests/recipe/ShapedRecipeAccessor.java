package me.pajic.tiered_chests.recipe;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

public interface ShapedRecipeAccessor {
    Recipe.CommonInfo getCommonInfo();
    CraftingRecipe.CraftingBookInfo getBookInfo();
    ShapedRecipePattern getPattern();
    ItemStackTemplate getResult();
}
