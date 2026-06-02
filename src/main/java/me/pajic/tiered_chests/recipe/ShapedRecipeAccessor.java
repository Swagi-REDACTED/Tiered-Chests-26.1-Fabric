package me.pajic.tiered_chests.recipe;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

public interface ShapedRecipeAccessor {
    Recipe.CommonInfo tieredchests$getCommonInfo();
    CraftingRecipe.CraftingBookInfo tieredchests$getBookInfo();
    ShapedRecipePattern tieredchests$getPattern();
    ItemStackTemplate tieredchests$getResult();
}
